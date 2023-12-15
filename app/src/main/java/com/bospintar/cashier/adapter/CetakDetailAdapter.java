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
import com.bospintar.cashier.model.MpendingDetail;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CetakDetailAdapter extends RecyclerView.Adapter<CetakDetailAdapter.MyViewHolder> {
    private ArrayList<MpendingDetail> arrayJenis;
    private Context mContext;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtnamaproduk, txtharga,txtjumlah,txttotal;
        LinearLayout btpindah;




        MyViewHolder(View view) {
            super(view);
            txtnamaproduk = view.findViewById(R.id.txtnamaproduk);
            txtharga = view.findViewById(R.id.txtharga);

            txtjumlah=view.findViewById(R.id.txtjumlah);
            txttotal=view.findViewById(R.id.txttotal);
            btpindah = view.findViewById(R.id.btpindah);

        }
    }

    public CetakDetailAdapter(ArrayList<MpendingDetail> arrayJenis, Context context) {
        this.arrayJenis = arrayJenis;
        this.mContext = context;
    }
    public void removeItem(int position) {
        arrayJenis.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public CetakDetailAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detail, parent, false);
        return new CetakDetailAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final CetakDetailAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        double amount =Double.parseDouble(arrayJenis.get(position).getHargajual());
        double total =Integer.parseInt(arrayJenis.get(position).getQty())*Integer.parseInt(arrayJenis.get(position).getHargajual());
        DecimalFormat rupiahFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        rupiahFormat.setParseBigDecimal(true);
        rupiahFormat.applyPattern("#,##0");
        String formattedRupiah = rupiahFormat.format(amount);
        String formattedRupiah2 = rupiahFormat.format(total);

        holder.txtnamaproduk.setText(arrayJenis.get(position).getNama());
        holder.txtharga.setText("Rp"+formattedRupiah);
        holder.txtjumlah.setText("x"+arrayJenis.get(position).getQty());
        holder.txttotal.setText("Rp"+formattedRupiah2);



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