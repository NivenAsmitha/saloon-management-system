# saloon-management-system
A Java Swing + MySQL-based desktop application to manage bookings, services, products, employees, sales, and billing for beauty salons. Features include password management, role-based access (Admin/Employee), reporting, and printable bills.




# 💇‍♀️ Saloon Management System

A **Java Swing** + **MySQL** desktop application for managing salon operations.  
Designed for **Admins** and **Employees** with features like product/service management, bookings, sales tracking, billing, and reporting.

---

## 📸 Screenshots
> _Add screenshots in `screenshots/` folder and link here_
![Login Screen](screenshots/login.png)
![Admin Dashboard](screenshots/admin-dashboard.png)
![Billing Panel](screenshots/billing.png)
![Employee Panel](screenshots/employee.png)
![Report Panel](screenshots/report.png)> 

---

## 🚀 Features
- 🔑 **Role-based login** (Admin & Employee)
- 👥 Employee management
- 💇 Service management
- 📦 Product management
- 📅 Booking management
- 💰 Billing system with product/service combination
- 📊 Sales and reports
- 🔍 Search & filter in tables
- 🛡 Password change enforcement for new employees

---

## 🛠 Tech Stack
- **Java** (Swing for GUI)
- **MySQL** (Database)
- **JDBC** for database connection
- **NetBeans IDE** (Recommended)
- **Maven** (optional for dependency management)

---

## 📂 Project Structure


│── src/
│ ├── controller/ # Handles DB operations & business logic
│ ├── database/ # Database connection
│ ├── model/ # Data models (POJOs)
│ ├── view/ # GUI forms (Java Swing panels/frames)
│── sql/ # SQL scripts for database setup
│── lib/ # MySQL Connector JAR
│── screenshots/ # App screenshots
│── README.md
│── .gitignore


Import to NetBeans or IntelliJ

Open as a Java project

Add MySQL Connector JAR (lib/mysql-connector-j-X.X.X.jar) to classpath



Set up the database

Create the database in MySQL:
SOURCE sql/saloon_management.sql;
Update DatabaseManager.java with your MySQL credentials:

String url = "jdbc:mysql://localhost:3306/saloon_management?allowPublicKeyRetrieval=true&useSSL=false";
String user = "root";
String pass = "1234";
Run the application

Start the MySQL server

Run LoginFrame.java

👤 Default Login Credentials
Admin
Username: admin
Password: admin123

Employee
Username: emma
Password: pass123
