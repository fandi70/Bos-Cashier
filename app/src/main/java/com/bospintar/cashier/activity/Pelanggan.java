package com.bospintar.cashier.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.bospintar.cashier.adapter.PelangganAdapter;
import com.bospintar.cashier.app.AppController;
import com.bospintar.cashier.convert.RupiahTextWatcher;
import com.bospintar.cashier.model.Mpelanggan;
import com.bospintar.cashier.server.URL_SERVER;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Pelanggan extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    String xidpetugas,xnama_petugas,xalamat_petugas,xnohp,xlevel,xidtoko,xnama_toko,xalamat_toko,xstatus_toko,xketnota,xnohp_toko;
    public static final String TAG_RESULTS = "pelanggan";
    public static final String TAG_VALUE = "status";
    ProgressDialog pDialog;
    String tag_json_obj = "json_obj_req";
    PelangganAdapter adapter;
    SwipeRefreshLayout swipe;
    RecyclerView rcList;
    ArrayList<Mpelanggan> arraylist = new ArrayList<>();
    TextView add;
    ImageView btBack;


    SimpleDateFormat sdcurrentdate = new SimpleDateFormat("yyyy-MM-dd", new Locale("id", "ID"));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelanggan);
        bacaPreferensi();
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refreshdata);
        add=(TextView) findViewById(R.id.bt_tambah);


        adapter = new PelangganAdapter(arraylist, this, rcList);
        rcList = findViewById(R.id.rcList);
        final GridLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcList.setLayoutManager(mLayoutManager);
        rcList.setAdapter(adapter);



        swipe.setOnRefreshListener(this);

        swipe.post(new Runnable() {
                       @Override
                       public void run() {
                           swipe.setRefreshing(true);
                           callData();
                       }
                   }
        );

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(Pelanggan.this);
                View dialogView = getLayoutInflater().inflate(R.layout.activity_pengeluaranadd, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);
                final EditText ddket= dialogView.findViewById(R.id.etxt_keterangan);
                final EditText ddnominAL= dialogView.findViewById(R.id.etxt_nominal);
                final TextView judul= dialogView.findViewById(R.id.txt_judul);
                ddnominAL.addTextChangedListener(new RupiahTextWatcher(ddnominAL));

                final TextView dialogBtnSubmit = dialogView.findViewById(R.id.btlogin);
                dialogBtnSubmit.setText("Simpan");
                judul.setText("Tambah");
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
//                        simpancustomerData(ddket.getText().toString(),ddnominAL.getText().toString());

                    }
                });
            }
        });
        btBack=findViewById(R.id.bt_back);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Pelanggan.this, Menu.class);
                startActivity(intent);
                finish();
            }
        });

    }

