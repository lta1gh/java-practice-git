package ru.mirea.project;

import ru.mirea.project.model.Booking;
import ru.mirea.project.model.BookingStatus;
import ru.mirea.project.model.User;
import ru.mirea.project.repository.BookingRepository;
import ru.mirea.project.repository.UserRepository;
import ru.mirea.project.service.BookingService;
import ru.mirea.project.service.UserService;
import ru.mirea.project.util.ExcelExporter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main {
    static final Scanner sc = new Scanner(System.in);
    static final UserService userService = new UserService(new UserRepository());
    static final BookingService bookingService = new BookingService(new BookingRepository(), userService);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== Аптека: бронирование лекарств ===");
            System.out.println("1. Создать бронь");
            System.out.println("2. Показать все брони");
            System.out.println("3. Найти бронь по ID");
            System.out.println("4. Изменить бронь");
            System.out.println("5. Удалить бронь");
            System.out.println("6. Поиск");
            System.out.println("7. Фильтрация");
            System.out.println("8. Сортировка");
            System.out.println("9. Статистика");
            System.out.println("10. Экспорт в Excel");
            System.out.println("11. Добавить пользователя");
            System.out.println("12. Показать всех пользователей");
            System.out.println("0. Выход");
            System.out.print("Выбор: ");

            Integer choice = readIntOrNull();
            if (choice == null) continue;

            try {
                switch (choice) {
                    case 1 -> createBooking();
                    case 2 -> printList(bookingService.findAll());
                    case 3 -> findById();
                    case 4 -> updateBooking();
                    case 5 -> deleteBooking();
                    case 6 -> searchMenu();
                    case 7 -> filterMenu();
                    case 8 -> sortMenu();
                    case 9 -> statistics();
                    case 10 -> ExcelExporter.export(bookingService.findAll(), "bookings.xlsx");
                    case 11 -> addUser();
                    case 12 -> printUsers(userService.findAll());
                    case 0 -> { return; }
                    default -> System.out.println("Неверный пункт меню");
                }
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    //CRUD

    static void createBooking() {
        System.out.println("Доступные пользователи:");
        printUsers(userService.findAll());

        System.out.print("ID пользователя: ");
        Integer userId = readIntOrNull();
        if (userId == null) return;

        System.out.print("Лекарства (одной строкой): ");
        String meds = sc.nextLine();

        Booking b = new Booking();
        b.setUserId(userId);
        b.setMedicines(meds);
        b.setStatus(BookingStatus.ACCEPTED);
        b.setCreatedAt(LocalDateTime.now());
        try {
            bookingService.create(b);
            System.out.println("Бронь создана с ID=" + b.getId());
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    static void findById() {
        System.out.print("ID брони: ");
        Integer id = readIntOrNull();
        if (id == null) return;
        try {
            System.out.println(bookingService.findById(id));
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    static void updateBooking() {
        System.out.print("ID брони: ");
        Integer id = readIntOrNull();
        if (id == null) return;
        try {
            Booking b = bookingService.findById(id);
            System.out.println("Текущая: " + b);

            System.out.print("Новый ID пользователя (Enter — оставить " + b.getUserId() + "): ");
            String uidStr = sc.nextLine().trim();
            if (!uidStr.isEmpty()) {
                try { b.setUserId(Integer.parseInt(uidStr)); }
                catch (NumberFormatException e) {
                    System.out.println("Ошибка: ID должен быть целым числом."); return;
                }
            }

            System.out.print("Новые лекарства (Enter — оставить): ");
            String meds = sc.nextLine();
            if (!meds.isBlank()) b.setMedicines(meds);

            System.out.println("Доступные статусы:");
            for (BookingStatus s : BookingStatus.values())
                System.out.println("  " + s.name() + " - " + s.getTitle());
            System.out.print("Новый статус (Enter — оставить): ");
            String st = sc.nextLine().trim();
            if (!st.isBlank()) {
                BookingStatus ns = BookingStatus.fromTitle(st);
                if (ns == null) { System.out.println("Ошибка: некорректный статус"); return; }
                b.setStatus(ns);
            }

            bookingService.update(b);
            System.out.println("Бронь обновлена");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    static void deleteBooking() {
        System.out.print("ID брони для удаления: ");
        Integer id = readIntOrNull();
        if (id == null) return;
        try {
            bookingService.delete(id);
            System.out.println("Удалено");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    //Поиск / Фильтр / Сортировка

    static void searchMenu() {
        System.out.println("1. По названию лекарства");
        System.out.println("2. По имени пользователя");
        System.out.print("Выбор: ");
        Integer c = readIntOrNull();
        if (c == null) return;
        if (c == 1) {
            System.out.print("Часть названия: ");
            String s = sc.nextLine();
            printList(bookingService.searchByMedicine(s));
        } else if (c == 2) {
            System.out.print("Часть имени: ");
            String s = sc.nextLine();
            printList(bookingService.searchByUserName(s));
        } else {
            System.out.println("Неверный пункт");
        }
    }

    static void filterMenu() {
        System.out.println("1. По статусу");
        System.out.println("2. По ID пользователя");
        System.out.print("Выбор: ");
        Integer c = readIntOrNull();
        if (c == null) return;
        if (c == 1) {
            for (BookingStatus s : BookingStatus.values())
                System.out.println("  " + s.name() + " - " + s.getTitle());
            System.out.print("Статус: ");
            String st = sc.nextLine();
            BookingStatus bs = BookingStatus.fromTitle(st);
            if (bs == null) { System.out.println("Ошибка: некорректный статус"); return; }
            printList(bookingService.filterByStatus(bs));
        } else if (c == 2) {
            System.out.print("ID пользователя: ");
            Integer uid = readIntOrNull();
            if (uid == null) return;
            printList(bookingService.filterByUserId(uid));
        } else {
            System.out.println("Неверный пункт");
        }
    }

    static void sortMenu() {
        System.out.println("1. По дате создания");
        System.out.println("2. По статусу");
        System.out.print("Выбор: ");
        Integer c = readIntOrNull();
        if (c == null) return;
        if (c == 1) printList(bookingService.sortByDate());
        else if (c == 2) printList(bookingService.sortByStatus());
        else System.out.println("Неверный пункт");
    }

    //статистика

    static void statistics() {
        System.out.println("\n=== Статистика ===");
        System.out.println("Всего пользователей: " + userService.findAll().size());
        System.out.println("Всего броней:       " + bookingService.findAll().size());
        for (BookingStatus s : BookingStatus.values()) {
            System.out.println(s.getTitle() + ": " + bookingService.countByStatus(s));
        }
    }

    // Пользователи

    static void addUser() {
        System.out.print("Телефон: ");
        String phone = sc.nextLine();
        System.out.print("Имя: ");
        String name = sc.nextLine();
        User u = new User();
        u.setPhone(phone);
        u.setName(name);
        try {
            userService.create(u);
            System.out.println("Пользователь создан с ID=" + u.getId());
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    // Ввод / Вывод

    static Integer readIntOrNull() {
        String s = sc.nextLine().trim();
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должен быть целым числом.");
            return null;
        }
    }

    static void printList(List<Booking> list) {
        if (list.isEmpty()) { System.out.println("(пусто)"); return; }
        for (Booking b : list) System.out.println(b);
    }

    static void printUsers(List<User> list) {
        if (list.isEmpty()) { System.out.println("(пусто)"); return; }
        for (User u : list) System.out.println(u);
    }
}