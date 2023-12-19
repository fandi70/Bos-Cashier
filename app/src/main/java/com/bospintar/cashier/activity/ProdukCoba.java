package com.bospintar.cashier.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bospintar.cashier.R;
import com.bospintar.cashier.adapter.ProdukAdapter;
import com.bospintar.cashier.model.Mproduk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProdukCoba extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    String xidpetugas,xnama_petugas,xalamat_petugas,xnohp,xlevel,xidtoko,xnama_toko,xalamat_toko,xstatus_toko,xketnota,xnohp_toko;

    SwipeRefreshLayout swipe;
    RecyclerView rcList;
    TextView addproduk;
    ImageView btBack;


    private ProdukAdapter adaptercoba;

    ArrayList<Mproduk> arraylist = new ArrayList<>();
    private int currentPage = 1; // Halaman data yang sedang dimuat
    private boolean isLoading = false;
    String c_search="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produk);
        bacaPreferensi();
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refreshdatahistorykegiatan);
        addproduk=(TextView) findViewById(R.id.bt_tambah);

        EditText searchEditText = findViewById(R.id.edt_cariproduk);

        searchEditText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                c_search=s.toString();
                loadMoreData();
                String text = s.toString().toLowerCase(Locale.getDefault());
                TextView txt = findViewById(R.id.txtpesan);
                adaptercoba.filter(text, txt,btBack);

            }
        });
        addproduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Produk_Add.class));
                finish();
            }
        });
        btBack=findViewById(R.id.bt_back);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProdukCoba.this, Menu.class);
                startActivity(intent);
                finish();
            }
        });
//        rcList = findViewById(R.id.rcList);
//        adaptercoba = new ProdukAdapter(arraylist,this);
//        rcList.setAdapter(adaptercoba);
//        rcList.setLayoutManager(new LinearLayoutManager(this));

        adaptercoba = new ProdukAdapter(arraylist, this);
        rcList = findViewById(R.id.rcList);
        final GridLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcList.setLayoutManager(mLayoutManager);
        rcList.setAdapter(adaptercoba);
        // Panggil metode untuk memuat data pertama kali
        loadData();

        // Tambahkan scroll listener untuk mendeteksi scroll ke bawah
        rcList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

                if (!isLoading) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        // Load more data here
                        loadMoreData();
                    }
                }
            }
        });

// Fungsi untuk memuat lebih banyak data


    }


private void loadData() {
    // Menjalankan request pertama ke server
    String url = "https://anikgrosir.majujayaelt.com/data.php?page="+ currentPage+"&idtoko="+ xidtoko+"&idpetugas="+xidpetugas+"&search="+c_search;


    Toast.makeText(ProdukCoba.this, c_search, Toast.LENGTH_SHORT).show();
    JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
//                    List<Mproduk> newItems = parseJsonArray(response); // Metode untuk parsing JSON menjadi objek Item
                    ArrayList<Mproduk> newItems = parseJsonArray(response);
                    // Tambahkan data pertama ke itemList
                    arraylist.addAll(newItems);

                    // Perbarui adapter setelah menambahkan data pertama
                    adaptercoba.setItemList(arraylist);

                    // Tingkatkan nomor halaman untuk request halaman berikutnya
                    currentPage++;
                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            // Handle kesalahan saat melakukan request
            error.printStackTrace();
        }
    });

    // Menambahkan request ke dalam queue Volley
    Volley.newRequestQueue(this).add(jsonArrayRequest);
}

    private void loadMoreData() {
        isLoading = true;

        // Menjalankan request ke server untuk halaman berikutnya
        String url = "https://anikgrosir.majujayaelt.com/data.php?page="+ currentPage+"&idtoko="+ xidtoko+"&idpetugas="+xidpetugas+"&search="+c_search;

        Toast.makeText(ProdukCoba.this, c_search, Toast.LENGTH_SHORT).show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<Mproduk> newItems = parseJsonArray(response); // Metode untuk parsing JSON menjadi objek Item

                        // Tambahkan data halaman selanjutnya ke itemList
                        arraylist.addAll(newItems);

                        // Perbarui adapter setelah menambahkan data baru
                        adaptercoba.setItemList(arraylist);

                        // Selesai memuat data, atur isLoading menjadi false
                        isLoading = false;

                        // Tingkatkan nomor halaman untuk request halaman berikutnya
                        currentPage++;
                        adaptercoba.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle kesalahan saat melakukan request
                error.printStackTrace();
                isLoading = false;
            }
        });

        // Menambahkan request ke dalam queue Volley
        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    private ArrayList<Mproduk> parseJsonArray(JSONArray jsonArray) {
        ArrayList<Mproduk> items = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String nama = jsonObject.getString("nama");
                String hargajual = jsonObject.getString("hargajual");
                String hargabeli = jsonObject.getString("hargabeli");
                String grosir = jsonObject.getString("grosir");

                String stok = jsonObject.getString("stok");
                String satuan = jsonObject.getString("satuan");

                String isi_stok = jsonObject.getString("isi_stok");

                // Membuat objek Item dan menambahkannya ke dalam list
                Mproduk item = new Mproduk(id , nama, hargajual,hargabeli,grosir,stok,satuan,isi_stok);
                items.add(item);
            }
        } catch (JSONException e) {
            // JSON Parsing error
            e.printStackTrace();
        }

        adaptercoba.notifyDataSetChanged();
        return items;
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
    public void onBackPressed() {
        Intent intent = new Intent(ProdukCoba.this, Menu.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onRefresh() {
        loadData();
    }
}