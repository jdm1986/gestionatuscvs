// Evento que se activa cuando el DOM ha sido cargado completamente
document.addEventListener('DOMContentLoaded', function() {

    // Establece la URL base dependiendo de si se está en local o en producción
    const baseUrl = window.location.hostname.includes('localhost')
        ? 'http://localhost:8080' // Si es localhost, usar esta URL
        : 'https://gestionatuscv.es'; // Si no, usar la URL de producción

    // Obtiene el token de autenticación almacenado en localStorage
    const token = localStorage.getItem('token');

    // Obtiene el ID de usuario almacenado
    const userId = localStorage.getItem('userId');
    const linkKey = `generatedLink_${userId}`; // Clave para almacenar el enlace generado
    const expirationKey = `linkExpiration_${userId}`; // Clave para la expiración del enlace
    const expirationTimeInSeconds = 5 * 24 * 60 * 60; // Tiempo de expiración en segundos (5 días)
    const errorMessage = document.getElementById('errorMessage'); // Mensaje de error
    const logo = document.getElementById('logo'); // Logo para el evento de clic

    // Manejador de clics en el logo para cerrar sesión
    if (logo) {
        logo.addEventListener('click', function() {
            if (token) {
                const confirmLogout = confirm("Se va a cerrar la sesión, ¿estás seguro?"); // Confirmación de cierre de sesión
                if (confirmLogout) {
                    localStorage.removeItem('token'); // Elimina el token de localStorage
                    localStorage.removeItem('username'); // Elimina el nombre de usuario
                    localStorage.removeItem('userId'); // Elimina el ID de usuario
                    window.location.href = 'index.html'; // Redirige a la página de inicio
                }
            } else {
                window.location.href = 'index.html'; // Si no hay token, redirige a la página de inicio
            }
        });
    }

    // Botones de inicio de sesión y registro
    const loginButton = document.getElementById('loginButton'); // Botón de inicio de sesión
    const registerButton = document.getElementById('registerButton'); // Botón de registro

    // Evento para redirigir a la página de login
    if (loginButton) {
        loginButton.addEventListener('click', function() {
            window.location.href = 'login.html';
        });
    }

    // Evento para redirigir a la página de registro
    if (registerButton) {
        registerButton.addEventListener('click', function() {
            window.location.href = 'register.html';
        });
    }

    // Botón para generar el enlace de subida de currículum
    const generateLinkButton = document.getElementById('generateLinkButton');
    const generatedLink = document.getElementById('generatedLink'); // Enlace generado
    const copyButton = document.getElementById('copyButton'); // Botón para copiar el enlace
    const linkContainer = document.querySelector(".link-container"); // Contenedor del enlace generado

    // Función para formatear la diferencia de tiempo
    function formatTimeDifference(timeDifference) {
        const days = Math.floor(timeDifference / (24 * 60 * 60)); // Calcula días
        const hours = Math.floor((timeDifference % (24 * 60 * 60)) / (60 * 60)); // Calcula horas
        const minutes = Math.floor((timeDifference % (60 * 60)) / 60); // Calcula minutos
        return `${days} días, ${hours} horas, y ${minutes} minutos`; // Devuelve la diferencia formateada
    }

    // Verifica si ya existe un enlace generado
    function checkExistingLink() {
        const existingLink = localStorage.getItem(linkKey); // Obtiene el enlace existente
        const linkExpiration = localStorage.getItem(expirationKey); // Obtiene la fecha de expiración

        if (existingLink && linkExpiration) { // Si existe un enlace y una expiración
            const currentTime = Math.floor(Date.now() / 1000); // Tiempo actual en segundos
            const timeDifference = linkExpiration - currentTime; // Calcula la diferencia de tiempo

            if (timeDifference > 0) { // Si aún no ha expirado
                generatedLink.textContent = `${existingLink}`; // Muestra el enlace
                generatedLink.classList.remove('hidden'); // Lo hace visible
                linkContainer.classList.add('show'); // Muestra el contenedor
                copyButton.classList.add('show'); // Muestra el botón de copiar

                const timeRemaining = formatTimeDifference(timeDifference); // Calcula el tiempo restante
                const warningMessage = `Ya tienes un enlace generado que caducará en ${timeRemaining}.`; // Mensaje de advertencia
                alert(warningMessage); // Muestra el mensaje de advertencia
                return true; // Retorna true si hay un enlace válido
            } else {
                localStorage.removeItem(linkKey); // Elimina el enlace expirado
                localStorage.removeItem(expirationKey); // Elimina la expiración
            }
        }
        return false; // Retorna false si no hay enlace válido
    }

    // Evento para generar un nuevo enlace
        if (generateLinkButton) {
            generateLinkButton.addEventListener('click', function() {
                if (checkExistingLink()) { // Verifica si ya existe un enlace
                    return;
                }

                // Realiza la petición para generar un nuevo enlace
                fetch(`${baseUrl}/curriculums/generate-upload-link`, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}` // Envía el token en la cabecera
                    }
                })
                .then(response => response.text()) // Obtiene el texto del enlace
                .then(link => {
                    const currentTime = Math.floor(Date.now() / 1000); // Tiempo actual en segundos
                    const expirationTime = currentTime + expirationTimeInSeconds; // Calcula el tiempo de expiración

                    localStorage.setItem(linkKey, link); // Almacena el enlace en localStorage
                    localStorage.setItem(expirationKey, expirationTime); // Almacena la fecha de expiración

                    generatedLink.textContent = `${link}`; // Muestra el enlace generado
                    generatedLink.classList.remove('hidden'); // Lo hace visible
                    linkContainer.classList.add('show'); // Muestra el contenedor
                    copyButton.classList.add('show'); // Muestra el botón de copiar
                })
                .catch(error => {
                    alert('Error al generar el enlace.'); // Muestra un error si algo falla
                });
            });

            // Evento para copiar el enlace al portapapeles
            copyButton.addEventListener('click', function() {
                navigator.clipboard.writeText(generatedLink.textContent).then(function() {
                    alert("Enlace copiado al portapapeles"); // Muestra éxito
                }, function() {
                    alert("No se pudo copiar el enlace"); // Muestra error
                });
            });
        }

        // Función para obtener los currículums del usuario
        async function fetchCurriculums() {
            const response = await fetch(`${baseUrl}/curriculums/usuario`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}` // Envía el token en la cabecera
                }
            });

            if (response.ok) {
                const results = await response.json(); // Convierte la respuesta a JSON
                results.sort((a, b) => new Date(b.fechaInsercion) - new Date(a.fechaInsercion)); // Ordena los currículums por fecha
                const resultsBody = document.getElementById('resultsBody'); // Cuerpo de los resultados
                if (resultsBody) {
                    resultsBody.innerHTML = ''; // Limpia el contenido previo
                    results.forEach(result => { // Itera sobre cada currículum
                        const tr = document.createElement('tr'); // Crea una nueva fila

                        // Inserta los datos del currículum en la fila, incluyendo el ID con data-id-curriculum
                        tr.innerHTML = `
                            <td data-id-curriculum="${result.id}" data-label="Nombre">${result.nombre}</td>
                            <td data-label="Apellido">${result.apellido}</td>
                            <td data-label="Fecha">${new Date(result.fechaInsercion).toLocaleDateString()}</td>
                            <td data-label="Sexo">${result.sexo}</td>
                            <td data-label="Teléfono">${result.telefono}</td>
                            <td data-label="Email">${result.email}</td>
                            <td data-label="Departamento">
                                 <select class="departamento-dropdown">
                                        <option value="${result.departamento}" selected>${result.departamento}</option>
                                 </select>
                            </td>
                        `;

                        const actionsTd = document.createElement('td'); // Columna para las acciones
                        actionsTd.setAttribute('data-label', 'Acciones'); // Etiqueta para móviles
                        actionsTd.classList.add('action-buttons'); // Añade clase de botones de acción

                        // **Nuevo** Botón para editar el currículum
                        const editButton = document.createElement('button');
                        editButton.textContent = 'Editar'; // Texto del botón
                        editButton.addEventListener('click', () => {
                        // Redirige a cambiar-datos.html pasando el ID del currículum como parámetro en la URL
                        window.location.href = `cambiar-datos.html?id=${result.id}`;
                        });
                        actionsTd.appendChild(editButton); // Añade el botón a la columna

                        // Botón para ver el currículum
                        const viewButton = document.createElement('button');
                        viewButton.textContent = 'Ver'; // Texto del botón
                        viewButton.addEventListener('click', () => viewCv(result.id)); // Evento para ver currículum
                        actionsTd.appendChild(viewButton); // Añade el botón a la columna

                        // Botón para eliminar el currículum
                        const deleteButton = document.createElement('button');
                        deleteButton.textContent = 'Eliminar'; // Texto del botón
                        deleteButton.addEventListener('click', () => deleteCv(result.id)); // Evento para eliminar currículum
                        actionsTd.appendChild(deleteButton); // Añade el botón a la columna

                        // Botón para añadir notas
                        const notesButton = document.createElement('button');
                        notesButton.textContent = 'Notas'; // Texto del botón
                        notesButton.addEventListener('click', () => openNoteModal(result.id)); // Evento para añadir notas
                        actionsTd.appendChild(notesButton); // Añade el botón a la columna

                        tr.appendChild(actionsTd); // Añade la columna a la fila
                        resultsBody.appendChild(tr); // Añade la fila al cuerpo de la tabla
                    });
                    // Llama a la función para llenar los dropdowns de departamentos después de cargar los currículums
                    llenarDropdowns();
                }
            } else {
                alert('Error al cargar los currículums'); // Muestra un error si algo falla
            }
        }

        // Función para obtener departamentos dinámicamente desde el backend
        async function cargarDepartamentos() {
            if (!userId) {
                    console.error('userId no está definido.');
                    return [];
                }
            const response = await fetch(`${baseUrl}/departamentos?userId=${userId}`, { // Ruta a tu API de departamentos
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}` // Envía el token en la cabecera si es necesario
                }
            });

            if (!response.ok) {
                    console.error(`Error al cargar departamentos: ${response.statusText}`);
                    return [];
                }

            const departamentos = await response.json();
                if (!Array.isArray(departamentos)) {
                    console.error('La respuesta no es un array de departamentos.');
                    return [];
                }

                return departamentos;
        }

        // Función para llenar el dropdown con departamentos
        async function llenarDropdowns() {
            const departamentos = await cargarDepartamentos();

            // Asumiendo que los resultados del currículum están en una tabla en #resultsBody
            const rows = document.querySelectorAll('#resultsBody tr');

            if (!departamentos || !rows.length) {
                    console.error('No se encontraron filas en la tabla de currículums.');
                    return;
                }

            rows.forEach(row => {
                    const dropdown = row.querySelector('.departamento-dropdown');
                    const selectedValue = dropdown.value; // Guardo el valor seleccionado previamente

                    dropdown.innerHTML = ''; // Limpia opciones anteriores

                    // Añadir opción de cada departamento
                    departamentos.forEach(depto => {
                        const option = document.createElement('option');
                        option.value = depto.nombre; // Aquí uso el nombre del departamento, ajústalo si usas ID en vez de nombre
                        option.textContent = depto.nombre;
                        if (depto.nombre === selectedValue) {
                            option.selected = true; // Mantengo el departamento seleccionado
                        }
                        dropdown.appendChild(option);
                    });

                    // Evento para actualizar el departamento en la base de datos
                    dropdown.addEventListener('change', async function() {
                        const nuevoDepartamento = this.value;
                        const idCurriculum = row.querySelector('td[data-id-curriculum]').getAttribute('data-id-curriculum');

                        const response = await fetch(`${baseUrl}/curriculums/${idCurriculum}/departamento`, {
                            method: 'PUT',
                            headers: {
                                'Content-Type': 'application/json',
                                'Authorization': `Bearer ${token}`
                            },
                            body: JSON.stringify({ departamento: nuevoDepartamento })
                        });

                        if (response.ok) {
                            alert('Departamento actualizado');
                        } else {
                            alert('Error al actualizar el departamento');
                        }
                });
            });
        }

        // Función para ver un currículum en formato PDF
        async function viewCv(id) {
            const response = await fetch(`${baseUrl}/curriculums/pdf/${id}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}` // Envía el token en la cabecera
                }
            });

            if (response.ok) {
                const blob = await response.blob(); // Convierte la respuesta en un archivo binario
                const url = URL.createObjectURL(blob); // Crea una URL para el archivo
                window.open(url); // Abre el PDF en una nueva ventana
            } else {
                alert('Error al ver el currículum'); // Muestra un error si algo falla
            }
        }

        // Función para eliminar un currículum
        async function deleteCv(id) {
            if (confirm('¿Estás seguro?')) { // Confirmación de eliminación
                const response = await fetch(`${baseUrl}/curriculums/${id}`, {
                    method: 'DELETE',
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('token')}` // Envía el token en la cabecera
                    }
                });

                if (response.ok) {
                    fetchCurriculums(); // Actualiza la lista de currículums
                } else {
                    alert('Error al eliminar el currículum'); // Muestra un error si algo falla
                }
            }
        }

        // Función para abrir el modal de notas
        async function openNoteModal(curriculumId) {
            const modal = document.getElementById('noteModal'); // Modal de notas
            const noteText = document.getElementById('noteText'); // Área de texto de la nota
            const saveNoteButton = document.getElementById('saveNoteButton'); // Botón para guardar la nota
            const notesList = document.getElementById('notesList'); // Lista de notas

            notesList.innerHTML = ''; // Limpia la lista de notas

            // Obtiene las notas del currículum
            const response = await fetch(`${baseUrl}/curriculums/${curriculumId}/notas`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}` // Envía el token en la cabecera
                }
            });

            if (response.ok) {
                const notes = await response.json(); // Convierte la respuesta a JSON
                notes.sort((a, b) => new Date(b.fechaCreacion) - new Date(a.fechaCreacion)); // Ordena las notas por fecha

                // Itera sobre las notas y las añade a la lista
                notes.forEach(note => {
                    const noteElement = document.createElement('div'); // Crea un contenedor para cada nota
                    noteElement.classList.add('note'); // Añade la clase de estilo de nota
                    noteElement.innerHTML = `
                        <p>${note.contenido}</p> <!-- Muestra el contenido de la nota -->
                        <span>${new Date(note.fechaCreacion).toLocaleString()}</span> <!-- Muestra la fecha de creación -->
                    `;
                    notesList.appendChild(noteElement); // Añade la nota a la lista
                });
            } else {
                alert('Error al cargar las notas'); // Muestra un error si algo falla
            }

            modal.style.display = 'block'; // Muestra el modal
            saveNoteButton.onclick = async function() { // Evento para guardar la nota
                const content = noteText.value.trim(); // Obtiene el contenido de la nota
                if (content) {
                    const response = await fetch(`${baseUrl}/curriculums/${curriculumId}/notas`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json', // Indica que el contenido es JSON
                            'Authorization': `Bearer ${token}` // Envía el token en la cabecera
                        },
                        body: JSON.stringify({ contenido: content }) // Envía la nota en el cuerpo de la solicitud
                    });

                    if (response.ok) {
                        noteText.value = ''; // Limpia el campo de texto
                        modal.style.display = 'none'; // Oculta el modal
                        fetchCurriculums(); // Actualiza la lista de currículums
                    } else {
                        alert('Error al guardar la nota'); // Muestra un error si algo falla
                    }
                } else {
                    alert('La nota no puede estar vacía'); // Muestra un mensaje si el campo está vacío
                }
            };

            const closeModal = document.getElementsByClassName('close')[0]; // Botón para cerrar el modal
            closeModal.onclick = function() {
                modal.style.display = 'none'; // Oculta el modal al hacer clic en el botón de cerrar
            };
            window.onclick = function(event) {
                if (event.target == modal) { // Si se hace clic fuera del modal, lo oculta
                    modal.style.display = 'none';
                }
            };
        }

        // Si estamos en el dashboard, obtenemos los currículums
        if (window.location.pathname.endsWith('dashboard.html')) {
            fetchCurriculums();

            const username = localStorage.getItem('username'); // Obtiene el nombre de usuario
            if (username === 'admin') { // Si el usuario es admin, muestra el panel de administración
                const adminPanel = document.createElement('div'); // Crea un panel de administración
                adminPanel.style.marginTop = '20px';
                adminPanel.style.textAlign = 'center';

                const viewLogsButton = document.createElement('button'); // Botón para ver registros de usuarios
                viewLogsButton.textContent = 'Ver registros de usuarios'; // Texto del botón
                viewLogsButton.style.backgroundColor = '#e573fe'; // Estilo del botón
                viewLogsButton.style.color = 'white';
                viewLogsButton.style.border = 'none';
                viewLogsButton.style.padding = '10px 20px';
                viewLogsButton.style.borderRadius = '4px';
                viewLogsButton.style.cursor = 'pointer';
                viewLogsButton.style.marginTop = '20px';

                // Evento para ver los registros de usuarios
                viewLogsButton.addEventListener('click', async function() {
                    const response = await fetch(`${baseUrl}/admin/registro-usuarios`, {
                        method: 'GET',
                        headers: {
                            'Authorization': `Bearer ${token}` // Envía el token en la cabecera
                        }
                    });

                    if (response.ok) {
                        const logs = await response.json(); // Convierte la respuesta a JSON
                        let formattedLogs = logs.map(log => { // Formatea los registros
                            let logTime = new Date(log.timestamp); // Convierte la fecha
                            logTime.setMinutes(logTime.getMinutes() - 30); // Ajusta la zona horaria

                            return `Usuario: ${log.username} | Fecha de Registro: ${logTime.toLocaleString()}`; // Formatea el log
                        }).join('\n');

                        alert(formattedLogs); // Muestra los registros
                    } else {
                        alert('Error al cargar los registros'); // Muestra un error si algo falla
                    }
                });

                const container = document.querySelector('.container'); // Contenedor principal
                container.insertBefore(adminPanel, container.firstChild); // Inserta el panel de admin al inicio
                adminPanel.appendChild(viewLogsButton); // Añade el botón al panel
            }
        }

        // Manejo del formulario de registro
        document.getElementById('registerForm')?.addEventListener('submit', async function(event) {
            event.preventDefault(); // Evita el comportamiento predeterminado del formulario
            const username = document.getElementById('username').value; // Obtiene el nombre de usuario
            const email = document.getElementById('email').value; // Obtiene el email
            const password = document.getElementById('password').value; // Obtiene la contraseña
            const roles = "USER"; // Asigna el rol de usuario por defecto

            // Realiza la solicitud de registro
            const response = await fetch(`${baseUrl}/auth/register`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json' // El cuerpo de la solicitud es JSON
                },
                body: JSON.stringify({ username, email, password, roles }) // Envía los datos del formulario
            });

            const messageContainer = document.getElementById('message'); // Contenedor del mensaje

            if (response.ok) {
                messageContainer.classList.remove('hidden'); // Muestra el mensaje de éxito
                messageContainer.style.color = '#E573FE'; // Color rosado
                messageContainer.textContent = 'Registro exitoso. Redirigiendo a inicio de sesión...'; // Mensaje de éxito
                setTimeout(() => {
                    window.location.href = 'login.html'; // Redirige al login
                }, 3000); // Espera 3 segundos antes de redirigir
            } else {
                const errorText = await response.text(); // Obtiene el mensaje de error
                messageContainer.classList.remove('hidden'); // Muestra el mensaje de error
                messageContainer.style.color = '#FF6F61'; // Color rojo
                messageContainer.innerHTML = errorText.replace('<a href=\'/reset-password\'>', '<a href=\'forgot-password.html\' style="color: white;">'); // Ajusta el enlace de recuperación de contraseña

                if (errorText.includes("Redirigiendo a la recuperación de contraseña")) {
                    setTimeout(() => {
                        window.location.href = 'forgot-password.html'; // Redirige a la página de recuperación de contraseña
                    }, 3000); // Espera 3 segundos antes de redirigir
                }
            }
        });

        // Manejo del formulario de inicio de sesión
        document.getElementById('loginForm')?.addEventListener('submit', async function(event) {
            event.preventDefault(); // Evita el comportamiento predeterminado del formulario
            const username = document.getElementById('login').value; // Obtiene el nombre de usuario
            const password = document.getElementById('password').value; // Obtiene la contraseña

            // Realiza la solicitud de inicio de sesión
            const response = await fetch(`${baseUrl}/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json' // El cuerpo de la solicitud es JSON
                },
                body: JSON.stringify({ username, password }) // Envía los datos del formulario
            });

            const messageContainer = document.getElementById('loginMessage'); // Contenedor del mensaje
            messageContainer.style.display = 'block'; // Muestra el mensaje

            if (response.ok) {
                const data = await response.json(); // Convierte la respuesta a JSON
                localStorage.setItem('token', data.token); // Almacena el token en localStorage
                localStorage.setItem('username', data.username); // Almacena el nombre de usuario
                localStorage.setItem('userId', data.userId); // Almacena el ID de usuario
                window.location.href = 'dashboard.html'; // Redirige al dashboard
            } else {
                const errorData = await response.text(); // Obtiene el mensaje de error
                if (errorData.includes('El usuario no existe')) {
                    messageContainer.innerHTML = 'El usuario no existe.'; // Muestra un mensaje de error
                } else if (errorData.includes('Contraseña incorrecta')) {
                    messageContainer.innerHTML = 'Contraseña incorrecta. Inténtalo de nuevo. <a href="forgot-password.html" style="color: white;">¿Olvidaste tu contraseña?</a>'; // Mensaje de contraseña incorrecta
                } else if (errorData.includes('La cuenta está bloqueada')) {
                    messageContainer.innerHTML = 'Has alcanzado el límite de intentos. Redirigiendo a la recuperación de contraseña...'; // Mensaje de cuenta bloqueada
                    setTimeout(() => {
                        window.location.href = 'forgot-password.html'; // Redirige a la página de recuperación de contraseña
                    }, 3000); // Espera 3 segundos antes de redirigir
                } else {
                    messageContainer.innerHTML = 'Error en el inicio de sesión'; // Mensaje de error genérico
                }
                messageContainer.style.color = '#FF6F61'; // Color rojo
                messageContainer.style.marginTop = '20px'; // Espaciado superior
                messageContainer.style.textAlign = 'center'; // Alineación centrada
            }
        });

        // Establece el nombre de usuario en la interfaz
        if (document.getElementById('username')) {
            document.getElementById('username').innerText = localStorage.getItem('username');
        }

        // Evento para cerrar sesión
        document.getElementById('logoutButton')?.addEventListener('click', function() {
            localStorage.removeItem('token'); // Elimina el token de localStorage
            localStorage.removeItem('username'); // Elimina el nombre de usuario
            localStorage.removeItem('userId'); // Elimina el ID de usuario
            window.location.href = 'index.html'; // Redirige a la página de inicio
        });

        // Botón de búsqueda de currículums
        document.getElementById('searchButton')?.addEventListener('click', async function() {
            performSearch(); // Llama a la función de búsqueda
        });

        // Busca currículums al presionar Enter en el input de búsqueda
        document.getElementById('searchInput')?.addEventListener('keypress', function(event) {
            if (event.key === 'Enter') {
                event.preventDefault(); // Evita el comportamiento por defecto del Enter
                performSearch(); // Realiza la búsqueda
            }
        });

        // Botón para limpiar el campo de búsqueda y recargar currículums
        document.getElementById('clearButton')?.addEventListener('click', function() {
            document.getElementById('searchInput').value = ''; // Limpia el input de búsqueda
            fetchCurriculums(); // Recarga la lista de currículums
        });

        // Formulario de recuperación de contraseña
        document.getElementById('forgotPasswordForm')?.addEventListener('submit', async function(event) {
            event.preventDefault(); // Evita el comportamiento predeterminado del formulario
            const email = document.getElementById('email').value; // Obtiene el email del usuario
            const messageContainer = document.getElementById('message'); // Contenedor del mensaje

            document.querySelector('button[type="submit"]').disabled = true; // Deshabilita el botón de envío

            // Realiza la solicitud de recuperación de contraseña
            const response = await fetch(`${baseUrl}/auth/forgot-password`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json' // El cuerpo de la solicitud es JSON
                },
                body: JSON.stringify({ email }) // Envía el email en el cuerpo de la solicitud
            });

            if (response.ok) {
                const data = await response.json(); // Convierte la respuesta a JSON
                messageContainer.classList.remove('hidden'); // Muestra el mensaje de éxito
                messageContainer.style.color = '#E573FE'; // Color rosado
                messageContainer.textContent = data.message; // Muestra el mensaje de éxito
                setTimeout(() => {
                    window.location.href = 'login.html'; // Redirige a la página de login
                }, 3000); // Espera 3 segundos antes de redirigir
            } else {
                const errorData = await response.json(); // Convierte la respuesta a JSON
                messageContainer.classList.remove('hidden'); // Muestra el mensaje de error
                messageContainer.style.color = '#FF6F61'; // Color rojo
                messageContainer.textContent = errorData.message; // Muestra el mensaje de error
            }

            document.querySelector('button[type="submit"]').disabled = false; // Habilita el botón de envío
        });

        // Parámetros de la URL
        const params = new URLSearchParams(window.location.search);
        const userIdParam = params.get('user'); // Obtiene el parámetro "user" de la URL

        // Manejo del formulario de subida de currículum
        document.getElementById('uploadForm')?.addEventListener('submit', function(event) {
            event.preventDefault(); // Evita el comportamiento predeterminado del formulario
            const uploadId = document.getElementById('uploadId').value; // Obtiene el ID de subida
            const password = document.getElementById('password').value; // Obtiene la contraseña
            const pdfFile = document.getElementById('pdfFile').files[0]; // Obtiene el archivo PDF

            if (!pdfFile || pdfFile.type !== "application/pdf") { // Verifica que el archivo sea PDF
                errorMessage.style.display = 'block'; // Muestra mensaje de error si no es PDF
                return;
            } else {
                errorMessage.style.display = 'none'; // Oculta el mensaje de error si es PDF
            }

            const formData = new FormData(); // Crea un nuevo objeto FormData
            formData.append('uploadId', uploadId); // Añade el ID de subida
            formData.append('password', password); // Añade la contraseña
            formData.append('file', pdfFile); // Añade el archivo PDF
            formData.append('nombre', document.getElementById('nombre').value); // Añade el nombre
            formData.append('apellido', document.getElementById('apellido').value); // Añade el apellido
            formData.append('sexo', document.getElementById('sexo').value); // Añade el sexo
            formData.append('telefono', document.getElementById('telefono').value); // Añade el teléfono
            formData.append('email', document.getElementById('email').value); // Añade el email

            // Realiza la solicitud de subida del currículum
            fetch('/curriculums/upload_with_password', {
                method: 'POST', // Método POST
                body: formData // Envía los datos del formulario
            })
            .then(response => response.text()) // Convierte la respuesta a texto
            .then(text => {
                if (text === "Currículum subido exitosamente") {
                    document.getElementById('uploadMessage').classList.remove('hidden'); // Muestra mensaje de éxito
                    setTimeout(() => {
                        document.getElementById('uploadMessage').classList.add('hidden'); // Oculta el mensaje
                        location.href = 'success_page.html'; // Redirige a la página de éxito
                    }, 3000); // Espera 3 segundos antes de redirigir
                } else {
                    alert(text); // Muestra mensaje de error
                }
            })
            .catch(error => {
                console.error('Error al subir el currículum:', error); // Muestra el error en la consola
                alert('Error al subir el currículum.'); // Muestra un mensaje de error
            });
        });

        // Manejo de la edición de currículum en cambiar-datos.html
        document.addEventListener('DOMContentLoaded', async function () {

        const token = localStorage.getItem('token'); // Asegúrate de que el token esté disponible
            if (!token) {
                alert('No se ha encontrado el token de autenticación. Por favor, inicia sesión nuevamente.');
                window.location.href = 'login.html'; // Redirige al login si no hay token
                return;
            }

            const params = new URLSearchParams(window.location.search);
            const id = params.get('id');

            if (id) {
                // Obtener los datos del currículum por su ID
                const response = await fetch(`${baseUrl}/curriculums/${id}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });

                if (response.ok) {
                    const curriculum = await response.json();

                    // Rellenar los campos del formulario
                    document.getElementById('nombre').value = curriculum.nombre;
                    document.getElementById('apellido').value = curriculum.apellido;
                    document.getElementById('telefono').value = curriculum.telefono;
                    document.getElementById('sexo').value = curriculum.sexo;
                } else {
                    alert('Error al cargar los datos del currículum.');
                }
            }

            // Manejo del formulario para guardar los cambios
            document.getElementById('editForm').addEventListener('submit', async function(event) {
                event.preventDefault();

                const nombre = document.getElementById('nombre').value;
                const apellido = document.getElementById('apellido').value;
                const telefono = document.getElementById('telefono').value;
                const sexo = document.getElementById('sexo').value;
                const departamento = document.getElementById('departamento').value;

                const response = await fetch(`${baseUrl}/curriculums/update/${id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify({ nombre, apellido, telefono, sexo })
                });

                if (response.ok) {
                    const successMessage = document.getElementById('successMessage');
                    successMessage.style.display = 'block'; // Mostrar el mensaje de éxito
                    setTimeout(() => {
                        window.location.href = 'dashboard.html'; // Redirigir al dashboard después de 2 segundos
                    }, 2000);
                } else {
                    alert('Error al guardar los datos');
                }
            });
        });


        // Función para realizar la búsqueda de currículums
        async function performSearch() {
            const searchInput = document.getElementById('searchInput').value; // Obtiene el valor de búsqueda
            const response = await fetch(`${baseUrl}/curriculums/buscar/clave?clave=${searchInput}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}` // Envía el token en la cabecera
                }
            });

            if (response.ok) {
                const results = await response.json(); // Convierte la respuesta a JSON
                const resultsBody = document.getElementById('resultsBody'); // Cuerpo de la tabla de resultados
                if (resultsBody) {
                    resultsBody.innerHTML = ''; // Limpia los resultados previos
                    results.forEach(result => { // Itera sobre los resultados
                        const tr = document.createElement('tr'); // Crea una nueva fila

                        // Inserta los datos del currículum en la fila
                        tr.innerHTML = `
                            <td data-label="Nombre">${result.nombre}</td>
                            <td data-label="Apellido">${result.apellido}</td>
                            <td data-label="Fecha">${new Date(result.fechaInsercion).toLocaleDateString()}</td>
                            <td data-label="Sexo">${result.sexo}</td>
                            <td data-label="Teléfono">${result.telefono}</td>
                            <td data-label="Email">${result.email}</td>
                            <td data-label="Departamento">
                                <select class="departamento-dropdown">
                                    <option value="${result.departamento}" selected>${result.departamento}</option>
                                </select>
                            </td>
                        `;

                        const actionsTd = document.createElement('td'); // Columna para las acciones
                        actionsTd.setAttribute('data-label', 'Acciones'); // Etiqueta para móviles
                        actionsTd.classList.add('action-buttons'); // Añade clase de botones de acción

                        // Botón para ver el currículum
                        const viewButton = document.createElement('button');
                        viewButton.textContent = 'Ver'; // Texto del botón
                        viewButton.addEventListener('click', () => viewCv(result.id)); // Evento para ver currículum
                        actionsTd.appendChild(viewButton); // Añade el botón a la columna

                        // Botón para eliminar el currículum
                        const deleteButton = document.createElement('button');
                        deleteButton.textContent = 'Eliminar'; // Texto del botón
                        deleteButton.addEventListener('click', () => deleteCv(result.id)); // Evento para eliminar currículum
                        actionsTd.appendChild(deleteButton); // Añade el botón a la columna

                        // Botón para añadir notas
                        const notesButton = document.createElement('button');
                        notesButton.textContent = 'Notas'; // Texto del botón
                        notesButton.addEventListener('click', () => openNoteModal(result.id)); // Evento para añadir notas
                        actionsTd.appendChild(notesButton); // Añade el botón a la columna

                        tr.appendChild(actionsTd); // Añade la columna a la fila
                        resultsBody.appendChild(tr); // Añade la fila al cuerpo de la tabla
                    });

                    // Llama a la función para llenar los dropdowns de departamentos después de cargar los currículums
                    llenarDropdowns();
                }
            } else {
                alert('Error en la búsqueda'); // Muestra un mensaje de error si algo falla
            }
        }
    });

