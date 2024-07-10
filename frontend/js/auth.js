document.addEventListener('DOMContentLoaded', () => {
    const registerForm = document.getElementById('registerForm');
    const loginForm = document.getElementById('loginForm');

    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const username = document.getElementById('username').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const roles = "USER"; // Asignar un rol por defecto
            const response = await fetch('http://localhost:8080/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, email, password, roles })
            });
            if (response.ok) {
                document.getElementById('registerMessage').innerText = 'Registro exitoso';
                window.location.href = 'login.html'; // Redirigir a la página de inicio de sesión
            } else {
                const errorText = await response.text();
                document.getElementById('registerMessage').innerText = 'Error al registrarse: ' + errorText;
            }
        });
    }

    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            const response = await fetch('http://localhost:8080/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password })
            });
            if (response.ok) {
                const data = await response.json();
                localStorage.setItem('token', data.token);
                localStorage.setItem('user', JSON.stringify({ username: data.username, id: data.userId, token: data.token }));
                document.getElementById('loginMessage').innerText = 'Inicio de sesión exitoso';
                window.location.href = 'inicio.html';
            } else {
                const errorText = await response.text();
                document.getElementById('loginMessage').innerText = 'Error al iniciar sesión: ' + errorText;
            }
        });
    }
});
