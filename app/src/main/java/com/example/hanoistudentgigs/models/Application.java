package com.example.hanoistudentgigs.models;

import com.google.firebase.Timestamp;

public class Application {
    private String id;
    private String jobId;
    private String studentUid;
    private String cvUrl;
    private String status;
    private Timestamp appliedDate;  // ⚠️ đúng kiểu

    public Application() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getStudentUid() { return studentUid; }
    public void setStudentUid(String studentUid) { this.studentUid = studentUid; }

    public String getCvUrl() { return cvUrl; }
    public void setCvUrl(String cvUrl) { this.cvUrl = cvUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getAppliedDate() { return appliedDate; }
    public void setAppliedDate(Timestamp appliedDate) { this.appliedDate = appliedDate; }
}
