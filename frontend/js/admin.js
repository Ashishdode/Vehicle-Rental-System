
// Function to toggle navbar menu on mobile
function toggleMenu() {
    document.querySelector(".navbar-menu").classList.toggle("active");
}

// Functions to show/hide sections
function showAddVehicleForm() {
    document.getElementById("addVehicleSection").style.display = "block";
    document.getElementById("updateVehicleSection").style.display = "none";
    document.getElementById("manageVehiclesSection").style.display = "none";
    document.getElementById("allBookingsSection").style.display = "none";
}

function showManageVehicles() {
    document.getElementById("addVehicleSection").style.display = "none";
    document.getElementById("updateVehicleSection").style.display = "none";
    document.getElementById("manageVehiclesSection").style.display = "block";
    document.getElementById("allBookingsSection").style.display = "none";
}

function showAllBookings() {
    document.getElementById("addVehicleSection").style.display = "none";
    document.getElementById("updateVehicleSection").style.display = "none";
    document.getElementById("manageVehiclesSection").style.display = "none";
    document.getElementById("allBookingsSection").style.display = "block";
}

// Cancel functions for forms
function cancelAdd() {
    document.getElementById("addVehicleForm").reset();
    showManageVehicles();
}

function cancelUpdate() {
    document.getElementById("updateVehicleForm").reset();
    showManageVehicles();
}

async function loadVehicles() {
    const userId = localStorage.getItem("userId");
    if (!userId) {
        window.location.href = "index.html";
        return;
    }
    try {
        const response = await fetch("http://localhost:8080/api/vehicles");
        if (!response.ok) throw new Error("Failed to fetch vehicles: " + response.statusText);
        const vehicles = await response.json();
        const vehicleList = document.getElementById("vehicleList");
        const noVehiclesMessage = document.getElementById("noVehiclesMessage");
        vehicleList.innerHTML = "";
        if (vehicles.length === 0) {
            noVehiclesMessage.style.display = "block";
        } else {
            noVehiclesMessage.style.display = "none";
            vehicles.forEach((vehicle) => {
                const card = document.createElement("div");
                card.className = "vehicle-card";
                if (!vehicle.availabilityStatus) card.classList.add("unavailable");
                const randomImage = getRandomVehicleImage(vehicle.type);
                card.innerHTML = `
                            <img src="${randomImage}" alt="${vehicle.name}" class="vehicle-image">
                            <h3>${vehicle.name}</h3>
                            <p>Type: ${vehicle.type}</p>
                            <p>Description: ${vehicle.description || "No description"}</p>
                            <p>Status: ${vehicle.availabilityStatus ? "Available" : "Unavailable"}</p>
                            <button class="btn" onclick="showUpdateForm(${vehicle.vehicleId}, '${vehicle.name}', '${vehicle.type}', '${vehicle.description || ""}', ${vehicle.availabilityStatus})">Update</button>
                            <button class="btn btn-danger" onclick="deleteVehicle(${vehicle.vehicleId})">Delete</button>
                        `;
                vehicleList.appendChild(card);
            });
        }
    } catch (error) {
        alert("Failed to load vehicles: " + error.message);
    }
}

async function loadBookings() {
    const userId = localStorage.getItem("userId");
    if (!userId) {
        window.location.href = "index.html";
        return;
    }
    try {
        const response = await fetch("http://localhost:8080/api/bookings/all", {
            headers: { "X-User-Id": userId },
        });
        if (!response.ok) throw new Error("Failed to fetch bookings: " + response.statusText);
        const bookings = await response.json();
        const bookingList = document.getElementById("bookingList");
        const noBookingsMessage = document.getElementById("noBookingsMessage");
        bookingList.innerHTML = "";
        if (bookings.length === 0) {
            noBookingsMessage.style.display = "block";
        } else {
            noBookingsMessage.style.display = "none";
            bookings.forEach((booking) => {
                const card = document.createElement("div");
                card.className = "booking-card";
                const randomImage = getRandomVehicleImage(booking.vehicle.type);
                card.innerHTML = `
                            <img src="${randomImage}" alt="${booking.vehicle.name}" class="vehicle-image">
                            <h3>${booking.vehicle.name}</h3>
                            <p>Type: ${booking.vehicle.type}</p>
                            <p>Description: ${booking.vehicle.description || "No description"}</p>
                            <p>Start Date: ${new Date(booking.startDate).toLocaleString()}</p>
                            <p>End Date: ${new Date(booking.endDate).toLocaleString()}</p>
                            <p>Status: ${booking.status}</p>
                            <p>User: ${booking.user.username}</p>
                        `;
                bookingList.appendChild(card);
            });
        }
    } catch (error) {
        alert("Failed to load bookings: " + error.message);
    }
}

