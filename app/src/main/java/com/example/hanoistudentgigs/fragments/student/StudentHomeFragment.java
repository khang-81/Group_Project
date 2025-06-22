    package com.example.hanoistudentgigs.fragments.student;

    import android.content.Intent;
    import android.os.Bundle;
    import android.util.Log;
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
    import com.example.hanoistudentgigs.models.Filter;
    public class StudentHomeFragment extends Fragment {
        private RecyclerView recyclerViewFeaturedJobs, recyclerViewPopularJobs;
        private FeaturedJobAdapter featuredAdapter;
        private PopularJobAdapter popularAdapter;
        private TextView textViewUserName;
        private View searchBar; // Sử dụng View để bắt sự kiện click
        private ImageButton buttonFilter;
        private FirebaseFirestore db = FirebaseFirestore.getInstance();
        private FirebaseAuth mAuth = FirebaseAuth.getInstance();
        private TextView textViewNoPopularResults;
        private TextView textViewNoFeaturedResults;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            Log.d("StudentHomeFragment", "onCreateView called"); // THÊM LOG NÀY
            View view = inflater.inflate(R.layout.fragment_student_home, container, false);

            // Ánh xạ các views
            textViewUserName = view.findViewById(R.id.textViewCurrentUserName);
            recyclerViewFeaturedJobs = view.findViewById(R.id.recyclerViewFeaturedJobs);
            recyclerViewPopularJobs = view.findViewById(R.id.recyclerViewPopularJobs);
            searchBar = view.findViewById(R.id.searchBar); // Ánh xạ CardView làm thanh tìm kiếm

            textViewNoPopularResults = view.findViewById(R.id.textViewNoPopularResults); // <-- THÊM DÒNG NÀY
            textViewNoFeaturedResults = view.findViewById(R.id.textViewNoFeaturedResults); // <-- THÊM DÒNG NÀY

            // Ánh xạ nút lọc từ layout
            buttonFilter = view.findViewById(R.id.buttonFilter); // Ánh xạ ImageButton

            // Thiết lập sự kiện cho thanh tìm kiếm chung
            // Khi nhấn vào thanh tìm kiếm chung, sẽ mở SearchActivity mà không có bộ lọc mặc định
            searchBar.setOnClickListener(v -> openSearchActivity(null, false)); // false: không phải từ nút filter


            // Thiết lập sự kiện cho nút lọc (buttonFilter)
            buttonFilter.setOnClickListener(v -> {
                // Khi nút filter được nhấn, mở SearchActivity và cho biết nó được mở từ nút filter.
                // SearchActivity sau đó có thể hiển thị dialog/fragment lọc ngay lập tức.
                openSearchActivity(null, true); // true: từ nút filter
            });




            loadUserName();
            setupRecyclerViews();

            return view;
        }

    //    private void openSearchActivity() {
    //        if (getActivity() != null) {
    //            Intent intent = new Intent(getActivity(), SearchActivity.class);
    //            startActivity(intent);
    //        }
    //    }
    private void openSearchActivity(@Nullable Filter filter, boolean openedFromFilterButton) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            if (filter != null) {
                intent.putExtra(Constants.KEY_FILTER_OBJECT, filter);
            }
            // Thêm một extra để SearchActivity biết nó được mở từ nút filter
            intent.putExtra(Constants.KEY_OPENED_FROM_FILTER_BUTTON, openedFromFilterButton);
            startActivity(intent);
        }
    }
        private void loadUserName() {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null && isAdded()) {
                db.collection(Constants.STUDENTS_COLLECTION).document(currentUser.getUid()).get()
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
                    .whereEqualTo("approved", false)
                    .whereEqualTo("featured", true)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .limit(5);
            recyclerViewFeaturedJobs.setHasFixedSize(true);
            FirestoreRecyclerOptions<Job> featuredOptions = new FirestoreRecyclerOptions.Builder<Job>()
                    .setQuery(featuredQuery, Job.class)
                    .build();

            featuredAdapter = new FeaturedJobAdapter(featuredOptions, requireContext(), recyclerViewFeaturedJobs, textViewNoFeaturedResults);
            recyclerViewFeaturedJobs.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerViewFeaturedJobs.setAdapter(featuredAdapter);

            // Query cho các công việc phổ biến (tất cả các công việc còn lại)
            Query popularQuery = db.collection(Constants.JOBS_COLLECTION)
                    .whereEqualTo("approved", false)
                    .whereEqualTo("featured", false)
                    .orderBy("createdAt", Query.Direction.DESCENDING);

            FirestoreRecyclerOptions<Job> popularOptions = new FirestoreRecyclerOptions.Builder<Job>()
                    .setQuery(popularQuery, Job.class)
                    .build();
            recyclerViewPopularJobs.setHasFixedSize(true);
            recyclerViewPopularJobs.setNestedScrollingEnabled(false);
            popularAdapter = new PopularJobAdapter(popularOptions, requireContext(), recyclerViewPopularJobs, textViewNoPopularResults); // <-- SỬA DÒNG NÀY
            recyclerViewPopularJobs.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerViewPopularJobs.setAdapter(popularAdapter);
            recyclerViewFeaturedJobs.setItemAnimator(null);
            recyclerViewPopularJobs.setItemAnimator(null);
        }

        @Override
        public void onStart() {
            super.onStart();
            Log.d("StudentHomeFragment", "onStart called"); // THÊM LOG NÀY
            if (featuredAdapter != null) {
                featuredAdapter.startListening();
                Log.d("StudentHomeFragment", "FeaturedJobAdapter started listening."); // THÊM LOG NÀY
            }
            if (popularAdapter != null) {
                popularAdapter.startListening();
                Log.d("StudentHomeFragment", "PopularJobAdapter started listening."); // THÊM LOG NÀY
            }
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.d("StudentHomeFragment", "onStop called"); // THÊM LOG NÀY
            if (featuredAdapter != null) featuredAdapter.stopListening();
            if (popularAdapter != null) popularAdapter.stopListening();
        }
    }