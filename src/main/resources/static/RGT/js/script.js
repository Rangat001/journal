// Base URL for your Spring Boot backend
const API_BASE_URL = 'http://localhost:8080';


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

// Handle signup and login form submission
document.addEventListener('DOMContentLoaded', function() {
    const signupForm = document.getElementById('signupForm');
    if (signupForm) {
        signupForm.addEventListener('submit', handleSignup);
    }

    const loginForm = document.getElementById('loginForm');
        if (loginForm) {
            loginForm.addEventListener('submit', handleLogin);
        }
});

async function handleSignup(event) {
    event.preventDefault();

    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch('/RGT/signup', {  // matches your @PostMapping("signup")
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

// Handle login form submission
async function handleLogin(event) {
    event.preventDefault();
    clearMessages();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch('/public/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });

        // Get the raw response text
        const token = await response.text();
        console.log('Raw token:', token);

        if (response.ok) {
            // Store the JWT token
            localStorage.setItem('jwt_token', token);

            // Store user info with current UTC time
            const currentUtcTime = new Date().toISOString()
                .replace('T', ' ')
                .substring(0, 19);

            localStorage.setItem('user', JSON.stringify({
                username: username,
                loginTime: currentUtcTime
            }));

            showSuccess_login('Login successful! Redirecting...');
            setTimeout(() => {
                window.location.href = 'http://localhost:8080/RGT/home.html';
            }, 1000);
        } else {
            const errorMessage = token || 'Login failed. Please check your credentials.';
            showError_login(errorMessage);
        }
    } catch (error) {
        console.error('Login error:', error);
        showError('Network error. Please try again later.');
    }
}
function showSuccess_login(message) {
    clearMessages();
    const successDiv = document.createElement('div');
    successDiv.className = 'success-message';
    successDiv.textContent = message;
    document.getElementById('loginForm').appendChild(successDiv);
}

function showError_login(message) {
    clearMessages();
    const errorDiv = document.createElement('div');
    errorDiv.className = 'error-message';
    errorDiv.textContent = message;
    document.getElementById('loginForm').appendChild(errorDiv);
}

// Utility function to make authenticated requests
async function makeAuthenticatedRequest(url, options = {}) {
    const token = localStorage.getItem('jwt_token');
    if (!token) {
        throw new Error('No authentication token found');
    }

    // Add authorization header
    const headers = {
        ...options.headers,
        'Authorization': `Bearer ${token}`
    };

    const response = await fetch(url, {
        ...options,
        headers
    });

    if (response.status === 401) {
        // Token expired or invalid
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('user');
        window.location.href = '/login.html';
        throw new Error('Session expired. Please login again.');
    }

    return response;
}



// Function to check if user is authenticated
function isAuthenticated() {
    return localStorage.getItem('jwt_token') !== null;
}

// Add these functions to handle protected pages
function protectPage() {
    if (!isAuthenticated()) {
        window.location.href = '/login.html';
    }
}



