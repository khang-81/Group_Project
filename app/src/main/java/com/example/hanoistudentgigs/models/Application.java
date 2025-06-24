package com.example.hanoistudentgigs.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Application {
    private String id;
    private String jobId;
    private String studentUid;
    private String studentName;

    //    private String cvUrl;

    private String status;
    private String cvFileName;
    @ServerTimestamp
    private Date appliedDate;

    // Constructor rỗng
    public Application() {}

    // Constructor đầy đủ (optional, có thể sử dụng nếu cần khởi tạo đối tượng nhanh chóng)
    public Application(String id, String jobId, String studentUid, String studentName, String cvUrl, String status, Date appliedDate) {
        this.id = id;
        this.jobId = jobId;
        this.studentUid = studentUid;
        this.studentName = studentName;
//        this.cvUrl = cvUrl;
        this.status = status;
        this.appliedDate = appliedDate;
        this.cvFileName = cvFileName;
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


//        return cvUrl;
//    }
//
//    public void setCvUrl(String cvUrl) {
//        this.cvUrl = cvUrl;
//    }
    public String getCvFileName() {
        return cvFileName;
    }


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
