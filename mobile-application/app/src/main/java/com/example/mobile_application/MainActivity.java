package com.example.mobile_application;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mobile_application.dto.auth.LogoutRequestDTO;
import com.example.mobile_application.dto.auth.LogoutResponseDTO;
import com.example.mobile_application.repository.AuthRepository;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.TokenManager;
import com.example.mobile_application.ui.LoginFragment;
import com.example.mobile_application.ui.ProfileFragment;
import com.example.mobile_application.ui.RegisterFragment;
import com.example.mobile_application.ui.ResetPasswordFragment;
import com.example.mobile_application.ui.DriverRegistrationFragment;
import com.example.mobile_application.ui.FavoriteRoutesFragment;
import com.example.mobile_application.ui.admin_home.AdminHomeFragment;
import com.example.mobile_application.ui.chat.ChatDialogFragment;
import com.example.mobile_application.ui.chat.ChatListDialogFragment;
import com.example.mobile_application.ui.driver_ride_history.RideHistoryFragment;
import com.example.mobile_application.ui.map.MapFragment;
import com.example.mobile_application.ui.track_ride.TrackRideFragment;
import com.example.mobile_application.ui.unregistered_home.UnregisteredHomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements LoginFragment.OnLoginSuccessListener {

    private DrawerLayout drawerLayout;
    private NavigationView drawerMenuView;
    private BottomNavigationView bottomNavigationView;
    private ImageButton chatButton;
    private TokenManager tokenManager;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chatRepository = new ChatRepository();

        // Initialize ApiClient with context (for SharedPreferences token storage)
        ApiClient.init(getApplicationContext());
        tokenManager = ApiClient.getTokenManager();
        authRepository = new AuthRepository();

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerMenuView = findViewById(R.id.navigation_view);
        bottomNavigationView = findViewById(R.id.navbar);
        chatButton = findViewById(R.id.btnChat);

        chatButton.setOnClickListener(v -> {
            String role = tokenManager.getRole();
            if ("ADMIN".equalsIgnoreCase(role)) {
                new ChatListDialogFragment()
                        .show(getSupportFragmentManager(), "ChatListDialog");
            } else {
                ChatDialogFragment.newInstance(getString(R.string.support_chat))
                        .show(getSupportFragmentManager(), "ChatDialog");
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        setUpBottomNavigation();
        setUpDrawerMenu();
        updateUIForAuthState();

        // Handle deep link for password reset (from email)
        handleDeepLink(getIntent());

        if (savedInstanceState == null) {
            if (tokenManager.isLoggedIn()) {
                loadFragment(TrackRideFragment.newInstance(14L));
            } else {
                loadFragment(new UnregisteredHomeFragment());
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleDeepLink(intent);
    }

    private void handleDeepLink(Intent intent) {
        if (intent == null || intent.getData() == null) return;
        Uri uri = intent.getData();

        String path = uri.getPath();
        if (path != null && path.contains("reset-password")) {
            String token = uri.getQueryParameter("token");
            if (token != null && !token.isEmpty()) {
                loadFragment(ResetPasswordFragment.newInstance(token));
            }
        }
    }

    @Override
    public void onLoginSuccess(Long userId, String role) {
        updateUIForAuthState();
        loadHomeForRole();
    }

    private void updateUIForAuthState() {
        boolean loggedIn = tokenManager.isLoggedIn();
        String role = tokenManager.getRole();

        chatButton.setVisibility(loggedIn ? View.VISIBLE : View.GONE);

        Menu bottomMenu = bottomNavigationView.getMenu();
        bottomMenu.findItem(R.id.nav_hamburger).setVisible(loggedIn);

        Menu drawerMenu = drawerMenuView.getMenu();
        if (loggedIn && role != null) {
            drawerMenu.findItem(R.id.logout).setVisible(true);

            switch (role.toUpperCase()) {
                case "ADMIN":
                    drawerMenu.findItem(R.id.favorites).setVisible(false);
                    drawerMenu.findItem(R.id.drivers).setVisible(true);
                    drawerMenu.findItem(R.id.pricing).setVisible(true);
                    break;
                case "DRIVER":
                    drawerMenu.findItem(R.id.favorites).setVisible(false);
                    drawerMenu.findItem(R.id.drivers).setVisible(false);
                    drawerMenu.findItem(R.id.pricing).setVisible(false);
                    break;
                default: // PASSENGER
                    drawerMenu.findItem(R.id.favorites).setVisible(true);
                    drawerMenu.findItem(R.id.drivers).setVisible(false);
                    drawerMenu.findItem(R.id.pricing).setVisible(false);
                    break;
            }
        }
    }

    private void loadHomeForRole() {
        String role = tokenManager.getRole();
        if (role == null) {
            loadFragment(new UnregisteredHomeFragment());
            return;
        }

        switch (role.toUpperCase()) {
            case "ADMIN":
                loadFragment(new AdminHomeFragment());
                break;
            case "DRIVER":
            case "PASSENGER":
            default:
                loadFragment(new MapFragment());
                break;
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
                if (tokenManager.isLoggedIn()) {
                    loadHomeForRole();
                } else {
                    loadFragment(new UnregisteredHomeFragment());
                }
                return true;
            } else if (id == R.id.nav_profile) {
                if (tokenManager.isLoggedIn()) {
                    String role = tokenManager.getRole();
                    Long userId = tokenManager.getUserId();
                    loadFragment(ProfileFragment.newInstance(
                            role != null ? role : "PASSENGER", userId));
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
            } else if (id == R.id.notifications) {
                // TODO: implement NotificationsFragment
            } else if (id == R.id.reports) {
                // TODO: implement ReportsFragment
            } else if (id == R.id.logout) {
                performLogout();
                drawerLayout.closeDrawers();
                return true;
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

    private void performLogout() {
        Long userId = tokenManager.getUserId();

        authRepository.logout(new LogoutRequestDTO(userId),
                new Callback<LogoutResponseDTO>() {
                    @Override
                    public void onResponse(@NonNull Call<LogoutResponseDTO> call,
                                           @NonNull Response<LogoutResponseDTO> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            LogoutResponseDTO body = response.body();
                            if (body.isSuccess()) {
                                clearSessionAndGoToLogin();
                            } else {
                                Toast.makeText(MainActivity.this,
                                        body.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            clearSessionAndGoToLogin();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<LogoutResponseDTO> call,
                                          @NonNull Throwable t) {
                        clearSessionAndGoToLogin();
                    }
                });
    }

    private void clearSessionAndGoToLogin() {
        tokenManager.clearAuthData();
        ApiClient.resetClient();
        updateUIForAuthState();

        getSupportFragmentManager()
                .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        loadFragment(new UnregisteredHomeFragment());
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }
}