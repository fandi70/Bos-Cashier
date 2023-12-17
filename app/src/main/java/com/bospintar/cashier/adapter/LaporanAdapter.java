package com.bospintar.cashier.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bospintar.cashier.R;
import com.bospintar.cashier.activity.Laporanperpetugas;
import com.bospintar.cashier.model.Mlaporan;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class LaporanAdapter extends RecyclerView.Adapter<LaporanAdapter.MyViewHolder> {
    private ArrayList<Mlaporan> arrayJenis;
    private Context mContext;
    private ArrayList<Mlaporan> arraylist;
    private String baru = "";
    private String xdari,xsampai;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nmpegawai,level,totalpenjualan,totaltransaksi;
        LinearLayout btpindah;



        MyViewHolder(View view) {
            super(view);
            nmpegawai = view.findViewById(R.id.txt_namapegawai);
            level = view.findViewById(R.id.txt_level);

            totalpenjualan = view.findViewById(R.id.txt_totalpenjualan);
            totaltransaksi = view.findViewById(R.id.txt_totaltransaksi);
            btpindah = view.findViewById(R.id.btpindah);

        }
    }

    public LaporanAdapter(ArrayList<Mlaporan> arrayJenis,String dari,String sampai, Context context) {
        this.arrayJenis = arrayJenis;
        this.mContext = context;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(arrayJenis);
        this.xdari = dari;
        this.xsampai = sampai;
    }
    public void setItemList(ArrayList<Mlaporan> arrayJenis) {
        this.arrayJenis = arrayJenis;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LaporanAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_laporan_item, parent, false);
        return new LaporanAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final LaporanAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        double amount =Double.parseDouble(arrayJenis.get(position).getTotal_penjualan());
        DecimalFormat rupiahFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        rupiahFormat.setParseBigDecimal(true);
        rupiahFormat.applyPattern("#,##0");
        String formattedRupiah = rupiahFormat.format(amount);

        holder.nmpegawai.setText(arrayJenis.get(position).getNama_petugas());
        holder.level.setText(arrayJenis.get(position).getLevel());
        holder.totaltransaksi.setText(arrayJenis.get(position).getTotal_transaksi()+" Transaksi");
        holder.totalpenjualan.setText("Rp"+formattedRupiah);
        holder.btpindah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,"dari:"+xdari+" sampai:"+xsampai, Toast.LENGTH_SHORT).show();
                Intent kotak = new Intent(mContext, Laporanperpetugas.class);
                kotak.putExtra("idpetugas",arrayJenis.get(position).getIdpetugas());
                kotak.putExtra("dari",xdari);
                kotak.putExtra("sampai",xsampai);
                mContext.startActivity(kotak);

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