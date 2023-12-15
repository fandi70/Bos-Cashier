package com.bospintar.cashier.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bospintar.cashier.R;
import com.bospintar.cashier.adapter.TransaksiDetailAdapter;
import com.bospintar.cashier.app.AppController;
import com.bospintar.cashier.convert.RupiahTextWatcher;
import com.bospintar.cashier.model.MtransaksiDetail;
import com.bospintar.cashier.model.StringWithTagPelangan;
import com.bospintar.cashier.server.URL_SERVER;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import cn.pedant.SweetAlert.SweetAlertDialog;
import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.connection.usb.UsbConnection;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.bospintar.cashier.async.AsyncBluetoothEscPosPrint;
import com.bospintar.cashier.async.AsyncEscPosPrint;
import com.bospintar.cashier.async.AsyncEscPosPrinter;
import com.bospintar.cashier.async.AsyncUsbEscPosPrint;
public class TransaksiDetailActivity extends AppCompatActivity {
    private ToggleButton toggleButtonstok,toggleButtonmetodepembayaran;
    String idpelanggan="0";
    EditText ed_namaPelanggan;
    TextView addpelanggan;
    List<StringWithTagPelangan> categoryNames = new ArrayList<StringWithTagPelangan>();
    public static final String TAG_RESULTS = "pelanggan";
    public static final String TAG_VALUE = "status";
    ProgressDialog pDialog;
    String tag_json_obj = "json_obj_req";
    String xidpetugas,xnama_petugas,xalamat_petugas,xnohp,xlevel,xidtoko,xnama_toko,xalamat_toko,xstatus_toko,xketnota,xnohp_toko;
    TransaksiDetailAdapter adapter;
    RecyclerView rcList;
    ArrayList<MtransaksiDetail> arraylist = new ArrayList<>();
    TextView totalpenjualan,btn_tombolbayar,btn_tombolsimpan;
    EditText ebayar,ekembali;
    int bayar=0;
    int totalbarang=0;
    JSONObject datalistkeranjang ;
    String jstatusbayar;
    JSONObject datalist;
    JSONArray array = new JSONArray();
    ImageView btBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jstatusbayar="tunai";
        bacaPreferensi();
        setContentView(R.layout.activity_transaksi_detail);



