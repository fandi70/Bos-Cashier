package com.bospintar.cashier.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bospintar.cashier.R;
import com.bospintar.cashier.adapter.ProdukTransaksiAdapter;
import com.bospintar.cashier.app.AppController;
import com.bospintar.cashier.convert.RupiahTextWatcher;
import com.bospintar.cashier.model.Mproduk;
import com.bospintar.cashier.server.URL_SERVER;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProdukTransaksi extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    String xidpetugas, xnama_petugas, xalamat_petugas, xnohp, xlevel, xidtoko, xnama_toko, xalamat_toko, xstatus_toko, xketnota, xnohp_toko;
    public static final String TAG_RESULTS = "produk";
    public static final String TAG_VALUE = "status";
    private static final String TAG_SUCCESS = "total";
    String tag_json_obj = "json_obj_req";
    ProdukTransaksiAdapter adapter;
    SwipeRefreshLayout swipe;
    RecyclerView rcList;
    ProgressDialog pDialog;
    ArrayList<Mproduk> arraylist = new ArrayList<>();
    double total;
    ImageView btBack;
    DecimalFormat rupiahFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    TextView txtnotif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi);
        bacaPreferensi();
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refreshdatahistorykegiatan);
        btBack = findViewById(R.id.bt_back);
        TextView btcekout = findViewById(R.id.btcekout);
        btcekout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProdukTransaksi.this, TransaksiDetailActivity.class);
                startActivity(intent);
            }
        });

        RelativeLayout btpending = findViewById(R.id.btpending);
        btpending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProdukTransaksi.this, PendingActivity.class);
                startActivity(intent);
            }
        });

        txtnotif = findViewById(R.id.txtnotif);
        rcList = findViewById(R.id.rcList);
        final GridLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcList.setLayoutManager(mLayoutManager);
        rcList.setAdapter(adapter);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });

        swipe.setOnRefreshListener(this);
        swipe.post(new Runnable() {
                       @Override
                       public void run() {
                           swipe.setRefreshing(true);
                           callData();
                       }
                   }
        );
        EditText yourEditText = findViewById(R.id.edsearch);

        yourEditText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String text = s.toString().toLowerCase(Locale.getDefault());
                TextView txt = findViewById(R.id.txtpesan);
                adapter.filter(text, txt);

            }
        });


    }

    private void callData() {
        arraylist.clear();
        swipe.setRefreshing(true);
        StringRequest jArr = new StringRequest(Request.Method.POST, URL_SERVER.CPRODUKTRANSAKSI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    String value = jObj.getString(TAG_VALUE);

                    if (value.equals("ada")) {
                        arraylist.clear();
                        TextView txtTotal = findViewById(R.id.txtTotal);
                        TextView btcekout = findViewById(R.id.btcekout);
                        total = Double.parseDouble(jObj.getString("total"));
                        rupiahFormat.setParseBigDecimal(true);
                        rupiahFormat.applyPattern("#,##0");
                        txtTotal.setText("Rp" + rupiahFormat.format(total));
                        btcekout.setText("Checkout (" + jObj.getString("totalitem") + ")");
                        txtnotif.setText(jObj.getString("pending"));
                        String getObject = jObj.getString(TAG_RESULTS);
                        JSONArray jsonArray = new JSONArray(getObject);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject data = jsonArray.getJSONObject(i);
                            Mproduk wp = new Mproduk(data.getString("id"), data.getString("nama"), data.getString("hargajual"),
                                    data.getString("hargabeli"), data.getString("grosir"), data.getString("stok"), data.getString("satuan"), data.getString("isi_stok"));
                            arraylist.add(wp);

                        }
                        adapter = new ProdukTransaksiAdapter(arraylist, ProdukTransaksi.this);
                        rcList.setAdapter(adapter);
                    } else {
                        Toast.makeText(ProdukTransaksi.this, "Kosong", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

//                adapter.notifyDataSetChanged();
                swipe.setRefreshing(false);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Koneksi Lemah", Toast.LENGTH_SHORT).show();
                swipe.setRefreshing(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("idtoko", xidtoko);
                params.put("idpetugas", xidpetugas);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jArr, tag_json_obj);
    }

    public void tambahData(String idbarang, String namabarang) {
        pDialog = new ProgressDialog(ProdukTransaksi.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_SERVER.CADDTEM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    int value = jObj.getInt(TAG_VALUE);
                    if (value == 1) {
                        Toast.makeText(ProdukTransaksi.this, namabarang, Toast.LENGTH_SHORT).show();
                        TextView txtTotal = findViewById(R.id.txtTotal);
                        total = Double.parseDouble(jObj.getString("total"));
                        rupiahFormat.setParseBigDecimal(true);
                        rupiahFormat.applyPattern("#,##0");
                        txtTotal.setText("Rp" + rupiahFormat.format(total));
                        TextView btcekout = findViewById(R.id.btcekout);
                        btcekout.setText("Checkout (" + jObj.getString("totalitem") + ")");
                    } else {
                        Toast.makeText(ProdukTransaksi.this, "Gagal", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(ProdukTransaksi.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }) {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                params.put("idproduk", idbarang);
                params.put("idpetugas", xidpetugas);


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
        startActivity(new Intent(getApplicationContext(), Menu.class));

        finish();
    }

    @Override
    public void onRefresh() {
        callData();
    }
}