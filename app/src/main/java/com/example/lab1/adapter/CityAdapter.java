package com.example.lab1.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab1.R;
import com.example.lab1.model.City;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {
    private final Context context;
    public ArrayList<City> list;
    FirebaseFirestore db;
    public CityAdapter(Context context, ArrayList<City> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public CityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.iteam, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityAdapter.ViewHolder holder, int position) {
        City city = list.get(position);
        db = FirebaseFirestore.getInstance();
        holder.txtTen.setText(city.getName());
        holder.txtQuocGia.setText(city.getCountry());
        holder.txtSoDan.setText(String.valueOf(city.getPopulation()));
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
                notifyItemRemoved(position);
                deleteCityFromFirestore(city,position);
            }
        });
        holder.lil.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showUpdateDialog(city, position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    private void updateCityOnFirestore(String cityId, String newCityName, String newCountry, int newPopulation, int position) {
        DocumentReference cityRef = db.collection("cities").document(cityId);
        cityRef.update("name", newCityName, "country", newCountry, "population", newPopulation)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            list.get(position).setName(newCityName);
                            list.get(position).setCountry(newCountry);
                            list.get(position).setPopulation(newPopulation);
                            notifyItemChanged(position);
                        } else {
                            Toast.makeText(context, "Thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void showUpdateDialog(City city, int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.iteam_add_update, null);
        dialogBuilder.setView(dialogView);

        final EditText edtCity = dialogView.findViewById(R.id.edtCity);
        final EditText edtCountry = dialogView.findViewById(R.id.edtCountry);
        final EditText edtPoPu = dialogView.findViewById(R.id.edtPoPu);

        edtCity.setText(city.getName());
        edtCountry.setText(city.getCountry());
        edtPoPu.setText(String.valueOf(city.getPopulation()));

        dialogBuilder.setTitle("Update Item");
        dialogBuilder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newCityName = edtCity.getText().toString();
                String newCountry = edtCountry.getText().toString();
                int newPopulation = Integer.parseInt(edtPoPu.getText().toString());

                if (!newCityName.isEmpty() && !newCountry.isEmpty() && newPopulation > 0) {
                    // Cập nhật thành phố trên Firestore
                    updateCityOnFirestore(city.getId(), newCityName, newCountry, newPopulation, position);
                }
            }
        });

        dialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void deleteCityFromFirestore(City city, int position) {
        if (city.getId() != null && !city.getId().isEmpty()) {
            db.collection("cities").document(city.getId()).delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (position >= 0 && position < list.size()) {
                                    list.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, list.size());
                                } else {
                                    Log.d("CityAdapter", "Invalid position: " + position);
                                }
                            } else {
                                Log.d("CityAdapter", "Xóa thất bại", task.getException());
                            }
                        }
                    });
        } else {
            Log.d("CityAdapter", "City ID is null or empty");
        }}


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTen, txtQuocGia, txtSoDan;

        ImageButton btnDelete;
        LinearLayout lil;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTen = itemView.findViewById(R.id.txtCity);
            txtQuocGia = itemView.findViewById(R.id.txtCoun);
            txtSoDan = itemView.findViewById(R.id.txtPopu);
            btnDelete = itemView.findViewById(R.id.imgBtnDelete);
            lil = itemView.findViewById(R.id.liner1);
        }
    }
}
