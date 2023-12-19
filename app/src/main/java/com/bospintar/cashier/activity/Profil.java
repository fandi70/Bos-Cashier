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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bospintar.cashier.BuildConfig;
import com.bospintar.cashier.R;
import com.bospintar.cashier.app.AppController;
import com.bospintar.cashier.convert.RupiahTextWatcher;
import com.bospintar.cashier.server.URL_SERVER;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Profil extends AppCompatActivity {
    public static final String TAG_VALUE = "status";
    String tag_json_obj = "json_obj_req";
    LinearLayout btKeluar;
    String xidpetugas,xnama_petugas,xalamat_petugas,xnohp,xlevel,xidtoko,xnama_toko,xalamat_toko,xstatus_toko,xketnota,xnohp_toko;
    ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        bacaPreferensi();
        TextView txtversi = findViewById(R.id.txtversi);
        TextView txtnama=findViewById(R.id.txtnama);
        TextView txtnamaprofile=findViewById(R.id.txtnamaprofile);
        TextView  txtnohp=findViewById(R.id.txtnohp);
        TextView txtalamat=findViewById(R.id.txtalamat);
        LinearLayout ln_ubahalamat=findViewById(R.id.ln_ubahalamat);
        LinearLayout ln_nohp=findViewById(R.id.ln_ubahnohp);
        LinearLayout ln_ln_ubahnama=findViewById(R.id.ln_ubahnama);
        txtnamaprofile.setText(xnama_petugas);
        txtalamat.setText(xalamat_petugas);
        txtnohp.setText(xnohp);
        txtnama.setText(xnama_petugas);
        txtversi.setText(BuildConfig.VERSION_NAME);
        btKeluar = findViewById(R.id.btKeluar);
        ln_ubahalamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showhdialog("Alamat",xalamat_petugas);
            }
        });
        ln_ln_ubahnama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showhdialog("Nama",xnama_petugas);
            }
        });
        ln_nohp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
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
    private void showhdialog(String stsedit,String isidata) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_profile);
        TextView txt_judul=dialog.findViewById(R.id.txt_judul);
        TextView txt_judulketerangan=dialog.findViewById(R.id.txt_judulketerangan);
        EditText edt_keterangan= dialog.findViewById(R.id.edt_keterangan);
        edt_keterangan.setText(isidata);
        txt_judul.setText("Ubah "+stsedit);
        txt_judulketerangan.setText("Tambahkan "+stsedit+" untuk update Profile");
        TextView cancelButton = dialog.findViewById(R.id.cancelButton);
        TextView okButton = dialog.findViewById(R.id.okButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatedata(stsedit,edt_keterangan.getText().toString());
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
    public void updatedata(String ket, String isi) {

        pDialog = new ProgressDialog(Profil.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_SERVER.link + "update_profile.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    int value = jObj.getInt(TAG_VALUE);
                    if (value == 1) {
                        finish();
                        Toast.makeText(Profil.this, "Berhasil Di update", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Profil.this, "Gagal Di update", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Profil.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }) {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("keterangan",ket);
                params.put("update_isi",isi);
                params.put("idtoko",xidtoko);
                params.put("idpetugas", xidpetugas);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }
}