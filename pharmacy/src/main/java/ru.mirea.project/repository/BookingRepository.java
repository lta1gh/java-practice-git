package ru.mirea.project.repository;

import ru.mirea.project.exception.BusinessException;
import ru.mirea.project.exception.EntityNotFoundException;
import ru.mirea.project.model.Booking;
import ru.mirea.project.model.BookingStatus;
import ru.mirea.project.util.DatabaseManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingRepository implements CrudRepository<Booking> {

    @Override
    public void create(Booking booking) throws BusinessException {
        String sql = "INSERT INTO bookings (user_id, medicines, status, created_at) VALUES (?, ?, ?, ?)";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, booking.getUserId());
            ps.setString(2, booking.getMedicines());
            ps.setString(3, booking.getStatus().name());
            ps.setTimestamp(4, Timestamp.valueOf(booking.getCreatedAt()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) booking.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new BusinessException("Ошибка БД: " + e.getMessage());
        }
    }

    @Override
    public List<Booking> findAll() {
        List<Booking> result = new ArrayList<>();
        String sql = "SELECT id, user_id, medicines, status, created_at FROM bookings ORDER BY id";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(map(rs));
        } catch (SQLException e) {
            System.out.println("Ошибка БД: " + e.getMessage());
        }
        return result;
    }

    @Override
    public Booking findById(int id) throws EntityNotFoundException {
        String sql = "SELECT id, user_id, medicines, status, created_at FROM bookings WHERE id = ?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка БД: " + e.getMessage());
        }
        throw new EntityNotFoundException("Бронь с ID=" + id + " не найдена");
    }

    @Override
    public void update(Booking booking) throws BusinessException, EntityNotFoundException {
        findById(booking.getId());
        String sql = "UPDATE bookings SET user_id = ?, medicines = ?, status = ? WHERE id = ?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, booking.getUserId());
            ps.setString(2, booking.getMedicines());
            ps.setString(3, booking.getStatus().name());
            ps.setInt(4, booking.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BusinessException("Ошибка БД: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) throws EntityNotFoundException, BusinessException {
        findById(id);
        String sql = "DELETE FROM bookings WHERE id = ?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BusinessException("Ошибка БД: " + e.getMessage());
        }
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM bookings";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Ошибка БД: " + e.getMessage());
        }
        return 0;
    }

    private Booking map(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setId(rs.getInt("id"));
        b.setUserId(rs.getInt("user_id"));
        b.setMedicines(rs.getString("medicines"));
        b.setStatus(BookingStatus.valueOf(rs.getString("status")));
        b.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return b;
    }
}