package com.bospintar.cashier.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bospintar.cashier.R;
import com.bospintar.cashier.activity.TransaksiDetailActivity;
import com.bospintar.cashier.model.MtransaksiDetail;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TransaksiDetailAdapter extends RecyclerView.Adapter<TransaksiDetailAdapter.MyViewHolder> {
    private ArrayList<MtransaksiDetail> arrayJenis;
    private Context mContext;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtnamaproduk, txtharga,txttotal;
        ImageView minus,plus;
        EditText txtjumlah;
        LinearLayout btpindah;




        MyViewHolder(View view) {
            super(view);
            txtnamaproduk = view.findViewById(R.id.txtnamaproduk);
            txtharga = view.findViewById(R.id.txtharga);

            txtjumlah=view.findViewById(R.id.txtjumlah);
            plus=view.findViewById(R.id.plus);
            minus=view.findViewById(R.id.minus);
            txttotal=view.findViewById(R.id.txttotal);
            btpindah = view.findViewById(R.id.btpindah);

            //tombol enter di keyboard
            txtjumlah.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(actionId == EditorInfo.IME_ACTION_DONE){
                        //do stuff
                        return true;
                    }
                    return false;
                }
            });

        }
    }

    public TransaksiDetailAdapter(ArrayList<MtransaksiDetail> arrayJenis, Context context) {
        this.arrayJenis = arrayJenis;
        this.mContext = context;
    }
    public void removeItem(int position) {
        arrayJenis.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public TransaksiDetailAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaksi_detail, parent, false);
        return new TransaksiDetailAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final TransaksiDetailAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        double amount =Double.parseDouble(arrayJenis.get(position).getHarga_jual());
        double total =Integer.parseInt(arrayJenis.get(position).getJumlah_penjualan())*Integer.parseInt(arrayJenis.get(position).getHarga_jual());
        DecimalFormat rupiahFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        rupiahFormat.setParseBigDecimal(true);
        rupiahFormat.applyPattern("#,##0");
        String formattedRupiah = rupiahFormat.format(amount);
        String formattedRupiah2 = rupiahFormat.format(total);

        holder.txtnamaproduk.setText(arrayJenis.get(position).getNamabarang());
        holder.txtharga.setText("Rp"+formattedRupiah);
        holder.txtjumlah.setText(arrayJenis.get(position).getJumlah_penjualan());
        holder.txttotal.setText("Rp"+formattedRupiah2);
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int jumlah = Integer.parseInt(arrayJenis.get(position).getJumlah_penjualan());
                if (jumlah > 1) {
                    jumlah--;
                    arrayJenis.get(position).setJumlah_penjualan(String.valueOf(jumlah));

                    ((TransaksiDetailActivity)mContext).tambahDataplusmin(arrayJenis.get(position).getIdb(),"kurang");
                   // Toast.makeText(mContext, arrayJenis.get(position).getIdb()+" takutang", Toast.LENGTH_SHORT).show();

                    notifyDataSetChanged();
                }
            }
        });

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int jumlah = Integer.parseInt(arrayJenis.get(position).getJumlah_penjualan());
                jumlah++;
                arrayJenis.get(position).setJumlah_penjualan(String.valueOf(jumlah));
                ((TransaksiDetailActivity)mContext).tambahDataplusmin(arrayJenis.get(position).getIdb(),"tambah");
                notifyDataSetChanged();
            }
        });
        holder.btpindah.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
//                    Log.d("Adapter", "Long click position: " + position);
                    showDeleteDialog(position,arrayJenis.get(position).getIdb());
                    return true;
                }
                return false;
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
    private void showDeleteDialog(final int position,final String idb) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Hapus Item");
        builder.setMessage("Apakah Anda yakin ingin menghapus item ini?");

        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeItem(position);
                ((TransaksiDetailActivity)mContext).hapusitem(idb);
            }
        });

        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

