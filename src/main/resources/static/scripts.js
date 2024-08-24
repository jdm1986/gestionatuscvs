document.addEventListener('DOMContentLoaded', function() {
    const baseUrl = window.location.hostname.includes('localhost')
        ? 'http://localhost:8080'
        : 'https://gestionatuscv.es';

    const token = localStorage.getItem('token');
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

    async function fetchCurriculums() {
        const response = await fetch(`${baseUrl}/curriculums/usuario`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const results = await response.json();
            // Ordenar los resultados por fecha de inserción del más reciente al menos reciente
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

            notes.sort((a, b) => new Date(b.fechaCreacion) - new Date(a.fechaCreacion)); //Orden de  la nota más reciente a la menos.

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
                    fetchCurriculums(); // Refresh curriculums to show new note
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

            // Verificar si el usuario es admin para mostrar el botón
            const username = localStorage.getItem('username');
            if (username === 'admin') { // Cambia 'admin' al nombre de usuario del administrador en tu base de datos
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
                        const logs = await response.text();
                        alert(logs);
                    } else {
                        alert('Error al cargar los registros');
                    }
                });

                adminPanel.appendChild(viewLogsButton);
                document.body.appendChild(adminPanel);
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
            messageContainer.style.color = '#E573FE'; // Color púrpura de éxito
            messageContainer.textContent = 'Registro exitoso. Redirigiendo a inicio de sesión...';
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 3000);
        } else {
            const errorText = await response.text();
            messageContainer.classList.remove('hidden');
            messageContainer.style.color = '#FF6F61'; // Color rojo para el error
            messageContainer.innerHTML = errorText.replace('<a href=\'/reset-password\'>', '<a href=\'forgot-password.html\' style="color: white;">');
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

        const response = await fetch(`${baseUrl}/auth/forgot-password`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email })
        });

        if (response.ok) {
            document.getElementById('message').classList.remove('hidden');
        } else {
            alert('Error al enviar la solicitud. Inténtalo de nuevo.');
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
                fetchCurriculums();
                document.getElementById('uploadForm').reset();
            }, 3000);
        } else {
            alert('Error al subir el currículum');
        }
    });

    // Código para capturar foto desde la cámara
    const captureButton = document.createElement('button');
    captureButton.textContent = 'Capturar Foto';
    captureButton.addEventListener('click', () => {
        const video = document.createElement('video');
        const canvas = document.createElement('canvas');
        const context = canvas.getContext('2d');
        const constraints = {
            video: true
        };

        navigator.mediaDevices.getUserMedia(constraints)
            .then((stream) => {
                video.srcObject = stream;
                video.play();

                const capturePhotoButton = document.createElement('button');
                capturePhotoButton.textContent = 'Tomar Foto';
                capturePhotoButton.addEventListener('click', () => {
                    context.drawImage(video, 0, 0, canvas.width, canvas.height);
                    canvas.toBlob(async (blob) => {
                        const nombre = document.getElementById('nombre').value;
                        const apellido = document.getElementById('apellido').value;
                        const sexo = document.getElementById('sexo').value;
                        const telefono = document.getElementById('telefono').value;
                        const email = document.getElementById('email').value;
                        const userId = localStorage.getItem('userId');

                        const formData = new FormData();
                        formData.append('file', blob, 'foto.png');
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
                                fetchCurriculums();
                                document.getElementById('uploadForm').reset();
                            }, 3000);
                        } else {
                            alert('Error al subir el currículum');
                        }
                    }, 'image/png');
                });

                document.body.appendChild(video);
                document.body.appendChild(capturePhotoButton);
            })
            .catch((error) => {
                console.error('Error al acceder a la cámara', error);
                alert('Error al acceder a la cámara');
            });
    });

    // Agrega el botón de captura de foto solo si existe el formulario de carga
    const uploadForm = document.getElementById('uploadForm');
    if (uploadForm) {
        uploadForm.appendChild(captureButton);
    }

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
