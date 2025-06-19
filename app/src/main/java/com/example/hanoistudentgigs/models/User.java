package com.example.hanoistudentgigs.models;

public class User {
    private String uid;
    private String fullName;
    private String email;
    private String role;
    private String cvUrl;
    private String companyName;
    private String school;
    private String phone;

    private boolean verified = false;

    // Constructor rỗng
    public User() {}

    // Constructor đầy đủ (optional, có thể sử dụng nếu cần khởi tạo đối tượng nhanh chóng)
    public User(String uid, String fullName, String email, String role, String cvUrl, String companyName, String school, String phone) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.cvUrl = cvUrl;
        this.companyName = companyName;
        this.school = school;
        this.phone = phone;
    }

    // Constructor với 3 tham số (fullName, email, role) để tiện tạo user mẫu
    public User(String fullName, String email, String role) {
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    // Getter và Setter cho từng trường
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCvUrl() {
        return cvUrl;
    }

    public void setCvUrl(String cvUrl) {
        this.cvUrl = cvUrl;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
}
