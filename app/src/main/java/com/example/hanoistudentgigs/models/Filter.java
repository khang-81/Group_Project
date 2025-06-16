package com.example.hanoistudentgigs.models;

import java.io.Serializable;

public class Filter implements Serializable {
    private String category;
    private String location;
    private String jobType;

    public Filter() {}

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }
}