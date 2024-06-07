package com.example.lab6_20125424_iot.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab6_20125424_iot.R;

import java.util.List;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {
    private List<ListElementIngreso> ingresos;
    private List<ListElementEgreso> egresos;
    private Context context;

    public EntryAdapter(List<ListElementIngreso> ingresos, List<ListElementEgreso> egresos, Context context) {
        this.ingresos = ingresos;
        this.egresos = egresos;
        this.context = context;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingreso, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        if (ingresos != null) {
            ListElementIngreso ingreso = ingresos.get(position);
            holder.bind(ingreso);
        } else if (egresos != null) {
            ListElementEgreso egreso = egresos.get(position);
            holder.bind(egreso);
        }
    }

    @Override
    public int getItemCount() {
        return ingresos != null ? ingresos.size() : egresos.size();
    }

    public class EntryViewHolder extends RecyclerView.ViewHolder {
        private TextView title, amount, description, date;
        private ImageButton deleteButton;

        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            amount = itemView.findViewById(R.id.amount);
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(ListElementIngreso ingreso) {
            title.setText(ingreso.getTitulo());
            amount.setText(String.valueOf(ingreso.getMonto()));
            description.setText(ingreso.getDescripcion());
            date.setText(ingreso.getFecha());
            deleteButton.setOnClickListener(v -> {
                // Eliminar ingreso de Firebase
            });
        }

        public void bind(ListElementEgreso egreso) {
            title.setText(egreso.getTitulo());
            amount.setText(String.valueOf(egreso.getMonto()));
            description.setText(egreso.getDescripcion());
            date.setText(egreso.getFecha());
            deleteButton.setOnClickListener(v -> {
                // Eliminar egreso de Firebase
            });
        }
    }
}