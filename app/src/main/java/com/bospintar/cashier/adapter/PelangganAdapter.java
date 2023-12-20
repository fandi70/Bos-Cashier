package com.bospintar.cashier.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bospintar.cashier.R;
import com.bospintar.cashier.activity.Pelanggan;
import com.bospintar.cashier.activity.Produk;
import com.bospintar.cashier.model.Mpelanggan;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PelangganAdapter extends RecyclerView.Adapter<PelangganAdapter.MyViewHolder> {
    private ArrayList<Mpelanggan> arrayJenis;
    private Context mContext;
    private ArrayList<Mpelanggan> arraylist;
    private String baru = "";

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_namapelanggan, txt_nohp;
        LinearLayout btpindah;
        ImageView btedit,btdelete;



        MyViewHolder(View view) {
            super(view);
            txt_namapelanggan = view.findViewById(R.id.txt_namapelanggan);
            txt_nohp = view.findViewById(R.id.txt_nohp);
            btpindah = view.findViewById(R.id.btpindah);
            btedit = view.findViewById(R.id.btedit);
            btdelete = view.findViewById(R.id.btdelete);

        }
    }

    public PelangganAdapter(ArrayList<Mpelanggan> arrayJenis, Context context, RecyclerView recyclerView) {
        this.arrayJenis = arrayJenis;
        this.mContext = context;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(arrayJenis);
    }
    public void setItemList(ArrayList<Mpelanggan> arrayJenis) {
        this.arrayJenis = arrayJenis;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PelangganAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_pelanggan_item, parent, false);
        return new PelangganAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final PelangganAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {


        holder.txt_namapelanggan.setText(arrayJenis.get(position).getNama());
        holder.txt_nohp.setText(arrayJenis.get(position).getNohp());



        holder.btedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Pelanggan)mContext).onClick(arrayJenis.get(position).getId().toString(),arrayJenis.get(position).getNama().toString(),arrayJenis.get(position).getAlamat().toString(),arrayJenis.get(position).getNohp().toString());


            }
        });
        holder.btdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_informasi);

                TextView cancelButton = dialog.findViewById(R.id.cancelButton);
                TextView okButton = dialog.findViewById(R.id.okButton);
                TextView txtjudul = dialog.findViewById(R.id.txtjudul);
                TextView txtsubjudul = dialog.findViewById(R.id.txtsubjudul);
                ImageView imginfo = dialog.findViewById(R.id.imginfo);
                imginfo.setImageResource(R.drawable.ic_alert_delete);
                txtjudul.setText("Hapus data Pelanggan");
                txtsubjudul.setText("Apakah anda yakin untuk menghapus pelanggan "+arrayJenis.get(position).getNama()+"?");
                okButton.setText("Yakin");
                cancelButton.setText("Tidak");

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        ((Pelanggan)mContext).HapusData(arrayJenis.get(position).getId());

                        dialog.dismiss();
                        arrayJenis.remove(position);
                        // Notify RecyclerView about the item being removed
                        notifyItemRemoved(position);

                    }
                });

                dialog.setCancelable(false);


                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            }
        });
        String notes = arrayJenis.get(position).getNama();
        SpannableStringBuilder sb = new SpannableStringBuilder(notes);
        Pattern p = Pattern.compile(baru, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(notes);
        while (m.find()) {
            sb.setSpan(new ForegroundColorSpan(Color.rgb(255, 0, 0)), m.start(), m.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        holder.txt_namapelanggan.setText(sb);
    }
    @SuppressLint("SetTextI18n")
    public void filter(String charText, TextView itemView, ImageView img) {
        charText = charText.toLowerCase(Locale.getDefault());
        arrayJenis.clear();
        if (charText.length() == 0) {
            arrayJenis.addAll(arraylist);
            baru = "";
            itemView.setVisibility(View.GONE);
            img.setVisibility(View.GONE);
        } else {
            for (Mpelanggan wp : arraylist) {
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