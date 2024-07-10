document.addEventListener('DOMContentLoaded', () => {
    const registerForm = document.getElementById('registerForm');

    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const username = document.getElementById('username').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const sexo = document.getElementById('sexo').value;
            const response = await fetch('http://localhost:8080/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, email, password, roles: 'USER', sexo })
            });
            if (response.ok) {
                alert('Registro exitoso');
                window.location.href = 'login.html';
            } else {
                alert('Error al registrarse');
            }
        });
    }
});
