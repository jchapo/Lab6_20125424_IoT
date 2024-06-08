package com.example.lab6_20125424_iot.item;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab6_20125424_iot.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

// EgresosAdapter.java
public class EgresosAdapter extends RecyclerView.Adapter<EgresosAdapter.EgresoViewHolder> {

    private List<ListElementEgreso> egresosList;
    private OnItemClickListener listener;

    public EgresosAdapter(List<ListElementEgreso> egresosList, OnItemClickListener listener) {
        this.egresosList = egresosList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EgresoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingreso, parent, false);
        return new EgresoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EgresoViewHolder holder, int position) {
        ListElementEgreso egreso = egresosList.get(position);
        holder.title.setText(egreso.getTitle());
        holder.amount.setText(String.valueOf(egreso.getAmount()));
        holder.description.setText(egreso.getDescription());
        holder.date.setText(egreso.getDate());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(egreso));

        holder.deleteButton.setOnClickListener(v -> {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String path = "users/" + uid + "/" + "egresos";
            FirebaseFirestore.getInstance().collection(path).document(egreso.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        egresosList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, egresosList.size());
                    })
                    .addOnFailureListener(e -> {
                        // Manejar error
                    });
        });
    }

    @Override
    public int getItemCount() {
        return egresosList.size();
    }

    public static class EgresoViewHolder extends RecyclerView.ViewHolder {
        TextView title, amount, description, date;
        ImageButton deleteButton;

        public EgresoViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            amount = itemView.findViewById(R.id.amount);
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ListElementEgreso egreso);
    }
}
