package ru.mirea.project.model;

import java.time.LocalDateTime;

public class Booking {
    private int id;
    private int userId;
    private String medicines;
    private BookingStatus status;
    private LocalDateTime createdAt;

    public Booking() {}

    public Booking(int id, int userId, String medicines, BookingStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.medicines = medicines;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getMedicines() { return medicines; }
    public void setMedicines(String medicines) { this.medicines = medicines; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Booking{id=" + id + ", userId=" + userId
                + ", medicines='" + medicines + "'"
                + ", status=" + (status == null ? "null" : status.getTitle())
                + ", createdAt=" + createdAt + "}";
    }
}