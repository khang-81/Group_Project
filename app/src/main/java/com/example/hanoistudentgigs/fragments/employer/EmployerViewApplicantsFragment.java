package com.example.hanoistudentgigs.fragments.employer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.adapters.ApplicantAdapter;
import com.example.hanoistudentgigs.models.Application;
import com.example.hanoistudentgigs.utils.Constants;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class EmployerViewApplicantsFragment extends Fragment {
    private RecyclerView recyclerViewApplicants;
    private TextView textViewNoApplicants; // Dòng này giờ sẽ không báo lỗi
    private ApplicantAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String jobId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Nạp đúng file layout
        View view = inflater.inflate(R.layout.fragment_view_applicants, container, false);

        if (getArguments() != null) {
            jobId = getArguments().getString("JOB_ID");
        }

        if (jobId == null || jobId.isEmpty()) {
            Toast.makeText(getContext(), "Lỗi: Không tìm thấy ID công việc.", Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
            return view;
        }

        recyclerViewApplicants = view.findViewById(R.id.recyclerViewApplicants);
        textViewNoApplicants = view.findViewById(R.id.textViewNoApplicants); // Ánh xạ ID từ XML
        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {
        Query query = db.collection(Constants.JOBS_COLLECTION).document(jobId)
                .collection(Constants.APPLICATIONS_COLLECTION)
                .orderBy("appliedDate", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Application> options = new FirestoreRecyclerOptions.Builder<Application>()
                .setQuery(query, Application.class)
                .build();

        adapter = new ApplicantAdapter(options, getContext());

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                updateEmptyViewVisibility();
            }
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                updateEmptyViewVisibility();
            }
            void updateEmptyViewVisibility() {
                if (adapter.getItemCount() == 0) {
                    recyclerViewApplicants.setVisibility(View.GONE);
                    textViewNoApplicants.setVisibility(View.VISIBLE);
                } else {
                    recyclerViewApplicants.setVisibility(View.VISIBLE);
                    textViewNoApplicants.setVisibility(View.GONE);
                }
            }
        });

        recyclerViewApplicants.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewApplicants.setAdapter(adapter);
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
