package com.example.mobile_application;

import android.os.Bundle;
import android.view.Menu;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.mobile_application.map.MapFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView drawerMenuView;
    private BottomNavigationView bottomNavigationView;
    private MaterialToolbar toolbarView;

    private boolean isLoggedIn = true;
    private String userRole = "user"; // "driver" | "admin"

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerMenuView = findViewById(R.id.navigation_view);
        bottomNavigationView = findViewById(R.id.navbar);
        toolbarView = findViewById(R.id.main_toolbar);

        findViewById(R.id.nav_hamburger).setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.END)
        );

        if (!isLoggedIn) {
            Menu menu = bottomNavigationView.getMenu();
            menu.findItem(R.id.nav_hamburger).setVisible(false);
        }

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
}