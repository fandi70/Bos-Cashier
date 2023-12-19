package com.bospintar.cashier.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bospintar.cashier.BuildConfig;
import com.bospintar.cashier.R;

public class Profil extends AppCompatActivity {

    LinearLayout btKeluar;
    String xidpetugas,xnama_petugas,xalamat_petugas,xnohp,xlevel,xidtoko,xnama_toko,xalamat_toko,xstatus_toko,xketnota,xnohp_toko;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        bacaPreferensi();
        TextView txtversi = findViewById(R.id.txtversi);
        txtversi.setText(BuildConfig.VERSION_NAME);
        btKeluar = findViewById(R.id.btKeluar);
        btKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getSharedPreferences("akun", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                editor.putString("idpetugas","0");
                editor.putString("nama_petugas", "0");
                editor.putString("alamat_petugas", "0");
                editor.putString("nohp", "0");
                editor.putString("level", "0");
                editor.putString("idtoko", "0");
                editor.putString("nama_toko", "0");
                editor.putString("alamat_toko", "0");
                editor.putString("status_toko", "0");
                editor.putString("ketnota", "0");
                editor.putString("nohp_toko", "0");
                editor.commit();
                Intent intent = new Intent(Profil.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
    private void bacaPreferensi() {

        SharedPreferences pref = getSharedPreferences("akun", MODE_PRIVATE);
        xidpetugas = pref.getString("idpetugas", "0");
        xnama_petugas = pref.getString("nama_petugas", "0");
        xalamat_petugas = pref.getString("alamat_petugas", "0");
        xnohp= pref.getString("nohp", "0");
        xlevel= pref.getString("level", "0");
        xidtoko= pref.getString("idtoko", "0");
        xnama_toko= pref.getString("nama_toko", "0");
        xalamat_toko= pref.getString("alamat_toko", "0");
        xstatus_toko= pref.getString("status_toko", "0");
        xketnota= pref.getString("ketnota", "0");
        xnohp_toko=pref.getString("nohp_toko","0");

    }
}