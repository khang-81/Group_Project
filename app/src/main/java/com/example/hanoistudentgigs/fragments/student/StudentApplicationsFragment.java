package com.example.hanoistudentgigs.fragments.student;

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
import com.example.hanoistudentgigs.adapters.StudentApplicationAdapter;
import com.example.hanoistudentgigs.models.Application;
import com.example.hanoistudentgigs.utils.Constants;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class StudentApplicationsFragment extends Fragment {
    private RecyclerView recyclerViewMyApplications;
    private StudentApplicationAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_applications, container, false);
        recyclerViewMyApplications = view.findViewById(R.id.recyclerViewMyApplications);
        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {
        if (mAuth.getCurrentUser() == null) return;
        String currentUserId = mAuth.getCurrentUser().getUid();

        // Dùng collectionGroup để tìm kiếm trong tất cả các sub-collection 'applications'
        Query query = db.collectionGroup(Constants.APPLICATIONS_COLLECTION)
                .whereEqualTo("studentUid", currentUserId)
                .orderBy("appliedDate", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Application> options = new FirestoreRecyclerOptions.Builder<Application>()
                .setQuery(query, Application.class)
                .build();

        // FIX: Xóa tham số thứ hai (getContext()) vì constructor không cần nó.
        adapter = new StudentApplicationAdapter(options);
        recyclerViewMyApplications.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMyApplications.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }
}
