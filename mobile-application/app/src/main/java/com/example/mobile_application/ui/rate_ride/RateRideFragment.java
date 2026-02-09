package com.example.mobile_application.ui.rate_ride;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.RateRideRequestDTO;

public class RateRideFragment extends Fragment {

    private static final String ARG_RIDE = "ride";
    private RateRideRequestDTO ride;

    public static RateRideFragment newInstance(RateRideRequestDTO ride) {
        RateRideFragment fragment = new RateRideFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RIDE, ride);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rate_ride, container, false);

        return view;
    }
}