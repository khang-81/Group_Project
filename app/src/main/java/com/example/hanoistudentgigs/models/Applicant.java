package com.example.hanoistudentgigs.models;

public class Applicant {
    public String name;
    public String job;
    public String salary;
    public String city;

    public Applicant() {}

    public Applicant(String name, String job, String salary, String city) {
        this.name = name;
        this.job = job;
        this.salary = salary;
        this.city = city;
    }
}