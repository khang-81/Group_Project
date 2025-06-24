package com.example.hanoistudentgigs.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.adapters.JobManageAdapter;
import com.example.hanoistudentgigs.models.Job;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.Arrays;

public class QLTinActivity extends AppCompatActivity {
    private RecyclerView recyclerViewActive, recyclerViewInactive;
    private JobManageAdapter activeAdapter, inactiveAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qltin);

        recyclerViewActive = findViewById(R.id.recyclerViewActive);
        recyclerViewInactive = findViewById(R.id.recyclerViewInactive);

        recyclerViewActive.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewInactive.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String uid;
        try {
            uid = user.getUid();
        } catch (Exception e) {
            Toast.makeText(this, "Không lấy được thông tin người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            // Clear adapter trước khi gán mới
           // recyclerViewActive.setAdapter(null);
            // recyclerViewInactive.setAdapter(null);

            // ✅ Query 1: Đang tuyển
            Query activeQuery = db.collection("jobs")
                    .whereEqualTo("status", "Đang tuyển")
                    .orderBy("createdAt.timestamp", Query.Direction.DESCENDING);

            FirestoreRecyclerOptions<Job> activeOptions = new FirestoreRecyclerOptions.Builder<Job>()
                    .setQuery(activeQuery, Job.class)
                    .setLifecycleOwner(this)
                    .build();

            activeAdapter = new JobManageAdapter(activeOptions, this);
            recyclerViewActive.setAdapter(activeAdapter);

            // ✅ Query 2: Đã tuyển hoặc Đã đóng
            Query inactiveQuery = db.collection("jobs")
                    .whereIn("status", Arrays.asList("Đã tuyển", "Đã đóng"))
                    .orderBy("createdAt.timestamp", Query.Direction.DESCENDING);

            FirestoreRecyclerOptions<Job> inactiveOptions = new FirestoreRecyclerOptions.Builder<Job>()
                    .setQuery(inactiveQuery, Job.class)
                    .setLifecycleOwner(this)
                    .build();

            inactiveAdapter = new JobManageAdapter(inactiveOptions, this);
            recyclerViewInactive.setAdapter(inactiveAdapter);

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi tải dữ liệu", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
