package com.example.hanoistudentgigs.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Job {
    private String id;
    private String title;
    private String companyName;
    private String companyLogoUrl; // Link ảnh logo công ty
    private String location;
    private String salary;
    private String description;
    private String requirements;
    private String employerUid;
    private String jobType; // PartTime, Freelance, Internship
    private String categoryName;
    private List<String> requiredSkills; // Các kỹ năng yêu cầu cho công việc
    private boolean isApproved = false;
    private String status;

    private List<String> searchKeywords; // Bổ sung: Mảng chứa các từ khóa để tìm kiếm
    private long minSalary; // Bổ sung: Số lương tối thiểu để lọc và sắp xếp

    @ServerTimestamp
    private Date createdAt;

    // Constructor rỗng
    public Job() {}

    // --- Getters và Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCompanyLogoUrl() { return companyLogoUrl; }
    public void setCompanyLogoUrl(String companyLogoUrl) { this.companyLogoUrl = companyLogoUrl; }

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

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }

    public boolean isApproved() { return isApproved; }
    public void setApproved(boolean approved) { isApproved = approved; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public List<String> getSearchKeywords() { return searchKeywords; }
    public void setSearchKeywords(List<String> searchKeywords) { this.searchKeywords = searchKeywords; }

    public long getMinSalary() { return minSalary; }
    public void setMinSalary(long minSalary) { this.minSalary = minSalary; }

    // --- Override equals() và hashCode() ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return isApproved == job.isApproved &&
                minSalary == job.minSalary &&
                Objects.equals(id, job.id) &&
                Objects.equals(title, job.title) &&
                Objects.equals(companyName, job.companyName) &&
                Objects.equals(companyLogoUrl, job.companyLogoUrl) &&
                Objects.equals(location, job.location) &&
                Objects.equals(salary, job.salary) &&
                Objects.equals(description, job.description) &&
                Objects.equals(requirements, job.requirements) &&
                Objects.equals(employerUid, job.employerUid) &&
                Objects.equals(jobType, job.jobType) &&
                Objects.equals(categoryName, job.categoryName) &&
                Objects.equals(requiredSkills, job.requiredSkills) &&
                Objects.equals(status, job.status) &&
                Objects.equals(searchKeywords, job.searchKeywords) &&
                Objects.equals(createdAt, job.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, companyName, companyLogoUrl, location, salary, description, requirements, employerUid, jobType, categoryName, requiredSkills, isApproved, status, searchKeywords, minSalary, createdAt);
    }

    // --- Override toString() ---
    @Override
    public String toString() {
        return "Job{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", companyName='" + companyName + '\'' +
                ", companyLogoUrl='" + companyLogoUrl + '\'' +
                ", location='" + location + '\'' +
                ", salary='" + salary + '\'' +
                ", description='" + description + '\'' +
                ", requirements='" + requirements + '\'' +
                ", employerUid='" + employerUid + '\'' +
                ", jobType='" + jobType + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", requiredSkills=" + requiredSkills +
                ", isApproved=" + isApproved +
                ", status='" + status + '\'' +
                ", searchKeywords=" + searchKeywords +
                ", minSalary=" + minSalary +
                ", createdAt=" + createdAt +
                '}';
    }
}
