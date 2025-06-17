package com.example.hanoistudentgigs.fragments.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.activities.SearchActivity;
import com.example.hanoistudentgigs.adapters.FeaturedJobAdapter;
import com.example.hanoistudentgigs.adapters.PopularJobAdapter;
import com.example.hanoistudentgigs.models.Job;
import com.example.hanoistudentgigs.utils.Constants;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class StudentHomeFragment extends Fragment {
    private RecyclerView recyclerViewFeaturedJobs, recyclerViewPopularJobs;
    private FeaturedJobAdapter featuredAdapter;
    private PopularJobAdapter popularAdapter;
    private TextView textViewUserName;
    private View searchBar; // Sử dụng View để bắt sự kiện click
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_home, container, false);

        // Ánh xạ các views
        textViewUserName = view.findViewById(R.id.textViewCurrentUserName);
        recyclerViewFeaturedJobs = view.findViewById(R.id.recyclerViewFeaturedJobs);
        recyclerViewPopularJobs = view.findViewById(R.id.recyclerViewPopularJobs);
        searchBar = view.findViewById(R.id.searchBar); // Ánh xạ CardView làm thanh tìm kiếm

        // Thiết lập sự kiện
        searchBar.setOnClickListener(v -> openSearchActivity());

        loadUserName();
        setupRecyclerViews();

        return view;
    }

    private void openSearchActivity() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        }
    }

    private void loadUserName() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && getContext() != null) {
            db.collection(Constants.USERS_COLLECTION).document(currentUser.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (getContext() != null && documentSnapshot.exists()) {
                            String fullName = documentSnapshot.getString("fullName");
                            if (fullName != null && !fullName.isEmpty()) {
                                // Chỉ lấy tên, ví dụ "Nguyễn Văn An" -> "An"
                                String[] names = fullName.split(" ");
                                textViewUserName.setText("Hi, " + names[names.length - 1] + " 👋");
                            } else {
                                textViewUserName.setText("Discover Jobs 🔥");
                            }
                        }
                    });
        }
    }

    private void setupRecyclerViews() {
        // Query cho các công việc nổi bật (5 công việc mới nhất)
        Query featuredQuery = db.collection(Constants.JOBS_COLLECTION)
                .whereEqualTo("isApproved", true)
                .whereEqualTo("status", "Open")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(5);

        FirestoreRecyclerOptions<Job> featuredOptions = new FirestoreRecyclerOptions.Builder<Job>()
                .setQuery(featuredQuery, Job.class)
                .build();

        featuredAdapter = new FeaturedJobAdapter(featuredOptions, getContext());
        recyclerViewFeaturedJobs.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewFeaturedJobs.setAdapter(featuredAdapter);

        // Query cho các công việc phổ biến (tất cả các công việc còn lại)
        Query popularQuery = db.collection(Constants.JOBS_COLLECTION)
                .whereEqualTo("isApproved", true)
                .whereEqualTo("status", "Open")
                .orderBy("createdAt", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Job> popularOptions = new FirestoreRecyclerOptions.Builder<Job>()
                .setQuery(popularQuery, Job.class)
                .build();

        popularAdapter = new PopularJobAdapter(popularOptions, getContext());
        recyclerViewPopularJobs.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewPopularJobs.setAdapter(popularAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (featuredAdapter != null) featuredAdapter.startListening();
        if (popularAdapter != null) popularAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (featuredAdapter != null) featuredAdapter.stopListening();
        if (popularAdapter != null) popularAdapter.stopListening();
    }
}