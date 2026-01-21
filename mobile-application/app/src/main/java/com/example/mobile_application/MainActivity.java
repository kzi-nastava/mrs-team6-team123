package com.example.mobile_application;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.mobile_application.map.MapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView drawerMenuView;
    private BottomNavigationView bottomNavigationView;
    private ImageButton chatButton;

    private boolean isLoggedIn = true;
    private String userRole = "driver"; // "driver" | "admin"

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerMenuView = findViewById(R.id.navigation_view);
        bottomNavigationView = findViewById(R.id.navbar);
        chatButton = findViewById(R.id.btnChat);

        chatButton.setOnClickListener(v -> {
            if ("admin".equals(userRole)) {
                ChatListDialogFragment listDialog = new ChatListDialogFragment();
                listDialog.show(getSupportFragmentManager(), "ChatListDialog");
            } else {
                ChatDialogFragment dialog = ChatDialogFragment.newInstance(
                        getString(R.string.support_chat));
                dialog.show(getSupportFragmentManager(), "ChatDialog");
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        Menu menu = drawerMenuView.getMenu();
        if (userRole.equals("admin")) {
            menu.findItem(R.id.favorites).setVisible(false);
        } else if (userRole.equals("driver")) {
            menu.findItem(R.id.favorites).setVisible(false);
            menu.findItem(R.id.drivers).setVisible(false);
            menu.findItem(R.id.pricing).setVisible(false);
        } else if (userRole.equals("user")) {
            menu.findItem(R.id.drivers).setVisible(false);
            menu.findItem(R.id.pricing).setVisible(false);
        }

        if (!isLoggedIn) {
            chatButton.setVisibility(View.GONE);
            menu = bottomNavigationView.getMenu();
            menu.findItem(R.id.nav_hamburger).setVisible(false);
        }

        setUpBottomNavigation();
        setUpDrawerMenu();

        if (saveInstanceState == null) {
            loadFragment(new MapFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit();
    }

    private void setUpBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_hamburger
                    && findViewById(R.id.nav_hamburger).getVisibility() == View.VISIBLE) {
                drawerLayout.openDrawer(GravityCompat.START);
                return false;
            } else if (id == R.id.nav_home) {
                loadFragment(new MapFragment());
                return true;
            } else if (id == R.id.nav_profile) {
                if (isLoggedIn) {
                    loadFragment(ProfileFragment.newInstance(userRole));
                } else {
                    loadFragment(new LoginFragment());
                }
                return true;
            }

            return false;
        });
    }

    private void setUpDrawerMenu() {
        drawerMenuView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragment = null;
            if (id == R.id.history) {
                fragment = new RideHistoryFragment();
            } else if (id == R.id.favorites) {
                fragment = new FavoriteRoutesFragment();
            } else if (id == R.id.drivers) {
                fragment = new DriverRegistrationFragment();
            }

            if (fragment != null) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.main_container, fragment);
                ft.commit();
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }
}