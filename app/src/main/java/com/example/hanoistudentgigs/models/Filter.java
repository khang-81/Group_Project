package com.example.hanoistudentgigs.models;

import java.io.Serializable;

public class Filter implements Serializable {
    private String category;
    private String location;
    private String jobType;
    private Long minPostedDateMillis; // <-- THÊM DÒNG NÀY
    private Long maxPostedDateMillis;
    public Filter() {}

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }
    // <-- THÊM GETTER VÀ SETTER CHO minPostedDateMillis -->
    public Long getMinPostedDateMillis() {
        return minPostedDateMillis;
    }

    public void setMinPostedDateMillis(Long minPostedDateMillis) {
        this.minPostedDateMillis = minPostedDateMillis;
    }

    // <-- THÊM GETTER VÀ SETTER CHO maxPostedDateMillis -->
    public Long getMaxPostedDateMillis() {
        return maxPostedDateMillis;
    }

    public void setMaxPostedDateMillis(Long maxPostedDateMillis) {
        this.maxPostedDateMillis = maxPostedDateMillis;
    }
}