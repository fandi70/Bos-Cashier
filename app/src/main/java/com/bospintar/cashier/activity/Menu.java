package com.bospintar.cashier.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bospintar.cashier.R;
import com.bospintar.cashier.adapter.HomeAdapter;
import com.bospintar.cashier.adapter.PetugasAdapter;
import com.bospintar.cashier.app.AppController;
import com.bospintar.cashier.model.Mhome;
import com.bospintar.cashier.server.URL_SERVER;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

//coba keren evan
//coba
public class Menu extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    TextView txtnamapetugas, produktransaksi;
    String xidpetugas, xnama_petugas, xalamat_petugas, xnohp, xlevel, xidtoko, xnama_toko, xalamat_toko, xstatus_toko, xketnota, xnohp_toko;
    LinearLayout produk, pegawai, pengeluaran, lainnya, laporan, pelanggan;
    public static final String TAG_RESULTS = "penjualan_petugas";
    public static final String TAG_VALUE = "status";
    String tag_json_obj = "json_obj_req";
    SwipeRefreshLayout swipe;
    TextView txttotalpenjualanhariini;
    HomeAdapter adapter;
    RecyclerView rcList;
    ArrayList<Mhome> arraylist = new ArrayList<>();
    DecimalFormat rupiahFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    TextView lihatsemuahistory;
    ImageView lockproduk, locklainnya, lproduk, llainnya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        lihatsemuahistory = findViewById(R.id.lihatsemuahistory);
        txttotalpenjualanhariini = findViewById(R.id.txttotalpenjualanhariini);
        swipe = findViewById(R.id.swipe_refreshdata);
        txtnamapetugas = findViewById(R.id.txt_namapetugas);
        produk = findViewById(R.id.ln_produk);

        lockproduk = findViewById(R.id.imgproduk);
        locklainnya = findViewById(R.id.imglainnya);
        lproduk = findViewById(R.id.lockproduk);
        llainnya = findViewById(R.id.locklainnya);

        lainnya = findViewById(R.id.ln_lainnya);
        pegawai = findViewById(R.id.ln_pegawai);
        pengeluaran = findViewById(R.id.ln_pengeluaran);
        produktransaksi = findViewById(R.id.bttransaksi);
        bacaPreferensi();
        txtnamapetugas.setText(xnama_petugas);

        adapter = new HomeAdapter(arraylist, this);
        rcList = findViewById(R.id.rcList);
        final GridLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcList.setLayoutManager(mLayoutManager);
        rcList.setAdapter(adapter);

        lainnya.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // dialog.show(getSupportFragmentManager(), dialog.getTag());

                showhdialog();

            }
        });
        lihatsemuahistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Histori.class));
            }
        });
        produk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Produk.class));

            }
        });

        pegawai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Pelanggan.class));
            }
        });
        produktransaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProdukTransaksi.class));

            }
        });
        pengeluaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Pengeluaran.class));

            }
        });
        callData();
        swipe.setOnRefreshListener(this);

        swipe.post(new Runnable() {
                       @Override
                       public void run() {
                           swipe.setRefreshing(true);
                           callData();
                       }
                   }
        );
        if (xlevel.equals("Kasir")) {
            lockproduk.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_product_lock));
            locklainnya.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_lainnya_lock));
            produk.setEnabled(false);
            lainnya.setEnabled(false);
            llainnya.setVisibility(View.VISIBLE);
            lproduk.setVisibility(View.VISIBLE);
        } else if (xlevel.equals("Admin")) {
            lockproduk.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_product));
            locklainnya.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_lainnya));
            produk.setEnabled(true);
            lainnya.setEnabled(true);
            llainnya.setVisibility(View.GONE);
            lproduk.setVisibility(View.GONE);
        }
    }

    private void callData() {
        arraylist.clear();
        swipe.setRefreshing(true);

        // Creating volley request obj
        StringRequest jArr = new StringRequest(Request.Method.POST, URL_SERVER.CHOME, new Response.Listener<String>() {

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

                            Mhome wp = new Mhome(data.getString("id"), data.getString("idpetugas"), data.getString("nota"), data.getString("totalbayar"), data.getString("tanggal"),
                                    data.getString("statusbayar"), data.getString("idtoko"));
                            arraylist.add(wp);
                        }
                        adapter = new HomeAdapter(arraylist, Menu.this);
                        rcList.setAdapter(adapter);
                        rupiahFormat.setParseBigDecimal(true);
                        rupiahFormat.applyPattern("#,##0");
                        Double xtotal = Double.parseDouble(jObj.getString("total_penjualan"));
                        txttotalpenjualanhariini.setText("Rp" + rupiahFormat.format(xtotal));


                    } else {
                        Toast.makeText(Menu.this, "Kosong", Toast.LENGTH_SHORT).show();
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

    private void showhdialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_dialog);

        produk = dialog.findViewById(R.id.ln_produk);
        pegawai = dialog.findViewById(R.id.ln_pegawai);
        pengeluaran = dialog.findViewById(R.id.ln_pengeluaran);
        pelanggan = dialog.findViewById(R.id.ln_pelanggan);
        laporan = dialog.findViewById(R.id.ln_laporan);
        laporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Menu.this, Laporan.class));
            }
        });
        produk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Menu.this, Produk.class));
            }
        });
        pelanggan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Menu.this, Pelanggan.class));

            }
        });
        pegawai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Menu.this, Petugas.class));

            }
        });
        pengeluaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, Pengeluaran.class);
                startActivity(intent);

            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimasi;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }


    @Override
    public void onRefresh() {
        callData();
    }


}