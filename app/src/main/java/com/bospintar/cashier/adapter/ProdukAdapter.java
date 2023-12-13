package com.bospintar.cashier.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bospintar.cashier.R;
import com.bospintar.cashier.model.Mproduk;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ProdukAdapter extends RecyclerView.Adapter<ProdukAdapter.MyViewHolder> {
    private ArrayList<Mproduk> arrayJenis;
    private Context mContext;
    private ArrayList<Mproduk> arraylist;
    private String baru = "";

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_nm, txt_hrg,txt_status;
        LinearLayout btpindah;



        MyViewHolder(View view) {
            super(view);
            txt_nm = view.findViewById(R.id.txtnamabarang);
            txt_hrg = view.findViewById(R.id.txtharga);

            txt_status=view.findViewById(R.id.txtstatusgrosir);
            btpindah = view.findViewById(R.id.btpindah);

        }
    }

    public ProdukAdapter(ArrayList<Mproduk> arrayJenis, Context context) {
        this.arrayJenis = arrayJenis;
        this.mContext = context;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(arrayJenis);
    }
    public void setItemList(ArrayList<Mproduk> arrayJenis) {
        this.arrayJenis = arrayJenis;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProdukAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_produk_item, parent, false);
        return new ProdukAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ProdukAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        double amount =Double.parseDouble(arrayJenis.get(position).getHargajual());
        DecimalFormat rupiahFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        rupiahFormat.setParseBigDecimal(true);
        rupiahFormat.applyPattern("#,##0");
        String formattedRupiah = rupiahFormat.format(amount);

        holder.txt_nm.setText(arrayJenis.get(position).getNama());
        holder.txt_hrg.setText(formattedRupiah);
        if(arrayJenis.get(position).getGrosir().equals("Y")){
            holder.txt_status.setText("Grosir");
        }else{
            holder.txt_status.setText("");
        }


        holder.btpindah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, arrayJenis.get(position).getGrosir(), Toast.LENGTH_SHORT).show();

            }
        });
        String notes = arrayJenis.get(position).getNama();
        SpannableStringBuilder sb = new SpannableStringBuilder(notes);
        Pattern p = Pattern.compile(baru, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(notes);
        while (m.find()) {
            sb.setSpan(new ForegroundColorSpan(Color.rgb(255, 0, 0)), m.start(), m.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        holder.txt_nm.setText(sb);
    }
    @SuppressLint("SetTextI18n")
    public void filter(String charText, TextView itemView) {
        charText = charText.toLowerCase(Locale.getDefault());
        arrayJenis.clear();
        if (charText.length() == 0) {
            arrayJenis.addAll(arraylist);
            baru = "";
            itemView.setVisibility(View.GONE);
        } else {
            for (Mproduk wp : arraylist) {
                if (wp.getNama().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayJenis.add(wp);
                    baru = charText;
                    itemView.setVisibility(View.GONE);
                }
            }
            if (arrayJenis.isEmpty()) {
                itemView.setVisibility(View.VISIBLE);
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

