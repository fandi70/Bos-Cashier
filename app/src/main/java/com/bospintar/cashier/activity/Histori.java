package com.bospintar.cashier.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.bospintar.cashier.adapter.HomeAdapter;
import com.bospintar.cashier.app.AppController;
import com.bospintar.cashier.model.Mhome;
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

public class Histori extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    String xidpetugas,xnama_petugas,xalamat_petugas,xnohp,xlevel,xidtoko,xnama_toko,xalamat_toko,xstatus_toko,xketnota,xnohp_toko;

    TextView caribydate,txttotalkeuangan,txttotalpengeluaran,txttotalpenjualan;
    String stspembayaran,_dari,_sampai;
    ArrayList<Mhome> arraylist = new ArrayList<>();
    public static final String TAG_RESULTS = "penjualan_petugas";
    public static final String TAG_VALUE = "status";
    String tag_json_obj = "json_obj_req";
    SwipeRefreshLayout swipe;
    HomeAdapter adapter;
    RecyclerView rcList;
    DecimalFormat rupiahFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    SimpleDateFormat sdcurrentdate = new SimpleDateFormat("yyyy-MM-dd", new Locale("id", "ID"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        bacaPreferensi();
        caribydate=findViewById(R.id.caribydate);
        txttotalkeuangan= findViewById(R.id.txttotalkeuangan);
        txttotalpengeluaran= findViewById(R.id.txttotalpengeluaran);
        txttotalpenjualan= findViewById(R.id.txttotalpenjualan);
        swipe = findViewById(R.id.swipe_refreshdata);
        _dari=sdcurrentdate.format(new Date());
        _sampai=sdcurrentdate.format(new Date());
        callData("lunas",_dari,_sampai);

        caribydate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                            stspembayaran = String.valueOf("hutang");
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
                                        _dari=year + "-"
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

                        DatePickerDialog dpd = new DatePickerDialog(Histori.this,
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        sampai.setText(year + "-"
                                                + (monthOfYear + 1) + "-" +dayOfMonth);
                                        _sampai=year + "-"
                                                + (monthOfYear + 1) + "-" +dayOfMonth;

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
                        callData(stspembayaran,dari.getText().toString(),sampai.getText().toString());

                    }
                });
            }
        });
        adapter = new HomeAdapter(arraylist, this);
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
                           callData(stspembayaran,_dari,_sampai);
                       }
                   }
        );
    }


    private void callData(final String xstspembayaran, String xdari, String xsampai) {
        arraylist.clear();
        swipe.setRefreshing(true);

        // Creating volley request obj
        StringRequest jArr = new StringRequest(Request.Method.POST, URL_SERVER.CHISTORYBYDATE, new Response.Listener<String>() {

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

                            Mhome wp = new Mhome(data.getString("id"), data.getString("idpetugas"),data.getString("nota"), data.getString("totalbayar"), data.getString("tanggal"),
                                    data.getString("statusbayar"), data.getString("idtoko"));
                            arraylist.add(wp);
                        }
                        adapter = new HomeAdapter(arraylist, Histori.this);
                        rcList.setAdapter(adapter);
                        rupiahFormat.setParseBigDecimal(true);
                        rupiahFormat.applyPattern("#,##0");
                        Double xtotal = Double.parseDouble(jObj.getString("total_penjualan"));
                        Double xpengeluaran = Double.parseDouble(jObj.getString("total_pengeluaran"));
                        txttotalpenjualan.setText("Rp" + rupiahFormat.format(xtotal));
                        txttotalpengeluaran.setText("Rp" + rupiahFormat.format(xpengeluaran));

                        txttotalkeuangan.setText("Rp" + rupiahFormat.format(xtotal-xpengeluaran));


                    } else {
                        Toast.makeText(Histori.this, "Kosong", Toast.LENGTH_SHORT).show();
                        rcList.setAdapter(adapter);
                        txttotalpenjualan.setText("Rp0");


                        txttotalpengeluaran.setText("Rp0");

                        txttotalkeuangan.setText("Rp0");

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
                params.put("dari", xdari);
                params.put("sampai", xsampai);
                params.put("sbayar", xstspembayaran);

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
        callData(stspembayaran,_dari,_sampai);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), ProdukTransaksi.class));

        finish();
    }
}