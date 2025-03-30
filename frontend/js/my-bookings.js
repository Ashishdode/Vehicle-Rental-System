async function loadBookings() {
    const userId = localStorage.getItem("userId");
    if (!userId) {
        window.location.href = "index.html";
        return;
    }
    try {
        const response = await fetch("http://localhost:8080/api/bookings/my-bookings", {
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
                const canCancel = (booking.status === "PENDING" || booking.status === "CONFIRMED") && new Date(booking.startDate) > new Date();
                const randomImage = getRandomVehicleImage(booking.vehicle.type); // Use vehicle type to get image
                card.innerHTML = `
                    <img src="${randomImage}" alt="${booking.vehicle.name}" class="vehicle-image">
                    <h3>${booking.vehicle.name}</h3>
                    <p>Type: ${booking.vehicle.type}</p>
                    <p>Description: ${booking.vehicle.description || "No description"}</p>
                    <p>Start Date: ${new Date(booking.startDate).toLocaleString()}</p>
                    <p>End Date: ${new Date(booking.endDate).toLocaleString()}</p>
                    <p>Status: ${booking.status}</p>
                    ${canCancel ? `<button class="btn btn-danger" onclick="cancelBooking(${booking.bookingId})">Cancel</button>` : ""}
                `;
                bookingList.appendChild(card);
            });
        }
    } catch (error) {
        console.error("Error loading bookings:", error);
        alert("Failed to load bookings: " + error.message);
    }
}

async function cancelBooking(bookingId) {
    if (!confirm("Are you sure you want to cancel this booking?")) return;
    const userId = localStorage.getItem("userId");
    try {
        const response = await fetch(`http://localhost:8080/api/bookings/${bookingId}`, {
            method: "DELETE",
            headers: { "X-User-Id": userId },
        });
        if (response.ok) {
            alert("Booking canceled successfully");
            loadBookings();
        } else {
            const data = await response.json();
            alert(data.message || "Failed to cancel booking");
        }
    } catch (error) {
        console.error("Error canceling booking:", error);
        alert("An error occurred while canceling the booking");
    }
}

function toggleMenu() {
    document.querySelector(".navbar-menu").classList.toggle("active");
}

loadBookings();
