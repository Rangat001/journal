    package com.rgt.journal.controller;

    import com.rgt.journal.entity.JournalEntity;
    import com.rgt.journal.entity.UserEntity;
    import com.rgt.journal.enums.Sentiment;
    import com.rgt.journal.service.JournalEntryService;
    import com.rgt.journal.service.UserService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.redis.core.RedisTemplate;
    import org.springframework.data.redis.core.ValueOperations;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.time.Duration;
    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
    import java.util.stream.Collectors;

    @RestController
    @RequestMapping("/explore")
    public class ExploreJournalController {

        @Autowired
        private JournalEntryService journalEntryService;

        @Autowired
        private UserService userService;

        @Autowired
        private RedisTemplate<String, Object> redisTemplate;

        private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        private static final String CACHE_KEY_PREFIX = "journal:explore:";
        private static final String CACHE_UPDATE_KEY = "journal:lastUpdate";
        private static final Duration CACHE_DURATION = Duration.ofMinutes(15);

        private boolean isCacheValid(String cacheKey) {
            String lastUpdate = (String) redisTemplate.opsForValue().get(CACHE_UPDATE_KEY);
            String cacheTimestamp = (String) redisTemplate.opsForValue().get(cacheKey + ":timestamp");

            if (lastUpdate == null || cacheTimestamp == null) {
                return false;
            }

            return cacheTimestamp.compareTo(lastUpdate) >= 0;
        }

        @GetMapping("/journals")
        public ResponseEntity<?> exploreAllJournals(
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size,
                @RequestParam(required = false) String sortBy) {

            try {
                String cacheKey = CACHE_KEY_PREFIX + "page:" + page + ":size:" + size + ":sort:" + sortBy;
                ValueOperations<String, Object> ops = redisTemplate.opsForValue();

                // Check if cache is valid
                if (isCacheValid(cacheKey)) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> cachedResponse = (Map<String, Object>) ops.get(cacheKey);
                    if (cachedResponse != null) {
                        return new ResponseEntity<>(cachedResponse, HttpStatus.OK);
                    }
                }

                // Get all journals
                List<Map<String, Object>> allJournals = getAllJournalsFromUsers();

                // Apply sorting
                sortJournals(allJournals, sortBy);

                // Calculate pagination
                int totalItems = allJournals.size();
                int totalPages = (int) Math.ceil((double) totalItems / size);
                int startIndex = page * size;

                if (startIndex >= totalItems) {
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }

                // Create a new ArrayList from the subList to avoid serialization issues
                List<Map<String, Object>> paginatedJournals = new ArrayList<>(
                        allJournals.subList(
                                startIndex,
                                Math.min(startIndex + size, totalItems)
                        )
                );

                Map<String, Object> response = new HashMap<>();
                response.put("journals", paginatedJournals);
                response.put("currentPage", page);
                response.put("totalItems", totalItems);
                response.put("totalPages", totalPages);

                // Cache the response with timestamp
                try {
                    ops.set(cacheKey, response, CACHE_DURATION);
                    ops.set(cacheKey + ":timestamp",
                            LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>("An error occurred while processing the request",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        @GetMapping("/trending")
        public ResponseEntity<?> getTrendingJournals() {
            try {
                String cacheKey = CACHE_KEY_PREFIX + "trending";
                ValueOperations<String, Object> ops = redisTemplate.opsForValue();

                // Check if cache is valid
                if (isCacheValid(cacheKey)) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> cachedTrending = (List<Map<String, Object>>) ops.get(cacheKey);
                    if (cachedTrending != null) {
                        return new ResponseEntity<>(cachedTrending, HttpStatus.OK);
                    }
                }

                List<Map<String, Object>> allJournals = getAllJournalsFromUsers();

                // Create new ArrayList for trending journals to avoid serialization issues
                List<Map<String, Object>> trendingJournals = new ArrayList<>(
                        allJournals.stream()
                                .filter(journal -> "HAPPY".equals(journal.get("sentiment")))
                                .sorted((a, b) -> ((String)b.get("date")).compareTo((String)a.get("date")))
                                .limit(5)
                                .collect(Collectors.toList())
                );

                if (trendingJournals.size() < 5) {
                    List<Map<String, Object>> additionalJournals = new ArrayList<>(
                            allJournals.stream()
                                    .filter(journal -> !"HAPPY".equals(journal.get("sentiment")))
                                    .sorted((a, b) -> ((String)b.get("date")).compareTo((String)a.get("date")))
                                    .limit(5 - trendingJournals.size())
                                    .collect(Collectors.toList())
                    );
                    trendingJournals.addAll(additionalJournals);
                }

                // Cache the trending journals with timestamp
                try {
                    ops.set(cacheKey, trendingJournals, CACHE_DURATION);
                    ops.set(cacheKey + ":timestamp",
                            LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return new ResponseEntity<>(trendingJournals, HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>("An error occurred while processing the request",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        private List<Map<String, Object>> getAllJournalsFromUsers() {
            List<UserEntity> allUsers = userService.getAllUser();
            return allUsers.stream()
                    .filter(user -> user.getJournalEntries() != null)
                    .flatMap(user -> user.getJournalEntries().stream()
                            .filter(entry -> entry != null && entry.getTitle() != null)
                            .map(entry -> {
                                Map<String, Object> map = new HashMap<>();
                                map.put("id", entry.getId().toString());
                                map.put("title", entry.getTitle());
                                map.put("content", entry.getContent());
                                map.put("date", entry.getDate() != null ?
                                        entry.getDate().format(DATE_FORMATTER) :
                                        LocalDateTime.now().format(DATE_FORMATTER));
                                map.put("sentiment", entry.getSentiment() != null ?
                                        entry.getSentiment().name() :
                                        Sentiment.HAPPY.name());
                                map.put("author", user.getUsername());
                                return map;
                            }))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        private void sortJournals(List<Map<String, Object>> journals, String sortBy) {
            if (sortBy != null) {
                switch (sortBy.toLowerCase()) {
                    case "date":
                        journals.sort((a, b) -> ((String)b.get("date")).compareTo((String)a.get("date")));
                        break;
                    case "sentiment":
                        Map<String, Integer> sentimentOrder = new HashMap<>();
                        sentimentOrder.put("HAPPY", 4);
                        sentimentOrder.put("SAD", 3);
                        sentimentOrder.put("ANXIOUS", 2);
                        sentimentOrder.put("ANGRY", 1);

                        journals.sort((a, b) -> {
                            String sentimentA = (String) a.get("sentiment");
                            String sentimentB = (String) b.get("sentiment");
                            return Integer.compare(
                                    sentimentOrder.getOrDefault(sentimentB, 0),
                                    sentimentOrder.getOrDefault(sentimentA, 0)
                            );
                        });
                        break;
                    case "title":
                        journals.sort((a, b) ->
                                ((String)a.get("title")).compareToIgnoreCase((String)b.get("title")));
                        break;
                }
            }
        }
    }