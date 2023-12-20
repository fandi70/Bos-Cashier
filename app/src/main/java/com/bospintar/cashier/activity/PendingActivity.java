package com.bospintar.cashier.activity;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.bospintar.cashier.adapter.PendingAdapter;
import com.bospintar.cashier.adapter.PendingDetailAdapter;
import com.bospintar.cashier.app.AppController;
import com.bospintar.cashier.model.Mpending;
import com.bospintar.cashier.model.MpendingDetail;
import com.bospintar.cashier.server.URL_SERVER;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PendingActivity extends AppCompatActivity {
    public static final String TAG_RESULTS = "pending";
    public static final String TAG_VALUE = "status";
    PendingAdapter adapter;
    PendingDetailAdapter adapterDetail;
    ArrayList<Mpending> arraylist = new ArrayList<>();
    ArrayList<MpendingDetail> arraylistDetail = new ArrayList<>();

    public Dialog dialog;
    ProgressDialog pDialog;
    RecyclerView rcList;
    DecimalFormat rupiahFormat = ((DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("id", "ID")));
    //SwipeRefreshLayout swipe;
    String tag_json_obj = "json_obj_req";
    double total;
    String xalamat_petugas;
    String xalamat_toko;
    String xidpetugas;
    String xidtoko;
    String xketnota;
    String xlevel;
    String xnama_petugas;
    String xnama_toko;
    String xnohp;
    String xnohp_toko;
    String xstatus_toko;
    EditText yourEditText;
    ImageView img_kosong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);


        bacaPreferensi();
        img_kosong = findViewById(R.id.img_kosong);
        // this.swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refreshdatahistorykegiatan);
         ImageView btBack = (ImageView) findViewById(R.id.bt_back);
        this.rcList = (RecyclerView) findViewById(R.id.recyclerView);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.rcList.setLayoutManager(mLayoutManager);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PendingActivity.this, ProdukTransaksi.class);
                startActivity(intent);
                finish();
            }
        });
//
//        this.swipe.setOnRefreshListener(this);
//        this.swipe.post(new Runnable() {
//            public void run() {
//                //  swipe.setRefreshing(true);
//
//            }
//        });
        ((EditText) findViewById(R.id.edsearch)).addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filter(s.toString().toLowerCase(Locale.getDefault()), (TextView) PendingActivity.this.findViewById(R.id.txtpesan), img_kosong);

                }
            }
        });
        callData();
    }

    /* access modifiers changed from: private */
    public void callData() {
        this.arraylist.clear();
        img_kosong.setVisibility(View.GONE);
//        this.swipe.setRefreshing(true);
        AppController.getInstance().addToRequestQueue(new StringRequest(1, URL_SERVER.link + "getpending.php", new Response.Listener<String>() {
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getString("status").equals("ada")) {
                        arraylist.clear();
                        JSONArray jsonArray = new JSONArray(jObj.getString(TAG_RESULTS));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject data = jsonArray.getJSONObject(i);
                            arraylist.add(new Mpending(data.getString("idtrnasaksi"), data.getString("totalbayar"), data.getString("tanggal"),data.getString("keterangan")));
                        }
                        adapter = new PendingAdapter(arraylist, PendingActivity.this);
                        rcList.setAdapter(adapter);
                        img_kosong.setVisibility(View.GONE);
                    } else {
                        img_kosong.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    img_kosong.setVisibility(View.VISIBLE);
                }
                //swipe.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                img_kosong.setVisibility(View.VISIBLE);
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idtoko", xidtoko);
                params.put("idpetugas", xidpetugas);
                return params;
            }
        }, this.tag_json_obj);
    }

    private void bacaPreferensi() {
        SharedPreferences pref = getSharedPreferences("akun", 0);
        this.xidpetugas = pref.getString("idpetugas", "0");
        this.xnama_petugas = pref.getString("nama_petugas", "0");
        this.xalamat_petugas = pref.getString("alamat_petugas", "0");
        this.xnohp = pref.getString("nohp", "0");
        this.xlevel = pref.getString("level", "0");
        this.xidtoko = pref.getString("idtoko", "0");
        this.xnama_toko = pref.getString("nama_toko", "0");
        this.xalamat_toko = pref.getString("alamat_toko", "0");
        this.xstatus_toko = pref.getString("status_toko", "0");
        this.xketnota = pref.getString("ketnota", "0");
        this.xnohp_toko = pref.getString("nohp_toko", "0");
    }

    public void onBackPressed() {
        Intent intent = new Intent(PendingActivity.this, ProdukTransaksi.class);
        startActivity(intent);
        finish();
    }

    public void showDialogPending(String idtransaksi) {
        Dialog dialog2 = new Dialog(this);
        this.dialog = dialog2;
        dialog2.requestWindowFeature(1);
        this.dialog.setContentView(R.layout.activity_popup_pending);
        RecyclerView rcList2 = (RecyclerView) this.dialog.findViewById(R.id.rcList);
        LinearLayout btlanjut = dialog.findViewById(R.id.btlanjut);
        LinearLayout btBayar = dialog.findViewById(R.id.btBayar);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcList2.setLayoutManager(mLayoutManager);
        rcList2.setAdapter(this.adapterDetail);
        btlanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tambahData(idtransaksi,"lanjut");
            }
        });
        btBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tambahData(idtransaksi,"cetak");
            }
        });

        getPending((LinearLayout) this.dialog.findViewById(R.id.liKosong), rcList2, idtransaksi);
        this.dialog.show();
        this.dialog.getWindow().setLayout(-1, -2);
        this.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        this.dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        this.dialog.getWindow().setGravity(80);
    }

    public void getPending(LinearLayout liKosong, final RecyclerView rcList2, String idtransaksi) {
        liKosong.setVisibility(View.GONE);
        rcList2.setVisibility(View.GONE);
        this.arraylistDetail.clear();
        //this.swipe.setRefreshing(true);
        final String str = idtransaksi;
        AppController.getInstance().addToRequestQueue(new StringRequest(1, URL_SERVER.link + "getdetailpending.php", new Response.Listener<String>() {
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getString("status").equals("ada")) {
                        arraylistDetail.clear();
                        JSONArray jsonArray = new JSONArray(jObj.getString(TAG_RESULTS));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject data = jsonArray.getJSONObject(i);
                            arraylistDetail.add(new MpendingDetail(data.getString("nama"), data.getString("qty"), data.getString("hargajual"), data.getString("harganego")));
                        }
                        adapterDetail = new PendingDetailAdapter(arraylistDetail, PendingActivity.this);
                        rcList2.setAdapter(adapterDetail);
                        rcList2.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(PendingActivity.this, "Kosong", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //swipe.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Koneksi Lemah", Toast.LENGTH_SHORT).show();
                // swipe.setRefreshing(false);
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idtransaksi", str);
                return params;
            }
        }, this.tag_json_obj);
    }

    public void tambahData(String idtransaksi, String pindah) {
        pDialog = new ProgressDialog(PendingActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_SERVER.link+"gettemp_pending.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    int value = jObj.getInt(TAG_VALUE);
                    if (value == 1) {
                        Intent intent = null;
                        if (pindah.equals("lanjut")) {
                             intent = new Intent(PendingActivity.this, ProdukTransaksi.class);
                        }else if (pindah.equals("cetak")){
                            intent = new Intent(PendingActivity.this, TransaksiDetailActivity.class);

                        }
                        startActivity(intent);
                        finish();
                    }  else {
                        Toast.makeText(PendingActivity.this, "Gagal", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(PendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }) {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("idpetugas", xidpetugas);
                params.put("idtransaksi", idtransaksi);


                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

}