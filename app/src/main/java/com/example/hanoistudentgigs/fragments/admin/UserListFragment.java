package com.example.hanoistudentgigs.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.adapters.UserAdapter;
import com.example.hanoistudentgigs.models.User;
import com.example.hanoistudentgigs.utils.Constants;

public class UserListFragment extends Fragment {
   private static final String ARG_ROLE = "role";
   private String userRole;
   private UserAdapter adapter;

   public static UserListFragment newInstance(String role) {
       UserListFragment fragment = new UserListFragment();
       Bundle args = new Bundle();
       args.putString(ARG_ROLE, role);
       fragment.setArguments(args);
       return fragment;
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       if (getArguments() != null) {
           userRole = getArguments().getString(ARG_ROLE);
       }
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_user_list, container, false);
       RecyclerView recyclerView = view.findViewById(R.id.recyclerViewUsers);

       Query query = FirebaseFirestore.getInstance()
               .collection(Constants.USERS_COLLECTION)
               .whereEqualTo("role", userRole);

       FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
               .setQuery(query, User.class)
               .build();

       adapter = new UserAdapter(options, getContext());
       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
       recyclerView.setAdapter(adapter);

       return view;
   }

   @Override
   public void onStart() {
       super.onStart();
       adapter.startListening();
   }

   @Override
   public void onStop() {
       super.onStop();
       adapter.stopListening();
   }
}
