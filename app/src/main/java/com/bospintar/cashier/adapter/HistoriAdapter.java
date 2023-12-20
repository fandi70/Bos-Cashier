package com.bospintar.cashier.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bospintar.cashier.R;
import com.bospintar.cashier.activity.Histori;
import com.bospintar.cashier.activity.LaporanCetakActivity;
import com.bospintar.cashier.activity.Menu;
import com.bospintar.cashier.activity.TransaksiCetakActivity;
import com.bospintar.cashier.model.Mhome;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HistoriAdapter extends RecyclerView.Adapter<HistoriAdapter.MyViewHolder> {
    private ArrayList<Mhome> arrayJenis;
    private Context mContext;
    private ArrayList<Mhome> arraylist;
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

    public HistoriAdapter(ArrayList<Mhome> arrayJenis, Context context) {
        this.arrayJenis = arrayJenis;
        this.mContext = context;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(arrayJenis);
    }
    public void setItemList(ArrayList<Mhome> arrayJenis) {
        this.arrayJenis = arrayJenis;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoriAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_home_item, parent, false);
        return new HistoriAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final HistoriAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
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
                Intent kotak = new Intent(mContext, LaporanCetakActivity.class);
                kotak.putExtra("id",arrayJenis.get(position).getId());
                mContext.startActivity(kotak);

            }
        });
        String notes = arrayJenis.get(position).getNota();
        SpannableStringBuilder sb = new SpannableStringBuilder(notes);
        Pattern p = Pattern.compile(baru, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(notes);
        while (m.find()) {
            sb.setSpan(new ForegroundColorSpan(Color.rgb(255, 0, 0)), m.start(), m.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        holder.nota.setText(sb);
    }
//    @SuppressLint("SetTextI18n")
//    public void filter(String charText, TextView itemView) {
//        charText = charText.toLowerCase(Locale.getDefault());
//        arrayJenis.clear();
//        if (charText.length() == 0) {
//            arrayJenis.addAll(arraylist);
//            baru = "";
//            itemView.setVisibility(View.GONE);
//        } else {
//            for (Mhome wp : arraylist) {
//                if (wp.getNota().toLowerCase(Locale.getDefault()).contains(charText)) {
//                    arrayJenis.add(wp);
//                    baru = charText;
//                    itemView.setVisibility(View.GONE);
//                }
//            }
//            if (arrayJenis.isEmpty()) {
//                itemView.setVisibility(View.VISIBLE);
//                itemView.setText("Tidak ada data untuk " + "'" + charText + "'");
//            }
//        }
//        notifyDataSetChanged();
//    }

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