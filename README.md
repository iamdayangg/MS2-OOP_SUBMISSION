MotorPH Payroll System
Project Overview

The MotorPH Payroll System is a Java-based desktop application developed using Java Swing. The system manages employee records, attendance tracking, payroll processing, leave management, and user account administration. The application follows Object-Oriented Programming principles and applies layered architecture using GUI, Service, DAO, and Model layers.

This project was developed as part of the MO-IT110 Object-Oriented Programming course.

System Architecture

The system follows a layered architecture to ensure separation of concerns:

GUI Layer – Handles user interface and user interactions
Service Layer – Contains business logic and validations
DAO Layer – Handles file operations and data persistence
Model Layer – Represents system entities such as Employee, Payslip, Leave, Attendance
CSV / Text Files – Used for data storage

The GUI does not directly access files. All operations pass through the Service layer and DAO layer.

Object-Oriented Programming Concepts Applied

The system implements the following OOP principles:

Encapsulation – Data fields are private and accessed through methods
Inheritance – Employee subclasses such as Admin, HR, Finance, ITStaff extend Employee
Polymorphism – Payroll calculation uses the Strategy Pattern through PayrollCalculator interface
Abstraction – Services and DAOs abstract file handling and business logic
Layered Architecture – GUI → Service → DAO → File
System Modules

The system includes the following modules:

Login and Authentication
Validates credentials
Determines user role
Displays role-based menu
Employee Management
Add Employee
Update Employee
Delete Employee
View Employee Information
Attendance Management
Time In
Time Out
Attendance Records
Role-based attendance deletion
Payroll Management
Standard Payroll Calculation
Overtime Payroll Calculation
Deduction Calculation (Tax, SSS, PhilHealth, Pag-IBIG)
Payslip Generation
Payroll Record Storage
Leave Management
Submit Leave Request
Approve or Reject Leave Request
View Leave Records
Account Management
Add Account
Edit Account
Delete Account
Payroll Computation Logic

The payroll system computes salary using the following logic:

Gross Pay
Standard Payroll = Basic Salary + Allowance
Overtime Payroll = Hours Worked × Hourly Rate (Overtime > 8 hours at 1.5× rate)
Deductions
Tax = 10%
SSS = 4.5%
PhilHealth = 3%
Pag-IBIG = 2%
Net Pay
Net Pay = Gross Pay − Total Deductions

Payroll calculations are handled by:

PayrollService
StandardPayrollCalculator
OvertimePayrollCalculator
DeductionCalculator
Payslip
Data Storage Files

The system uses CSV and text files for persistent storage:

File Name	Description
credentials.csv	User login accounts
employee_data.txt	Employee records
attendance.txt	Attendance logs
payroll_data.csv	Payroll records
leave_data.csv	Leave requests

All file operations are handled through DAO classes.

Documentation and Project Sheets

The repository also includes documentation and system design sheets such as:

Database Design / ERD
Data Dictionary
System Design Document
CP2 Logic to OOP Mapping
Smoke Test Checklist
Known Issues List
Project Documentation Sheets
OOP Architecture Documentation

These documents describe the system design, architecture decisions, testing results, and known issues identified during development.

How to Run the System
Open the project in NetBeans or any Java IDE.
Ensure Java JDK 17 or higher is installed.
Run Main.java.
The Login Screen will appear.
Log in using credentials from credentials.csv.
Developers

This project was developed by:

Diane Ababao
Emilyn Maguad
Jeah May Pareja
Patrick Louis Pineda

Purpose

This project is developed for academic purposes for the course:
MO-IT110 Object-Oriented Programming
