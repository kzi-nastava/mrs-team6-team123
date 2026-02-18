package com.example.mobile_application.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mobile_application.ui.notifications.NotificationListFragment;

public class NotificationPagerAdapter extends FragmentStateAdapter {

    private final NotificationListFragment[] fragments = new NotificationListFragment[2];

    public NotificationPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        fragments[position] = NotificationListFragment.newInstance(position == 0);
        return fragments[position];
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public NotificationListFragment getFragment(int position) {
        return fragments[position];
    }
}
