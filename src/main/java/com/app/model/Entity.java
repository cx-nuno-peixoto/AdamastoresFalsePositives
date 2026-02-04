package com.app.model;

public class Entity {
    private long id;
    private String name;
    private String email;
    private int status;
    private boolean active;
    private double balance;
    private String description;
    private String type;
    private String category;
    private String uuid;
    private String content;

    public Entity() {}

    public Entity(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getCode() { return (int)(id % 1000); }
    public long getTimestamp() { return System.currentTimeMillis(); }
}

