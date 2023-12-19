package com.bospintar.cashier.adapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.bospintar.cashier.R; // Replace with your project's R import
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bospintar.cashier.R;
import com.bospintar.cashier.activity.Pengeluaran;
import com.bospintar.cashier.activity.TransaksiDetailActivity;
import com.bospintar.cashier.model.Mpengeluaran;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PengeluaranAdapter extends RecyclerView.Adapter<PengeluaranAdapter.MyViewHolder> {
    private ArrayList<Mpengeluaran> arrayJenis;
    private Context mContext;
    private ArrayList<Mpengeluaran> arraylist;
    private String baru = "";
    private ItemTouchHelper itemTouchHelper;

    static class MyViewHolder extends RecyclerView.ViewHolder {
                TextView txt_tanggal,keterangan,nominal;
        LinearLayout btpindah;
        ImageView btedit,btdelete;



        MyViewHolder(View view) {
            super(view);
                        txt_tanggal = view.findViewById(R.id.txttanggal);
            keterangan = view.findViewById(R.id.txtketerangan);

            nominal = view.findViewById(R.id.txtnominal);
            btpindah = view.findViewById(R.id.btpindah);
            btedit = view.findViewById(R.id.btedit);
            btdelete = view.findViewById(R.id.btdelete);

        }
    }

    public PengeluaranAdapter(ArrayList<Mpengeluaran> arrayJenis, Context context, RecyclerView recyclerView) {
        this.arrayJenis = arrayJenis;
        this.mContext = context;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(arrayJenis);

    }
    public void setItemList(ArrayList<Mpengeluaran> arrayJenis) {
        this.arrayJenis = arrayJenis;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PengeluaranAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_pengeluaran_item, parent, false);
        return new PengeluaranAdapter.MyViewHolder(itemView);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final PengeluaranAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        double amount =Double.parseDouble(arrayJenis.get(position).getNominal());
        DecimalFormat rupiahFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        rupiahFormat.setParseBigDecimal(true);
        rupiahFormat.applyPattern("#,##0");
        String formattedRupiah = rupiahFormat.format(amount);

        holder.txt_tanggal.setText(arrayJenis.get(position).getTanggal());
        holder.keterangan.setText(arrayJenis.get(position).getKeterangan());
        holder.nominal.setText("Rp"+formattedRupiah);

        holder.btedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        ((Pengeluaran)mContext).onClick(arrayJenis.get(position).getId().toString(),arrayJenis.get(position).getKeterangan().toString(),"Rp"+formattedRupiah);


            }
        });
        holder.btdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Anda yakin ingin menghapus data ini?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteItem(position,arrayJenis.get(position).getId());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                notifyDataSetChanged(); // Notify to refresh view
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

    }
    @SuppressLint("SetTextI18n")


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
    public void deleteItem(int position,String idpengeluaran) {
        // Remove item at the given position from your data list
        arrayJenis.remove(position);
        // Notify RecyclerView about the item being removed
        notifyItemRemoved(position);
        ((Pengeluaran)mContext).hapusitem(idpengeluaran);

    }
}