        callDatacustomer();
        totalpenjualan=findViewById(R.id.totalpenjualan);
        ebayar=findViewById(R.id.bayarpenjuan);
        ekembali=findViewById(R.id.kembalian);
        btn_tombolbayar=findViewById(R.id.btn_tombolbayar);
        btn_tombolsimpan=findViewById(R.id.btn_tombolsimpan);
        toggleButtonstok = findViewById(R.id.toggleButtonstok);
        ed_namaPelanggan = findViewById(R.id.ed_namaCustomer);
        addpelanggan = findViewById(R.id.buttonaddcustomer);
        toggleButtonmetodepembayaran=findViewById(R.id.toggleButtonmetodebayar);
        toggleButtonmetodepembayaran.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                jstatusbayar="transfer";
            } else {
                jstatusbayar="tunai";
            }
        });
        toggleButtonstok.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ed_namaPelanggan.setVisibility(View.VISIBLE);
                addpelanggan.setVisibility(View.VISIBLE);
            } else {
                ed_namaPelanggan.setVisibility(View.GONE);
                addpelanggan.setVisibility(View.GONE);
                idpelanggan="0";
            }
        });
        btBack=findViewById(R.id.bt_back);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        adapter = new TransaksiDetailAdapter(arraylist, this);
        rcList = findViewById(R.id.recyclerView);
        final GridLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcList.setLayoutManager(mLayoutManager);
        rcList.setAdapter(adapter);
        callData();

        ed_namaPelanggan.setFocusableInTouchMode(false);
        ed_namaPelanggan.setFocusable(false);
        ed_namaPelanggan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayAdapter<StringWithTagPelangan> categoryAdapter = new ArrayAdapter<>(TransaksiDetailActivity.this, android.R.layout.simple_list_item_1);
                categoryAdapter.addAll(categoryNames);
                AlertDialog.Builder dialog = new AlertDialog.Builder(TransaksiDetailActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_search_customer, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialogButton = dialogView.findViewById(R.id.dialog_button);
                EditText dialogInput = dialogView.findViewById(R.id.edt_cari);
                TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
                ListView dialogList = dialogView.findViewById(R.id.dialog_list);


                dialogTitle.setText("Pelangan ");
                dialogInput.setHint("Cari");
                dialogList.setVerticalScrollBarEnabled(true);
                dialogList.setAdapter(categoryAdapter);

                dialogInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        Log.d("data", s.toString());
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        categoryAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.d("data", s.toString());
                    }
                });


                final AlertDialog alertDialog = dialog.create();

                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


                dialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        alertDialog.dismiss();
                        ((TextView) parent.getChildAt(0)).setTextSize(8);
                        StringWithTagPelangan s = (StringWithTagPelangan) parent.getItemAtPosition(position);

                        Object tag1 = s.tag;
                        Object tag2 = s.string;
                        ed_namaPelanggan.setText(tag2.toString());
                        ed_namaPelanggan.setError(null);
                        idpelanggan=tag1.toString();

                    }
                });
            }
        });
        addpelanggan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(TransaksiDetailActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.activity_add_pelanggan, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);
                final EditText ddnama= dialogView.findViewById(R.id.etxt_namacustomer);
                final EditText ddalamat= dialogView.findViewById(R.id.etxt_alamatcustomer);
                final EditText ddnohp = dialogView.findViewById(R.id.etxt_nohpcustomer);

                final TextView dialogBtnSubmit = dialogView.findViewById(R.id.btlogin);
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
                        simpancustomerData(ddnama.getText().toString(),ddalamat.getText().toString(),ddnohp.getText().toString());

                    }
                });



            }
        });
        ebayar.addTextChangedListener(new RupiahTextWatcher(ebayar));
        ebayar.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (String.valueOf(s).equals("")){
                    ekembali.setText("");
                }else {
                    BigDecimal _bayar = RupiahTextWatcher.parseCurrencyValue(String.valueOf(s));
                    BigDecimal _total = RupiahTextWatcher.parseCurrencyValue(totalpenjualan.getText().toString());
                    int kembali=Integer.parseInt(_bayar.toString())-Integer.parseInt(_total.toString());

                    double amount =kembali;
                    DecimalFormat rupiahFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                    rupiahFormat.setParseBigDecimal(true);
                    rupiahFormat.applyPattern("#,##0");
                    String formattedRupiah = rupiahFormat.format(amount);
                    ekembali.setText("Rp"+String.valueOf(formattedRupiah));
                }


            }
        });
        btn_tombolsimpan.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if(ebayar.getText().toString().equals("")){
                    ebayar.setError("harus di isi");
                    ebayar.requestFocus();
                }else{
//                        BigDecimal kbayar = RupiahTextWatcher.parseCurrencyValue(ebayar.getText().toString());
//                        BigDecimal ktotal = RupiahTextWatcher.parseCurrencyValue(totalpenjualan.getText().toString());
//                        if(Integer.parseInt(String.valueOf(kbayar))<Integer.parseInt(String.valueOf(ktotal))){
//                            Toast.makeText(TransaksiDetailActivity.this, "Pembayaran Kurang", Toast.LENGTH_SHORT).show();
//                        }else{
                    simpanserver("pending");
//                        }
                }
            }

        });
        btn_tombolbayar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (idpelanggan.equals("0")){
                    if(ebayar.getText().toString().equals("")){
                        ebayar.setError("harus di isi");
                        ebayar.requestFocus();
                    }else{
                        BigDecimal kbayar = RupiahTextWatcher.parseCurrencyValue(ebayar.getText().toString());
                        BigDecimal ktotal = RupiahTextWatcher.parseCurrencyValue(totalpenjualan.getText().toString());
                        if(Integer.parseInt(String.valueOf(kbayar))<Integer.parseInt(String.valueOf(ktotal))){
                            Toast.makeText(TransaksiDetailActivity.this, "Pembayaran Kurang", Toast.LENGTH_SHORT).show();
                        }else{
                            simpanserver("selesai");
//                            new SweetAlertDialog(TransaksiDetailActivity.this, SweetAlertDialog.WARNING_TYPE)
//                                    .setTitleText("Informasi")
//                                    .setContentText("Apakah Transaksi Mau Dicetak?")
//                                    .setConfirmText("Iya")
//                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                        @Override
//                                        public void onClick(SweetAlertDialog sDialog) {
//                                            sDialog.dismissWithAnimation();
//                                            simpanserver("selesai");
//                                            printBluetooth();
//                                        }
//                                    }).setCancelText("Tidak")
//                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                        @Override
//                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                            sweetAlertDialog.dismissWithAnimation();
//                                            simpanserver("selesai");
//                                        }
//                                    })
//                                    .show();

                        }
                    }

                }else{
                    simpanserver("selesai");
                }
            }

        });
