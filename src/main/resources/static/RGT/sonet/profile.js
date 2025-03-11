// Check if user is authenticated
function checkAuth() {
    const token = localStorage.getItem('jwt_token');
    if (!token) {
        window.location.href = '/RGT/sonet/login.html';
    }
}
function handleAuthResponse(response) {
    if (response.status === 401) {
        // Token expired or invalid → Clear token
        localStorage.removeItem("jwtToken");
        console.warn("Unauthorized: Token expired or invalid");
        return;
    }
    if (!response.ok) {
        console.error("Request failed with status:", response.status);
        return;
    }
    // Update token if available
    let newToken = response.headers.get("Authorization");
    if (newToken) {
        localStorage.setItem("jwtToken", newToken);
        console.log("JWT Token updated successfully.");
    }
}
document.addEventListener("DOMContentLoaded", async function() {
    try {
        // Fetch user data
        const userResponse = await fetch("/user", {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${localStorage.getItem('jwt_token')}`
            }
        });

        handleAuthResponse(userResponse);
        userData = await userResponse.json();

        document.getElementById("username").placeholder = userData.username;
        document.getElementById("email").placeholder = userData.email;
        document.getElementById("sentimentAnalysis_val").textContent = userData.sentimentAnalysis ? "true" : "false";


        // Fetch journal entries
        const journalResponse = await fetch("/journal", {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${localStorage.getItem('jwt_token')}`
            }
        });

        handleAuthResponse(journalResponse);
        const journalData = await journalResponse.json();
        const journalList = document.getElementById("journalList");
        journalList.innerHTML = "";

        journalData.forEach(entry => {
            const li = document.createElement("li");
            li.className = "journal-entry";
            li.innerHTML = `<strong>${entry.title}</strong><br>
                        <em>${new Date(entry.date).toLocaleString()}</em><br>
                        <p>${entry.content}</p>
                        <span>Sentiment: ${entry.sentiment}</span>`;
            journalList.appendChild(li);
        });
    } catch (error) {
        console.error("Error fetching user data:", error);
    }
});

// Handle profile update
document.getElementById('profileForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const updatedProfile = {
        username: document.getElementById('username').value,
        email: document.getElementById('email').value,
        sentimentAnalysis: document.getElementById('sentimentAnalysis').checked
    };

    const password = document.getElementById('password').value;
    if (password) {
        updatedProfile.password = password;
    }

    try {
        const response = await fetch('/user', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('jwt_token')}`
            },
            body: JSON.stringify(updatedProfile)
        });

        if (response.ok) {
            alert('Profile updated successfully');
            reload();
        } else {
            throw new Error('Failed to update profile');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Failed to update profile');
    }
});

// Handle account deletion
document.getElementById('deleteAccount').addEventListener('click', async () => {
    if (confirm('Are you sure you want to delete your account? This action cannot be undone.')) {
        try {
            const response = await fetch('/user/delete', {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('jwt_token')}`
                }
            });

            if (response.ok) {
                localStorage.removeItem('jwt_token');
                window.location.href = '/RGT/sonet/login.html';
            } else {
                throw new Error('Failed to delete account');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('Failed to delete account');
        }
    }
});

// Initialize page
checkAuth();