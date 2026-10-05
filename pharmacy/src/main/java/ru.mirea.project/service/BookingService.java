package ru.mirea.project.service;

import ru.mirea.project.exception.BusinessException;
import ru.mirea.project.exception.EntityNotFoundException;
import ru.mirea.project.model.Booking;
import ru.mirea.project.model.BookingStatus;
import ru.mirea.project.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BookingService {
    private final BookingRepository repo;
    private final UserService userService;

    public BookingService(BookingRepository repo, UserService userService) {
        this.repo = repo;
        this.userService = userService;
    }

    public void create(Booking b) throws BusinessException {
        //обязательные лекарства
        if (b.getMedicines() == null || b.getMedicines().isBlank())
            throw new BusinessException("Список лекарств не может быть пустым");

        //пользователь должен существовать
        if (!userService.exists(b.getUserId()))
            throw new BusinessException("Пользователь с ID=" + b.getUserId() + " не существует");

        //некорректный статус
        if (b.getStatus() == null)
            throw new BusinessException("Статус не может быть пустым");

        //у новой брони допустим только статус ACCEPTED
        if (b.getStatus() != BookingStatus.ACCEPTED)
            throw new BusinessException("Новая бронь должна иметь статус 'Принят'");

        if (b.getCreatedAt() == null) b.setCreatedAt(LocalDateTime.now());
        repo.create(b);
    }

    public List<Booking> findAll() { return repo.findAll(); }

    public Booking findById(int id) throws EntityNotFoundException { return repo.findById(id); }

    public void update(Booking updated) throws BusinessException, EntityNotFoundException {
        Booking old = repo.findById(updated.getId());

        if (updated.getMedicines() == null || updated.getMedicines().isBlank())
            throw new BusinessException("Список лекарств не может быть пустым");

        if (!userService.exists(updated.getUserId()))
            throw new BusinessException("Пользователь с ID=" + updated.getUserId() + " не существует");

        //проверка разрешенных переходов статусов
        if (old.getStatus() != updated.getStatus()) {
            checkTransition(old.getStatus(), updated.getStatus());
        }

        repo.update(updated);
    }

    public void delete(int id) throws EntityNotFoundException, BusinessException { repo.delete(id); }

    //переходы статусов
    private void checkTransition(BookingStatus from, BookingStatus to) throws BusinessException {
        boolean ok =
            (from == BookingStatus.ACCEPTED   && (to == BookingStatus.ASSEMBLING || to == BookingStatus.CANCELLED)) ||
            (from == BookingStatus.ASSEMBLING && (to == BookingStatus.ASSEMBLED  || to == BookingStatus.CANCELLED)) ||
            (from == BookingStatus.ASSEMBLED  && (to == BookingStatus.PURCHASED  || to == BookingStatus.CANCELLED));
        if (!ok)
            throw new BusinessException("Недопустимый переход статуса: "
                    + from.getTitle() + " -> " + to.getTitle());
    }

    //поиск по названию лекарства
    public List<Booking> searchByMedicine(String part) {
        return repo.findAll().stream()
                .filter(b -> b.getMedicines().toLowerCase().contains(part.toLowerCase()))
                .collect(Collectors.toList());
    }

    //поиск по имени пользователя
    public List<Booking> searchByUserName(String part) {
        return repo.findAll().stream()
                .filter(b -> {
                    try {
                        return userService.findById(b.getUserId()).getName()
                                .toLowerCase().contains(part.toLowerCase());
                    } catch (Exception e) { return false; }
                })
                .collect(Collectors.toList());
    }

    //фильтр по статусу
    public List<Booking> filterByStatus(BookingStatus status) {
        return repo.findAll().stream()
                .filter(b -> b.getStatus() == status)
                .collect(Collectors.toList());
    }

    //фильтр по ID пользователя
    public List<Booking> filterByUserId(int userId) {
        return repo.findAll().stream()
                .filter(b -> b.getUserId() == userId)
                .collect(Collectors.toList());
    }

    //сортировка по дате
    public List<Booking> sortByDate() {
        return repo.findAll().stream()
                .sorted(Comparator.comparing(Booking::getCreatedAt))
                .collect(Collectors.toList());
    }

    //сортировка по статусу (по имени enum)
    public List<Booking> sortByStatus() {
        return repo.findAll().stream()
                .sorted(Comparator.comparing(b -> b.getStatus().name()))
                .collect(Collectors.toList());
    }

    public long countByStatus(BookingStatus status) {
        return repo.findAll().stream().filter(b -> b.getStatus() == status).count();
    }
}