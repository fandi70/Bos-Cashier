package com.bospintar.cashier.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import com.bospintar.cashier.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Histori extends AppCompatActivity {
    TextView caribydate;
    String stspembayaran;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        caribydate=findViewById(R.id.caribydate);
        caribydate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdcurrentdate = new SimpleDateFormat("yyyy-MM-dd", new Locale("id", "ID"));

                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(Histori.this);
                View dialogView = getLayoutInflater().inflate(R.layout.activity_history_dialog, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);
                final EditText dari= dialogView.findViewById(R.id.edt_dari);
                final EditText sampai= dialogView.findViewById(R.id.edt_sampai);
                final RadioGroup remotePembayaran = dialogView.findViewById(R.id.opsistatuspembayaranAdd);
                final RadioButton rsemua = dialogView.findViewById(R.id.semua);
                final RadioButton rlunas = dialogView.findViewById(R.id.lunas);
                final RadioButton rutang= dialogView.findViewById(R.id.utang);
                final TextView judul= dialogView.findViewById(R.id.txt_judul);
                remotePembayaran.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == R.id.lunas){
                            stspembayaran = String.valueOf("lunas");
                        }else if (checkedId == R.id.utang){
                            stspembayaran = String.valueOf("utang");
                        }else if (checkedId == R.id.semua){
                            stspembayaran = String.valueOf("semua");
                        }else {
                            Toast.makeText(Histori.this, "Hari Belum diisi", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dari.setText(sdcurrentdate.format(new Date()));
                sampai.setText(sdcurrentdate.format(new Date()));
                dari.setFocusableInTouchMode(false);
                dari.setFocusable(false);
                sampai.setFocusableInTouchMode(false);
                sampai.setFocusable(false);
                dari.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar c = Calendar.getInstance();

                        DatePickerDialog dpd = new DatePickerDialog(Histori.this,
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        dari.setText(year + "-"
                                                + (monthOfYear + 1) + "-" + dayOfMonth);

                                    }
                                }, c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE));
                        dpd.show();
                    }
                });
                sampai.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar c = Calendar.getInstance();

                        DatePickerDialog dpd = new DatePickerDialog(Histori.this,
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        sampai.setText(year + "-"
                                                + (monthOfYear + 1) + "-" +dayOfMonth );

                                    }
                                }, c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE));
                        dpd.show();
                    }
                });

                final TextView dialogBtnSubmit = dialogView.findViewById(R.id.btlogin);
                dialogBtnSubmit.setText("Terapkan");
                judul.setText("cari by date");
                final ImageView dialogBtnClose = dialogView.findViewById(R.id.bt_back);
                final android.app.AlertDialog alertDialog = dialog.create();
                alertDialog.show();
                dialogBtnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alertDialog.dismiss();
                    }
                });
                dialogBtnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alertDialog.dismiss();

                    }
                });
            }
        });

    }


}