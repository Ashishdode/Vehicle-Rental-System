function logout() {
    localStorage.removeItem('userId');
    localStorage.removeItem('role');
    window.location.href = 'index.html';
}

// Object mapping vehicle types to image arrays
const carImages = {
    car: [
    "img/car1.png",
    "img/car3.png",
    "img/car4.png",
    "img/car5.png",
    "img/car6.png",
    "img/car7.png",
    "img/car8.png",
    "img/car9.png"
    ],
    bike: [
        "img/bike1.png",
        "img/bike3.png",
        "img/bike4.png",
        "img/bike5.png",
    ],
    truck: [
        "img/truck1.png"
    ]
};

// Function to get a random image based on vehicle type
function getRandomVehicleImage(vehicleType) {
    const images = carImages[vehicleType.toLowerCase()] || carImages.car; // Default to car images if type not found
    const randomIndex = Math.floor(Math.random() * images.length);
    return images[randomIndex];
}