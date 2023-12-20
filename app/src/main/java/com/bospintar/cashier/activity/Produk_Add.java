
package com.bospintar.cashier.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.bospintar.cashier.R;
import com.bospintar.cashier.app.AppController;
import com.bospintar.cashier.convert.RupiahTextWatcher;
import com.bospintar.cashier.server.URL_SERVER;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Produk_Add extends AppCompatActivity {
    // ArrayList<ItemGrosir> myList = new ArrayList<>();
    String xidpetugas, xnama_petugas, xalamat_petugas, xnohp, xlevel, xidtoko, xnama_toko, xalamat_toko, xstatus_toko, xketnota, xnohp_toko;


    private ToggleButton toggleButton, toggleButtonstok;
    private RecyclerView recyclerView;
    private TextView btnTambahGrosir;
    private List<ItemGrosir> itemGrosirList;
    private GrosirAdapter grosirAdapter;
    EditText nmproduk, hbeli, hjual, stock, satuan;
    ImageView btBack;
    TextView bsave;
    String status_stock = "T";
    String status_grosir = "T";
    public static final String TAG_VALUE = "success";
    ProgressDialog pDialog;
    JSONObject datalist;
    JSONArray array = new JSONArray();
    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produk_add);
        bacaPreferensi();


        toggleButton = findViewById(R.id.toggleButton);
        toggleButtonstok = findViewById(R.id.toggleButtonstok);
        recyclerView = findViewById(R.id.recyclerView);
        btnTambahGrosir = findViewById(R.id.btnTambahGrosir);
        bsave = findViewById(R.id.btsave);
        nmproduk = findViewById(R.id.edt_namaproduk);
        hbeli = findViewById(R.id.edt_hargabeli);
        hjual = findViewById(R.id.edt_hargajual);
        stock = findViewById(R.id.edt_stock);
        satuan = findViewById(R.id.edt_satuan);


        itemGrosirList = new ArrayList<>();
        grosirAdapter = new GrosirAdapter(itemGrosirList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(grosirAdapter);

        hbeli.addTextChangedListener(new RupiahTextWatcher(hbeli));
        hjual.addTextChangedListener(new RupiahTextWatcher(hjual));

        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Jika switch ON, tambahkan satu item secara otomatis
                addItemGrosir();

                // Tampilkan item grosir dan tombol tambah grosir
                recyclerView.setVisibility(View.VISIBLE);
                btnTambahGrosir.setVisibility(View.VISIBLE);
                status_grosir = "Y";
            } else {
                // Jika switch OFF, hapus semua item
                clearAllItemGrosir();

                // Sembunyikan item grosir
                btnTambahGrosir.setVisibility(View.GONE);

                // Matikan switch secara asinkron setelah RecyclerView selesai diupdate
                recyclerView.post(() -> toggleButton.setChecked(false));
                status_grosir = "T";
            }
        });
        toggleButtonstok.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                satuan.setEnabled(true);
                stock.setEnabled(true);
                status_stock = "Y";
            } else {
                // Jika switch OFF, hapus semua item
                satuan.setEnabled(false);
                stock.setEnabled(false);
                status_stock = "T";
            }
        });
        btBack = findViewById(R.id.bt_back);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Produk_Add.this, Produk.class));
                finish();
            }
        });
        btnTambahGrosir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemGrosir();
            }
        }


        );
        bsave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (nmproduk.getText().toString().trim().isEmpty()) {
                    nmproduk.setError("Belum diisi");
                    nmproduk.setText("");
                    nmproduk.requestFocus();
                } else if (hbeli.getText().toString().trim().isEmpty()|| hbeli.getText().toString().equals("Rp0")) {
                    hjual.setError("Belum diisi");
                    hjual.requestFocus();
                } else if (hjual.getText().toString().trim().isEmpty() || hjual.getText().toString().equals("Rp0")) {
                    hbeli.setError("Belum diisi");
                    hbeli.requestFocus();
                } else {
                    final Dialog dialog = new Dialog(Produk_Add.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_informasi);

                    TextView cancelButton = dialog.findViewById(R.id.cancelButton);
                    TextView okButton = dialog.findViewById(R.id.okButton);
                    TextView txtjudul = dialog.findViewById(R.id.txtjudul);
                    TextView txtsubjudul = dialog.findViewById(R.id.txtsubjudul);
                    txtjudul.setText("Tambah data Produk");
                    txtsubjudul.setText("Apakah anda sudah yakin semua data yang diinputkan sudah benar?");
                    okButton.setText("Yakin");
                    cancelButton.setText("Cek lagi");

                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialog.dismiss();

                            tambahData();
                        }
                    });

                    dialog.setCancelable(false);


                    dialog.show();
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


                }

            }
        });
    }

    private void addItemGrosir() {
        ItemGrosir newItem = new ItemGrosir();
        itemGrosirList.add(newItem);
        grosirAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(itemGrosirList.size() - 1);
    }

    private void clearAllItemGrosir() {
        itemGrosirList.clear();
        grosirAdapter.notifyDataSetChanged();

        // Periksa apakah semua item telah dihapus
        if (itemGrosirList.isEmpty()) {
            toggleButton.setChecked(false); // Matikan switch jika tidak ada item
        }
    }

    private static class GrosirAdapter extends RecyclerView.Adapter<GrosirAdapter.ViewHolder> {


        private static List<ItemGrosir> itemGrosirList;

        public GrosirAdapter(List<ItemGrosir> itemGrosirList) {
            this.itemGrosirList = itemGrosirList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.item_add_grosir, null);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, // Lebar
                    ViewGroup.LayoutParams.WRAP_CONTENT // Tinggi
            );
            view.setLayoutParams(layoutParams);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ItemGrosir itemGrosir = itemGrosirList.get(position);
            holder.bind(itemGrosir);
            // Request focus if it's the last item added
            if (position == itemGrosirList.size() - 1) {
                holder.edtMinimal.requestFocus();
            }
        }

        @Override
        public int getItemCount() {
            return itemGrosirList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            EditText edtMinimal;
            EditText edtHargaJual;
            ImageView imgRemove;

            public ViewHolder(View itemView) {
                super(itemView);
                edtMinimal = itemView.findViewById(R.id.edtMinimal);
                edtHargaJual = itemView.findViewById(R.id.edtHargaJual);
                imgRemove = itemView.findViewById(R.id.imgRemove);

                // Tambahkan listener untuk menghapus item grosir
                imgRemove.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        itemGrosirList.remove(position);
                        notifyItemRemoved(position);
                    }
                });
                // Tambahkan TextWatcher untuk menyimpan data yang dimasukkan pengguna
                edtMinimal.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            ItemGrosir itemGrosir = itemGrosirList.get(position);
                            itemGrosir.setMinimal(charSequence.toString());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });
                edtHargaJual.addTextChangedListener(new RupiahTextWatcher(edtHargaJual));
                edtHargaJual.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            ItemGrosir itemGrosir = itemGrosirList.get(position);
                            itemGrosir.setHargaJual(charSequence.toString());
