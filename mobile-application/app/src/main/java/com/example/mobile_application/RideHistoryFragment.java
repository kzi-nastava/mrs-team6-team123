package com.example.mobile_application;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RideHistoryFragment extends Fragment {

    private LinearLayout tableHeader;
    private LinearLayout rowsContainer;
    private String userRole = "driver"; // "user" | "admin"

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_history, container, false);

        tableHeader = view.findViewById(R.id.tableHeader);
        rowsContainer = view.findViewById(R.id.rowsContainer);

        setUpTableColumns();
        loadSampleRows();

        return view;
    }

    private void setUpTableColumns() {
        tableHeader.removeAllViews();

        if (userRole.equals("driver")) {
            addHeaderCell("Date");
            addHeaderCell("From");
            addHeaderCell("To");
            addHeaderCell("Started at");
            addHeaderCell("Ended at");
            addHeaderCell("Price");
            addHeaderCell("PANIC");
            addHeaderCell("Canceled by");
            addHeaderCell("Passengers");
        }
    }

    private void addHeaderCell(String text) {
        TextView tv = new TextView(requireContext());
        tv.setText(text);
        tv.setWidth(350);
        tv.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.nobile_bold));
        tv.setPadding(16, 8, 16, 8); // px
        tv.setTextColor(getResources().getColor(R.color.black, null));
        tableHeader.addView(tv);
    }

    private void loadSampleRows() {
        rowsContainer.removeAllViews();
        LinearLayout row = createRow();

        addCell(row, "12.12.2024");
        addCell(row, "Location A");
        addCell(row, "Location B");
        addCell(row, "09:47");
        addCell(row, "10:08");
        addCell(row, "1400");
        addCell(row, "-");
        addCell(row, "-");
        addCell(row, "Jane Doe, Minnie Mouse");

        rowsContainer.addView(row);
    }

    private LinearLayout createRow() {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 8, 0, 8);
        return row;
    }

    private void addCell(LinearLayout row, String text) {
        TextView tv = new TextView(requireContext());
        tv.setText(text);
        tv.setWidth(350);
        tv.setPadding(16, 8, 16, 8);
        tv.setTextColor(getResources().getColor(R.color.black, null));

        row.addView(tv);
    }

}