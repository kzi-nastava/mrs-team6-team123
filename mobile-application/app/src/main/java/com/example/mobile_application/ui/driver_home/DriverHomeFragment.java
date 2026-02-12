package com.example.mobile_application.ui.driver_home;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mobile_application.R;
import com.example.mobile_application.adapter.AssignedRideAdapter;
import com.example.mobile_application.dto.DriverAssignedRideDTO;
import com.example.mobile_application.dto.GeoPointDTO;
import com.example.mobile_application.helper.DrawMarkerHelper;
import com.example.mobile_application.helper.MapRouteHelper;
import com.example.mobile_application.repository.DriverRepository;
import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.TokenManager;
import com.example.mobile_application.ui.track_ride.TrackRideFragment;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverHomeFragment extends Fragment implements AssignedRideAdapter.OnRideActionListener {

    private MapView mapView;
    private ViewPager2 ridesViewPager;
    private ImageView arrowLeft, arrowRight;
    private View cardContainer;
    private ProgressBar ridesLoading;
    private View emptyState;
    private AssignedRideAdapter adapter;
    private DriverRepository driverRepository;
    private MapRouteHelper mapRouteHelper;
    private List<DriverAssignedRideDTO> currentRides = new ArrayList<>();

    private Long driverId;
    private DrawMarkerHelper drawMarkerHelper;
    private BitmapDrawable locationIcon;

    public static DriverHomeFragment newInstance(Long driverId) {
        DriverHomeFragment fragment = new DriverHomeFragment();
        Bundle args = new Bundle();
        args.putLong("driver_id", driverId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_home, container, false);

        initializeViews(view);
        setupMap();
        setupViewPager();
        loadDriverId();

        return view;
    }

    private void initializeViews(View view) {
        mapView = view.findViewById(R.id.map_view);
        ridesViewPager = view.findViewById(R.id.rides_view_pager);
        arrowLeft = view.findViewById(R.id.arrow_left);
        arrowRight = view.findViewById(R.id.arrow_right);
        cardContainer = view.findViewById(R.id.card_container);
        ridesLoading = view.findViewById(R.id.rides_loading);
        emptyState = view.findViewById(R.id.empty_state);

        // Initially hide card container until data loads
        cardContainer.setVisibility(View.GONE);

        driverRepository = new DriverRepository();

        // Initialize location icon (36dp)
        int iconSize = 36;
        Bitmap originalBitmap = ((BitmapDrawable) ContextCompat.getDrawable(requireContext(), R.drawable.location_icon))
                .getBitmap();
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, iconSize, iconSize, true);
        locationIcon = new BitmapDrawable(getResources(), scaledBitmap);
    }

    private void setupMap() {
        // Initialize DrawMarkerHelper
        drawMarkerHelper = new DrawMarkerHelper(mapView);

        // Configure OSMDroid
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(14.0);

        // Default map center (will be updated when rides are loaded)
        mapView.getController().setCenter(new GeoPoint(45.2671, 19.8335)); // Novi Sad

        mapRouteHelper = new MapRouteHelper(mapView);
    }

    private void setupViewPager() {
        adapter = new AssignedRideAdapter(this);
        ridesViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        ridesViewPager.setAdapter(adapter);
    }

    private void setupViewPagerListeners() {
        // Set up page change listener to update map and arrows
        ridesViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (currentRides != null && !currentRides.isEmpty() && position < currentRides.size()) {
                    displayRideOnMap(currentRides.get(position));
                    updateArrows(position);
                }
            }
        });

        // Arrow click listeners
        arrowLeft.setOnClickListener(v -> {
            int currentItem = ridesViewPager.getCurrentItem();
            if (currentItem > 0) {
                ridesViewPager.setCurrentItem(currentItem - 1, true);
            }
        });

        arrowRight.setOnClickListener(v -> {
            int currentItem = ridesViewPager.getCurrentItem();
            if (currentRides != null && currentItem < currentRides.size() - 1) {
                ridesViewPager.setCurrentItem(currentItem + 1, true);
            }
        });
    }

    private void loadDriverId() {
        // Get driver ID from Bundle arguments
        Bundle args = getArguments();
        if (args != null) {
            driverId = args.getLong("driver_id", 0);
        }

        if (driverId == null || driverId == 0) {
            TokenManager tokenManager = ApiClient.getTokenManager();
            driverId = tokenManager.getUserId();
        }

        if (driverId != null && driverId != 0) {
            loadAssignedRides();
        } else {
            Toast.makeText(requireContext(), "Driver ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAssignedRides() {
        if (driverId == null || driverId == 0) {
            return;
        }

        showLoading(true);

        driverRepository.getAssignedRides(driverId, new Callback<List<DriverAssignedRideDTO>>() {
            @Override
            public void onResponse(Call<List<DriverAssignedRideDTO>> call,
                    Response<List<DriverAssignedRideDTO>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<DriverAssignedRideDTO> rides = response.body();
                    updateUI(rides);
                } else {
                    Toast.makeText(requireContext(), "Failed to load rides", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DriverAssignedRideDTO>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(List<DriverAssignedRideDTO> rides) {
        currentRides = rides;

        if (rides == null || rides.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            cardContainer.setVisibility(View.GONE);
            clearMap();
        } else {
            emptyState.setVisibility(View.GONE);
            cardContainer.setVisibility(View.VISIBLE);
            adapter.setRides(rides);

            // Set up listeners after data is loaded
            setupViewPagerListeners();

            // Check if any ride is already started
            for (DriverAssignedRideDTO ride : rides) {
                if ("STARTED".equals(ride.getStatus())) {
                    navigateToTrackRide(ride.getRideId());
                    return;
                }
            }

            // Display the first ride on the map
            displayRideOnMap(rides.get(0));
            ridesViewPager.setCurrentItem(0, false);
            updateArrows(0);
        }
    }

    private void updateArrows(int position) {
        if (currentRides == null || currentRides.isEmpty()) {
            arrowLeft.setVisibility(View.INVISIBLE);
            arrowRight.setVisibility(View.INVISIBLE);
            return;
        }
        // Show/hide arrows based on position
        arrowLeft.setVisibility(position > 0 ? View.VISIBLE : View.INVISIBLE);
        arrowRight.setVisibility(position < currentRides.size() - 1 ? View.VISIBLE : View.INVISIBLE);
    }

    private void displayRideOnMap(DriverAssignedRideDTO ride) {
        if (ride.getStartLatitude() != null && ride.getStartLongitude() != null &&
                ride.getEndLatitude() != null && ride.getEndLongitude() != null) {

            mapView.getOverlays().clear();

            // Create GeoPointDTO objects for start and end
            GeoPointDTO startPoint = new GeoPointDTO(
                    ride.getStartLatitude(),
                    ride.getStartLongitude(),
                    ride.getStartLocation());
            GeoPointDTO endPoint = new GeoPointDTO(
                    ride.getEndLatitude(),
                    ride.getEndLongitude(),
                    ride.getEndLocation());

            // Fetch and draw route
            mapRouteHelper.fetchRoute(startPoint, endPoint);

            // Add markers using DrawMarkerHelper for consistency with other fragments
            drawMarkerHelper.drawMarkers(startPoint, locationIcon);
            drawMarkerHelper.drawMarkers(endPoint, locationIcon);

            // Center map on start location
            mapView.getController().setCenter(new GeoPoint(ride.getStartLatitude(), ride.getStartLongitude()));
            mapView.invalidate();
        }
    }

    private void clearMap() {
        mapView.getOverlays().clear();
        mapView.invalidate();
    }

    private void showLoading(boolean show) {
        ridesLoading.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    // AssignedRideAdapter.OnRideActionListener implementation
    @Override
    public void onStartRide(Long rideId) {
        if (driverId == null || driverId == 0) {
            Toast.makeText(requireContext(), "Invalid driver ID", Toast.LENGTH_SHORT).show();
            return;
        }

        driverRepository.startRide(driverId, rideId, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    navigateToTrackRide(rideId);
                } else {
                    Toast.makeText(requireContext(), "Failed to start ride", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToTrackRide(Long rideId) {
        Fragment fragment = TrackRideFragment.newInstance(rideId);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }
}
