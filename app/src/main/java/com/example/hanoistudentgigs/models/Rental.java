package com.example.hanoistudentgigs.models;

public class Rental {
    private String id;
    private String name;

    public Rental() {}
    public Rental(String name) { this.name = name; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
} 