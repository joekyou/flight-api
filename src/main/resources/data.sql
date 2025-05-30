-- Insert sample airports
INSERT INTO c (code, name, city, country) VALUES
('JFK', 'John F. Kennedy International Airport', 'New York', 'USA'),
('LHR', 'Heathrow Airport', 'London', 'UK'),
('CDG', 'Charles de Gaulle Airport', 'Paris', 'France'),
('DXB', 'Dubai International Airport', 'Dubai', 'UAE'),
('HKG', 'Hong Kong International Airport', 'Hong Kong', 'China');

-- Insert sample flights
INSERT INTO flights (flight_number, airline, departure_airport_id, destination_airport_id, 
                    departure_time, arrival_time, price, available_seats, aircraft_type) VALUES
('BA112', 'British Airways', 1, 2, '2025-06-10 08:00:00', '2025-06-10 13:00:00', 550.00, 200, 'Boeing 747'),
('AF100', 'Air France', 1, 3, '2025-06-10 09:30:00', '2025-06-10 15:00:00', 520.00, 180, 'Airbus A380'),
('EK200', 'Emirates', 2, 4, '2025-06-10 12:00:00', '2025-06-10 21:30:00', 780.00, 250, 'Boeing 777'),
('CX888', 'Cathay Pacific', 4, 5, '2025-06-11 09:00:00', '2025-06-11 16:00:00', 650.00, 190, 'Airbus A350');

-- Insert admin user
INSERT INTO users (email, password, first_name, last_name, country, phone) VALUES
('admin@flightapi.com', '$2a$10$GckdgpfIJ7J/W9qZ9u1XUOiYpwQglUFfcHxFQXPgZkM1KsQfKxlTi', 'Admin', 'User', 'USA', '1234567890'),
('aaa@aaa.com', '$2a$10$GckdgpfIJ7J/W9qZ9u1XUOiYpwQglUFfcHxFQXPgZkM1KsQfKxlTi', 'Test', 'User', 'China', '1234567890');
-- Password for both users: zaq12wsx
