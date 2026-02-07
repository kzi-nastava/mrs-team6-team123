package com.example.mobile_application.helper;

import android.graphics.drawable.BitmapDrawable;

import com.example.mobile_application.dto.GeoPointDTO;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class DrawMarkerHelper {
    private MapView mapView;

    public DrawMarkerHelper(MapView mapView) {
        this.mapView = mapView;
    }

    public void drawMarkers(GeoPointDTO dto, BitmapDrawable icon) {
        Marker marker = new Marker(mapView);
        GeoPoint point = new GeoPoint(dto.getLatitude(), dto.getLongitude());
        marker.setPosition(point);
        marker.setTitle(dto.getLocation());
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(icon);
        mapView.getOverlays().add(marker);
    }
}
