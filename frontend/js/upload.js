document.addEventListener("DOMContentLoaded", function () {
    const user = JSON.parse(localStorage.getItem("user"));
    if (!user || !user.token || !user.id) {
        window.location.href = "login.html";
        return;
    }

    document.getElementById("uploadForm").addEventListener("submit", function (e) {
        e.preventDefault();
        const formData = new FormData(this);
        formData.append("userId", user.id);  // Asegurarse de que userId se envía en el formulario
        fetch("http://localhost:8080/curriculums/upload", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + user.token
            },
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            alert("Curriculum subido con éxito");
        })
        .catch(error => {
            console.error("Error al subir el curriculum:", error);
        });
    });
});
