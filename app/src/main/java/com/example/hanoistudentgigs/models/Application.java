package com.example.hanoistudentgigs.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Application {
    private String id;
    private String jobId;
    private String studentUid;
    private String studentName;
    private String status;
    private String cvFileName; // Khai báo trường cvFileName
    @ServerTimestamp
    private Date appliedDate;

    // Constructor rỗng (BẮT BUỘC cho Firestore)
    public Application() {}

    // Constructor đầy đủ (đã sửa lỗi)
    // Đã loại bỏ 'cvUrl' và thêm 'cvFileName' vào danh sách tham số
    public Application(String id, String jobId, String studentUid, String studentName, String status, String cvFileName, Date appliedDate) {
        this.id = id;
        this.jobId = jobId;
        this.studentUid = studentUid;
        this.studentName = studentName;
        this.status = status;
        this.cvFileName = cvFileName; // Gán đúng tham số cvFileName
        this.appliedDate = appliedDate;
    }

    // Getter và Setter cho từng trường
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getStudentUid() {
        return studentUid;
    }

    public void setStudentUid(String studentUid) {
        this.studentUid = studentUid;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    // Getter cho cvFileName
    public String getCvFileName() {
        return cvFileName;
    }

    // Setter cho cvFileName
    public void setCvFileName(String cvFileName) {
        this.cvFileName = cvFileName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(Date appliedDate) {
        this.appliedDate = appliedDate;
    }
}