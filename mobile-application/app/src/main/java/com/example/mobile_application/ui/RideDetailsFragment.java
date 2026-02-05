package com.example.mobile_application.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobile_application.R;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class RideDetailsFragment extends Fragment {

    private MapView mapView;
    private GeoPoint startPoint = new GeoPoint(45.25187418449059, 19.837206696300832);
    private GeoPoint endPoint = new GeoPoint(45.24625972559177, 19.85169677628525);

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_details, container, false);

        mapView = view.findViewById(R.id.map);
        mapSetup();

        return view;
    }

    private void mapSetup() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        GeoPoint centerPoint = new GeoPoint(
                (startPoint.getLatitude() + endPoint.getLatitude()) / 2,
                (startPoint.getLongitude() + endPoint.getLongitude()) / 2
        );
        MapController mapController = (MapController) mapView.getController();
        mapController.setZoom(16.0);
        mapController.setCenter(centerPoint);

        Marker startMark = new Marker(mapView);
        startMark.setPosition(startPoint);
        startMark.setTitle("Start");
        startMark.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(startMark);

        Marker endMark = new Marker(mapView);
        endMark.setPosition(endPoint);
        endMark.setTitle("End");
        endMark.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(endMark);
    }
}