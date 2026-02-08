package com.example.mobile_application.helper;

import com.example.mobile_application.dto.GeoPointDTO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MapRouteHelper {
    private final MapView mapView;

    public MapRouteHelper(MapView mapView) {
        this.mapView = mapView;
    }
    public void fetchRoute(GeoPointDTO stop1, GeoPointDTO stop2) {
        new Thread(() -> {
            try {
                if (stop1 == null || stop2 == null)
                    return;
                GeoPoint startPoint = new GeoPoint(stop1.getLatitude(), stop1.getLongitude());
                GeoPoint endPoint = new GeoPoint(stop2.getLatitude(), stop2.getLongitude());
                List<GeoPoint> routePoints = getRoute(startPoint, endPoint);
                if (routePoints != null) {
                    mapView.post(() -> drawRoute(routePoints));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private List<GeoPoint> getRoute(GeoPoint startPoint, GeoPoint endPoint)
            throws IOException, JSONException {
        String url = "https://router.project-osrm.org/route/v1/driving/" +
                startPoint.getLongitude() + "," + startPoint.getLatitude() + ";" +
                endPoint.getLongitude() + "," + endPoint.getLatitude() +
                "?overview=simplified&geometries=geojson";

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "OSMDroidExample/1.0, osmdroid@gmail.com")
                .build();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                return null;
            }
            JSONObject json = new JSONObject(response.body().string());
            JSONArray routes = json.optJSONArray("routes");
            if (routes == null || routes.length() == 0) {
                return null;
            }

            JSONArray coordinates = routes.getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONArray("coordinates");

            List<GeoPoint> routePoints = new ArrayList<>(coordinates.length());

            for (int i = 0; i < coordinates.length(); i++) {
                JSONArray point = coordinates.getJSONArray(i);
                double lon = point.getDouble(0);
                double lat = point.getDouble(1);
                routePoints.add(new GeoPoint(lat, lon));
            }

            return routePoints;
        }
    }

    private void drawRoute(List<GeoPoint> routePoints) {
        Polyline routeLine = new Polyline();
        routeLine.setPoints(routePoints);
        routeLine.setColor(0xFF0000FF);
        routeLine.setWidth(10.0f);

        mapView.getOverlays().add(routeLine);
        mapView.invalidate();

        if (!routePoints.isEmpty()) {
            IMapController mapController = mapView.getController();
            mapController.setZoom(15.0);
            mapController.setCenter(routePoints.get(0));
        }
    }
}
