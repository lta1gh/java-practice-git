package ru.mirea.project.service;

import ru.mirea.project.exception.BusinessException;
import ru.mirea.project.exception.EntityNotFoundException;
import ru.mirea.project.model.User;
import ru.mirea.project.repository.UserRepository;

import java.util.List;

public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public void create(User user) throws BusinessException { repo.create(user); }
    public List<User> findAll() { return repo.findAll(); }
    public User findById(int id) throws EntityNotFoundException { return repo.findById(id); }
    public void update(User user) throws BusinessException, EntityNotFoundException { repo.update(user); }
    public void delete(int id) throws EntityNotFoundException, BusinessException { repo.delete(id); }

    public boolean exists(int id) {
        try { repo.findById(id); return true; }
        catch (EntityNotFoundException e) { return false; }
    }
}