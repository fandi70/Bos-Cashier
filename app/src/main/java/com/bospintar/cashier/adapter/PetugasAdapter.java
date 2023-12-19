package com.bospintar.cashier.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bospintar.cashier.R;
import com.bospintar.cashier.model.Mpelanggan;
import com.bospintar.cashier.model.Mpetugas;

import java.util.ArrayList;
import java.util.Locale;

public class PetugasAdapter extends RecyclerView.Adapter<PetugasAdapter.MyViewHolder> {
    private ArrayList<Mpetugas> arrayJenis;
    private Context mContext;
    private ArrayList<Mpetugas> arraylist;
    private String baru = "";

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_nm, txt_hrg;
        LinearLayout btpindah;



        MyViewHolder(View view) {
            super(view);
            txt_nm = view.findViewById(R.id.txt_namapegawai);
            txt_hrg = view.findViewById(R.id.txt_level);
            btpindah = view.findViewById(R.id.btpindah);

        }
    }

    public PetugasAdapter(ArrayList<Mpetugas> arrayJenis, Context context) {
        this.arrayJenis = arrayJenis;
        this.mContext = context;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(arrayJenis);
    }

    @NonNull
    @Override
    public PetugasAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_petugas_item, parent, false);
        return new PetugasAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final PetugasAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.txt_nm.setText(arrayJenis.get(position).getNama());
        holder.txt_hrg.setText(arrayJenis.get(position).getLevel());

        holder.btpindah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    public void filter(String charText, TextView itemView, ImageView img) {
        charText = charText.toLowerCase(Locale.getDefault());
        arrayJenis.clear();
        if (charText.length() == 0) {
            arrayJenis.addAll(arraylist);
            baru = "";
            itemView.setVisibility(View.GONE);
            img.setVisibility(View.GONE);
        } else {
            for (Mpetugas wp : arraylist) {
                if (wp.getNama().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayJenis.add(wp);
                    baru = charText;
                    itemView.setVisibility(View.GONE);
                    img.setVisibility(View.GONE);
                }
            }
            if (arrayJenis.isEmpty()) {
                itemView.setVisibility(View.VISIBLE);
                img.setVisibility(View.VISIBLE);
                img.setImageResource(R.drawable.ic_search_empty);
                itemView.setText("Tidak ada data untuk " + "'" + charText + "'");
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return arrayJenis.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}