package com.example.hanoistudentgigs.models;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

public class Job {
    private String id;
    private String title;
    private String companyName;
    private Map<String, Object> createdAt;
    private String companyLogoUrl; // Link ảnh logo công ty
    private String locationName;
    private String salaryDescription;
    private String description;
    private String requirements;
    private String employerUid;
    private String jobType; // PartTime, Freelance, Internship
    private String categoryName;

    private List<String> requiredSkills;
    private boolean isApproved;
    private boolean isFeatured;

    private String status;


    private List<String> searchKeywords;
    private long minSalary;
    private String postedDate;

    private String contact;

    private String fireStoreId;

    // Constructor rỗng bắt buộc cho Firestore
    public Job() {
    }


    // 🔸 Firestore cần constructor rỗng
   // public Job(String contact) {
     //   this.contact = contact;
   // }

    // Getter & Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }


    public Map<String, Object> getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Map<String, Object> createdAt) {
        this.createdAt = createdAt;
    }

    public String getCompanyLogoUrl() {
        return companyLogoUrl;
    }


    public void setCompanyLogoUrl(String companyLogoUrl) {
        this.companyLogoUrl = companyLogoUrl;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getSalaryDescription() {
        return salaryDescription;
    }

    public void setSalaryDescription(String salaryDescription) {
        this.salaryDescription = salaryDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }


    public String getEmployerUid() {
        return employerUid;
    }

    public void setEmployerUid(String employerUid) {
        this.employerUid = employerUid;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        this.isApproved = approved;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        this.isFeatured = featured;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<String> getSearchKeywords() {
        return searchKeywords;
    }

    public void setSearchKeywords(List<String> searchKeywords) {
        this.searchKeywords = searchKeywords;
    }

    public long getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(long minSalary) {
        this.minSalary = minSalary;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getFireStoreId() {
        return fireStoreId;
    }

    public void setFireStoreId(String fireStoreId) {
        this.fireStoreId = fireStoreId;

    }

    // --- Override toString() ---
    @Override
    public String toString() {
        return "Job{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", companyName='" + companyName + '\'' +
                ", companyLogoUrl='" + companyLogoUrl + '\'' +
                ", location='" + locationName + '\'' +
                ", salary='" + salaryDescription + '\'' +
                ", description='" + description + '\'' +
                ", requirements='" + requirements + '\'' +
                ", employerUid='" + employerUid + '\'' +
                ", jobType='" + jobType + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", requiredSkills=" + requiredSkills +
                ", isApproved=" + isApproved +
                ", isFeatured=" + isFeatured +

                ", status='" + status + '\'' +
                ", searchKeywords=" + searchKeywords +
                ", minSalary=" + minSalary +
                ", postedDate='" + postedDate + '\'' +
                '}';
    }
}
