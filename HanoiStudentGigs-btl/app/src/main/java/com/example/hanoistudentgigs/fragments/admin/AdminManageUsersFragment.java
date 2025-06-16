package com.example.hanoistudentgigs.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.hanoistudentgigs.R;
import com.example.hanoistudentgigs.adapters.AdminViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AdminManageUsersFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_manage_users, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tabLayoutUsers);
        ViewPager2 viewPager = view.findViewById(R.id.viewPagerUsers);

        AdminViewPagerAdapter adapter = new AdminViewPagerAdapter(getActivity());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Sinh viên");
            } else {
                tab.setText("Nhà tuyển dụng");
            }
        }).attach();

        return view;
    }
}