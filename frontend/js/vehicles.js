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
            vehicles.forEach(async (vehicle) => {
                const card = document.createElement("div");
                card.className = "vehicle-card";
                if (!vehicle.availabilityStatus) card.classList.add("unavailable");
                let availabilityText = vehicle.availabilityStatus ? "Available" : "Unavailable";
                if (!vehicle.availabilityStatus) {
                    const bookingResponse = await fetch(`http://localhost:8080/api/bookings/vehicle/${vehicle.vehicleId}`);
                    if (bookingResponse.ok) {
                        const bookings = await bookingResponse.json();
                        const activeBooking = bookings.find(b => new Date(b.startDate) <= new Date() && new Date(b.endDate) >= new Date());
                        if (activeBooking) {
                            availabilityText = `Unavailable until ${new Date(activeBooking.endDate).toLocaleString()}`;
                        }
                    }
                }
                const randomImage = getRandomVehicleImage(vehicle.type); // Use vehicle type to get image
                card.innerHTML = `
                            <img src="${randomImage}" alt="${vehicle.name}" class="vehicle-image">
                            <h3>${vehicle.name}</h3>
                            <p>Type: ${vehicle.type}</p>
                            <p>Status: ${availabilityText}</p>
                            ${vehicle.availabilityStatus ? `<button class="btn" onclick="showBookingForm(${vehicle.vehicleId})">Book Now</button>` : ""}
                        `;
                vehicleList.appendChild(card);
            });
        }
    } catch (error) {
        console.error("Error loading vehicles:", error);
        alert("Failed to load vehicles: " + error.message);
    }
}

function showBookingForm(vehicleId) {
    document.getElementById("vehicleId").value = vehicleId;
    document.getElementById("bookingForm").style.display = "block";
}

function cancelBooking() {
    document.getElementById("bookingForm").style.display = "none";
}

document.getElementById("bookVehicleForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const userId = localStorage.getItem("userId");
    const vehicleId = document.getElementById("vehicleId").value;
    const startDate = document.getElementById("startDate").value;
    const endDate = document.getElementById("endDate").value;
    const start = new Date(startDate);
    const end = new Date(endDate);
    if (start >= end) {
        alert("End date must be after start date");
        return;
    }
    try {
        const response = await fetch("http://localhost:8080/api/bookings", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded", "X-User-Id": userId },
            body: new URLSearchParams({ vehicleId, startDate, endDate }),
        });
        const data = await response.json();
        if (response.ok) {
            alert("Booking successful!");
            cancelBooking();
            loadVehicles();
        } else {
            alert(data.message || "Booking failed");
        }
    } catch (error) {
        console.error("Error booking vehicle:", error);
        alert("An error occurred while booking: " + error.message);
    }
});

function toggleMenu() {
    document.querySelector(".navbar-menu").classList.toggle("active");
}

function logout() {
    localStorage.removeItem("userId");
    localStorage.removeItem("role");
    window.location.href = "index.html";
}

loadVehicles();
