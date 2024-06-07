package com.example.lab6_20125424_iot;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab6_20125424_iot.dataHolder.DataManager;
import com.example.lab6_20125424_iot.item.ListElementEgreso;
import com.example.lab6_20125424_iot.item.ListElementIngreso;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NuevoIngresoEgreso extends AppCompatActivity {

    private EditText etTitle, etAmount, etDescription, etDate;
    private Button btnSave;
    private FloatingActionButton fabEdit;
    private FirebaseAuth mAuth;

    private FirebaseFirestore db;
    private MaterialToolbar topAppBar;
    private ListElementIngreso currentIngreso;
    private ListElementEgreso currentEgreso;
    private String entryType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        etTitle = findViewById(R.id.title);
        etAmount = findViewById(R.id.amount);
        etDescription = findViewById(R.id.description);
        etDate = findViewById(R.id.date);
        btnSave = findViewById(R.id.createButton);
        fabEdit = findViewById(R.id.fabEdit);
        topAppBar = findViewById(R.id.topAppBar);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        // Configurar título dinámico basado en el intent
        entryType = getIntent().getStringExtra("entry_type");
        if ("ingreso".equals(entryType)) {
            currentIngreso = (ListElementIngreso) getIntent().getSerializableExtra("ListElement");
        } else if ("egreso".equals(entryType)) {
            currentEgreso = (ListElementEgreso) getIntent().getSerializableExtra("ListElement");
        }

        if (currentIngreso != null) {
            configureViewForDetails(currentIngreso.getTitle(), currentIngreso.getAmount(), currentIngreso.getDescription(), currentIngreso.getDate());
            btnSave.setOnClickListener(v -> updateIngreso());
        } else if (currentEgreso != null) {
            configureViewForDetails(currentEgreso.getTitle(), currentEgreso.getAmount(), currentEgreso.getDescription(), currentEgreso.getDate());
            btnSave.setOnClickListener(v -> updateEgreso());
        } else {
            topAppBar.setTitle("Nuevo " + entryType);
            fabEdit.setVisibility(View.GONE);
            etTitle.setEnabled(true);
            etDate.setEnabled(true);
            btnSave.setOnClickListener(v -> saveEntry(entryType));
        }

        topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void configureViewForDetails(String title, double amount, String description, String date) {
        topAppBar.setTitle("Detalles de " + entryType);
        etTitle.setText(title);
        etAmount.setText(String.valueOf(amount));
        etDescription.setText(description);
        etDate.setText(date);

        etTitle.setEnabled(false);
        etDate.setEnabled(false);
        btnSave.setText("Actualizar");
        btnSave.setVisibility(View.INVISIBLE);

        fabEdit.setVisibility(View.VISIBLE);
        fabEdit.setOnClickListener(v -> {
            etAmount.setEnabled(true);
            etDescription.setEnabled(true);
            btnSave.setVisibility(View.VISIBLE);
            fabEdit.setVisibility(View.GONE); // Ocultar el botón fabEdit
        });
    }

    private void saveEntry(String entryType) {
        String title = etTitle.getText().toString().trim();
        String amountString = etAmount.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String date = etDate.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(amountString) || TextUtils.isEmpty(description) || TextUtils.isEmpty(date)) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> entry = new HashMap<>();
        entry.put("title", title);
        entry.put("amount", amount);
        entry.put("description", description);
        entry.put("date", date);

        String uid = mAuth.getCurrentUser().getUid();
        String path = "users/" + uid + "/" + entryType + "s";
        Log.d("msg-test", path);

        db.collection(path)
                .add(entry)
                .addOnSuccessListener(documentReference -> {
                    String id = documentReference.getId();
                    if ("ingreso".equals(entryType)) {
                        ListElementIngreso newEntry = new ListElementIngreso(id, title, amount, description, date);
                        DataManager.getInstance().addIngreso(newEntry);
                    } else {
                        ListElementEgreso newEntry = new ListElementEgreso(id, title, amount, description, date);
                        DataManager.getInstance().addEgreso(newEntry);
                    }
                    Toast.makeText(NuevoIngresoEgreso.this, "Entrada guardada", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NuevoIngresoEgreso.this, "Error al guardar entrada", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateIngreso() {
        String amountString = etAmount.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (TextUtils.isEmpty(amountString) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> entry = new HashMap<>();
        entry.put("amount", amount);
        entry.put("description", description);

        String collection = "users/" + DataManager.getInstance().getUserId() + "/ingresos";

        db.collection(collection).document(currentIngreso.getId())
                .update(entry)
                .addOnSuccessListener(aVoid -> {
                    currentIngreso.setAmount(amount);
                    currentIngreso.setDescription(description);
                    Toast.makeText(NuevoIngresoEgreso.this, "Ingreso actualizado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NuevoIngresoEgreso.this, "Error al actualizar ingreso", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateEgreso() {
        String amountString = etAmount.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (TextUtils.isEmpty(amountString) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> entry = new HashMap<>();
        entry.put("amount", amount);
        entry.put("description", description);

        String collection = "users/" + DataManager.getInstance().getUserId() + "/egresos";

        db.collection(collection).document(currentEgreso.getId())
                .update(entry)
                .addOnSuccessListener(aVoid -> {
                    currentEgreso.setAmount(amount);
                    currentEgreso.setDescription(description);
                    Toast.makeText(NuevoIngresoEgreso.this, "Egreso actualizado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NuevoIngresoEgreso.this, "Error al actualizar egreso", Toast.LENGTH_SHORT).show();
                });
    }
}
