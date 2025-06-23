package com.example.hanoistudentgigs.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;
import java.util.List;
import java.util.Objects;

// Lớp này ánh xạ tới một document trong collection 'jobs' trên Firestore
public class Job {

    // --- Khai báo đủ 21 trường ---
    private String id;
    private String title;
    private String description;
    private String requirements;
    private String salaryDescription;
    private long minSalary;
    private String jobType;
    private String categoryName;
    private List<String> requiredSkills;
    private List<String> searchKeywords;
    private String locationName;
    private String companyName;
    private String companyLogoUrl;
    private String employerUid;
    private String contact;
    private Timestamp createdAt;
    private String postedDate;
    private String status;
    private boolean active;
    private boolean approved;
    private boolean featured;

    // Constructor rỗng là bắt buộc cho việc ánh xạ của Firestore
    public Job() {}

    // --- Getters and Setters cho tất cả 21 trường ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }

    public String getSalaryDescription() { return salaryDescription; }
    public void setSalaryDescription(String salaryDescription) { this.salaryDescription = salaryDescription; }

    public long getMinSalary() { return minSalary; }
    public void setMinSalary(long minSalary) { this.minSalary = minSalary; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }

    public List<String> getSearchKeywords() { return searchKeywords; }
    public void setSearchKeywords(List<String> searchKeywords) { this.searchKeywords = searchKeywords; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCompanyLogoUrl() { return companyLogoUrl; }
    public void setCompanyLogoUrl(String companyLogoUrl) { this.companyLogoUrl = companyLogoUrl; }

    public String getEmployerUid() { return employerUid; }
    public void setEmployerUid(String employerUid) { this.employerUid = employerUid; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getPostedDate() { return postedDate; }
    public void setPostedDate(String postedDate) { this.postedDate = postedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    // Sử dụng @PropertyName để ánh xạ chính xác với tên trường trên Firebase
    @PropertyName("approved")
    public boolean isApproved() { return approved; }
    @PropertyName("approved")
    public void setApproved(boolean approved) { this.approved = approved; }

    @PropertyName("featured")
    public boolean isFeatured() { return featured; }
    @PropertyName("featured")
    public void setFeatured(boolean featured) { this.featured = featured; }


    // --- Ghi đè các phương thức tiện ích (đã cập nhật đủ trường) ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return minSalary == job.minSalary &&
                active == job.active &&
                approved == job.approved &&
                featured == job.featured &&
                Objects.equals(id, job.id) &&
                Objects.equals(title, job.title) &&
                Objects.equals(description, job.description) &&
                Objects.equals(requirements, job.requirements) &&
                Objects.equals(salaryDescription, job.salaryDescription) &&
                Objects.equals(jobType, job.jobType) &&
                Objects.equals(categoryName, job.categoryName) &&
                Objects.equals(requiredSkills, job.requiredSkills) &&
                Objects.equals(searchKeywords, job.searchKeywords) &&
                Objects.equals(locationName, job.locationName) &&
                Objects.equals(companyName, job.companyName) &&
                Objects.equals(companyLogoUrl, job.companyLogoUrl) &&
                Objects.equals(employerUid, job.employerUid) &&
                Objects.equals(contact, job.contact) &&
                Objects.equals(createdAt, job.createdAt) &&
                Objects.equals(postedDate, job.postedDate) &&
                Objects.equals(status, job.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, requirements, salaryDescription, minSalary, jobType, categoryName, requiredSkills, searchKeywords, locationName, companyName, companyLogoUrl, employerUid, contact, createdAt, postedDate, status, active, approved, featured);
    }

    @Override
    public String toString() {
        return "Job{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", companyName='" + companyName + '\'' +
                ", status='" + status + '\'' +
                ", active=" + active +
                ", approved=" + approved +
                '}';
    }
}

