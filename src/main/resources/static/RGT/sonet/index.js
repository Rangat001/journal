// Check if user is authenticated
function checkAuth() {
    const token = localStorage.getItem('jwt_token');
    if (!token) {
        window.location.href = '/RGT/sonet/login.html';
    }
}

// Fetch greeting and daily quote
async function fetchGreetingAndQuote() {
    try {
        const response = await fetch('/user/index', {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('jwt_token')}`
            }
        });

        if (response.ok) {
            const data = await response.text();
            const [username, quote] = data.split('\n');

            document.getElementById('greeting').textContent = `Welcome, ${username}!`;
            document.getElementById('dailyQuote').textContent = quote;
        } else {
            window.location.href = "/RGT/sonet/login.html";
        }
    } catch (error) {
        console.error('Error:', error);
        document.getElementById('dailyQuote').textContent = 'Failed to load daily quote';
    }
}

// Handle logout
document.getElementById('logoutBtn').addEventListener('click', () => {
    localStorage.removeItem('jwt_token');
    window.location.href = "/RGT/sonet/login.html";
});

function formatDateTime(date) {
    // Format: YYYY-MM-DDTHH:mm:ss
    return date.toISOString().slice(0, 19); // This will give format like "2025-03-11T17:11:44"
}
function formatDisplayDate(dateString) {
    return new Date(dateString).toLocaleString();
}
async function fetchJournalEntries() {
    try {
        const response = await fetch('/journal', {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('jwt_token')}`
            }
        });

        if (response.ok) {
            const entries = await response.json();
            displayJournalEntries(entries);
        } else if (response.status === 204) {
            document.getElementById('entriesList').innerHTML = '<p>No journal entries yet. Create your first one!</p>';
        }
    } catch (error) {
        console.error('Error:', error);
        showError('Failed to load journal entries');
    }
}
// Display journal entries
function displayJournalEntries(entries) {
    const entriesList = document.getElementById('entriesList');
    entriesList.innerHTML = '';

    entries.forEach(entry => {
        console.log(entry)    ;
        const entryElement = document.createElement('div');
        entryElement.className = 'journal-entry';
        entryElement.innerHTML = `
            <div class="entry-header">
                <div class="entry-title-group">
                    <h3>${entry.title}</h3>
                    ${entry.sentiment ? `<span class="sentiment-indicator sentiment-${entry.sentiment}">${entry.sentiment}</span>` : ''}
                </div>
                <div class="entry-date">${formatDisplayDate(entry.date)}</div>
            </div>
            <div class="journal-entry-content">${entry.content || ''}</div>
            <div class="journal-actions">

                <button onclick="editEntry('${entry.id}')" class="edit-button">Edit</button>
                <button onclick="deleteEntry('${entry.id}')" class="delete-button">Delete</button>
            </div>
        `;
        entriesList.appendChild(entryElement);
    });
}
document.getElementById('journalForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const journalData = {
        title: document.getElementById('title').value,
        content: document.getElementById('content').value,
        date: formatDateTime(new Date()), // This will now produce the correct format for LocalDateTime
        sentiment: document.getElementById('sentiment').value || null
    };

    try {
        const response = await fetch('/journal', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('jwt_token')}`
            },
            body: JSON.stringify(journalData)
        });

        if (response.ok) {
            const createdEntry = await response.json();
            document.getElementById('journalForm').reset();
            await fetchJournalEntries();
            showSuccess('Journal entry created successfully!');
        } else {
            throw new Error('Failed to create journal entry');
        }
    } catch (error) {
        console.error('Error:', error);
        showError('Failed to create journal entry');
    }
});

// Update entry function
async function updateEntry(id, data) {
    // Ensure the date is in the correct format for updating
    const updateData = {
        ...data,
        date: data.date.includes('T') ? data.date : formatDateTime(new Date(data.date))
    };

    try {
        const response = await fetch(`/journal/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('jwt_token')}`
            },
            body: JSON.stringify(updateData)
        });

        if (response.ok) {
            await fetchJournalEntries();
            showSuccess('Journal entry updated successfully!');
        }
    } catch (error) {
        console.error('Error:', error);
        showError('Failed to update journal entry');
    }
}


// Update the display function to handle the ISO format
function formatDisplayDate(dateString) {
    // Convert ISO date string to user-friendly format
    const date = new Date(dateString);
    return date.toLocaleString();
}

// Edit journal entry
async function editEntry(id) {
    try {
        const response = await fetch(`/journal/${id}`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('jwt_token')}`
            }
        });

        if (response.ok) {
            const entry = await response.json();
            document.getElementById('title').value = entry.title;
            document.getElementById('content').value = entry.content || '';
            document.getElementById('sentiment').value = entry.sentiment || '';

            // Change form submission to update mode
            const form = document.getElementById('journalForm');
            const submitBtn = form.querySelector('button[type="submit"]');
            submitBtn.textContent = 'Update Entry';

            form.onsubmit = async (e) => {
                e.preventDefault();
                await updateEntry(id, {
                    title: document.getElementById('title').value,
                    content: document.getElementById('content').value,
                    date: entry.date, // Maintain original date
                    sentiment: document.getElementById('sentiment').value || null
                });

                // Reset form to creation mode
                submitBtn.textContent = 'Save Entry';
                form.reset();
                form.onsubmit = null;
            };
        }
    } catch (error) {
        console.error('Error:', error);
        showError('Failed to load journal entry');
    }
}

// Delete journal entry
async function deleteEntry(id) {
    if (confirm('Are you sure you want to delete this journal entry?')) {
        try {
            const response = await fetch(`/journal/${id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('jwt_token')}`
                }
            });

            if (response.ok) {
                await fetchJournalEntries();
                showSuccess('Journal entry deleted successfully!');
            }
        } catch (error) {
            console.error('Error:', error);
            showError('Failed to delete journal entry');
        }
    }
}

// Show success message
function showSuccess(message) {
    const alert = document.createElement('div');
    alert.className = 'success-message';
    alert.textContent = message;
    document.querySelector('.journal-section').insertBefore(alert, document.getElementById('journalForm'));
    setTimeout(() => alert.remove(), 3000);
}

// Show error message
function showError(message) {
    const alert = document.createElement('div');
    alert.className = 'error-message';
    alert.textContent = message;
    document.querySelector('.journal-section').insertBefore(alert, document.getElementById('journalForm'));
    setTimeout(() => alert.remove(), 3000);
}

// Initialize page
checkAuth();
fetchGreetingAndQuote();
fetchJournalEntries();