package com.bospintar.cashier.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.bospintar.cashier.model.MtransaksiDetail;
import com.bospintar.cashier.server.URL_SERVER;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Keranjang extends AppCompatActivity {
    ProgressDialog pDialog;
    public static final String TAG_VALUE = "status";
    String tag_json_obj = "json_obj_req";
    LinearLayout bthapus;
    TextView edharga, edtotal, txtnamaproduk;
    EditText txtjumlah, edbayarpenjuan;
    double hargaJual = 0;
    int jumlah = 0;
    int total = 0;
    ImageView btminus, btplus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_keranjang);
        bacaPreferensi();
        btminus = findViewById(R.id.btminus);
        btplus = findViewById(R.id.btplus);
        bthapus = findViewById(R.id.bthapus);
        edharga = findViewById(R.id.edharga);
        edtotal = findViewById(R.id.edtotal);
        txtjumlah = findViewById(R.id.txtjumlah);
        txtnamaproduk = findViewById(R.id.txtnamaproduk);
        edbayarpenjuan = findViewById(R.id.edbayarpenjuan);
        ImageView btnClear = findViewById(R.id.btnClear);
        bthapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hapusitem();
            }
        });
        edbayarpenjuan.addTextChangedListener(new RupiahTextWatcher(edbayarpenjuan));
        edbayarpenjuan.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                BigDecimal harga = RupiahTextWatcher.parseCurrencyValue(s.toString());
                if (Integer.parseInt(String.valueOf(harga)) <= 0) {
                    DecimalFormat rupiahFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                    rupiahFormat.setParseBigDecimal(true);
                    rupiahFormat.applyPattern("#,##0");
                    String formattedRupiah = rupiahFormat.format(hargaJual);
                    edharga.setText("Rp" + String.valueOf(formattedRupiah));
                    btnClear.setVisibility(View.GONE);

                } else {
                    DecimalFormat rupiahFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                    rupiahFormat.setParseBigDecimal(true);
                    rupiahFormat.applyPattern("#,##0");
                    String formattedRupiah = rupiahFormat.format(Double.parseDouble(String.valueOf(harga)));
                    edharga.setText("Rp" + String.valueOf(formattedRupiah));
                    if (Integer.parseInt(String.valueOf(harga)) >= hargaJual) {
                        edbayarpenjuan.setError("Tidak boleh lebih/sama dari harga jual");
                        btnClear.setVisibility(View.GONE);

                    }
                    else if (Integer.parseInt(String.valueOf(harga)) > 0 && Integer.parseInt(String.valueOf(harga)) <= hargaJual) {
                        btnClear.setVisibility(View.VISIBLE);
                    }
                }

            }


        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edbayarpenjuan.getText().clear();
            }
        });
        txtjumlah.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.toString().equals("")||s.toString().equals("0")){
                    txtjumlah.setText("1");
                    txtjumlah.setGravity(View.TEXT_ALIGNMENT_TEXT_END);
                    txtjumlah.setSelection(txtjumlah.getText().length()); // Pindahkan kursor ke akhir teks
                }
            }


        });
        callData();

        btminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (jumlah > 1) {
                    jumlah--;
                    txtjumlah.setText(String.valueOf(jumlah));
                    txtjumlah.setGravity(View.TEXT_ALIGNMENT_TEXT_END);
                    txtjumlah.setSelection(txtjumlah.getText().length());
                    // ((TransaksiDetailActivity)mContext).tambahDataplusmin(arrayJenis.get(position).getIdb(),"kurang");
                    // Toast.makeText(mContext, arrayJenis.get(position).getIdb()+" takutang", Toast.LENGTH_SHORT).show();

                }
            }
        });

        btplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumlah = jumlah + 1;
                txtjumlah.setText(String.valueOf(jumlah));
                txtjumlah.setGravity(View.TEXT_ALIGNMENT_TEXT_END);
                txtjumlah.setSelection(txtjumlah.getText().length());

            }
        });
    }

    public void hapusitem() {
        pDialog = new ProgressDialog(Keranjang.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_SERVER.link + "hapustemp.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    int value = jObj.getInt(TAG_VALUE);
                    if (value == 1) {
                        finish();
                        Toast.makeText(Keranjang.this, "Item dihapus", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Keranjang.this, "Item Gagal", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Keranjang.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }) {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idproduk", getIntent().getStringExtra("idproduk"));
                params.put("idpetugas", xidpetugas);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void callData() {
        StringRequest jArr = new StringRequest(Request.Method.POST, URL_SERVER.link + "getdetailproduktemp.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);

                    String value = jObj.getString("status");

                    if (value.equals("ada")) {
                        hargaJual = Double.parseDouble(jObj.getString("harga_jual"));
                        DecimalFormat rupiahFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                        rupiahFormat.setParseBigDecimal(true);
                        rupiahFormat.applyPattern("#,##0");
                        String formattedRupiah = rupiahFormat.format(hargaJual);
                        edharga.setText("Rp" + String.valueOf(formattedRupiah));
                        txtnamaproduk.setText(jObj.getString("nama"));
                        txtjumlah.setText(jObj.getString("jumlah_penjualan"));
                        jumlah = Integer.parseInt(jObj.getString("jumlah_penjualan"));
                        total = (int) (hargaJual * jumlah);
                        edtotal.setText(String.valueOf(total));

                    } else {
                        Toast.makeText(Keranjang.this, "Kosong", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Koneksi Lemah", Toast.LENGTH_SHORT).show();
//                swipe.setRefreshing(false);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("idproduk", getIntent().getStringExtra("idproduk"));
                params.put("idpetugas", xidpetugas);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jArr, tag_json_obj);
    }

    String xidpetugas, xnama_petugas, xalamat_petugas, xnohp, xlevel, xidtoko, xnama_toko, xalamat_toko, xstatus_toko, xketnota, xnohp_toko;

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
}