package com.bospintar.cashier.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.bospintar.cashier.adapter.LaporanperpetugasAdapter;
import com.bospintar.cashier.app.AppController;
import com.bospintar.cashier.model.Mlaporanpetugas;
import com.bospintar.cashier.server.URL_SERVER;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Laporanperpetugas extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    String xidpetugas,xnama_petugas,xalamat_petugas,xnohp,xlevel,xidtoko,xnama_toko,xalamat_toko,xstatus_toko,xketnota,xnohp_toko;

    TextView caribydate,txtdari,txtsampai,txttotalpenjualan;
    String E_dari,E_sampai,E_idpetugas;
    ArrayList<Mlaporanpetugas> arraylist = new ArrayList<>();
    public static final String TAG_RESULTS = "omset";
    public static final String TAG_VALUE = "status";
    String tag_json_obj = "json_obj_req";
    SwipeRefreshLayout swipe;
    LaporanperpetugasAdapter adapter;
    RecyclerView rcList;
    DecimalFormat rupiahFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    SimpleDateFormat sdcurrentdate = new SimpleDateFormat("yyyy-MM-dd", new Locale("id", "ID"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporanperppetugas);

        bacaPreferensi();
        Intent intent=getIntent();
        if (intent !=null){
            E_dari=intent.getStringExtra("dari");
            E_sampai=intent.getStringExtra("sampai");
            E_idpetugas=intent.getStringExtra("idpetugas");

        }
        caribydate=findViewById(R.id.caribydate);
        txtsampai= findViewById(R.id.txtsampai);
        txtdari= findViewById(R.id.txtdari);
        txttotalpenjualan= findViewById(R.id.txttotalPenjualan);
        swipe = findViewById(R.id.swipe_refreshdata);

        callData(E_dari,E_sampai);

        caribydate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(Laporanperpetugas.this);
                View dialogView = getLayoutInflater().inflate(R.layout.activity_laporan_dialog, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);
                final EditText dari= dialogView.findViewById(R.id.edt_dari);
                final EditText sampai= dialogView.findViewById(R.id.edt_sampai);

                final TextView judul= dialogView.findViewById(R.id.txt_judul);
                judul.setText("Cari Transaksi");
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

                        DatePickerDialog dpd = new DatePickerDialog(Laporanperpetugas.this,
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        dari.setText(year + "-"
                                                + (monthOfYear + 1) + "-" + dayOfMonth);
                                        E_dari=year + "-"
                                                + (monthOfYear + 1) + "-" + dayOfMonth;

                                    }
                                }, c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE));
                        dpd.show();
                    }
                });
                sampai.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar c = Calendar.getInstance();

                        DatePickerDialog dpd = new DatePickerDialog(Laporanperpetugas.this,
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        sampai.setText(year + "-"
                                                + (monthOfYear + 1) + "-" +dayOfMonth);
                                        E_sampai=year + "-"
                                                + (monthOfYear + 1) + "-" +dayOfMonth;

                                    }
                                }, c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE));
                        dpd.show();
                    }
                });

                final TextView dialogBtnSubmit = dialogView.findViewById(R.id.btlogin);
                dialogBtnSubmit.setText("Terapkan");
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
                        callData(dari.getText().toString(),sampai.getText().toString());

                    }
                });
            }
        });
        adapter = new LaporanperpetugasAdapter(arraylist, this);
        rcList = findViewById(R.id.rcList);
        final GridLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcList.setLayoutManager(mLayoutManager);
        rcList.setAdapter(adapter);
        swipe.setOnRefreshListener(this);

        ImageView btBack=findViewById(R.id.bt_back);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        swipe.post(new Runnable() {
                       @Override
                       public void run() {
                           swipe.setRefreshing(true);
                           callData(E_dari,E_sampai);
                       }
                   }
        );
    }


    private void callData(final String xdari, String xsampai) {
        arraylist.clear();
        swipe.setRefreshing(true);

        // Creating volley request obj
        StringRequest jArr = new StringRequest(Request.Method.POST, URL_SERVER.CLAPORANPERPETUGAS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);

                    String value = jObj.getString(TAG_VALUE);

                    if (value.equals("ada")) {
                        arraylist.clear();
//                        adapter.notifyDataSetChanged();

                        String getObject = jObj.getString(TAG_RESULTS);
                        JSONArray jsonArray = new JSONArray(getObject);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject data = jsonArray.getJSONObject(i);

                            Mlaporanpetugas wp = new Mlaporanpetugas(data.getString("id"), data.getString("nota"),data.getString("tanggal"), data.getString("totalbayar"),
                                    data.getString("statusbayar"), data.getString("jbayar"), data.getString("idtoko"));
                            arraylist.add(wp);
                        }
                        adapter = new LaporanperpetugasAdapter(arraylist,Laporanperpetugas.this);
                        rcList.setAdapter(adapter);
                        rupiahFormat.setParseBigDecimal(true);
                        rupiahFormat.applyPattern("#,##0");
                        Double xtotal = Double.parseDouble(jObj.getString("total_penjualan"));
                        txttotalpenjualan.setText("Rp" + rupiahFormat.format(xtotal));
                        txtdari.setText(xdari);
                        txtsampai.setText(xsampai);


                    } else {
                        Toast.makeText(Laporanperpetugas.this, "Kosong", Toast.LENGTH_SHORT).show();
                        rcList.setAdapter(adapter);
                        txtdari.setText("----/--/--");
                        txtsampai.setText("----/--/--");

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
                params.put("idpetugas", E_idpetugas);
                params.put("dari", xdari);
                params.put("sampai", xsampai);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jArr, tag_json_obj);
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

    @Override
    public void onRefresh() {
        callData(E_dari,E_sampai);
    }

    @Override
    public void onBackPressed() {

        finish();
    }
}