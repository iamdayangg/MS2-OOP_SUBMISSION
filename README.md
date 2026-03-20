MotorPH Payroll System
Milestone 2 – Object-Oriented Programming Implementation
Project Overview

The MotorPH Payroll System is a Java-based payroll and employee management application developed using Object-Oriented Programming (OOP) principles and a layered architecture.

This project is the final implementation for Milestone 2 of the course MO-IT110 Object-Oriented Programming.

The system refactors the original procedural payroll implementation into a structured OOP system using:

Model classes

Service layer

DAO layer

Swing-based GUI

Role-Based Access Control (RBAC)

CSV file persistence

The application allows authorized users to manage employees, payroll, attendance, leave requests, and system accounts.

Repository Information

Course: MO-IT110 Object-Oriented Programming
Project: MotorPH OOP Implementation
Milestone: Milestone 2 – Final Implementation

GitHub Repository:
ADD YOUR GITHUB LINK HERE

Team Information
Team Name

MotorPH OOP Refactoring Team

Team Members

Diane Ababao

System Design

UI Development

Payroll Implementation

Authentication Integration

DAO Integration

Emilyn Maguad

File-based storage implementation

Payroll computation support

Authentication debugging assistance

Jeah Mae Pareja

Documentation preparation

Smoke testing

Debugging assistance

Patrick Louis Pineda

System analysis

CRC cards

OOP mapping

Refactoring documentation

Testing

Note:
A former member left the group during Week 7 and did not participate in the final implementation.

System Architecture

The project follows a Layered Architecture:

UI Layer (Swing GUI)
        ↓
Service Layer (Business Logic)
        ↓
DAO Layer (File Persistence)
        ↓
CSV Data Storage

This structure ensures:

Separation of concerns

Maintainable code

Proper encapsulation

Reusable business logic

Project Structure
com.mycompany.motorphapps

│
├── dao
│   ├── AccountDAO
│   ├── AttendanceDAO
│   ├── CredentialDAO
│   ├── EmployeeDAO
│   ├── LeaveDAO
│   └── PayrollDAO
│
├── model
│   ├── Person (Abstract Parent Class)
│   ├── Employee
│   ├── Admin
│   ├── HR
│   ├── Finance
│   ├── ITStaff
│   ├── Attendance
│   ├── LeaveRequest
│   ├── Payslip
│   └── Role (Enum)
│
├── payroll
│   ├── PayrollCalculator (Interface)
│   ├── StandardPayrollCalculator
│   ├── OvertimePayrollCalculator
│   ├── DeductionCalculator
│   └── PayrollCalculatorFactory
│
├── service
│   ├── AuthService
│   ├── AccountService
│   ├── EmployeeService
│   ├── AttendanceService
│   ├── LeaveService
│   └── PayrollService
│
├── ui
│   ├── LoginScreen
│   ├── Dashboard
│   ├── PayrollPanel
│   ├── PayslipPanel
│   ├── AllEmployeesPanel
│   ├── AttendancePanel
│   ├── TimeTracker
│   ├── LeavePanel
│   └── AccountManagementPanel
│
└── Main.java
Key OOP Concepts Implemented
Inheritance
Person (Abstract Class)
        ↓
     Employee
        ↓
 Admin | HR | Finance | ITStaff

Each subclass overrides the role behavior using method overriding.

Abstraction

The abstract class Person defines common properties for all system users.

Example:

public abstract class Person {
    public abstract String getRole();
}
Polymorphism (Strategy Pattern)

Payroll computation is implemented using the Strategy Pattern.

PayrollCalculator (interface)
        ↓
StandardPayrollCalculator
OvertimePayrollCalculator

This allows different payroll computation logic depending on employee work conditions.

Encapsulation

Business logic is encapsulated inside Service classes rather than being implemented in the GUI.

Example:

PayrollService
AttendanceService
EmployeeService
Key Features
Authentication System

Login using employee ID and password

Role-based system access

Secure credential validation

Role-Based Access Control

Different roles have access to different system modules:

Role	Access
Admin	Full system access
HR	Employee management and leave approval
Finance	Payroll and attendance
IT Staff	Account management
Employee	Limited access
Employee Management

Users can:

Add employees

Update employee records

Delete employees

View employee directory

All validation is handled in EmployeeService.

Payroll Processing

Payroll computation includes:

Gross pay calculation

Overtime computation

Government deductions:

Tax

SSS

PhilHealth

Pag-IBIG

Net pay calculation

Payroll results are stored in a Payslip object.

Attendance Tracking

Employees can record:

Time-in

Time-out

Attendance logs are stored using AttendanceDAO in CSV format.

Leave Management

Employees can submit leave requests which are stored and retrieved through LeaveService and LeaveDAO.

Data Storage

The system uses CSV files as a lightweight persistence layer.

Examples:

employee_data.csv
attendance_log.csv
credentials.csv
payroll_records.csv
leave_requests.csv

All file operations are handled in the DAO layer.

GUI Implementation

The system uses Java Swing for the graphical interface.

Major GUI components include:

Login screen

Dashboard

Payroll processing panel

Employee management panel

Attendance panel

Leave request panel

Account management panel

The GUI does not perform business logic, ensuring clean separation between UI and backend.

Testing and Debugging
Smoke Testing Results
Test	Status
GUI loads successfully	Pass
Authentication works	Pass
Payroll computation correct	Pass
Attendance recording works	Pass
Leave request storage works	Pass
Debugging Fixes Implemented
Issue	Fix
GUI writing files directly	Moved logic to Service layer
TimeTracker accessing CSV	Implemented AttendanceService
Duplicate payroll calculations	Centralized in Payslip
Missing service methods	Implemented in Service layer
Invalid employee input validation	Added regex validation
Input Validation

Employee data validation includes:

Field	Validation
Employee ID	5-digit numeric
Birthday	MM/DD/YYYY
Phone	09XXXXXXXXX
SSS	##-#######-#
PhilHealth	####-####-####
TIN	###-###-###
Pag-IBIG	####-####-####
Salary	Numeric with decimal support

Invalid inputs throw IllegalArgumentException handled by the GUI.

Technologies Used

Java

Java Swing

Object-Oriented Programming

Strategy Design Pattern

Layered Architecture

CSV File Persistence

Git & GitHub

How to Run the Project
1. Clone the repository
git clone https://github.com/https://github.com/iamdayangg/MS2-OOP-GROUP-17/tree/main
2. Open in NetBeans or any Java IDE
3. Run
Main.java
Milestone 2 Completion

This submission includes:

Fully refactored OOP backend

GUI to backend integration

Service layer implementation

DAO persistence layer

Internal testing and debugging

Completed milestone worksheet

Team contributions documentation

Acknowledgment

This project was developed as part of the MO-IT110 Object-Oriented Programming course, demonstrating practical implementation of OOP principles, layered architecture, and Java application development.

Group Members: Diane Ababao, Emilyn Maguad, Jeah Mae Pareja, and Patrick Louis Pineda
