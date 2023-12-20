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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bospintar.cashier.R;
import com.bospintar.cashier.app.AppController;
import com.bospintar.cashier.convert.RupiahTextWatcher;
import com.bospintar.cashier.server.URL_SERVER;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Produk_Edit extends AppCompatActivity {
    public static final String TAG_VALUE = "status";
    JSONArray array = new JSONArray();
    TextView bsave;
    ImageView btBack;
    private TextView btnTambahGrosir;
    JSONObject datalist;
    /* access modifiers changed from: private */
    public GrosirAdapter grosirAdapter;
    EditText hbeli;
    EditText hjual;
    /* access modifiers changed from: private */
    public List<ItemGrosir> itemGrosirList;
    EditText nmproduk;
    ProgressDialog pDialog;
    /* access modifiers changed from: private */
    public RecyclerView recyclerView;
    EditText satuan;
    String status_grosir = "T";
    String status_stock = "T";
    EditText stock;
    String tag_json_obj = "json_obj_req";
    /* access modifiers changed from: private */
    public ToggleButton toggleButton;
    /* access modifiers changed from: private */
    public ToggleButton toggleButtonstok;
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

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_produk_edit);
        bacaPreferensi();
        this.toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        this.toggleButtonstok = (ToggleButton) findViewById(R.id.toggleButtonstok);
        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        this.btnTambahGrosir = (TextView) findViewById(R.id.btnTambahGrosir);
        this.bsave = (TextView) findViewById(R.id.btsave);
        this.nmproduk = (EditText) findViewById(R.id.edt_namaproduk);
        this.hbeli = (EditText) findViewById(R.id.edt_hargabeli);
        this.hjual = (EditText) findViewById(R.id.edt_hargajual);
        this.stock = (EditText) findViewById(R.id.edt_stock);
        this.satuan = (EditText) findViewById(R.id.edt_satuan);
        this.itemGrosirList = new ArrayList();
        this.grosirAdapter = new GrosirAdapter(this.itemGrosirList);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setNestedScrollingEnabled(false);
        this.recyclerView.setAdapter(this.grosirAdapter);
        this.hbeli.addTextChangedListener(new RupiahTextWatcher(this.hbeli));
        this.hjual.addTextChangedListener(new RupiahTextWatcher(this.hjual));
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Jika switch ON, tambahkan satu item secara otomatis
                // addItemGrosir();

                // Tampilkan item grosir dan tombol tambah grosir
                recyclerView.setVisibility(View.VISIBLE);
                btnTambahGrosir.setVisibility(View.VISIBLE);
                status_grosir = "Y";
            } else {
                // Jika switch OFF, hapus semua item
                //clearAllItemGrosir();

                // Sembunyikan item grosir
                btnTambahGrosir.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);

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
        ImageView imageView = (ImageView) findViewById(R.id.bt_back);
        this.btBack = imageView;
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Produk_Edit.this.startActivity(new Intent(Produk_Edit.this, Produk.class));
                Produk_Edit.this.finish();
            }
        });
        this.btnTambahGrosir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemGrosir();
            }
        });
        this.bsave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(View v) {
                if (nmproduk.getText().toString().trim().isEmpty()) {
                    nmproduk.setError("Belum diisi");
                    nmproduk.requestFocus();
                    nmproduk.setText("");
                } else if (hbeli.getText().toString().trim().isEmpty() || hbeli.getText().toString().equals("Rp0")) {
                    hbeli.setError("Belum diisi");
                    hbeli.requestFocus();
                } else if (hjual.getText().toString().trim().isEmpty() || hjual.getText().toString().equals("Rp0")) {
                    hjual.setError("Belum diisi");
                    hjual.requestFocus();
                } else {
                    final Dialog dialog = new Dialog(Produk_Edit.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_informasi);

                    TextView cancelButton = dialog.findViewById(R.id.cancelButton);
                    TextView okButton = dialog.findViewById(R.id.okButton);
                    TextView txtjudul = dialog.findViewById(R.id.txtjudul);
                    TextView txtsubjudul = dialog.findViewById(R.id.txtsubjudul);
                    txtjudul.setText("Edit data Produk");
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
        callData();
        btnTambahGrosir.setOnClickListener(v -> addItemGrosir());
    }

    private void addItemGrosir() {
        this.itemGrosirList.add(new ItemGrosir("", ""));
        this.grosirAdapter.notifyDataSetChanged();
        this.recyclerView.smoothScrollToPosition(this.itemGrosirList.size() - 1);
    }


    private void callData() {
        AppController.getInstance().addToRequestQueue(new StringRequest(1, URL_SERVER.link + "getprodukdetail.php", new Response.Listener<String>() {
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getString("status").equals("ada")) {
                        Produk_Edit.this.nmproduk.setText(jObj.getString("nama"));
                        Produk_Edit.this.hbeli.setText(jObj.getString("hargabeli"));
                        Produk_Edit.this.hjual.setText(jObj.getString("hargajual"));
                        if (jObj.getString("s_grosir").equals("Y")) {
                            Produk_Edit.this.toggleButton.setChecked(true);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            Produk_Edit.this.toggleButton.setChecked(false);
                            recyclerView.setVisibility(View.GONE);
                        }
                        if (jObj.getString("s_stok").equals("Y")) {
                            Produk_Edit.this.toggleButtonstok.setChecked(true);
                            Produk_Edit.this.stock.setText(jObj.getString("isi_stok"));
                            Produk_Edit.this.satuan.setText(jObj.getString("satuan"));
                        } else {
                            Produk_Edit.this.toggleButtonstok.setChecked(false);
                        }
                        JSONArray jsonArray = new JSONArray(jObj.getString("grosir"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject data = jsonArray.getJSONObject(i);
                            Produk_Edit.this.itemGrosirList.add(new ItemGrosir(data.getString("hargajual"), data.getString("minimal")));
                        }
                        GrosirAdapter unused = Produk_Edit.this.grosirAdapter = new GrosirAdapter(Produk_Edit.this.itemGrosirList);
                        Produk_Edit.this.recyclerView.setAdapter(Produk_Edit.this.grosirAdapter);
                        return;
                    }
                    Toast.makeText(Produk_Edit.this, "Kosong", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Produk_Edit.this.getApplicationContext(), "Koneksi Lemah", Toast.LENGTH_SHORT).show();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idtoko", xidtoko);
                params.put("idproduk", getIntent().getStringExtra("idproduk"));
                return params;
            }
        }, this.tag_json_obj);
    }

    private static class GrosirAdapter extends RecyclerView.Adapter<GrosirAdapter.ViewHolder> {


        private static List<ItemGrosir> itemGrosirList;

        public GrosirAdapter(List<ItemGrosir> itemGrosirList) {
            this.itemGrosirList = itemGrosirList;
        }

        @Override
        public GrosirAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.item_add_grosir, null);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, // Lebar
                    ViewGroup.LayoutParams.WRAP_CONTENT // Tinggi
            );
            view.setLayoutParams(layoutParams);

            return new GrosirAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(GrosirAdapter.ViewHolder holder, int position) {
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

    public class ItemGrosir {
        private String hargaJual;
        private String minimal;

        public ItemGrosir(String hargaJual2, String minimal2) {
            this.hargaJual = hargaJual2;
            this.minimal = minimal2;
        }

        public String getMinimal() {
            return this.minimal;
        }

        public void setMinimal(String minimal2) {
            this.minimal = minimal2;
        }

        public String getHargaJual() {
            return this.hargaJual;
        }

        public void setHargaJual(String hargaJual2) {
            this.hargaJual = hargaJual2;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void tambahData() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        this.pDialog = progressDialog;
        progressDialog.setCancelable(false);
        this.pDialog.setMessage("Loading...");
        this.pDialog.show();
        for (ItemGrosir order : this.itemGrosirList) {
            JSONObject jSONObject = new JSONObject();
            this.datalist = jSONObject;
            try {
                jSONObject.put("minimal", order.getMinimal());
                this.datalist.put("hargajual", RupiahTextWatcher.parseCurrencyValue(order.getHargaJual()));
                this.array.put(this.datalist);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        AppController.getInstance().addToRequestQueue(new StringRequest(1, "https://anikgrosir.majujayaelt.com/editproduk.php", new Response.Listener<String>() {
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());
                try {
                    if (new JSONObject(response).getInt("success") == 1) {
                        Toast.makeText(Produk_Edit.this, "Sukses", Toast.LENGTH_SHORT).show();
                        Produk_Edit.this.startActivity(new Intent(Produk_Edit.this, Produk.class));
                        Produk_Edit.this.finish();
                    } else {
                        Toast.makeText(Produk_Edit.this, "Gagal", Toast.LENGTH_SHORT).show();
                        Produk_Edit.this.startActivity(new Intent(Produk_Edit.this, Produk.class));
                        Produk_Edit.this.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Produk_Edit.this.pDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Produk_Edit.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Produk_Edit.this.pDialog.dismiss();
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                BigDecimal hargajual = RupiahTextWatcher.parseCurrencyValue(Produk_Edit.this.hjual.getText().toString());
                BigDecimal hargabeli = RupiahTextWatcher.parseCurrencyValue(Produk_Edit.this.hbeli.getText().toString());
                params.put("nama", Produk_Edit.this.nmproduk.getText().toString());
                params.put("hargajual", hargajual.toString());
                params.put("hargabeli", hargabeli.toString());
                params.put("idtoko", Produk_Edit.this.xidtoko);
                params.put("stok", Produk_Edit.this.stock.getText().toString());
                params.put("satuan", Produk_Edit.this.satuan.getText().toString());
                params.put("s_stok", Produk_Edit.this.status_stock);
                params.put("s_grosir", Produk_Edit.this.status_grosir);
                params.put("list", String.valueOf(Produk_Edit.this.array));
                params.put("idproduk", Produk_Edit.this.getIntent().getStringExtra("idproduk"));
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Produk_Edit.this, Produk.class));
        finish();
    }
}