// Function to show the Update Vehicle Form with pre-filled values
function showUpdateForm(vehicleId, name, type, description, availabilityStatus) {
    document.getElementById("addVehicleSection").style.display = "none";
    document.getElementById("updateVehicleSection").style.display = "block";
    document.getElementById("manageVehiclesSection").style.display = "none";
    document.getElementById("allBookingsSection").style.display = "none";

    // Pre-fill the Update Vehicle Form
    document.getElementById("updateVehicleId").value = vehicleId;
    document.getElementById("updateName").value = name;
    document.getElementById("updateType").value = type;
    document.getElementById("updateDescription").value = description;
    document.getElementById("updateAvailabilityStatus").checked = availabilityStatus;
}

// Delete vehicle
async function deleteVehicle(vehicleId) {
    const userId = localStorage.getItem("userId");
    if (!userId) {
        window.location.href = "index.html";
        return;
    }

    if (!confirm("Are you sure you want to cancel this booking?")) return;
    try {
        const response = await fetch(`http://localhost:8080/api/vehicles/${vehicleId}`, {
            method: "DELETE",
            headers: {
                "X-User-Id": userId,
            },
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || "Failed to delete vehicle");
        }

        alert("Vehicle deleted successfully!");
        loadVehicles();
        showManageVehicles();
    } catch (error) {
        console.error("Error deleting vehicle:", error.message);
        alert(error.message); 
    }
}

// Handle Add Vehicle Form submission
document.getElementById("addVehicleForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    const userId = localStorage.getItem("userId");
    if (!userId) {
        window.location.href = "index.html";
        return;
    }
    const name = document.getElementById("name").value;
    const type = document.getElementById("type").value;
    const description = document.getElementById("description").value;
    const availabilityStatus = document.getElementById("availabilityStatus").checked;

    const vehicleData = {
        name,
        type,
        description: description || null,
        availabilityStatus,
    };

    try {
        const response = await fetch("http://localhost:8080/api/vehicles", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-User-Id": userId,
            },
            body: JSON.stringify(vehicleData),
        });
        if (!response.ok) throw new Error("Failed to add vehicle: " + response.statusText);
        alert("Vehicle added successfully!");
        document.getElementById("addVehicleForm").reset();
        loadVehicles();
        showManageVehicles();
    } catch (error) {
        alert("Failed to add vehicle: " + error.message);
    }
});

// Handle Update Vehicle Form submission
document.getElementById("updateVehicleForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    const userId = localStorage.getItem("userId");
    if (!userId) {
        window.location.href = "index.html";
        return;
    }
    const vehicleId = document.getElementById("updateVehicleId").value;
    const name = document.getElementById("updateName").value;
    const type = document.getElementById("updateType").value;
    const description = document.getElementById("updateDescription").value;
    const availabilityStatus = document.getElementById("updateAvailabilityStatus").checked;

    const vehicleData = {
        vehicleId: parseInt(vehicleId),
        name,
        type,
        description: description || null,
        availabilityStatus,
    };

    try {
        const response = await fetch(`http://localhost:8080/api/vehicles/${vehicleId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "X-User-Id": userId,
            },
            body: JSON.stringify(vehicleData),
        });
        if (!response.ok) throw new Error("Failed to update vehicle: " + response.statusText);
        alert("Vehicle updated successfully!");
        document.getElementById("updateVehicleForm").reset();
        loadVehicles();
        showManageVehicles();
    } catch (error) {
        alert("Failed to update vehicle: " + error.message);
    }
});

// Show Manage Vehicles by default on page load
document.addEventListener("DOMContentLoaded", () => {
    showManageVehicles();
    loadVehicles();
    loadBookings();
});

