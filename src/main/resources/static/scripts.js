document.addEventListener('DOMContentLoaded', function() {
    const baseUrl = window.location.hostname.includes('localhost')
        ? 'http://localhost:8080'
        : 'https://gestionatuscv.es';

    const token = localStorage.getItem('token');
    const errorMessage = document.getElementById('errorMessage');
    const logo = document.getElementById('logo');

    if (logo) {
        logo.addEventListener('click', function() {
            if (token) {
                const confirmLogout = confirm("Se va a cerrar la sesión, ¿estás seguro?");
                if (confirmLogout) {
                    localStorage.removeItem('token');
                    localStorage.removeItem('username');
                    localStorage.removeItem('userId');
                    window.location.href = 'index.html';
                }
            } else {
                window.location.href = 'index.html';
            }
        });
    }

    const loginButton = document.getElementById('loginButton');
    const registerButton = document.getElementById('registerButton');

    if (loginButton) {
        loginButton.addEventListener('click', function() {
            window.location.href = 'login.html';
        });
    }

    if (registerButton) {
        registerButton.addEventListener('click', function() {
            window.location.href = 'register.html';
        });
    }

    const generateLinkButton = document.getElementById('generateLinkButton');
    const generatedLink = document.getElementById('generatedLink');

    if (generateLinkButton) {
        generateLinkButton.addEventListener('click', function() {
            fetch(`${baseUrl}/curriculums/generate-upload-link`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            })
            .then(response => response.text())
            .then(link => {
                generatedLink.textContent = `Enlace generado: ${link}`;
                generatedLink.classList.remove('hidden');
            })
            .catch(error => {
                alert('Error al generar el enlace.');
            });
        });
    }


    async function fetchCurriculums() {
        const response = await fetch(`${baseUrl}/curriculums/usuario`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const results = await response.json();
            results.sort((a, b) => new Date(b.fechaInsercion) - new Date(a.fechaInsercion));
            const resultsBody = document.getElementById('resultsBody');
            if (resultsBody) {
                resultsBody.innerHTML = '';
                results.forEach(result => {
                    const tr = document.createElement('tr');

                    tr.innerHTML = `
                        <td data-label="Nombre">${result.nombre}</td>
                        <td data-label="Apellido">${result.apellido}</td>
                        <td data-label="Fecha">${new Date(result.fechaInsercion).toLocaleDateString()}</td>
                        <td data-label="Sexo">${result.sexo}</td>
                        <td data-label="Teléfono">${result.telefono}</td>
                    `;

                    const actionsTd = document.createElement('td');
                    actionsTd.setAttribute('data-label', 'Acciones');
                    actionsTd.classList.add('action-buttons');

                    const viewButton = document.createElement('button');
                    viewButton.textContent = 'Ver';
                    viewButton.addEventListener('click', () => viewCv(result.id));
                    actionsTd.appendChild(viewButton);

                    const deleteButton = document.createElement('button');
                    deleteButton.textContent = 'Eliminar';
                    deleteButton.addEventListener('click', () => deleteCv(result.id));
                    actionsTd.appendChild(deleteButton);

                    const notesButton = document.createElement('button');
                    notesButton.textContent = 'Notas';
                    notesButton.addEventListener('click', () => openNoteModal(result.id));
                    actionsTd.appendChild(notesButton);

                    tr.appendChild(actionsTd);
                    resultsBody.appendChild(tr);
                });
            }
        } else {
            alert('Error al cargar los currículums');
        }
    }

    async function viewCv(id) {
        const response = await fetch(`${baseUrl}/curriculums/pdf/${id}`, {
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
            const response = await fetch(`${baseUrl}/curriculums/${id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });

            if (response.ok) {
                fetchCurriculums();
            } else {
                alert('Error al eliminar el currículum');
            }
        }
    }

    async function openNoteModal(curriculumId) {
        const modal = document.getElementById('noteModal');
        const noteText = document.getElementById('noteText');
        const saveNoteButton = document.getElementById('saveNoteButton');
        const notesList = document.getElementById('notesList');

        notesList.innerHTML = ''; // Clear previous notes

        // Fetch existing notes
        const response = await fetch(`${baseUrl}/curriculums/${curriculumId}/notas`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const notes = await response.json();

            notes.sort((a, b) => new Date(b.fechaCreacion) - new Date(a.fechaCreacion));

            notes.forEach(note => {
                const noteElement = document.createElement('div');
                noteElement.classList.add('note');
                noteElement.innerHTML = `
                    <p>${note.contenido}</p>
                    <span>${new Date(note.fechaCreacion).toLocaleString()}</span>
                `;
                notesList.appendChild(noteElement);
            });
        } else {
            alert('Error al cargar las notas');
        }

        modal.style.display = 'block';
        saveNoteButton.onclick = async function() {
            const content = noteText.value.trim();
            if (content) {
                const response = await fetch(`${baseUrl}/curriculums/${curriculumId}/notas`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify({ contenido: content })
                });

                if (response.ok) {
                    noteText.value = '';
                    modal.style.display = 'none';
                    fetchCurriculums();
                } else {
                    alert('Error al guardar la nota');
                }
            } else {
                alert('La nota no puede estar vacía');
            }
        };

        const closeModal = document.getElementsByClassName('close')[0];
        closeModal.onclick = function() {
            modal.style.display = 'none';
        };
        window.onclick = function(event) {
            if (event.target == modal) {
                modal.style.display = 'none';
            }
        };
    }

    if (window.location.pathname.endsWith('dashboard.html')) {
        fetchCurriculums();

        const username = localStorage.getItem('username');
        if (username === 'admin') {
            const adminPanel = document.createElement('div');
            adminPanel.style.marginTop = '20px';
            adminPanel.style.textAlign = 'center';

            const viewLogsButton = document.createElement('button');
            viewLogsButton.textContent = 'Ver registros de usuarios';
            viewLogsButton.style.backgroundColor = '#e573fe';
            viewLogsButton.style.color = 'white';
            viewLogsButton.style.border = 'none';
            viewLogsButton.style.padding = '10px 20px';
            viewLogsButton.style.borderRadius = '4px';
            viewLogsButton.style.cursor = 'pointer';
            viewLogsButton.style.marginTop = '20px';

            viewLogsButton.addEventListener('click', async function() {
                const response = await fetch(`${baseUrl}/admin/registro-usuarios`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });

                if (response.ok) {
                        const logs = await response.json();
                        let formattedLogs = logs.map(log =>
                            `Usuario: ${log.username} | Fecha: ${new Date(log.timestamp).toLocaleString()}`
                        ).join('\n');

                        alert(formattedLogs);
                    } else {
                        alert('Error al cargar los registros');
                    }
                });

            const container = document.querySelector('.container');
            container.insertBefore(adminPanel, container.firstChild);
            adminPanel.appendChild(viewLogsButton);
        }

    }

    document.getElementById('registerForm')?.addEventListener('submit', async function(event) {
        event.preventDefault();
        const username = document.getElementById('username').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const roles = "USER";

        const response = await fetch(`${baseUrl}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, email, password, roles })
        });

        const messageContainer = document.getElementById('message');

        if (response.ok) {
            messageContainer.classList.remove('hidden');
            messageContainer.style.color = '#E573FE';
            messageContainer.textContent = 'Registro exitoso. Redirigiendo a inicio de sesión...';
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 3000);
        } else {
            const errorText = await response.text();
            messageContainer.classList.remove('hidden');
            messageContainer.style.color = '#FF6F61';
            messageContainer.innerHTML = errorText.replace('<a href=\'/reset-password\'>', '<a href=\'forgot-password.html\' style="color: white;">');

            if (errorText.includes("Redirigiendo a la recuperación de contraseña")) {
                setTimeout(() => {
                    window.location.href = 'forgot-password.html';
                }, 3000);
            }
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

        const messageContainer = document.getElementById('loginMessage');
        messageContainer.style.display = 'block';

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem('token', data.token);
            localStorage.setItem('username', data.username);
            localStorage.setItem('userId', data.userId);
            window.location.href = 'dashboard.html';
        } else {
            const errorData = await response.text();
            if (errorData.includes('El usuario no existe')) {
                messageContainer.innerHTML = 'El usuario no existe.';
            } else if (errorData.includes('Contraseña incorrecta')) {
                messageContainer.innerHTML = 'Contraseña incorrecta. Inténtalo de nuevo. <a href="forgot-password.html" style="color: white;">¿Olvidaste tu contraseña?</a>';
            } else if (errorData.includes('La cuenta está bloqueada')) {
                messageContainer.innerHTML = 'Has alcanzado el límite de intentos. Redirigiendo a la recuperación de contraseña...';
                setTimeout(() => {
                    window.location.href = 'forgot-password.html';
                }, 3000);
            } else {
                messageContainer.innerHTML = 'Error en el inicio de sesión';
            }
            messageContainer.style.color = '#FF6F61';
            messageContainer.style.marginTop = '20px';
            messageContainer.style.textAlign = 'center';
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
        performSearch();
    });

    document.getElementById('searchInput')?.addEventListener('keypress', function(event) {
        if (event.key === 'Enter') {
            event.preventDefault();
            performSearch();
        }
    });

    document.getElementById('clearButton')?.addEventListener('click', function() {
        document.getElementById('searchInput').value = '';
        fetchCurriculums();
    });

    document.getElementById('forgotPasswordForm')?.addEventListener('submit', async function(event) {
        event.preventDefault();
        const email = document.getElementById('email').value;
        const messageContainer = document.getElementById('message');

        document.querySelector('button[type="submit"]').disabled = true;

        const response = await fetch(`${baseUrl}/auth/forgot-password`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email })
        });

        if (response.ok) {
            const data = await response.json();
            messageContainer.classList.remove('hidden');
            messageContainer.style.color = '#E573FE';
            messageContainer.textContent = data.message;
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 3000);
        } else {
            const errorData = await response.json();
            messageContainer.classList.remove('hidden');
            messageContainer.style.color = '#FF6F61';
            messageContainer.textContent = errorData.message;
        }

        document.querySelector('button[type="submit"]').disabled = false;
    });

    const params = new URLSearchParams(window.location.search);
    const userId = params.get('user');

    document.getElementById('uploadForm')?.addEventListener('submit', function(event) {
        event.preventDefault();
        const uploadId = document.getElementById('uploadId').value;
        const password = document.getElementById('password').value;
        const pdfFile = document.getElementById('pdfFile').files[0];

        if (!pdfFile || pdfFile.type !== "application/pdf") {
            errorMessage.style.display = 'block';
            return;
        } else {
            errorMessage.style.display = 'none';
        }

        const formData = new FormData();
            formData.append('uploadId', uploadId);
            formData.append('password', password);
            formData.append('file', pdfFile);
            formData.append('nombre', document.getElementById('nombre').value);
            formData.append('apellido', document.getElementById('apellido').value);
            formData.append('sexo', document.getElementById('sexo').value);
            formData.append('telefono', document.getElementById('telefono').value);
            formData.append('email', document.getElementById('email').value);

            fetch('/curriculums/upload_with_password', {
                method: 'POST',
                body: formData
            })
            .then(response => response.text())
            .then(text => {
                if (text === "Currículum subido exitosamente") {
                    document.getElementById('uploadMessage').classList.remove('hidden');
                    setTimeout(() => {
                        document.getElementById('uploadMessage').classList.add('hidden');
                        location.href = 'success_page.html'; // Redirige a una página de éxito
                    }, 3000);
                } else {
                    alert(text);
                }
            })
            .catch(error => {
                console.error('Error al subir el currículum:', error);
                alert('Error al subir el currículum.');
        });
    });

    async function performSearch() {
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
            if (resultsBody) {
                resultsBody.innerHTML = '';
                results.forEach(result => {
                    const tr = document.createElement('tr');

                    tr.innerHTML = `
                        <td data-label="Nombre">${result.nombre}</td>
                        <td data-label="Apellido">${result.apellido}</td>
                        <td data-label="Fecha">${new Date(result.fechaInsercion).toLocaleDateString()}</td>
                        <td data-label="Sexo">${result.sexo}</td>
                        <td data-label="Teléfono">${result.telefono}</td>
                    `;

                    const actionsTd = document.createElement('td');
                    actionsTd.setAttribute('data-label', 'Acciones');
                    actionsTd.classList.add('action-buttons');

                    const viewButton = document.createElement('button');
                    viewButton.textContent = 'Ver';
                    viewButton.addEventListener('click', () => viewCv(result.id));
                    actionsTd.appendChild(viewButton);

                    const deleteButton = document.createElement('button');
                    deleteButton.textContent = 'Eliminar';
                    deleteButton.addEventListener('click', () => deleteCv(result.id));
                    actionsTd.appendChild(deleteButton);

                    const notesButton = document.createElement('button');
                    notesButton.textContent = 'Notas';
                    notesButton.addEventListener('click', () => openNoteModal(result.id));
                    actionsTd.appendChild(notesButton);

                    tr.appendChild(actionsTd);
                    resultsBody.appendChild(tr);
                });
            }
        } else {
            alert('Error en la búsqueda');
        }
    }
});
