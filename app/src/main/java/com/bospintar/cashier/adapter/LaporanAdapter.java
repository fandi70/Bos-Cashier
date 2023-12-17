package com.bospintar.cashier.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bospintar.cashier.R;
import com.bospintar.cashier.model.Mlaporan;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LaporanAdapter extends RecyclerView.Adapter<LaporanAdapter.MyViewHolder> {
    private ArrayList<Mlaporan> arrayJenis;
    private Context mContext;
    private ArrayList<Mlaporan> arraylist;
    private String baru = "";

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

    public LaporanAdapter(ArrayList<Mlaporan> arrayJenis, Context context) {
        this.arrayJenis = arrayJenis;
        this.mContext = context;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(arrayJenis);
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