//        btn_tombolbayar.setOnClickListener(view -> printBluetooth());
        ekembali.setEnabled(false);
    }
    public void simpancustomerData(final String _nama,final String _alamat,final String _nohp) {
        pDialog = new ProgressDialog(TransaksiDetailActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_SERVER.CPELANGGANADD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    int value = jObj.getInt("success");
                    if (value == 1) {
                        Toast.makeText(TransaksiDetailActivity.this, "Sukses", Toast.LENGTH_SHORT).show();
                        callDatacustomer();

                    } else {
                        Toast.makeText(TransaksiDetailActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
                        callDatacustomer();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TransaksiDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("nama", _nama);
                params.put("alamat", _alamat);
                params.put("nohp", _nohp);
                params.put("idtoko",xidtoko);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }
    private void callDatacustomer() {
        StringRequest jArr = new StringRequest(Request.Method.POST, URL_SERVER.CTCUSTOMER, new Response .Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    String value = jObj.getString(TAG_VALUE);

                    if (value.equals("ada")) {
                        String getObject = jObj.getString(TAG_RESULTS);
                        JSONArray jsonArray = new JSONArray(getObject);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            categoryNames.add(new StringWithTagPelangan(obj.getString("nama"), obj.getString("id")));
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Koneksi Lemah", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idtoko", xidtoko);
                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(jArr, tag_json_obj);
    }
    private void callData() {
        arraylist.clear();
       // adapter.notifyDataSetChanged();
//        swipe.setRefreshing(true);

        // Creating volley request obj
        StringRequest jArr = new StringRequest(Request.Method.POST, URL_SERVER.Ctimp, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);

                    String value = jObj.getString("status");

                    if (value.equals("ada")) {
                        arraylist.clear();


                        String getObject = jObj.getString("produk");
                        JSONArray jsonArray = new JSONArray(getObject);
                        totalbarang=0;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject data = jsonArray.getJSONObject(i);

                            MtransaksiDetail wp = new MtransaksiDetail(data.getString("idb"), data.getString("nama"), data.getString("jumlah_penjualan"),
                                    data.getString("idpetugas"), data.getString("harga_jual"), data.getString("harga_beli"));
                            arraylist.add(wp);
                            totalbarang+=Integer.parseInt(data.getString("jumlah_penjualan"))*Integer.parseInt(data.getString("harga_jual"));

                        }
                        double amount =totalbarang;
                        DecimalFormat rupiahFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                        rupiahFormat.setParseBigDecimal(true);
                        rupiahFormat.applyPattern("#,##0");
                        String formattedRupiah = rupiahFormat.format(amount);
                        totalpenjualan.setText("Rp"+String.valueOf(formattedRupiah));


                    } else {
                        Toast.makeText(TransaksiDetailActivity.this, "Kosong", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();
//                swipe.setRefreshing(false);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Koneksi Lemah", Toast.LENGTH_SHORT).show();
//                swipe.setRefreshing(false);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("idpetugas", xidpetugas);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jArr, tag_json_obj);
    }
    public void tambahDataplusmin(String xxidbarang,String xxketerangan) {
        pDialog = new ProgressDialog(TransaksiDetailActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_SERVER.CTAMBAHKERANJANG, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    int value = jObj.getInt(TAG_VALUE);
                    if (value == 1) {
                        callData();
                        ebayar.setText("");
                        ekembali.setText("");
                        ebayar.setHint("Rp0");
                        ekembali.setHint("Rp0");

                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TransaksiDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }) {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idproduk", xxidbarang.toString());
                params.put("idpetugas", xidpetugas);
                params.put("keterangan", xxketerangan.toString());
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }
    public void hapusitem(String xxidbarang) {
        pDialog = new ProgressDialog(TransaksiDetailActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_SERVER.Ctimphapus, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    int value = jObj.getInt(TAG_VALUE);
                    if (value == 1) {
                        Toast.makeText(TransaksiDetailActivity.this, "Item dihapus", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TransaksiDetailActivity.this, "Item Gagal", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TransaksiDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }) {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idproduk", xxidbarang.toString());
                params.put("idpetugas", xidpetugas);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }
    public void simpanserver(String statuspending) {
        pDialog = new ProgressDialog(TransaksiDetailActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();
        int laba=0;
        for (MtransaksiDetail order : arraylist) {
            datalist = new JSONObject();
            try {
                laba=(Integer.parseInt(order.getHarga_jual())-Integer.parseInt(order.getHarga_beli()))*Integer.parseInt(order.getJumlah_penjualan());
                datalist.put("idproduk", order.getIdb());
                datalist.put("hargajual", order.getHarga_jual());
                datalist.put("hargabeli",order.getHarga_beli());
                datalist.put("laba",laba);
                datalist.put("qty",order.getJumlah_penjualan());
                array.put(datalist);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_SERVER.CSIMPANKERANJANG, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    int value = jObj.getInt("success");
                    String ssid = jObj.getString("idtransaksi");

                    if (value == 1) {
                        Toast.makeText(TransaksiDetailActivity.this, "Sukses", Toast.LENGTH_SHORT).show();
                        if (statuspending.equals("selesai") ){
                            Intent kotak = new Intent(TransaksiDetailActivity.this, TransaksiCetakActivity.class);
                            kotak.putExtra("id",ssid);
                            startActivity(kotak);
                            finish();
                        }else if(statuspending.equals("pending")){
                            startActivity(new Intent(TransaksiDetailActivity.this, ProdukTransaksi.class));
                            finish();
                        }

                    } else {
                        Toast.makeText(TransaksiDetailActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(TransaksiDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }) {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                BigDecimal s_totalbayar = RupiahTextWatcher.parseCurrencyValue(totalpenjualan.getText().toString());
                BigDecimal s_bayar = RupiahTextWatcher.parseCurrencyValue(ebayar.getText().toString());
                BigDecimal s_kembali = RupiahTextWatcher.parseCurrencyValue(ekembali.getText().toString());
                params.put("idtoko", xidtoko);
                params.put("idpetugas", xidpetugas);
                params.put("totalbayar", s_totalbayar.toString());
                params.put("bayar", s_bayar.toString());
                params.put("sisa", s_kembali.toString());
                params.put("idpelanggan", idpelanggan);
                params.put("jbayar", jstatusbayar);
                params.put("status", statuspending);
                params.put("list", String.valueOf(array));


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
        xnohp= pref.getString("nohp", "0");
        xlevel= pref.getString("level", "0");
        xidtoko= pref.getString("idtoko", "0");
        xnama_toko= pref.getString("nama_toko", "0");
        xalamat_toko= pref.getString("alamat_toko", "0");
        xstatus_toko= pref.getString("status_toko", "0");
        xketnota= pref.getString("ketnota", "0");
        xnohp_toko=pref.getString("nohp_toko","0");

    }

//    cetak
public interface OnBluetoothPermissionsGranted {
    void onPermissionsGranted();
}

    public static final int PERMISSION_BLUETOOTH = 1;
    public static final int PERMISSION_BLUETOOTH_ADMIN = 2;
    public static final int PERMISSION_BLUETOOTH_CONNECT = 3;
    public static final int PERMISSION_BLUETOOTH_SCAN = 4;

    public TransaksiDetailActivity.OnBluetoothPermissionsGranted onBluetoothPermissionsGranted;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case TransaksiDetailActivity.PERMISSION_BLUETOOTH:
                case TransaksiDetailActivity.PERMISSION_BLUETOOTH_ADMIN:
                case TransaksiDetailActivity.PERMISSION_BLUETOOTH_CONNECT:
                case TransaksiDetailActivity.PERMISSION_BLUETOOTH_SCAN:
                    this.checkBluetoothPermissions(this.onBluetoothPermissionsGranted);
                    break;
            }
        }
    }

    public void checkBluetoothPermissions(TransaksiDetailActivity.OnBluetoothPermissionsGranted onBluetoothPermissionsGranted) {
        this.onBluetoothPermissionsGranted = onBluetoothPermissionsGranted;
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, TransaksiDetailActivity.PERMISSION_BLUETOOTH);
        } else if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, TransaksiDetailActivity.PERMISSION_BLUETOOTH_ADMIN);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, TransaksiDetailActivity.PERMISSION_BLUETOOTH_CONNECT);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, TransaksiDetailActivity.PERMISSION_BLUETOOTH_SCAN);
        } else {
            this.onBluetoothPermissionsGranted.onPermissionsGranted();
        }
    }

    private BluetoothConnection selectedDevice;

    public void printBluetooth() {
        this.checkBluetoothPermissions(() -> {
            new AsyncBluetoothEscPosPrint(
                    this,
                    new AsyncEscPosPrint.OnPrintFinished() {
                        @Override
                        public void onError(AsyncEscPosPrinter asyncEscPosPrinter, int codeException) {
                            Log.e("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : An error occurred !");
                        }

                        @Override
                        public void onSuccess(AsyncEscPosPrinter asyncEscPosPrinter) {
                            Log.i("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : Print is finished !");
                        }
                    }
            )
                    .execute(this.getAsyncEscPosPrinter(selectedDevice));
        });
    }


    /**
     * Asynchronous printing
     */
    @SuppressLint("SimpleDateFormat")
    public AsyncEscPosPrinter getAsyncEscPosPrinter(DeviceConnection printerConnection) {
        final Locale locale = new Locale("id", "ID");
        final DateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", locale);
        final NumberFormat nf = NumberFormat.getCurrencyInstance(locale);

        DecimalFormat rupiahFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        rupiahFormat.setParseBigDecimal(true);
        rupiahFormat.applyPattern("#,##0");

        StringBuilder a = new StringBuilder();
        for (MtransaksiDetail order : arraylist) {
                int totalprinttt=Integer.parseInt(order.getHarga_jual())*Integer.parseInt(order.getJumlah_penjualan());
                a.append(order.getNamabarang()+"\n").append(order.getJumlah_penjualan()).append(" x ").append(rupiahFormat.format(Double.parseDouble(order.getHarga_jual()))).append("[R]").append(""+rupiahFormat.format(totalprinttt)).append("\n\n");
        }
        // Mendapatkan waktu saat ini
        Date currentDate = new Date();

        // Mengatur zona waktu ke Indonesia
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Jakarta");
        dateFormat.setTimeZone(timeZone);

        // Format waktu sesuai dengan zona waktu Indonesia
        String tglsekarang = dateFormat.format(currentDate);

        AsyncEscPosPrinter printer = new AsyncEscPosPrinter(printerConnection, 203, 48f, 32);
        final String text = "[C]ANIK GROSIR\n" +
                "[L]\n" +


                "[L]" +"Tanggal : "+tglsekarang+"\n" +
                "[L]" +"Kasir   : "+xnama_petugas+"\n" +
                "[L]" +"No Hp   : 1\n" +
                "[C]================================\n" +
                a+
                "[C]--------------------------------\n" +
                "[L]Total[R]" + totalpenjualan.getText().toString() + "\n" +
                "[L]Bayar[R]" + ebayar.getText().toString() + "\n" +
                "[L]Kembali[R]" + ekembali.getText().toString()  + "\n" +

                "[C]--------------------------------\n" +

                "[C]Terimakasih Sudah Berbelanja\n" +
                "[C]Norek a/n Femina anik sri utami \n" +
                "[C]Bca : 2019707339  \n" +
                "[C]Bri : 058201000213562";
        return printer.addTextToPrint(text);
    }
    @Override
    public void onBackPressed() {
        finish();
    }


}