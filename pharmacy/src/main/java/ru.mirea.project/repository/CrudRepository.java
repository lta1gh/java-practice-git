package ru.mirea.project.repository;

import ru.mirea.project.exception.BusinessException;
import ru.mirea.project.exception.EntityNotFoundException;

import java.util.List;

public interface CrudRepository<T> {
    void create(T entity) throws BusinessException;
    List<T> findAll();
    T findById(int id) throws EntityNotFoundException;
    void update(T entity) throws BusinessException, EntityNotFoundException;
    void delete(int id) throws EntityNotFoundException, BusinessException;
}