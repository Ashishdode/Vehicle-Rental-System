function showLogin() {
    document.getElementById("loginOverlay").style.display = "flex";
    document.querySelector(".front-page").style.display = "none";
}

function hideLogin() {
    document.getElementById("loginOverlay").style.display = "none";
    document.querySelector(".front-page").style.display = "block";
}

function toggleMenu() {
    document.querySelector(".navbar-menu").classList.toggle("active");
}

document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const errorDiv = document.getElementById("error");
    try {
        const response = await fetch("http://localhost:8080/api/users/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password }),
        });
        const data = await response.json();
        if (response.ok) {
            localStorage.setItem("userId", data.userId);
            localStorage.setItem("role", data.role);
            if (data.role === "ADMIN") {
                window.location.href = "admin.html";
            } else {
                window.location.href = "vehicles.html";
            }
        } else {
            errorDiv.textContent = data.message || "Invalid username or password";
            errorDiv.style.display = "block";
        }
    } catch (error) {
        errorDiv.textContent = "An error occurred. Please try again.";
        errorDiv.style.display = "block";
    }
});
