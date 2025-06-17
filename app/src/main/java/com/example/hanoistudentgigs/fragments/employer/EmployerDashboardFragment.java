package com.example.hanoistudentgigs.fragments.employer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.activities.PostJobActivity;
import com.example.hanoistudentgigs.adapters.EmployerJobAdapter;
import com.example.hanoistudentgigs.models.Job;
import com.example.hanoistudentgigs.utils.Constants;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class EmployerDashboardFragment extends Fragment {
    private RecyclerView recyclerViewActiveJobs, recyclerViewClosedJobs;
    private EmployerJobAdapter activeJobsAdapter, closedJobsAdapter;
    private FloatingActionButton fabPostJob;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employer_dashboard, container, false);

        recyclerViewActiveJobs = view.findViewById(R.id.recyclerViewActiveJobs);
        recyclerViewClosedJobs = view.findViewById(R.id.recyclerViewClosedJobs);
        fabPostJob = view.findViewById(R.id.fabPostJob);

        fabPostJob.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), PostJobActivity.class));
        });

        setupRecyclerViews();
        return view;
    }

    private void setupRecyclerViews() {
        if (mAuth.getCurrentUser() == null) return;
        String currentUserId = mAuth.getCurrentUser().getUid();

        // Query cho các công việc đang tuyển (status == "Open")
        Query activeQuery = db.collection(Constants.JOBS_COLLECTION)
                .whereEqualTo("employerUid", currentUserId)
                .whereEqualTo("status", "Open")
                .orderBy("createdAt", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Job> activeOptions = new FirestoreRecyclerOptions.Builder<Job>()
                .setQuery(activeQuery, Job.class)
                .build();

        activeJobsAdapter = new EmployerJobAdapter(activeOptions, getContext());
        recyclerViewActiveJobs.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewActiveJobs.setAdapter(activeJobsAdapter);

        // Query cho các công việc đã đóng (status == "Closed")
        Query closedQuery = db.collection(Constants.JOBS_COLLECTION)
                .whereEqualTo("employerUid", currentUserId)
                .whereEqualTo("status", "Closed")
                .orderBy("createdAt", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Job> closedOptions = new FirestoreRecyclerOptions.Builder<Job>()
                .setQuery(closedQuery, Job.class)
                .build();

        closedJobsAdapter = new EmployerJobAdapter(closedOptions, getContext());
        recyclerViewClosedJobs.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewClosedJobs.setAdapter(closedJobsAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (activeJobsAdapter != null) activeJobsAdapter.startListening();
        if (closedJobsAdapter != null) closedJobsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (activeJobsAdapter != null) activeJobsAdapter.stopListening();
        if (closedJobsAdapter != null) closedJobsAdapter.stopListening();
    }
}
