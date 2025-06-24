//package com.example.hanoistudentgigs.adapters;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentActivity;
//import androidx.viewpager2.adapter.FragmentStateAdapter;
//
//import com.example.hanoistudentgigs.fragments.admin.UserListFragment;
//import com.example.hanoistudentgigs.utils.Constants;
//
//public class AdminViewPagerAdapter extends FragmentStateAdapter {
//
//    public AdminViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
//        super(fragmentActivity);
//    }
//
//    @NonNull
//    @Override
//    public Fragment createFragment(int position) {
//        if (position == 0) {
//            return UserListFragment.newInstance(Constants.ROLE_STUDENT);
//        }
//        return UserListFragment.newInstance(Constants.ROLE_EMPLOYER);
//    }
//
//    @Override
//    public int getItemCount() {
//        return 2; // Có 2 tab: Sinh viên và Nhà tuyển dụng
//    }
//}