//                            Toast.makeText(Produk_Add.this, String.valueOf(array), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });
            }

            public void bind(ItemGrosir itemGrosir) {
                // Set data ke tampilan item grosir
                edtMinimal.setText(itemGrosir.getMinimal());
                edtHargaJual.setText(itemGrosir.getHargaJual());
            }
        }
    }

    private static class ItemGrosir {
        private String minimal;
        private String hargaJual;

        public String getMinimal() {
            return minimal;
        }

        public void setMinimal(String minimal) {
            this.minimal = minimal;
        }

        public String getHargaJual() {
            return hargaJual;
        }

        public void setHargaJual(String hargaJual) {
            this.hargaJual = hargaJual;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void tambahData() {
        pDialog = new ProgressDialog(Produk_Add.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();
        for (ItemGrosir order : itemGrosirList) {
            datalist = new JSONObject();
            try {
                datalist.put("minimal", order.getMinimal());
                datalist.put("hargajual", RupiahTextWatcher.parseCurrencyValue(order.getHargaJual()));
                array.put(datalist);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_SERVER.CPRODUKS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    int value = jObj.getInt(TAG_VALUE);
                    if (value == 1) {
                        Toast.makeText(Produk_Add.this, "Sukses", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Produk_Add.this, Produk.class));
                        finish();

                    } else {
                        Toast.makeText(Produk_Add.this, "Gagal", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Produk_Add.this, Produk.class));
                        finish();
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
                Toast.makeText(Produk_Add.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }) {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                BigDecimal hargajual = RupiahTextWatcher.parseCurrencyValue(hjual.getText().toString());
                BigDecimal hargabeli = RupiahTextWatcher.parseCurrencyValue(hbeli.getText().toString());


                params.put("nama", nmproduk.getText().toString());
                params.put("hargajual", hargajual.toString());
                params.put("hargabeli", hargabeli.toString());
                params.put("idtoko", xidtoko);
                params.put("stok", stock.getText().toString());
                params.put("satuan", satuan.getText().toString());
                params.put("s_stok", status_stock);
                params.put("s_grosir", status_grosir);
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
        startActivity(new Intent(Produk_Add.this, Produk.class));
        finish();
    }
}