package com.example.hanoistudentgigs.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import java.util.List;

public class Job {
    // Các trường đã có từ JSON
    private String id, title, companyName, location, salary, description, requirements, employerUid, status;
    private boolean isApproved = false;
    @ServerTimestamp
    private Date createdAt;

    // Các trường bổ sung
    private String companyLogoUrl;
    private String jobType;
    private String categoryName;
    private List<String> searchKeywords;  // Từ khóa tìm kiếm
    private int minSalary;  // Mức lương tối thiểu
    private List<String> requiredSkills;  // Các kỹ năng yêu cầu

    // Constructor rỗng cần thiết cho Firestore
    public Job() {}

    // --- Getters và Setters cho tất cả các trường ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }

    public String getEmployerUid() { return employerUid; }
    public void setEmployerUid(String employerUid) { this.employerUid = employerUid; }

    public boolean isApproved() { return isApproved; }
    public void setApproved(boolean approved) { isApproved = approved; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getCompanyLogoUrl() { return companyLogoUrl; }
    public void setCompanyLogoUrl(String companyLogoUrl) { this.companyLogoUrl = companyLogoUrl; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public List<String> getSearchKeywords() { return searchKeywords; }
    public void setSearchKeywords(List<String> searchKeywords) { this.searchKeywords = searchKeywords; }

    public int getMinSalary() { return minSalary; }
    public void setMinSalary(int minSalary) { this.minSalary = minSalary; }

    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
