package com.example.lab6_20125424_iot.item;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab6_20125424_iot.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class IngresosAdapter extends RecyclerView.Adapter<IngresosAdapter.IngresoViewHolder> {

    private List<ListElementIngreso> ingresosList;

    public IngresosAdapter(List<ListElementIngreso> ingresosList) {
        this.ingresosList = ingresosList;
    }

    @NonNull
    @Override
    public IngresoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingreso, parent, false);
        return new IngresoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngresoViewHolder holder, int position) {
        ListElementIngreso ingreso = ingresosList.get(position);
        holder.title.setText(ingreso.getTitle());
        holder.amount.setText(String.valueOf(ingreso.getAmount()));
        holder.description.setText(ingreso.getDescription());
        holder.date.setText(ingreso.getDate());

        holder.deleteButton.setOnClickListener(v -> {
            // Eliminar ingreso de Firebase y de la lista
            FirebaseFirestore.getInstance().collection("ingresos").document(ingreso.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        ingresosList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, ingresosList.size());
                    })
                    .addOnFailureListener(e -> {
                        // Manejar error
                    });
        });
    }

    @Override
    public int getItemCount() {
        return ingresosList.size();
    }

    public static class IngresoViewHolder extends RecyclerView.ViewHolder {
        TextView title, amount, description, date;
        ImageButton deleteButton;

        public IngresoViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            amount = itemView.findViewById(R.id.amount);
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
