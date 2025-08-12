package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Sale {
    private int id;
    private Integer bookingId;     // optional link to a booking
    private BigDecimal amount;
    private LocalDateTime createdAt;

    public Sale() {}

    public Sale(int id, Integer bookingId, BigDecimal amount, LocalDateTime createdAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getBookingId() { return bookingId; }
    public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
