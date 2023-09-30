// Get DOM elements
const registerForm = document.getElementById('register-form');
const firstNameInput = document.getElementById('first-name-input');
const lastNameInput = document.getElementById('last-name-input');
const emailInput = document.getElementById('email-input');
const passwordInput = document.getElementById('password-input');
const registerButton = document.getElementById('register-button');
const backButton = document.getElementById('back-button');

// Add event listener for form submission
registerForm.addEventListener('submit', (e) => {
    e.preventDefault(); // Prevent form submission

    // Get input values
    const firstName = firstNameInput.value;
    const lastName = lastNameInput.value;
    const email = emailInput.value;
    const password = passwordInput.value;

    // Check if all fields are provided
    if (firstName && lastName && email && password) {
        console.log("Performing registration...");

        // Prepare data for registration
        const data = {
            firstName: firstName,
            lastName: lastName,
            email: email,
            password: password
        };

        // Send registration request to the server
        fetch('/api/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        })
            .then(result => {
                // Display success message
                alert("Registered successfully!");
            })
            .catch(error => {
                console.log('An error occurred during registration:', error);
            });
    }
});

// Add event listener for back button click
backButton.addEventListener('click', () => {
    // Redirect to index.html
    window.location.href = "../index.html";
});
