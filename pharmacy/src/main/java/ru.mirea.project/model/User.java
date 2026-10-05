package ru.mirea.project.model;

public class User {
    private int id;
    private String phone;
    private String name;

    public User() {}

    public User(int id, String phone, String name) {
        this.id = id;
        this.phone = phone;
        this.name = name;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return "User{id=" + id + ", phone='" + phone + "', name='" + name + "'}";
    }
}