package com.example.hanoistudentgigs.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.models.Application;
import com.example.hanoistudentgigs.utils.Constants;
import com.squareup.picasso.Picasso;

// Không cần import java.util.Date nếu Application model không còn @ServerTimestamp
// import java.util.Date;

public class JobDetailActivity extends AppCompatActivity {
    private TextView textViewDetailJobTitle, textViewDetailCompanyName, textViewDetailDescription, textViewDetailRequirements;
    private Button buttonApplyNow;
    private FirebaseFirestore db;
    private String jobId;
    private FirebaseAuth mAuth;
    private ImageView imageViewJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);
        imageViewJob = findViewById(R.id.imageViewDetailCompanyLogo); // Sử dụng đúng ID từ XML

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút back
            getSupportActionBar().setTitle(""); // Bỏ tiêu đề mặc định của Toolbar nếu CollapsingToolbarLayout quản lý
        }
        textViewDetailJobTitle = findViewById(R.id.textViewDetailJobTitle);
        textViewDetailCompanyName = findViewById(R.id.textViewDetailCompanyName);
        textViewDetailDescription = findViewById(R.id.textViewDetailDescription);
        textViewDetailRequirements = findViewById(R.id.textViewDetailRequirements);
        buttonApplyNow = findViewById(R.id.buttonApplyNow);

        // Nhận jobId từ Intent đã gửi từ JobAdapter
        jobId = getIntent().getStringExtra("JOB_ID");

        if (jobId != null) {
            loadJobDetails();
            checkIfAlreadyApplied(); // Kiểm tra xem người dùng đã ứng tuyển chưa
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin công việc.", Toast.LENGTH_SHORT).show();
            finish();
        }

        buttonApplyNow.setOnClickListener(v -> applyForJob());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Điều này sẽ đưa người dùng quay lại màn hình trước đó
        return true;
    }

    private void loadJobDetails() {
        db.collection("jobs").document(jobId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // Ánh xạ dữ liệu từ Firestore vào các TextView
                            textViewDetailJobTitle.setText(document.getString("title"));
                            textViewDetailCompanyName.setText(document.getString("companyName"));
                            textViewDetailDescription.setText(document.getString("description"));
                            textViewDetailRequirements.setText(document.getString("requirements"));

                            // Lấy URL ảnh từ DocumentSnapshot và tải bằng Picasso
                            String imageUrl = document.getString("companyLogoUrl"); // Lấy URL từ trường "imageUrl" trong Firestore
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Picasso.get().load(imageUrl)
                                        .placeholder(R.drawable.ic_business_24) // Ảnh placeholder khi đang tải
                                        .error(R.drawable.ic_error_24)       // Ảnh lỗi nếu tải thất bại
                                        .into(imageViewJob); // Tải vào imageViewJob
                                Log.d("JobDetailActivity", "URL ảnh công việc: " + imageUrl);
                            } else {
                                imageViewJob.setImageResource(R.drawable.ic_business_24); // Hiển thị ảnh mặc định
                                Log.w("JobDetailActivity", "URL ảnh công việc rỗng hoặc null cho Job ID: " + jobId);
                            }

                        } else {
                            Toast.makeText(this, "Không tìm thấy dữ liệu công việc.", Toast.LENGTH_SHORT).show();
                            Log.w("JobDetailActivity", "Document không tồn tại cho Job ID: " + jobId);
                        }
                    } else {
                        Toast.makeText(this, "Lỗi khi tải dữ liệu công việc.", Toast.LENGTH_SHORT).show();
                        Log.e("JobDetailActivity", "Lỗi khi tải dữ liệu công việc: ", task.getException());
                    }
                });
    }

    // Thêm phương thức này để kiểm tra nếu người dùng đã ứng tuyển rồi
    private void checkIfAlreadyApplied() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Không có người dùng đăng nhập, không cần kiểm tra
            return;
        }

        db.collection(Constants.APPLICATIONS_COLLECTION)
                .whereEqualTo("jobId", jobId)
                .whereEqualTo("studentUid", currentUser.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Người dùng đã tìm thấy đơn ứng tuyển cho job này
                        buttonApplyNow.setEnabled(false);
                        buttonApplyNow.setText("Đã ứng tuyển");
                    } else {
                        // Chưa ứng tuyển
                        buttonApplyNow.setEnabled(true);
                        buttonApplyNow.setText("Ứng tuyển ngay");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("JobDetailActivity", "Lỗi khi kiểm tra đơn ứng tuyển: " + e.getMessage());
                    // Vẫn để nút ứng tuyển hoạt động trong trường hợp lỗi
                    buttonApplyNow.setEnabled(true);
                    buttonApplyNow.setText("Ứng tuyển ngay");
                });
    }

    private void applyForJob() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để ứng tuyển.", Toast.LENGTH_SHORT).show();
            return;
        }

        // BƯỚC 1: Lấy VAI TRÒ của người dùng
        db.collection(Constants.USERS_COLLECTION).document(currentUser.getUid()).get()
                .addOnSuccessListener(userDocSnapshot -> {
                    if (userDocSnapshot.exists()) {
                        String userRole = userDocSnapshot.getString("role");
                        if (!Constants.ROLE_STUDENT.equals(userRole)) {
                            Toast.makeText(JobDetailActivity.this, "Bạn phải là sinh viên để ứng tuyển.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // BƯỚC 2: Nếu là sinh viên, lấy thông tin chi tiết từ STUDENTS_COLLECTION
                        db.collection(Constants.STUDENTS_COLLECTION).document(currentUser.getUid()).get()
                                .addOnSuccessListener(studentProfileSnapshot -> {
                                    if (studentProfileSnapshot.exists()) {
                                        String studentName = studentProfileSnapshot.getString("fullName");
                                        String cvFileName = studentProfileSnapshot.getString("cvFileName");
                                        // BỎ DÒNG LẤY cvUrl NÀY ĐI
                                        // String cvUrl = studentProfileSnapshot.getString("cvUrl");

                                        if (cvFileName == null || cvFileName.isEmpty()) {
                                            Toast.makeText(JobDetailActivity.this, "Vui lòng tải lên CV trong hồ sơ của bạn trước khi ứng tuyển.", Toast.LENGTH_LONG).show();
                                            return;
                                        }

                                        // BƯỚC 3: Tạo và lưu đơn ứng tuyển
                                        String applicationId = db.collection(Constants.APPLICATIONS_COLLECTION).document().getId();

                                        Application application = new Application();
                                        application.setId(applicationId);
                                        application.setJobId(jobId); // Vẫn cần jobId để biết đơn này ứng tuyển cho job nào
                                        application.setStudentUid(currentUser.getUid());
                                        application.setStudentName(studentName);
                                        // BỎ DÒNG GÁN cvUrl NÀY ĐI
                                        // application.setCvUrl(cvUrl);

                                        // THÊM DÒNG GÁN cvFileName VÀO ĐÂY
                                        application.setCvFileName(cvFileName); // Giả sử bạn có setter setCvFileName trong model Application

                                        application.setStatus("Submitted");

                                        // Lưu trực tiếp vào collection APPLICATIONS_COLLECTION cấp cao nhất
                                        db.collection(Constants.APPLICATIONS_COLLECTION).document(applicationId)
                                                .set(application)
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(JobDetailActivity.this, "Ứng tuyển thành công!", Toast.LENGTH_SHORT).show();
                                                    buttonApplyNow.setEnabled(false);
                                                    buttonApplyNow.setText("Đã ứng tuyển");
                                                    checkIfAlreadyApplied();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("JobDetailActivity", "Lỗi khi lưu đơn ứng tuyển: " + e.getMessage(), e);
                                                    Toast.makeText(JobDetailActivity.this, "Ứng tuyển thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        Toast.makeText(JobDetailActivity.this, "Không tìm thấy hồ sơ sinh viên của bạn. Vui lòng cập nhật hồ sơ.", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("JobDetailActivity", "Lỗi khi tải hồ sơ sinh viên: " + e.getMessage(), e);
                                    Toast.makeText(JobDetailActivity.this, "Lỗi khi tải hồ sơ sinh viên.", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(JobDetailActivity.this, "Không tìm thấy thông tin vai trò người dùng.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("JobDetailActivity", "Lỗi khi kiểm tra vai trò người dùng: " + e.getMessage(), e);
                    Toast.makeText(JobDetailActivity.this, "Lỗi khi lấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
                });
    }
}