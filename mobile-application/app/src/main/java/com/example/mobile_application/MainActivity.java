package com.example.mobile_application;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mobile_application.dto.ChatDTO;
import com.example.mobile_application.dto.auth.LogoutRequestDTO;
import com.example.mobile_application.dto.auth.LogoutResponseDTO;
import com.example.mobile_application.repository.AuthRepository;
import com.example.mobile_application.repository.ChatRepository;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.TokenManager;
import com.example.mobile_application.ui.LoginFragment;
import com.example.mobile_application.ui.ProfileFragment;
import com.example.mobile_application.ui.DriverRegistrationFragment;
import com.example.mobile_application.ui.FavoriteRoutesFragment;
import com.example.mobile_application.ui.admin_home.AdminHomeFragment;
import com.example.mobile_application.ui.chat.ChatFragment;
import com.example.mobile_application.ui.chat.ChatListFragment;
import com.example.mobile_application.ui.driver_home.DriverHomeFragment;
import com.example.mobile_application.ui.map.MapFragment;
import com.example.mobile_application.ui.registered_home.RegisteredHomeFragment;
import com.example.mobile_application.ui.unregistered_home.UnregisteredHomeFragment;
import com.example.mobile_application.ui.driver_ride_history.RideHistoryFragment;
import com.example.mobile_application.ui.notifications.NotificationsFragment;
import com.example.mobile_application.ui.pricing.PricingFragment;
import com.example.mobile_application.ui.reports.ReportsFragment;
import com.example.mobile_application.ui.track_ride.TrackRideFragment;
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
    private ChatRepository chatRepository;
    private AuthRepository authRepository;
    private TokenManager tokenManager;
    private final boolean isLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApiClient.init(this);
        tokenManager = ApiClient.getTokenManager();

        chatRepository = new ChatRepository();
        authRepository = new AuthRepository();

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerMenuView = findViewById(R.id.navigation_view);
        bottomNavigationView = findViewById(R.id.navbar);

        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        setUpBottomNavigation();
        setUpDrawerMenu();
        updateUIForAuthState();

        if (savedInstanceState == null) {
            loadHomeFragment();
        }
    }

    // ── Auth state helpers ───────────────────────────────────────

    private boolean isLoggedIn() {
        return tokenManager.isLoggedIn();
    }

    private String getUserRole() {
        return tokenManager.getRole();
    }

    private Long getUserId() {
        return tokenManager.getUserId();
    }

    // ── LoginFragment.OnLoginSuccessListener ─────────────────────

    @Override
    public void onLoginSuccess(Long userId, String role) {
        // Token is already saved by LoginFragment; just refresh UI
        updateUIForAuthState();
        loadHomeFragment();
    }

    // ── Logout ───────────────────────────────────────────────────

    public void performLogout() {
        Long userId = getUserId();
        authRepository.logout(new LogoutRequestDTO(userId),
                new Callback<LogoutResponseDTO>() {
                    @Override
                    public void onResponse(@NonNull Call<LogoutResponseDTO> call,
                            @NonNull Response<LogoutResponseDTO> resp) {
                        clearSessionAndRedirect();
                    }

                    @Override
                    public void onFailure(@NonNull Call<LogoutResponseDTO> call,
                            @NonNull Throwable t) {
                        // Clear locally even if server call fails
                        clearSessionAndRedirect();
                    }
                });
    }

    private void clearSessionAndRedirect() {
        tokenManager.clearAuthData();
        ApiClient.resetClient();
        updateUIForAuthState();
        loadFragment(new LoginFragment());
    }

    // ── UI updates based on auth state ───────────────────────────

    private void updateUIForAuthState() {
        updateDrawerMenu();
        updateBottomNav();
    }

    private void updateDrawerMenu() {
        Menu menu = drawerMenuView.getMenu();
        String role = getUserRole();

        if (!isLoggedIn() || role == null) {
            // Hide everything in drawer when not logged in
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
            return;
        }

        // Show all first, then hide per role
        for (int i = 0; i < menu.size(); i++)
            menu.getItem(i).setVisible(true);

        if (role.equals(getString(R.string.role_admin))) {
            menu.findItem(R.id.favorites).setVisible(false);
        } else if (role.equals(getString(R.string.role_driver))) {
            menu.findItem(R.id.favorites).setVisible(false);
            menu.findItem(R.id.drivers).setVisible(false);
            menu.findItem(R.id.pricing).setVisible(false);
        } else if (role.equals(getString(R.string.role_passenger))) {
            menu.findItem(R.id.drivers).setVisible(false);
            menu.findItem(R.id.pricing).setVisible(false);
        }

        // Show/hide logout item if you have one in the drawer
        if (menu.findItem(R.id.logout) != null) {
            menu.findItem(R.id.logout).setVisible(true);
        }
    }

    private void updateBottomNav() {
        Menu menu = bottomNavigationView.getMenu();
        menu.findItem(R.id.nav_hamburger).setVisible(isLoggedIn());
    }

    // ── Fragment loading ─────────────────────────────────────────

    private void loadHomeFragment() {
        if (!isLoggedIn()) {
            loadFragment(new UnregisteredHomeFragment());
            return;
        }

        String role = getUserRole();
        if (role == null) {
            loadFragment(new MapFragment());
            return;
        }

        switch (role) {
            case "ADMIN":
                loadFragment(new AdminHomeFragment());
                break;
            case "PASSENGER":
                loadFragment(new RegisteredHomeFragment());
                break;
            case "DRIVER":
                loadFragment(DriverHomeFragment.newInstance(getUserId()));
                break;
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

    // ── Navigation listeners ─────────────────────────────────────

    private void setUpBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_hamburger) {
                drawerLayout.openDrawer(GravityCompat.START);
                return false;
            } else if (id == R.id.nav_home) {
                loadHomeFragment();
                return true;
            } else if (id == R.id.nav_profile) {
                if (isLoggedIn()) {
                    loadFragment(ProfileFragment.newInstance(getUserRole(), getUserId()));
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
                fragment = new NotificationsFragment();
            } else if (id == R.id.pricing) {
                fragment = new PricingFragment();
            } else if (id == R.id.reports) {
                fragment = new ReportsFragment();
            } else if (id == R.id.chat) {
                String role = getUserRole();
                if (role != null && role.equals(getString(R.string.role_admin))) {
                    fragment = new ChatListFragment();
                } else {
                    findChat();
                }
            } else if (id == R.id.logout) {
                performLogout();
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

    private void findChat() {
        chatRepository.getMyChat(getUserId(), new Callback<ChatDTO>() {
            @Override
            public void onResponse(@NonNull Call<ChatDTO> call,
                    @NonNull Response<ChatDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loadFragment(ChatFragment.newInstance(response.body()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChatDTO> call,
                    @NonNull Throwable t) {
                // Optionally show error
            }
        });
    }
}