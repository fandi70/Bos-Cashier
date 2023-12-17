package com.bospintar.cashier.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bospintar.cashier.R;
import com.bospintar.cashier.model.MpendingDetail;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PendingDetailAdapter extends RecyclerView.Adapter<PendingDetailAdapter.MyViewHolder> {
    private ArrayList<MpendingDetail> arrayJenis;
    private ArrayList<MpendingDetail> arraylist;
    private String baru = "";
    private Context mContext;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtharga;
        TextView txthargax;
        TextView txtnama;

        MyViewHolder(View view) {
            super(view);
            this.txtnama = (TextView) view.findViewById(R.id.txtnama);
            this.txtharga = (TextView) view.findViewById(R.id.txtharga);
            this.txthargax = (TextView) view.findViewById(R.id.txthargax);
        }
    }

    public PendingDetailAdapter(ArrayList<MpendingDetail> arrayJenis2, Context context) {
        this.arrayJenis = arrayJenis2;
        this.mContext = context;
        ArrayList<MpendingDetail> arrayList = new ArrayList<>();
        this.arraylist = arrayList;
        arrayList.addAll(arrayJenis2);
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_popuppending, parent, false));
    }

    public void onBindViewHolder(MyViewHolder holder, int position) {
        double amount = Double.parseDouble(this.arrayJenis.get(position).getHargajual()) * Double.parseDouble(this.arrayJenis.get(position).getQty());
        NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String formattedRupiah = rupiahFormat.format(amount);
        String harga = rupiahFormat.format(Double.parseDouble(this.arrayJenis.get(position).getHargajual()));
        holder.txtnama.setText(this.arrayJenis.get(position).getNama() + " x" + this.arrayJenis.get(position).getQty());
        holder.txtharga.setText(formattedRupiah);
        holder.txthargax.setText(harga );
        holder.txthargax.setPaintFlags(holder.txthargax.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
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
