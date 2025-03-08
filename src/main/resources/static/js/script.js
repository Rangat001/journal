// Base URL for your Spring Boot backend
const API_BASE_URL = 'http://localhost:8080';



// Handle signup form submission
document.addEventListener('DOMContentLoaded', function() {
    const signupForm = document.getElementById('signupForm');
    if (signupForm) {
        signupForm.addEventListener('submit', handleSignup);
    }
});

async function handleSignup(event) {
    event.preventDefault();

    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch('/public/signup', {  // matches your @PostMapping("signup")
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: username,
                email: email,
                password: password
            })
        });

        console.log('Response status:', response.status);

        if (response.ok) {
            const data = await response.json();
            console.log('Signup successful:', data);
            showSuccess('Account created successfully! Redirecting to login...');
//            setTimeout(() => {
//                window.location.href = '/login.html';
//            }, 2000);
        } else {
            const errorData = await response.text();
            console.error('Signup failed:', errorData);
            showError('Failed to create account. Please try again.');
        }
    } catch (error) {
        console.error('Network error during signup:', error);
        showError('Network error. Please try again later.');
    }
}

function showSuccess(message) {
    clearMessages();
    const successDiv = document.createElement('div');
    successDiv.className = 'success-message';
    successDiv.textContent = message;
    document.getElementById('signupForm').appendChild(successDiv);
}

function showError(message) {
    clearMessages();
    const errorDiv = document.createElement('div');
    errorDiv.className = 'error-message';
    errorDiv.textContent = message;
    document.getElementById('signupForm').appendChild(errorDiv);
}

function clearMessages() {
    const messages = document.querySelectorAll('.error-message, .success-message');
    messages.forEach(message => message.remove());
}
// Handle login form submission
async function handleLogin(event) {
    event.preventDefault();

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch(`${API_BASE_URL}/public/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                email,
                password
            })
        });

        const data = await response.json();

        if (response.ok) {
            localStorage.setItem('token', data.token);
            window.location.href = '/dashboard.html';
        } else {
            showError(data.message || 'Invalid email or password');
        }
    } catch (error) {
        console.error('Network error during login:', error);
        showError('An error occurred. Please try again later.');
    }
}

// Initialize forms when the DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Initialize signup form
    const signupForm = document.getElementById('signupForm');
    if (signupForm) {
        signupForm.addEventListener('submit', handleSignup);
    }

    // Initialize login form
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
});