//    public void simpancustomerData(final String _keterangan,final String _nominal) {
//        pDialog = new ProgressDialog(Pengeluaran.this);
//        pDialog.setCancelable(false);
//        pDialog.setMessage("Loading...");
//        pDialog.show();
//        StringRequest strReq = new StringRequest(Request.Method.POST, URL_SERVER.CPENGELUARANADD, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.e("Response: ", response.toString());
//                try {
//                    JSONObject jObj = new JSONObject(response);
//                    int value = jObj.getInt("success");
//                    if (value == 1) {
//                        Toast.makeText(Pengeluaran.this, "Sukses", Toast.LENGTH_SHORT).show();
//                        callData(Tanggalfrom.getText().toString(),Tanggalto.getText().toString());
//
//                    } else {
//                        Toast.makeText(Pengeluaran.this, "Gagal", Toast.LENGTH_SHORT).show();
//                        callData(Tanggalfrom.getText().toString(),Tanggalto.getText().toString());
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                pDialog.dismiss();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(Pengeluaran.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                pDialog.dismiss();
//            }
//        }) {
//            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                BigDecimal hargabeli = RupiahTextWatcher.parseCurrencyValue(_nominal);
//
//                params.put("keterangan", _keterangan);
//                params.put("nominal", hargabeli.toString());
//                params.put("idtoko",xidtoko);
//                return params;
//            }
//        };
//        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
//    }
//    public void editdata(final String _id,final String _keterangan,final String _nominal) {
//        pDialog = new ProgressDialog(Pengeluaran.this);
//        pDialog.setCancelable(false);
//        pDialog.setMessage("Loading...");
//        pDialog.show();
//        StringRequest strReq = new StringRequest(Request.Method.POST, URL_SERVER.CPENGELUARANEDIT, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.e("Response: ", response.toString());
//                try {
//                    JSONObject jObj = new JSONObject(response);
//                    int value = jObj.getInt("success");
//                    if (value == 1) {
//                        Toast.makeText(Pelanggan.this, "Sukses", Toast.LENGTH_SHORT).show();
//                        callData(T);
//
//                    } else {
//                        Toast.makeText(Pelanggan.this, "Gagal", Toast.LENGTH_SHORT).show();
//                        callData();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                pDialog.dismiss();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(Pengeluaran.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                pDialog.dismiss();
//            }
//        }) {
//            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                BigDecimal nominal = RupiahTextWatcher.parseCurrencyValue(_nominal);
//
//                params.put("idpengeluaran", _id);
//                params.put("keterangan", _keterangan);
//                params.put("nominal", nominal.toString());
//                return params;
//            }
//        };
//        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
//    }
//    public void hapusitem(String xidpengeluaran) {
//        pDialog = new ProgressDialog(Pengeluaran.this);
//        pDialog.setCancelable(false);
//        pDialog.setMessage("Loading...");
//        pDialog.show();
//        StringRequest strReq = new StringRequest(Request.Method.POST, URL_SERVER.CPENGELUARANhapus, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.e("Response: ", response.toString());
//                try {
//                    JSONObject jObj = new JSONObject(response);
//                    int value = jObj.getInt(TAG_VALUE);
//                    if (value == 1) {
//                        Toast.makeText(Pengeluaran.this, "Pengeluaran dihapus", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(Pengeluaran.this, "Pengeluaran Gagal", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                pDialog.dismiss();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(Pengeluaran.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                pDialog.dismiss();
//            }
//        }) {
//            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("idtoko", xidtoko.toString());
//                params.put("idpengeluaran", xidpengeluaran);
//                return params;
//            }
//        };
//        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
//    }
    private void callData() {
        arraylist.clear();
        swipe.setRefreshing(true);

        StringRequest jArr = new StringRequest(Request.Method.POST, URL_SERVER.CPELANGGAN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);

                    String value = jObj.getString(TAG_VALUE);

                    if (value.equals("ada")) {
                        arraylist.clear();
                        String getObject = jObj.getString(TAG_RESULTS);
                        JSONArray jsonArray = new JSONArray(getObject);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject data = jsonArray.getJSONObject(i);

                            Mpelanggan wp = new Mpelanggan(data.getString("id"), data.getString("nama"), data.getString("alamat"),data.getString("nohp"),
                                    data.getString("idtoko"));
                            arraylist.add(wp);
                        }
                        adapter = new PelangganAdapter(arraylist, Pelanggan.this,rcList);
                        rcList.setAdapter(adapter);


                    } else {
                        Toast.makeText(Pelanggan.this, "Kosong", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),"Koneksi Lemah", Toast.LENGTH_SHORT).show();
                swipe.setRefreshing(false);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("idtoko", xidtoko);

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
    public void onClick(String _id, String _ket, String _nominal) {
//        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(Pengeluaran.this);
//        View dialogView = getLayoutInflater().inflate(R.layout.activity_pengeluaranadd, null);
//        dialog.setView(dialogView);
//        dialog.setCancelable(false);
//        final EditText ddket= dialogView.findViewById(R.id.etxt_keterangan);
//        final EditText ddnominAL= dialogView.findViewById(R.id.etxt_nominal);
//        final TextView judul= dialogView.findViewById(R.id.txt_judul);
//
//        ddnominAL.addTextChangedListener(new RupiahTextWatcher(ddnominAL));
//        final TextView dialogBtnSubmit = dialogView.findViewById(R.id.btlogin);
//        ddket.setText(_ket);
//        ddnominAL.setText(_nominal);
//        dialogBtnSubmit.setText("Simpan");
//        judul.setText("Edit");
//        final ImageView dialogBtnClose = dialogView.findViewById(R.id.bt_back);
//        final android.app.AlertDialog alertDialog = dialog.create();
//        alertDialog.show();
//        dialogBtnClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                alertDialog.dismiss();
//            }
//        });
//        dialogBtnSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                alertDialog.dismiss();
//                editdata(_id,ddket.getText().toString(),ddnominAL.getText().toString());
//
//            }
//        });

    }
    @Override
    public void onBackPressed() {

        finish();
    }
    @Override
    public void onRefresh() {

        callData();
    }
}