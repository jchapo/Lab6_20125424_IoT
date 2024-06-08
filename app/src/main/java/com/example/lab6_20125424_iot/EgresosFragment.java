package com.example.lab6_20125424_iot;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lab6_20125424_iot.dataHolder.DataManager;
import com.example.lab6_20125424_iot.item.EgresosAdapter;
import com.example.lab6_20125424_iot.item.ListElementIngreso;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

// EgresosFragment.java
public class EgresosFragment extends Fragment {
    private EgresosAdapter egresosAdapter;
    private RecyclerView recyclerViewUsers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank_egresos, container, false);
        initializeViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateEgresosList();
    }

    private void initializeViews(View view) {
        recyclerViewUsers = view.findViewById(R.id.recyclerViewEgresos);
        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getContext()));

        egresosAdapter = new EgresosAdapter(DataManager.getInstance().getEgresosList(), egreso -> {
            Intent intent = new Intent(getActivity(), NuevoIngresoEgreso.class);
            intent.putExtra("entry_type", "egreso");
            intent.putExtra("ListElement", egreso);
            startActivity(intent);
        });
        recyclerViewUsers.setAdapter(egresosAdapter);

        FloatingActionButton agregarUsuarioButton = view.findViewById(R.id.fabEgresos);
        agregarUsuarioButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NuevoIngresoEgreso.class);
            intent.putExtra("entry_type", "egreso");
            startActivity(intent);
        });
    }

    private void updateEgresosList() {
        egresosAdapter.notifyDataSetChanged();
    }
}

