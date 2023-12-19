package com.bospintar.cashier.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bospintar.cashier.BuildConfig;
import com.bospintar.cashier.R;

public class Profil extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        TextView txtversi = findViewById(R.id.txtversi);
        txtversi.setText(BuildConfig.VERSION_NAME);
    }
}