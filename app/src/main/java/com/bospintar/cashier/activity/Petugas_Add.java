package com.bospintar.cashier.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bospintar.cashier.R;
import com.bospintar.cashier.app.AppController;
import com.bospintar.cashier.convert.RupiahTextWatcher;
import com.bospintar.cashier.server.URL_SERVER;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Petugas_Add extends AppCompatActivity {
    String xidpetugas, xnama_petugas, xalamat_petugas, xnohp, xlevel, xidtoko, xnama_toko, xalamat_toko, xstatus_toko, xketnota, xnohp_toko;
    private boolean passwordVisible = false;

    private TextView btnTambahPegawai;
    EditText namapegawai, nohppegawai, alamatpegawai, passwordpegawai;
    public static final String TAG_VALUE = "success";
    String tag_json_obj = "json_obj_req";
    ProgressDialog pDialog;
    RadioButton radmin, rkasir;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petugas_add);
        bacaPreferensi();
        namapegawai = findViewById(R.id.edt_namapegawai);
        nohppegawai = findViewById(R.id.edt_nohppegawai);
        alamatpegawai = findViewById(R.id.edt_email);
        passwordpegawai = findViewById(R.id.passwordEditText);
        btnTambahPegawai = findViewById(R.id.btsimpan);
        rkasir = findViewById(R.id.rkasir);
        radmin = findViewById(R.id.radmin);
        ImageView passwordVisibilityToggle = findViewById(R.id.passwordVisibilityToggle);


        // Setup Button Click Listener
        btnTambahPegawai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(Petugas_Add.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_informasi);

                TextView cancelButton = dialog.findViewById(R.id.cancelButton);
                TextView okButton = dialog.findViewById(R.id.okButton);
                TextView txtjudul = dialog.findViewById(R.id.txtjudul);
                TextView txtsubjudul = dialog.findViewById(R.id.txtsubjudul);
                txtjudul.setText("Tambah data Petugas");
                txtsubjudul.setText("Apakah anda sudah yakin semua data yang diinputkan sudah benar>");
                okButton.setText("Yakin");
                cancelButton.setText("Cek lagi");

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (rkasir.isChecked()) {
                            status = "Kasir";
                        }
                        if (radmin.isChecked()) {
                            status = "Admin";
                        }
                        tambahData();
                        dialog.dismiss();
                    }
                });

                dialog.setCancelable(false);


                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        });
        passwordVisibilityToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordVisible = !passwordVisible;
                int inputType = passwordVisible ?
                        android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                        android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
                passwordpegawai.setInputType(inputType);
                passwordpegawai.setSelection(passwordpegawai.getText().length()); // Agar kursor tetap di akhir teks
                passwordVisibilityToggle.setImageResource(
                        passwordVisible ? R.drawable.ic_unvisible_pass : R.drawable.ic_visible_pass);
            }
        });
    }

    public void tambahData() {
        pDialog = new ProgressDialog(Petugas_Add.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_SERVER.CPETUGASADD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    int value = jObj.getInt(TAG_VALUE);
                    if (value == 1) {
                        Toast.makeText(Petugas_Add.this, "Sukses", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Petugas_Add.this, Petugas.class));
                        finish();

                    } else if (value == 2) {
                        nohppegawai.setError("Nohp sudah ada");
                        nohppegawai.requestFocus();
                    } else {
                        Toast.makeText(Petugas_Add.this, "Gagal", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                VolleyLog.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(Petugas_Add.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }) {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("idtoko", xidtoko);
                params.put("nama", namapegawai.getText().toString());
                params.put("alamat", alamatpegawai.getText().toString());
                params.put("nohp", nohppegawai.getText().toString());
                params.put("password ", passwordpegawai.getText().toString());
                params.put("status ", status);


                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void bacaPreferensi() {

        SharedPreferences pref = getSharedPreferences("akun", MODE_PRIVATE);
        xidpetugas = pref.getString("idpetugas", "0");
        xnama_petugas = pref.getString("nama_petugas", "0");
        xalamat_petugas = pref.getString("alamat_petugas", "0");
        xnohp = pref.getString("nohp", "0");
        xlevel = pref.getString("level", "0");
        xidtoko = pref.getString("idtoko", "0");
        xnama_toko = pref.getString("nama_toko", "0");
        xalamat_toko = pref.getString("alamat_toko", "0");
        xstatus_toko = pref.getString("status_toko", "0");
        xketnota = pref.getString("ketnota", "0");
        xnohp_toko = pref.getString("nohp_toko", "0");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(Petugas_Add.this, "Sukses", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(Petugas_Add.this, Petugas.class));
        finish();
    }

}