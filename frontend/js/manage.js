document.addEventListener("DOMContentLoaded", function () {
    const user = JSON.parse(localStorage.getItem("user"));
    if (!user || !user.token || !user.id) {
        window.location.href = "login.html";
        return;
    }

    function loadCurriculums() {
        fetch(`http://localhost:8080/curriculums/usuario/${user.id}`, {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + user.token
            }
        })
        .then(response => response.json())
        .then(data => {
            const curriculumTable = document.getElementById("curriculumTable");
            curriculumTable.innerHTML = "";
            data.forEach(curriculum => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${curriculum.nombre}</td>
                    <td>${curriculum.apellido}</td>
                    <td>${curriculum.fecha_insercion || 'N/A'}</td>
                    <td>${curriculum.sexo}</td>
                    <td><img src="path/to/thumbnails/${curriculum.pdf_path}" class="img-thumbnail" onclick="window.open('path/to/pdf/${curriculum.pdf_path}', '_blank')"></td>
                    <td><button class="delete-btn" data-id="${curriculum.id}">Eliminar</button></td>
                `;
                curriculumTable.appendChild(row);
            });
        })
        .catch(error => {
            console.error("Error al cargar los curriculums:", error);
        });
    }

    loadCurriculums();

    document.getElementById("search").addEventListener("input", function (e) {
        const searchTerm = e.target.value.toLowerCase();
        const rows = document.querySelectorAll("#curriculumTable tr");
        rows.forEach(row => {
            const text = row.innerText.toLowerCase();
            row.style.display = text.includes(searchTerm) ? "" : "none";
        });
    });

    document.getElementById("curriculumTable").addEventListener("click", function(e) {
        if (e.target.classList.contains("delete-btn")) {
            const curriculumId = e.target.getAttribute("data-id");
            if (confirm("¿Estás seguro de que deseas eliminar este currículum?")) {
                fetch(`http://localhost:8080/curriculums/${curriculumId}`, {
                    method: "DELETE",
                    headers: {
                        "Authorization": "Bearer " + user.token
                    }
                })
                .then(response => {
                    if (response.ok) {
                        loadCurriculums();
                    } else {
                        alert("Error al eliminar el currículum");
                    }
                })
                .catch(error => {
                    console.error("Error al eliminar el currículum:", error);
                });
            }
        }
    });
});
