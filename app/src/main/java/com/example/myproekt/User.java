package com.example.myproekt;

public class User {
    private int id;
    private String name;
    private String login;
    private String phone;
    private int roleId;

    public User(int id, String name, String login, String phone, int roleId) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.phone = phone;
        this.roleId = roleId;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public String getLogin() { return login; }
    public String getPhone() { return phone; }
    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }
}