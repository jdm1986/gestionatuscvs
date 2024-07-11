document.addEventListener('DOMContentLoaded', function() {
    const baseUrl = 'http://localhost:8080';
    const token = localStorage.getItem('token');

    async function fetchCurriculums() {
        const response = await fetch(`${baseUrl}/curriculums/usuario`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const results = await response.json();
            const resultsBody = document.getElementById('resultsBody');
            resultsBody.innerHTML = '';
            results.forEach(result => {
                resultsBody.innerHTML += `
                    <tr>
                        <td>${result.nombre}</td>
                        <td>${result.apellido}</td>
                        <td>${new Date(result.fechaInsercion).toLocaleDateString()}</td>
                        <td>${result.sexo}</td>
                        <td>
                            <button onclick="viewCv(${result.id})">Ver</button>
                            <button onclick="deleteCv(${result.id})">Eliminar</button>
                        </td>
                    </tr>
                `;
            });
        } else {
            alert('Error al cargar los currículums');
        }
    }

    // Llamada a fetchCurriculums al cargar el dashboard
    if (window.location.pathname.endsWith('dashboard.html')) {
        fetchCurriculums();
    }

    // Resto del código existente...
    document.getElementById('registerForm')?.addEventListener('submit', async function(event) {
        event.preventDefault();
        const username = document.getElementById('username').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const roles = "USER"; // O puedes dejarlo opcional en el formulario

        const response = await fetch(`${baseUrl}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, email, password, roles })
        });

        if (response.ok) {
            document.getElementById('message').classList.remove('hidden');
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 3000);
        } else {
            const errorData = await response.json();
            alert(`Error en el registro: ${errorData.error}`);
        }
    });

    document.getElementById('loginForm')?.addEventListener('submit', async function(event) {
        event.preventDefault();
        const username = document.getElementById('login').value;
        const password = document.getElementById('password').value;

        const response = await fetch(`${baseUrl}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem('token', data.token);
            localStorage.setItem('username', data.username);
            localStorage.setItem('userId', data.userId);
            window.location.href = 'dashboard.html';
        } else {
            alert('Error en el inicio de sesión');
        }
    });

    if (document.getElementById('username')) {
        document.getElementById('username').innerText = localStorage.getItem('username');
    }

    document.getElementById('logoutButton')?.addEventListener('click', function() {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        localStorage.removeItem('userId');
        window.location.href = 'index.html';
    });

    document.getElementById('searchButton')?.addEventListener('click', async function() {
        const searchInput = document.getElementById('searchInput').value;
        const response = await fetch(`${baseUrl}/curriculums/buscar/clave?clave=${searchInput}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const results = await response.json();
            const resultsBody = document.getElementById('resultsBody');
            resultsBody.innerHTML = '';
            results.forEach(result => {
                resultsBody.innerHTML += `
                    <tr>
                        <td>${result.nombre}</td>
                        <td>${result.apellido}</td>
                        <td>${new Date(result.fechaInsercion).toLocaleDateString()}</td>
                        <td>${result.sexo}</td>
                        <td>
                            <button onclick="viewCv(${result.id})">Ver</button>
                            <button onclick="deleteCv(${result.id})">Eliminar</button>
                        </td>
                    </tr>
                `;
            });
        } else {
            alert('Error en la búsqueda');
        }
    });

    document.getElementById('uploadForm')?.addEventListener('submit', async function(event) {
        event.preventDefault();
        const pdfFile = document.getElementById('pdfFile').files[0];
        const nombre = document.getElementById('nombre').value;
        const apellido = document.getElementById('apellido').value;
        const sexo = document.getElementById('sexo').value;
        const telefono = document.getElementById('telefono').value;
        const email = document.getElementById('email').value;
        const userId = localStorage.getItem('userId');

        const formData = new FormData();
        formData.append('file', pdfFile);
        formData.append('userId', userId);
        formData.append('nombre', nombre);
        formData.append('apellido', apellido);
        formData.append('sexo', sexo);
        formData.append('telefono', telefono);
        formData.append('email', email);

        const response = await fetch(`${baseUrl}/curriculums/upload`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        });

        if (response.ok) {
            document.getElementById('uploadMessage').classList.remove('hidden');
            setTimeout(() => {
                document.getElementById('uploadMessage').classList.add('hidden');
                fetchCurriculums(); // Actualiza la lista de currículums después de subir uno nuevo
            }, 3000);
        } else {
            alert('Error al subir el currículum');
        }
    });
});

async function viewCv(id) {
    const response = await fetch(`http://localhost:8080/curriculums/pdf/${id}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
    });

    if (response.ok) {
        const blob = await response.blob();
        const url = URL.createObjectURL(blob);
        window.open(url);
    } else {
        alert('Error al ver el currículum');
    }
}

async function deleteCv(id) {
    if (confirm('¿Estás seguro?')) {
        const response = await fetch(`http://localhost:8080/curriculums/${id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (response.ok) {
            location.reload();
        } else {
            alert('Error al eliminar el currículum');
        }
    }
}
