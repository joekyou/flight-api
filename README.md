# Flight Booking API

A comprehensive RESTful API for flight booking management built with Spring Boot 3.2.0. This system allows users to manage flights, bookings, passengers, and airports with secure JWT authentication.

## Technologies Used

- Java 17
- Spring Boot 3.2.0
- Spring Security with JWT Authentication
- Spring Data JPA
- MySQL Database
- Lombok
- ModelMapper
- OpenAPI (Swagger) Documentation
- Spring Retry
- Maven

## Features

- **User Management**
  - User registration and authentication
  - JWT based security
  - User profile management

- **Flight Management**
  - Flight search and booking
  - Flight schedule management
  - Airport information

- **Booking System**
  - Create and manage bookings
  - Passenger management
  - Booking status tracking

- **Passenger Management**
  - Passenger information management
  - Different passenger types support
  - User-passenger associations

## Setup Instructions

### Prerequisites
- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

### Installation Steps

1. Clone the repository:
```bash
git clone [repository-url]
cd flight-api
```

2. Configure MySQL database in `src/main/resources/application.yml`

3. Run the database scripts:
   - Schema creation: `src/main/resources/schema.sql`
   - Initial data: `src/main/resources/data.sql`

4. Build the project:
```bash
mvn clean install
```

5. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

Once the application is running, you can access the API documentation at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI Docs: `http://localhost:8080/v3/api-docs`

## API Endpoints

### Authentication
- POST `/api/auth/register` - Register new user
- POST `/api/auth/login` - Login user

### Flights
- GET `/api/flights` - List all flights
- GET `/api/flights/{id}` - Get flight details
- POST `/api/flights/search` - Search flights

### Bookings
- POST `/api/bookings` - Create new booking
- GET `/api/bookings/{id}` - Get booking details
- PUT `/api/bookings/{id}` - Update booking

### Passengers
- POST `/api/passengers` - Add new passenger
- GET `/api/passengers/{id}` - Get passenger details
- PUT `/api/passengers/{id}` - Update passenger information

### Users
- GET `/api/users/profile` - Get user profile
- PUT `/api/users/profile` - Update user profile

## Security

The API uses JWT (JSON Web Tokens) for authentication. Include the JWT token in the Authorization header for protected endpoints:
```
Authorization: Bearer [your-jwt-token]
```

## Database Schema

The application uses MySQL with the following key entities:
- Users
- Flights
- Bookings
- Passengers
- Airports
- UserPassengers
- BookingPassengers

Refer to `schema.sql` for detailed database structure.

## Error Handling

The API uses standard HTTP response codes:
- 2xx for successful operations
- 4xx for client errors
- 5xx for server errors

Detailed error messages are included in the response body.

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## AWS
qiaozhe-flight-api
Public IP 3.27.169.252