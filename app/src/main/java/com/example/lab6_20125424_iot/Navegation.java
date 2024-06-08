package com.example.lab6_20125424_iot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.lab6_20125424_iot.dataHolder.DataManager;
import com.example.lab6_20125424_iot.item.ListElementEgreso;
import com.example.lab6_20125424_iot.item.ListElementIngreso;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class Navegation extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ingresos");

        setSupportActionBar(toolbar);

        replaceFragment(new IngresosFragment());
        mAuth = FirebaseAuth.getInstance();

        // Cargar el fragmento por defecto

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_ingresos);
        toolbar.setTitle("Ingresos");
        replaceFragment(new IngresosFragment());
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_ingresos) {
                toolbar.setTitle("Ingresos");
                replaceFragment(new IngresosFragment());
                return true;
            } else if (itemId == R.id.navigation_egresos) {
                toolbar.setTitle("Egresos");
                replaceFragment(new EgresosFragment());
                return true;
            } else if (itemId == R.id.navigation_resumen) {
                toolbar.setTitle("Resumen");
                replaceFragment(new ResumenFragment());
                return true;
            } else if (itemId == R.id.navigation_logout) {
                toolbar.setTitle("Cerrar sesión");
                logout();
                return true;
            }
            return false;
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
        fragmentTransaction.commit();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(Navegation.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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
}
