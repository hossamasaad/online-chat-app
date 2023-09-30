// Get DOM elements
const loginForm = document.getElementById('login-form');
const emailInput = document.getElementById('email-input');
const passwordInput = document.getElementById('password-input');
const loginButton = document.getElementById('login-button');
const backButton = document.getElementById('back-button');

// Add event listener for form submission
loginForm.addEventListener('submit', (e) => {
    e.preventDefault(); // Prevent form submission

    // Get email and password values
    const email = emailInput.value;
    const password = passwordInput.value;

    // Check if email and password are provided
    if (email && password) {
        console.log('Performing authentication...');

        // Prepare data for authentication
        const data = {
            email: email,
            password: password
        };

        // Send authentication request to the server
        fetch('/api/auth/authenticate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        })
            .then(response => response.json())
            .then(result => {
                // Store JWT token and user ID in local storage
                localStorage.setItem('jwtToken', result.accessToken);
                localStorage.setItem('id', result.id);

                // Redirect to chat.html
                window.location.href = "chat.html";
            })
            .catch(error => {
                console.log('An error occurred during authentication:', error);
            });
    }
});

// Add event listener for back button click
backButton.addEventListener('click', () => {
    // Redirect to index.html
    window.location.href = "../index.html";
});
