# Vehicle Rental System

A web application for renting vehicles, built with Spring Boot (backend) and HTML/CSS/JavaScript (frontend). The system allows users to register, browse vehicles, book vehicles, and manage their bookings, while admins can manage vehicles and view all bookings.

## Project Structure
- `frontend/`: Contains frontend files (HTML, CSS, JS, images).
  - `admin.html`: Admin dashboard for managing vehicles and viewing bookings.
  - `index.html`: Landing page with login functionality.
  - `my-bookings.html`: User bookings page.
  - `register.html`: User registration page.
  - `vehicles.html`: Page to view and book available vehicles.
- `src/`: Contains the Spring Boot backend (Java code).
- `docs/`: Contains documentation files.

## Prerequisites
- Java 17 or higher
- Maven (or use the included Maven Wrapper: `mvnw`)
- PostgreSQL (version 12 or higher)
- A web browser (e.g., Chrome, Firefox)
- A web server for the frontend (e.g., VS Code Live Server)

## Setup Instructions

### 1. Clone the Repository
Clone the project repository to your local machine:
- Use the command: `git clone <repository-url>`
- Navigate to the project directory: `cd vehicle-rental-system`

### 2. Set Up the Database
The project uses PostgreSQL as the database. Follow these steps to set up the database:
- Install PostgreSQL if not already installed.
- Open the PostgreSQL command-line tool (e.g., `psql`) or a GUI tool like pgAdmin.
- Create a new database named `vehicle_rental_db`.
- Note down the database credentials (username, password, host, port). The default settings are:
  - Host: `localhost`
  - Port: `5432`
  - Username: `postgres`
  - Password: (set by you during PostgreSQL installation)

### 3. Configure the Backend
The backend requires a configuration file to connect to the PostgreSQL database.

#### Create the `resources` Folder
- In the `src/main/` directory, create a folder named `resources` if it doesn’t already exist:
  - Path: `src/main/resources/`

#### Create `application.properties`
- Inside `src/main/resources/`, create a file named `application.properties`.
- Configure the file with your PostgreSQL database settings. Use the following template:
  - Specify the database URL, username, and password.
  - Enable JPA to create/update the database schema automatically.
  - Example configuration:
    - `spring.datasource.url=jdbc:postgresql://localhost:5432/vehicle_rental_db`
    - `spring.datasource.username=postgres`
    - `spring.datasource.password=your_password`
    - `spring.datasource.driver-class-name=org.postgresql.Driver`
    - `spring.jpa.hibernate.ddl-auto=update`
    - `spring.jpa.properties.hibernate.jdbc.time_zone=UTC`


