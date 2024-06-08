package com.example.lab6_20125424_iot;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lab6_20125424_iot.R;
import com.example.lab6_20125424_iot.dataHolder.DataManager;
import com.example.lab6_20125424_iot.item.ListElementEgreso;
import com.example.lab6_20125424_iot.item.ListElementIngreso;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.components.XAxis;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ResumenFragment extends Fragment {

    private PieChart pieChart;
    private BarChart barChart;
    private MaterialButton monthPickerButton;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resumen, container, false);
        pieChart = view.findViewById(R.id.pieChart);
        barChart = view.findViewById(R.id.barChart);
        monthPickerButton = view.findViewById(R.id.monthPickerButton);

        mAuth = FirebaseAuth.getInstance();

        monthPickerButton.setText(monthYearFormat.format(calendar.getTime()));
        monthPickerButton.setOnClickListener(v -> showMonthPicker());

        loadDataAndUpdateCharts();

        return view;
    }

    private void showMonthPicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    monthPickerButton.setText(monthYearFormat.format(calendar.getTime()));
                    loadDataAndUpdateCharts();
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // Safe handling to avoid NullPointerException
        try {
            View dayView = datePickerDialog.getDatePicker().findViewById(
                    getResources().getIdentifier("android:id/day", null, null));
            if (dayView != null) {
                dayView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        datePickerDialog.show();
    }

    private void loadDataAndUpdateCharts() {
        loadIngresosFromFirestore(() -> loadEgresosFromFirestore(this::updateCharts));
    }

    public void loadIngresosFromFirestore(Runnable onSuccess) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        String path = "users/" + uid + "/ingresos";
        db.collection(path)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ListElementIngreso> ingresosList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ListElementIngreso ingreso = document.toObject(ListElementIngreso.class);
                            ingreso.setId(document.getId());
                            ingresosList.add(ingreso);
                        }
                        DataManager.getInstance().setIngresosList(ingresosList);

                        // Log the elements of ingresosList
                        for (ListElementIngreso ingreso : ingresosList) {
                            Log.d("msg-test", "Ingreso: " + ingreso.getAmount());
                        }

                        onSuccess.run();
                    } else {
                        Log.d("msg-test", "Error getting ingreso documents: ", task.getException());
                    }
                });
    }

    public void loadEgresosFromFirestore(Runnable onSuccess) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        String path = "users/" + uid + "/egresos";
        db.collection(path)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ListElementEgreso> egresosList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ListElementEgreso egreso = document.toObject(ListElementEgreso.class);
                            egreso.setId(document.getId());
                            egresosList.add(egreso);
                        }
                        DataManager.getInstance().setEgresosList(egresosList);

                        // Log the elements of egresosList
                        for (ListElementEgreso egreso : egresosList) {
                            Log.d("msg-test", "Egreso: " + egreso.getAmount());
                        }

                        onSuccess.run();
                    } else {
                        Log.d("msg-test", "Error getting egreso documents: ", task.getException());
                    }
                });
    }

    private void updateCharts() {
        List<ListElementIngreso> ingresosList = DataManager.getInstance().getIngresosList();
        List<ListElementEgreso> egresosList = DataManager.getInstance().getEgresosList();

        Map<Integer, Float> ingresosPorDia = new HashMap<>();
        Map<Integer, Float> egresosPorDia = new HashMap<>();

        float totalIngresos = 0f;
        float totalEgresos = 0f;

        for (ListElementIngreso ingreso : ingresosList) {
            if (isSameMonthAndYear(ingreso.getDate(), calendar)) {
                totalIngresos += ingreso.getAmount();
                int day = getDayOfMonth(ingreso.getDate());
                ingresosPorDia.put(day, (float) (ingresosPorDia.getOrDefault(day, 0f) + ingreso.getAmount()));
            }
        }

        for (ListElementEgreso egreso : egresosList) {
            if (isSameMonthAndYear(egreso.getDate(), calendar)) {
                totalEgresos += egreso.getAmount();
                int day = getDayOfMonth(egreso.getDate());
                egresosPorDia.put(day, (float) (egresosPorDia.getOrDefault(day, 0f) + egreso.getAmount()));
            }
        }

        updatePieChart(totalIngresos, totalEgresos);
        updateBarChart(ingresosPorDia, egresosPorDia);
    }

    private boolean isSameMonthAndYear(String date, Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar dateCalendar = Calendar.getInstance();
        try {
            dateCalendar.setTime(sdf.parse(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                dateCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH);
    }

    private int getDayOfMonth(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar dateCalendar = Calendar.getInstance();
        try {
            dateCalendar.setTime(sdf.parse(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateCalendar.get(Calendar.DAY_OF_MONTH);
    }

    private void updatePieChart(float ingresos, float egresos) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(egresos, "Egresos"));
        entries.add(new PieEntry(ingresos, "Ingresos"));

        PieDataSet dataSet = new PieDataSet(entries, "Ingresos vs Egresos");

        // Set colors for each slice
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.RED); // Color for Egresos
        colors.add(Color.GREEN); // Color for Ingresos
        dataSet.setColors(colors);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false); // Disable description label
        pieChart.invalidate(); // refresh
    }

    private void updateBarChart(Map<Integer, Float> ingresosPorDia, Map<Integer, Float> egresosPorDia) {
        List<BarEntry> ingresosEntries = new ArrayList<>();
        List<BarEntry> egresosEntries = new ArrayList<>();
        List<BarEntry> consolidadoEntries = new ArrayList<>();

        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        String[] days = new String[maxDay];

        for (int day = 1; day <= maxDay; day++) {
            float ingresos = ingresosPorDia.getOrDefault(day, 0f);
            float egresos = egresosPorDia.getOrDefault(day, 0f);
            ingresosEntries.add(new BarEntry(day, ingresos));
            egresosEntries.add(new BarEntry(day, egresos));
            consolidadoEntries.add(new BarEntry(day, ingresos + egresos));
            days[day - 1] = String.valueOf(day); // Save the day number as label
        }

        BarDataSet consolidadoDataSet = new BarDataSet(consolidadoEntries, "Consolidado");
        consolidadoDataSet.setColor(Color.argb(150, 127, 0, 255)); // Set semi-transparent color for consolidado

        BarDataSet ingresosDataSet = new BarDataSet(ingresosEntries, "Ingresos");
        ingresosDataSet.setColor(Color.argb(150, 0, 255, 0)); // Set semi-transparent color for ingresos

        BarDataSet egresosDataSet = new BarDataSet(egresosEntries, "Egresos");
        egresosDataSet.setColor(Color.argb(150, 255, 0, 0)); // Set semi-transparent color for egresos



        BarData barData = new BarData(ingresosDataSet, egresosDataSet, consolidadoDataSet);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false); // Disable description label

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days)); // Set days as labels
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Ensure each day is displayed
        xAxis.setLabelCount(maxDay);

        barChart.invalidate(); // refresh
    }

}
