# Timetable & Reservation Management System API

This is the official backend API for the University of Yaoundé I's Faculty of Science Timetable and Classroom Reservation Management System. The application is built with Spring Boot and leverages Google OR-Tools for complex scheduling problems. It provides a secure, robust, and scalable solution for managing academic resources.

## ✨ Features

- **Automated Timetable Generation**:
  - **Model-Based Generation**: Uses Google OR-Tools' Constraint Programming solver to generate optimal timetables based on a set of hard and soft constraints (teacher availability, room capacity, curriculum rules, etc.).
  - **Cloning**: Ability to clone a timetable from a previous academic year for quick setup.
- **Reservation Management**:
  - Teachers can request reservations for available rooms and time slots.
  - Administrators can review, approve, or reject reservation requests.
- **Real-time Updates**:
  - Uses **WebSockets (with STOMP)** to provide instant notifications to clients when reservations are created or their status is updated.
- **Secure Authentication**:
  - Stateless authentication using **JSON Web Tokens (JWT)**.
  - Role-based access control managed by Spring Security.
- **Comprehensive API Documentation**:
  - Auto-generated, interactive API documentation using **Springdoc OpenAPI (Swagger UI)**.
- **Core Resource Management**:
  - Full CRUD (Create, Read, Update, Delete) operations for all core entities: Faculties, Branches, Departments, Levels, Rooms, UEs (Course Units), and Courses.

## 🛠️ Technology Stack

- **Backend**: Java 17+, Spring Boot 3.x
- **Database**: MongoDB
- **Scheduling Engine**: Google OR-Tools
- **Real-time Communication**: Spring WebSocket, STOMP
- **Security**: Spring Security, JWT
- **API Documentation**: Springdoc OpenAPI v2
- **Build Tool**: Maven

## 🚀 Getting Started

Follow these instructions to get a local copy up and running for development and testing purposes.

### Prerequisites

