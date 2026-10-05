package ru.mirea.project.repository;

import ru.mirea.project.exception.BusinessException;
import ru.mirea.project.exception.EntityNotFoundException;
import ru.mirea.project.model.User;
import ru.mirea.project.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements CrudRepository<User> {

    @Override
    public void create(User user) throws BusinessException {
        if (user.getName() == null || user.getName().isBlank())
            throw new BusinessException("Имя пользователя обязательно");
        if (user.getPhone() == null || user.getPhone().isBlank())
            throw new BusinessException("Телефон пользователя обязателен");

        String sql = "INSERT INTO users (phone, name) VALUES (?, ?)";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getPhone());
            ps.setString(2, user.getName());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) user.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new BusinessException("Ошибка БД: " + e.getMessage());
        }
    }

    @Override
    public List<User> findAll() {
        List<User> result = new ArrayList<>();
        String sql = "SELECT id, phone, name FROM users ORDER BY id";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new User(rs.getInt("id"), rs.getString("phone"), rs.getString("name")));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка БД: " + e.getMessage());
        }
        return result;
    }

    @Override
    public User findById(int id) throws EntityNotFoundException {
        String sql = "SELECT id, phone, name FROM users WHERE id = ?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("phone"), rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка БД: " + e.getMessage());
        }
        throw new EntityNotFoundException("Пользователь с ID=" + id + " не найден");
    }

    @Override
    public void update(User user) throws BusinessException, EntityNotFoundException {
        findById(user.getId());
        if (user.getName() == null || user.getName().isBlank())
            throw new BusinessException("Имя пользователя обязательно");
        if (user.getPhone() == null || user.getPhone().isBlank())
            throw new BusinessException("Телефон пользователя обязателен");

        String sql = "UPDATE users SET phone = ?, name = ? WHERE id = ?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user.getPhone());
            ps.setString(2, user.getName());
            ps.setInt(3, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BusinessException("Ошибка БД: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) throws EntityNotFoundException, BusinessException {
        findById(id);
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BusinessException("Нельзя удалить пользователя (есть связанные брони): " + e.getMessage());
        }
    }
}