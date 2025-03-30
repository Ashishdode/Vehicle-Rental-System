function toggleMenu() {
    document.querySelector(".navbar-menu").classList.toggle("active");
}

document.getElementById("registerForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const username = document.getElementById("username").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const role = document.getElementById("role").value;
    const errorDiv = document.getElementById("error");
    try {
        const response = await fetch("http://localhost:8080/api/users/register", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, email, password, role }),
        });
        const data = await response.json();
        if (response.ok) {
            alert("Registration successful! Please login.");
            window.location.href = "index.html";
        } else {
            errorDiv.textContent = data.message || "Registration failed";
            errorDiv.style.display = "block";
        }
    } catch (error) {
        errorDiv.textContent = "An error occurred. Please try again.";
        errorDiv.style.display = "block";
    }
});
