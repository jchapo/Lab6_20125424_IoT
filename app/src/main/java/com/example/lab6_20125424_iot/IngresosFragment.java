package com.example.lab6_20125424_iot;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lab6_20125424_iot.item.IngresosAdapter;
import com.example.lab6_20125424_iot.item.ListElementIngreso;
import com.example.lab6_20125424_iot.viewModels.NavigationActivityViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class IngresosFragment extends Fragment {
    private ArrayList<ListElementIngreso> ingresoslista = new ArrayList<>();
    private IngresosAdapter ingresosAdapter;
    private RecyclerView recyclerViewUsers;
    private NavigationActivityViewModel navigationActivityViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank_ingresos, container, false);
        initializeViews(view);
        observeViewModel();
        return view;
    }

    private void observeViewModel() {

        if (navigationActivityViewModel != null) {
            navigationActivityViewModel.getListaIngreso().observe(getViewLifecycleOwner(), infresosActivos -> {
                ingresoslista.clear();
                ingresosAdapter.notifyDataSetChanged();
                ingresoslista.addAll(infresosActivos);
            });
        }
    }


    private void initializeViews(View view) {
        ingresosAdapter = new IngresosAdapter(ingresoslista);
        recyclerViewUsers = view.findViewById(R.id.recyclerView);
        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewUsers.setAdapter(ingresosAdapter);
        FloatingActionButton agregarUsuarioButton = view.findViewById(R.id.fabIngresos);
        agregarUsuarioButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NuevoIngresoEgreso.class);
            startActivity(intent);
        });

    }
}
