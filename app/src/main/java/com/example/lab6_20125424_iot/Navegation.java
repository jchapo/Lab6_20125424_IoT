package com.example.lab6_20125424_iot;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Navegation extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ingresos");

        setSupportActionBar(toolbar);

        replaceFragment(new IngresosFragment());

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
}
