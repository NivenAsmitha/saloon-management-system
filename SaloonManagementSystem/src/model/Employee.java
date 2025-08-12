package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Employee {
    private int id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private BigDecimal salary;
    private LocalDate hireDate;
    private Integer userId; // nullable FK to users.id

    public Employee() {}

    public Employee(int id, String name, String phone, String email, String address,
                    BigDecimal salary, LocalDate hireDate, Integer userId) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.salary = salary;
        this.hireDate = hireDate;
        this.userId = userId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
}
