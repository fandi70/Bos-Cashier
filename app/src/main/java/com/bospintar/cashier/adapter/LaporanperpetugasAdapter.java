package com.bospintar.cashier.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bospintar.cashier.R;
import com.bospintar.cashier.model.Mlaporanpetugas;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class LaporanperpetugasAdapter extends RecyclerView.Adapter<LaporanperpetugasAdapter.MyViewHolder> {
    private ArrayList<Mlaporanpetugas> arrayJenis;
    private Context mContext;
    private ArrayList<Mlaporanpetugas> arraylist;
    private String baru = "";

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nota,total,tgl,status;
        LinearLayout btpindah;



        MyViewHolder(View view) {
            super(view);
            nota = view.findViewById(R.id.txt_nota);
            total = view.findViewById(R.id.txt_totalbayar);

            tgl = view.findViewById(R.id.txt_tanggal);
            status = view.findViewById(R.id.txt_status);
            btpindah = view.findViewById(R.id.btpindah);

        }
    }

    public LaporanperpetugasAdapter(ArrayList<Mlaporanpetugas> arrayJenis, Context context) {
        this.arrayJenis = arrayJenis;
        this.mContext = context;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(arrayJenis);
    }
    public void setItemList(ArrayList<Mlaporanpetugas> arrayJenis) {
        this.arrayJenis = arrayJenis;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LaporanperpetugasAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_laporanperpetugas_item, parent, false);
        return new LaporanperpetugasAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final LaporanperpetugasAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        double amount =Double.parseDouble(arrayJenis.get(position).getTotalbayar());
        DecimalFormat rupiahFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        rupiahFormat.setParseBigDecimal(true);
        rupiahFormat.applyPattern("#,##0");
        String formattedRupiah = rupiahFormat.format(amount);

        holder.nota.setText(arrayJenis.get(position).getNota());
        holder.tgl.setText(arrayJenis.get(position).getTanggal());
        holder.total.setText("Rp"+formattedRupiah);
        holder.status.setText(arrayJenis.get(position).getJbayar());

        holder.btpindah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent kotak = new Intent(mContext, TransaksiCetakActivity.class);
//                kotak.putExtra("id",arrayJenis.get(position).getId());
//                mContext.startActivity(kotak);

            }
        });

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