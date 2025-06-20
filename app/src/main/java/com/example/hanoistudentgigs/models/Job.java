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
    private List<String> requiredSkills; // Các kỹ năng yêu cầu cho công việc
    private boolean isApproved = false;
    private boolean isFeatured;
    private String status;

    private List<String> searchKeywords; // Bổ sung: Mảng chứa các từ khóa để tìm kiếm
    private long minSalary; // Bổ sung: Số lương tối thiểu để lọc và sắp xếp
    private String postedDate;




    // Constructor rỗng
    public Job() {}

    // --- Getters và Setters ---
    // Getter và Setter cho isFeatured
    public boolean getIsFeatured() { // Hoặc isFeatured() theo quy ước Boolean
        return isFeatured;
    }

    public void setIsFeatured(boolean featured) {
        isFeatured = featured;
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCompanyLogoUrl() { return companyLogoUrl; }
    public void setCompanyLogoUrl(String companyLogoUrl) { this.companyLogoUrl = companyLogoUrl; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public String getSalaryDescription() { return salaryDescription; }
    public void setSalaryDescription(String salaryDescription) { this.salaryDescription = salaryDescription; }

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

    public boolean getIsApproved() { return isApproved; }
    public void setApproved(boolean approved) { isApproved = approved; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }


    public Map<String, Object> getCreatedAt() { return createdAt; }
    public void setCreatedAt(Map<String, Object> createdAt) { this.createdAt = createdAt; }

    public List<String> getSearchKeywords() { return searchKeywords; }
    public void setSearchKeywords(List<String> searchKeywords) { this.searchKeywords = searchKeywords; }

    public long getMinSalary() { return minSalary; }
    public void setMinSalary(long minSalary) { this.minSalary = minSalary; }
    public String getPostedDate() {
        return postedDate;
    }
    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }
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
                Objects.equals(locationName, job.locationName) &&
                Objects.equals(salaryDescription, job.salaryDescription) &&
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
        return Objects.hash(id, title, companyName, companyLogoUrl, locationName, salaryDescription, description, requirements, employerUid, jobType, categoryName, requiredSkills, isApproved, status, searchKeywords, minSalary, createdAt);
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
                ", status='" + status + '\'' +
                ", searchKeywords=" + searchKeywords +
                ", minSalary=" + minSalary +
                ", createdAt=" + createdAt +
                '}';
    }

}

