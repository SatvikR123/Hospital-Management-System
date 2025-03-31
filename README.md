# Hospital Management System

A comprehensive Java-based Hospital Management System that streamlines various hospital operations including patient management, appointments, treatments, and billing.

## Features

- **Patient Management**
  - Add new patients
  - View patient information
  - Update patient details
  - Track patient history

- **Appointment System**
  - Schedule appointments
  - Manage appointment calendar
  - View appointment history

- **Treatment Management**
  - Record patient treatments
  - Track treatment history
  - Manage treatment logs

- **Billing System**
  - Generate patient bills
  - Track payment history
  - Manage financial records

- **Doctor Management**
  - View doctor details
  - Track doctor schedules
  - Manage doctor assignments

- **Reception Management**
  - Handle patient check-ins
  - Manage patient queues
  - Process initial patient registration

## Technical Details

- **Programming Language**: Java
- **Database**: MySQL
- **Architecture**: Desktop Application
- **UI Framework**: Java Swing

## Prerequisites

- Java JDK (version 8 or higher)
- MySQL Database
- MySQL JDBC Driver

## Setup Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/Hospital-Management-System.git
   ```

2. Configure the database:
   - Create a MySQL database
   - Update the `db_config.properties` file with your database credentials

3. Build the project:
   - Open the project in your preferred IDE
   - Build and run the project

4. Run the application:
   - Execute the `Login.java` file to start the application
   - Use the provided credentials to access the system

## Project Structure

```
Hospital-Management-System/
├── src/
│   ├── Add_Patient.java
│   ├── AppointmentSystem.java
│   ├── Conn.java
│   ├── HospitalBillingSystem.java
│   ├── Login.java
│   ├── ManageTreatmentLog.java
│   ├── PatientItem.java
│   ├── Reception.java
│   ├── TreatmentManager.java
│   ├── Update_Patient.java
│   ├── View_Doc_Details.java
│   └── View_Patient_Info.java
├── db_config.properties
└── README.md
```

## Screenshots

The application includes several key interfaces:
- Patient Information Management
- Patient Treatment Tracking
- Appointment Scheduling
- Billing System
- Doctor Management

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support, please open an issue in the GitHub repository or contact the maintainers.