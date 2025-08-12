package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Booking {

    private int id;
    private String customerName;
    private Integer customerAge;      // nullable
    private String customerPhone;
    private LocalDate bookingDate;    // DATE
    private LocalTime bookingTime;    // TIME
    private Integer employeeId;       // FK to employees.id (nullable)
    private String status;            // PENDING / CONFIRMED / COMPLETED / CANCELLED
    private BigDecimal totalAmount;   // DECIMAL(10,2)
    private Integer createdBy;        // FK to users.id (nullable)
    private LocalDateTime createdAt;  // TIMESTAMP

    public Booking() {
        // sensible defaults
        this.status = "PENDING";
        this.totalAmount = BigDecimal.ZERO;
    }

    // ----- Getters & Setters -----

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getCustomerAge() {
        return customerAge;
    }

    public void setCustomerAge(Integer customerAge) {
        this.customerAge = customerAge;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // ----- Utility -----

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", customerAge=" + customerAge +
                ", customerPhone='" + customerPhone + '\'' +
                ", bookingDate=" + bookingDate +
                ", bookingTime=" + bookingTime +
                ", employeeId=" + employeeId +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                '}';
    }
}
