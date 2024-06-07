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
import com.example.lab6_20125424_iot.item.IngresosAdapter;
import com.example.lab6_20125424_iot.item.ListElementIngreso;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class IngresosFragment extends Fragment {
    private IngresosAdapter ingresosAdapter;
    private RecyclerView recyclerViewUsers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank_ingresos, container, false);
        initializeViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateIngresosList();
    }

    private void initializeViews(View view) {
        recyclerViewUsers = view.findViewById(R.id.recyclerViewIngresos);
        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getContext()));

        ingresosAdapter = new IngresosAdapter(DataManager.getInstance().getIngresosList(), item -> {
            Intent intent = new Intent(getActivity(), NuevoIngresoEgreso.class);
            intent.putExtra("entry_type", "ingreso");
            intent.putExtra("item_data", item); // Asegúrate de que ListElementIngreso implemente Parcelable o Serializable
            startActivity(intent);
        });
        recyclerViewUsers.setAdapter(ingresosAdapter);

        FloatingActionButton agregarUsuarioButton = view.findViewById(R.id.fabIngresos);
        agregarUsuarioButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NuevoIngresoEgreso.class);
            intent.putExtra("entry_type", "ingreso");
            startActivity(intent);
        });
    }

    private void updateIngresosList() {
        ingresosAdapter.notifyDataSetChanged();
    }
}