- **JDK 17 or higher**: [OpenJDK](https://jdk.java.net/17/) or an equivalent distribution.
- **Apache Maven 3.6+**: [Installation Guide](https://maven.apache.org/install.html).
- **MongoDB**: A running instance, either locally or a cloud service like [MongoDB Atlas](https://www.mongodb.com/atlas/database).

### Configuration & Environment Variables

The application is configured using `src/main/resources/application.properties`, which relies on environment variables for sensitive data. **You must set these variables in your local development environment before running the application.**

#### Required Environment Variables

| Variable          | Description                                                                            | Example Value                                              |
| ----------------- | -------------------------------------------------------------------------------------- | ---------------------------------------------------------- |
| `MONGODB_URI`     | Your full MongoDB connection string.                                                   | `mongodb://localhost:27017/timetable_db`                   |
| `JWT_SECRET`      | A strong, secret key for signing JWTs. Use a long, random string.                      | `your-super-secret-and-long-jwt-key-that-is-hard-to-guess` |
| `PASSWORD_SUFFIX` | A default suffix appended to user passwords on creation.                               | `@facsciences-uy1`                                         |
| `MAIL_USERNAME`   | The username for your SMTP mail server (e.g., your Gmail address).                     | `your-email@gmail.com`                                     |
| `MAIL_PASSWORD`   | The password for your SMTP mail server. For Gmail, this should be an **App Password**. | `abcd-efgh-ijkl-mnop`                                      |
| `MAIL_FROM`       | The email address that will appear in the "From" field.                                | `no-reply@yourdomain.com`                                  |
| `PORT`            | (Optional) The port the server will run on. Defaults to `8080`.                        | `8080`                                                     |

#### How to Set Environment Variables

You can set these variables in several ways. Choose the one that best fits your workflow.

**Method 1: IDE Run Configuration (Recommended)**

This is the most common and reliable method for local development.

1.  Open your project in your IDE (e.g., IntelliJ IDEA, Eclipse, VSCode).
2.  Find the "Run/Debug Configurations" for your main application class.
3.  In the configuration settings, locate the "Environment variables" section.
4.  Add each of the required variables and their values from the table above.
5.  Save the configuration and run the application.

**Method 2: Using a `.env` file with `dotenv-maven-plugin`**

If your project is configured with a plugin like `dotenv-maven-plugin`, you can create a `.env` file in the project's root directory. **This file should not be committed to Git.**

- Create a file named `.env` in the root of the project.
- Add the variables to it:
  ```sh
   MONGODB_URI=mongodb://localhost:27017/timetable_db
   JWT_SECRET=your-super-secret-and-long-jwt-key
   MAIL_USERNAME="your-email@yourdomain.com"
   MAIL_FROM="your-email@yourdomain.com"
   MAIL_PASSWORD="your-app-password"
   JWT_EXPIRATION_HOURS=
   PASSWORD_SUFFIX="your-preferred-password-suffix"
  ```
- Add the `.env` file to your `.gitignore` to prevent it from being tracked.
  ```
  echo ".env" >> .gitignore
  ```

**Method 3: Shell `export` (for a single terminal session)**

You can export the variables directly in your terminal before running the application. Note that these will only be active for the current terminal session.

```sh
# On Linux/macOS
export MONGODB_URI="mongodb://localhost:27017/timetable_db"
export JWT_SECRET="your-secret-key"
# ... set all other variables ...
mvn spring-boot:run

# On Windows (Command Prompt)
set MONGODB_URI="mongodb://localhost:27017/timetable_db"
# ... set all other variables ...
mvn spring-boot:run
```

### Build & Run

1.  **Clone the repository**:

    ```sh
     git clone https://github.com/your-org/facsciences_planning_management.git
     cd facsciences_planning_management
    ```

2.  **Set up your environment variables** using one of the methods described above.

3.  **Build the project with Maven**:

    ```sh
    mvn clean install
    ```

4.  **Run the application**:
    You can run the application directly using the Spring Boot Maven plugin:
    ```sh
    mvn spring-boot:run
    ```
    Or, you can run it from your favorite IDE by executing the main application class.

The application should now be running on the configured port (default: `http://localhost:8080`).

## 📖 API Documentation & Testing

The project includes interactive API documentation powered by Swagger UI.

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI Spec (JSON)**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### How to Test Secure Endpoints

Most endpoints are protected and require a JWT. Follow these steps to test them using Swagger UI:

1.  **Get a JWT**:

    - Navigate to the `auth-controller` section in Swagger UI.
    - Expand the `POST /api/auth/login` endpoint.
    - Click "Try it out".
    - Enter the credentials for an existing user in the request body.
    - Click "Execute". You will receive a response containing an `accessToken`.

2.  **Authorize Your Session**:

    - Copy the `accessToken` value from the response body (without the quotes).
    - At the top right of the Swagger UI page, click the green **"Authorize"** button.
    - In the "Value" field of the `bearerAuth` dialog, paste the token in the format: `Bearer <your_copied_token>`.
    - Click "Authorize" and then "Close".

3.  **Test a Protected Endpoint**:
    - You are now authenticated! Navigate to any other endpoint (e.g., `GET /api/v1/rooms`).
    - Click "Try it out" and "Execute". The request will now include the necessary `Authorization` header, and you should receive a successful response.

## 📡 Real-time with WebSockets

The application uses WebSockets to push real-time updates for reservations, eliminating the need for clients to poll the server.

- **Connection Endpoint**: Clients should connect to `http://localhost:8080/ws`.
- **Security**: The connection must be authenticated. The client needs to send its JWT in a STOMP header during the connection handshake.
- **Subscription Topic**: To receive reservation updates, clients must subscribe to the `/topic/reservations` destination.

### Example Client-Side Logic (JavaScript with Stomp.js)

```javascript
import SockJS from "sockjs-client";
import Stomp from "stompjs";

// Get the JWT from your authentication flow (e.g., from local storage)
const jwtToken = localStorage.getItem("accessToken");

// Create connection headers with the token
const connectHeaders = {
  Authorization: `Bearer ${jwtToken}`,
};

// Connect to the WebSocket endpoint
const socket = new SockJS("http://localhost:8080/ws");
const stompClient = Stomp.over(socket);

stompClient.connect(
  connectHeaders,
  (frame) => {
    console.log("Connected to WebSocket:", frame);

    // Subscribe to the reservations topic
    stompClient.subscribe("/topic/reservations", (message) => {
      const reservationUpdate = JSON.parse(message.body);
      console.log("Received reservation update:", reservationUpdate);
      // Update your UI here
    });
  },
  (error) => {
    console.error("WebSocket connection error:", error);
  }
);
```

When an admin processes a request or a teacher creates a new one, a `ReservationResponseDTO` object will be broadcast to all subscribers on this topic.

## ☁️ Deployment

The application is configured for deployment in a containerized environment (like Docker or cloud platforms such as Koyeb and Heroku).

- **Production Server**: A live version of this API is documented to be at `https://facsciences-uy1-planning-management-gedeontiga-eabfb5d3.koyeb.app`.
- **CI/CD**: Ensure all environment variables listed in the [Configuration](#configuration-environment-variables) section are securely set in your deployment environment's secrets or configuration management service.

## 🪵 Logging

- The default logging level is set to `ERROR` to minimize log noise in production.
- The log level for application-specific packages (`com.example.timetable`) is set to `INFO`.
- To enable more detailed debugging, you can modify the `logging.level.*` properties in `application.properties`.
- Stack traces and detailed error messages are disabled in HTTP responses for security. Check the server logs for detailed error information.
