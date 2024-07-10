document.addEventListener("DOMContentLoaded", function () {
    const user = JSON.parse(localStorage.getItem("user"));
    if (!user || !user.token || !user.id) {
        window.location.href = "login.html";
        return;
    }

    document.getElementById("username").textContent = user.username;

    document.getElementById("logout").addEventListener("click", function () {
        localStorage.removeItem("user");
        window.location.href = "index.html";
    });
});
