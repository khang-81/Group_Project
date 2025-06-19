package com.example.hanoistudentgigs.fragments.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.adapters.UserAdapter;
import com.example.hanoistudentgigs.models.User;

import java.util.ArrayList;
import java.util.List;

public class AdminManageUsersFragment extends Fragment {
    private Button btnStudentTab, btnEmployerTab;
    private EditText etSearchUser;
    private RecyclerView rvUserList;
    private UserAdapter userAdapter;
    private List<User> allUsers = new ArrayList<>();
    private List<User> filteredUsers = new ArrayList<>();
    private boolean isStudentTab = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_manage_users, container, false);
        btnStudentTab = view.findViewById(R.id.btnStudentTab);
        btnEmployerTab = view.findViewById(R.id.btnEmployerTab);
        etSearchUser = view.findViewById(R.id.etSearchUser);
        rvUserList = view.findViewById(R.id.rvUserList);

        userAdapter = new UserAdapter(filteredUsers);
        rvUserList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUserList.setAdapter(userAdapter);

        btnStudentTab.setOnClickListener(v -> switchTab(true));
        btnEmployerTab.setOnClickListener(v -> switchTab(false));

        etSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // TODO: Load user data từ database
        loadDummyUsers();
        filterUsers("");
        return view;
    }

    private void switchTab(boolean student) {
        isStudentTab = student;
        btnStudentTab.setEnabled(!student);
        btnEmployerTab.setEnabled(student);
        filterUsers(etSearchUser.getText().toString());
    }

    private void filterUsers(String query) {
        filteredUsers.clear();
        for (User user : allUsers) {
            if ((isStudentTab && user.getRole().equals("student")) || (!isStudentTab && user.getRole().equals("employer"))) {
                if ((user.getFullName() != null && user.getFullName().toLowerCase().contains(query.toLowerCase())) ||
                    (user.getEmail() != null && user.getEmail().toLowerCase().contains(query.toLowerCase()))) {
                    filteredUsers.add(user);
                }
            }
        }
        userAdapter.notifyDataSetChanged();
    }

    // Dummy data cho demo
    private void loadDummyUsers() {
        allUsers.clear();
        allUsers.add(new User("Nguyễn Văn A", "nguyenvana@gmail.com", "student"));
        allUsers.add(new User("Nguyễn Văn B", "nguyenvanb@gmail.com", "student"));
        allUsers.add(new User("Công ty A", "lienheA@gmail.com", "employer"));
        allUsers.add(new User("Công ty B", "lienheB@gmail.com", "employer"));
    }
}