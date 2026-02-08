package com.example.mobile_application.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobile_application.R;

/**
 * Displays the user's favorite routes.
 */
public class FavoriteRoutesFragment extends Fragment {

    public FavoriteRoutesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite_routes, container, false);
    }
}