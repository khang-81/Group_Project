//package com.example.hanoistudentgigs.activities;
//
//
//import android.content.Intent;
//
//import android.os.Bundle;
//
//import android.util.Log;
//import android.widget.EditText;
//
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//
//import com.example.hanoistudentgigs.R;
//
//import com.example.hanoistudentgigs.adapters.ApplicantAdapter;
//
//import com.example.hanoistudentgigs.adapters.JobAdapter;
//
//import com.example.hanoistudentgigs.models.Application;
//
//import com.example.hanoistudentgigs.models.Job;
//
//import com.firebase.ui.firestore.FirestoreRecyclerOptions;
//
//import com.google.firebase.Timestamp;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import com.google.firebase.firestore.Query;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Objects;
//
//
//public class TrangChuActivity extends AppCompatActivity {
//
//
//    private RecyclerView recyclerViewJobs, recyclerViewApplicants;
//
//    private JobAdapter jobAdapter;
//
//    private ApplicantAdapter applicantAdapter;
//
//    private FirebaseFirestore db;
//
//
//    private EditText newBlogEditText;
//
//
//    @Override
//
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_trangchu);
//        db = FirebaseFirestore.getInstance();
//        recyclerViewJobs = findViewById(R.id.recyclerViewJobs);
//        recyclerViewApplicants = findViewById(R.id.recyclerViewApplicants);
//        newBlogEditText = findViewById(R.id.editTextPostSearch);
////        pushFakeJobToFirebase();
////        pushFakeDataToFirebase();
//        setupJobsRecyclerView();
//        setupApplicantsRecyclerView();
//        findViewById(R.id.btnSeeAllApplicants).setOnClickListener(v -> {
//            startActivity(new Intent(this, DsUngVienActivity.class));
//        });
//        newBlogEditText.setOnClickListener(v -> {
//            startActivity(new Intent(this, DangTinActivity.class));
//        });
//        findViewById(R.id.btnSeeAllJobs).setOnClickListener(v -> {
//            startActivity(new Intent(this, QLTinActivity.class));
//        });
//    }
//    private void setupJobsRecyclerView() {
//
//// Truy vấn dữ liệu từ collection "jobs", sắp xếp theo thời gian tạo
//
//        Query query = db.collection("jobs")
//
//                .orderBy("createdAt", Query.Direction.DESCENDING); // Sửa postedDate thành createdAt
//
//
//        FirestoreRecyclerOptions<Job> options = new FirestoreRecyclerOptions.Builder<Job>()
//                .setQuery(query, Job.class)
//                .setLifecycleOwner(this)
//                .build();
//
//
//        jobAdapter = new JobAdapter(options, this);
//
//        recyclerViewJobs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//
//        recyclerViewJobs.setAdapter(jobAdapter);
//
//    }
//
//
//    private void setupApplicantsRecyclerView() {
//        Query query = db.collection("applications");
//
//        FirestoreRecyclerOptions<Application> options = new FirestoreRecyclerOptions.Builder<Application>()
//                .setQuery(query, Application.class)
//                .setLifecycleOwner(this)
//                .build();
//
//        applicantAdapter = new ApplicantAdapter(options, this);
//        recyclerViewApplicants.setLayoutManager(new LinearLayoutManager(this));
//        recyclerViewApplicants.setAdapter(applicantAdapter);
//    }
//
//    private void pushFakeDataToFirebase() {
//        Log.d("PUSH_DATA", "Push giả vào Firestore");
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        // Applications
//        Map<String, Object> app1 = new HashMap<>();
//        app1.put("id", "app_1");
//        app1.put("jobId", "job_test_1");
//        app1.put("studentUid", "std1");
//        app1.put("cvUrl", "https://example.com/cv1.pdf");
//        app1.put("status", "Đang chờ");
//        app1.put("studentName", "Lê Tuyết Nhung");
//        app1.put("appliedDate", Timestamp.now());
//
//        Map<String, Object> app2 = new HashMap<>();
//        app2.put("id", "app_2");
//        app2.put("jobId", "job_test_1");
//        app2.put("studentUid", "std2");
//        app2.put("cvUrl", "https://example.com/cv2.pdf");
//        app2.put("status", "Đã duyệt");
//        app1.put("studentName", "Lê Thái Hiếu");
//        app2.put("appliedDate", Timestamp.now());
//
//        db.collection("applications").document("app_1")
//                .set(app1)
//                .addOnSuccessListener(aVoid -> Log.d("PUSH_DATA", "Thêm app_1 thành công"))
//                .addOnFailureListener(e -> Log.e("PUSH_DATA", "Lỗi thêm app_1", e));
//
//        db.collection("applications").document("app_2")
//                .set(app2)
//                .addOnSuccessListener(aVoid -> Log.d("PUSH_DATA", "Thêm app_2 thành công"))
//                .addOnFailureListener(e -> Log.e("PUSH_DATA", "Lỗi thêm app_2", e));
//    }
//
//    private void pushFakeJobToFirebase() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//
//        String employerUid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
//        FirebaseUser currentUser = auth.getCurrentUser();
//        if (currentUser == null) {
//            Log.e("PUSH_DATA", "Chưa đăng nhập, không thể thêm dữ liệu");
//            return;
//        }
//        String uid = currentUser.getUid();
//        Log.d("PUSH_DATA", "UID hiện tại: " + uid);
//
//        Job job = new Job();
//        job.setId("job_" +
//                "test_1");
//        job.setTitle("Nhân viên bán hàng");
//        job.setCompanyName("Công ty TNHH ABC");
//        job.setCompanyLogoUrl("https://example.com/logo.png");
//        job.setLocationName("Hà Nội");
//        job.setSalaryDescription("5 triệu - 7 triệu");
//        job.setDescription("Công việc part-time, linh hoạt thời gian.");
//        job.setRequirements("Chăm chỉ, giao tiếp tốt");
//        job.setEmployerUid(employerUid); // 🔥 Cực quan trọng
//        job.setJobType("Part-time");
//        job.setCategoryName("Bán hàng");
//        job.setApproved(false);
//        job.setFeatured(false);
//        job.setStatus("Đang tuyển");
//        job.setActive(true);
//        job.setPostedDate("2025-06-22");
//        job.setMinSalary(5000000);
//        job.setSearchKeywords(Arrays.asList("nhân viên", "bán hàng", "part-time"));
//
//        Map<String, Object> createdAt = new HashMap<>();
//        createdAt.put("timestamp", Timestamp.now());
//        job.setCreatedAt(createdAt);
//
//        db.collection("jobs").document(job.getId())
//                .set(job)
//                .addOnSuccessListener(aVoid -> Log.d("FAKE_JOB", "Thêm job thành công"))
//                .addOnFailureListener(e -> Log.e("FAKE_JOB", "Lỗi thêm job", e));
//    }
//
//    @Override
//
//    protected void onStart() {
//
//        super.onStart();
//
//    }
//
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        setupJobsRecyclerView();
//        setupApplicantsRecyclerView();
//    }
//}