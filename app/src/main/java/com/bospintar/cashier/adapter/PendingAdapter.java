package com.bospintar.cashier.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bospintar.cashier.R;
import com.bospintar.cashier.activity.PendingActivity;
import com.bospintar.cashier.model.Mpending;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.MyViewHolder> {
    /* access modifiers changed from: private */
    public ArrayList<Mpending> arrayJenis;
    private ArrayList<Mpending> arraylist;
    private String baru = "";
    /* access modifiers changed from: private */
    public Context mContext;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout btpindah;
        TextView txtharga;
        TextView txtnama;
        TextView txttgl;

        MyViewHolder(View view) {
            super(view);
            this.txtnama = (TextView) view.findViewById(R.id.txtnama);
            this.txtharga = (TextView) view.findViewById(R.id.txtharga);
            this.txttgl = (TextView) view.findViewById(R.id.txttgl);
            this.btpindah = (LinearLayout) view.findViewById(R.id.btpindah);
        }
    }

    public PendingAdapter(ArrayList<Mpending> arrayJenis2, Context context) {
        this.arrayJenis = arrayJenis2;
        this.mContext = context;
        ArrayList<Mpending> arrayList = new ArrayList<>();
        this.arraylist = arrayList;
        arrayList.addAll(arrayJenis2);
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pending, parent, false));
    }

    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        String formattedRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID")).format(Double.parseDouble(this.arrayJenis.get(position).getTotalbayar()));
        holder.txtnama.setText(this.arrayJenis.get(position).getIdtrnasaksi());
        holder.txtharga.setText(formattedRupiah);
        holder.txttgl.setText(this.arrayJenis.get(position).getTanggal());
        holder.btpindah.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ((PendingActivity) mContext).showDialogPending((arrayJenis.get(position)).getIdtrnasaksi());
            }
        });
        String notes = this.arrayJenis.get(position).getIdtrnasaksi();
        SpannableStringBuilder sb = new SpannableStringBuilder(notes);
        Matcher m = Pattern.compile(this.baru, 2).matcher(notes);
        while (m.find()) {
            sb.setSpan(new ForegroundColorSpan(Color.rgb(255, 0, 0)), m.start(), m.end(), 18);
        }
        holder.txtnama.setText(sb);
    }

    public void filter(String charText, TextView itemView) {
        String charText2 = charText.toLowerCase(Locale.getDefault());
        this.arrayJenis.clear();
        if (charText2.length() == 0) {
            this.arrayJenis.addAll(this.arraylist);
            this.baru = "";
            itemView.setVisibility(View.GONE);
        } else {
            Iterator<Mpending> it = this.arraylist.iterator();
            while (it.hasNext()) {
                Mpending wp = it.next();
                if (wp.getIdtrnasaksi().toLowerCase(Locale.getDefault()).contains(charText2)) {
                    this.arrayJenis.add(wp);
                    this.baru = charText2;
                    itemView.setVisibility(View.GONE);
                }
            }
            if (this.arrayJenis.isEmpty()) {
                itemView.setVisibility(View.VISIBLE);
                itemView.setText("Tidak ada data untuk '" + charText2 + "'");
            }
        }
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return this.arrayJenis.size();
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public int getItemViewType(int position) {
        return position;
    }
}
