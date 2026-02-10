package com.example.mobile_application.ui;

import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.mobile_application.R;

public class ProfileImageLoader {
    private final String apiBaseUrl;

    public ProfileImageLoader(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public void load(Fragment fragment, ImageView imageView, String rawUrl) {
        String resolvedUrl = resolveProfileImageUrl(rawUrl);
        if (resolvedUrl != null && !resolvedUrl.isEmpty()) {
            Glide.with(fragment)
                    .load(resolvedUrl)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.user);
        }
    }

    private String resolveProfileImageUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.trim().isEmpty()) {
            return null;
        }

        String url = rawUrl.trim().replaceAll("http://(localhost|127\\.0\\.0\\.1)(:\\d+)?", apiBaseUrl);

        // Already absolute URL
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }

        // Just filename - assume it's in profile-images folder
        if (!url.contains("/")) {
            return apiBaseUrl + "/uploads/profile-images/" + url;
        }

        // Relative path
        return url.startsWith("/") ? apiBaseUrl + url : apiBaseUrl + "/" + url;
    }
}
