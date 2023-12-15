package com.bospintar.cashier.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bospintar.cashier.R;
import com.bospintar.cashier.adapter.CetakDetailAdapter;
import com.bospintar.cashier.adapter.TransaksiDetailAdapter;
import com.bospintar.cashier.app.AppController;
import com.bospintar.cashier.async.AsyncBluetoothEscPosPrint;
import com.bospintar.cashier.async.AsyncEscPosPrint;
import com.bospintar.cashier.async.AsyncEscPosPrinter;
import com.bospintar.cashier.model.MpendingDetail;
import com.bospintar.cashier.model.MtransaksiDetail;
import com.bospintar.cashier.server.URL_SERVER;
import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class TransaksiCetakActivity extends AppCompatActivity {
    CetakDetailAdapter adapter;
    String xidpetugas, xnama_petugas, xalamat_petugas, xnohp, xlevel, xidtoko, xnama_toko, xalamat_toko, xstatus_toko, xketnota, xnohp_toko;

    RecyclerView rcList;
    String idtransaksi;
    ArrayList<MpendingDetail> arraylist = new ArrayList<>();
    public static final String TAG_VALUE = "status";
    String tag_json_obj = "json_obj_req";
    TextView total,bayar,kembali;
    LinearLayout lanjut,cetak;
    TextView metodepembayaran,statuspembayaran,nota,namakasir,nohpkasir;
    ImageView btBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_transaksi);
        bacaPreferensi();
        Intent intent=getIntent();
        if (intent !=null){
            idtransaksi=intent.getStringExtra("id");

        }
        btBack=findViewById(R.id.bt_back);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });
        total=findViewById(R.id.total);
        lanjut=findViewById(R.id.lanjut);
        lanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProdukTransaksi.class));
                finish();
            }
        });
        cetak=findViewById(R.id.btcetak);
        cetak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printBluetooth();
            }
        });
        metodepembayaran=findViewById(R.id.metodepembayaran);
        statuspembayaran=findViewById(R.id.statuspembayaran);
        nohpkasir=findViewById(R.id.nohpkasir);
        namakasir=findViewById(R.id.namakasir);
        nota=findViewById(R.id.notatransaksi);
        bayar=findViewById(R.id.bayar);
        kembali=findViewById(R.id.kembalian);

        adapter = new CetakDetailAdapter(arraylist, this);
        rcList = findViewById(R.id.recyclerView);
        final GridLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcList.setLayoutManager(mLayoutManager);
        rcList.setAdapter(adapter);
        callData();
    }
    private void callData() {
        arraylist.clear();
        StringRequest jArr = new StringRequest(Request.Method.POST, URL_SERVER.Ccetakdetailtransaksi, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());


                try {
                    JSONObject jObj = new JSONObject(response);

                    String value = jObj.getString("status");

                    if (value.equals("ada")) {
                        arraylist.clear();


                        String getObject = jObj.getString("pending");
                        JSONArray jsonArray = new JSONArray(getObject);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject data = jsonArray.getJSONObject(i);


                            arraylist.add(new MpendingDetail(data.getString("nama"), data.getString("qty"), data.getString("hargajual")));
//                            totalbarang+=Integer.parseInt(data.getString("qty"))*Integer.parseInt(data.getString("hargajual"));

                        }
                        double amount =Double.parseDouble(jObj.getString("totalbayar"));
                        double _bayar =Double.parseDouble(jObj.getString("bayar"));
                        double _sisa =Double.parseDouble(jObj.getString("sisa"));
                        DecimalFormat rupiahFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                        rupiahFormat.setParseBigDecimal(true);
                        rupiahFormat.applyPattern("#,##0");
                        String formattedRupiah = rupiahFormat.format(amount);
                        total.setText("Rp"+String.valueOf(formattedRupiah));
                        namakasir.setText(xnama_petugas);
                        nohpkasir.setText(xnohp);
                        nota.setText(jObj.getString("nota"));
                        bayar.setText("Rp"+String.valueOf(rupiahFormat.format(_bayar)));
                        kembali.setText("Rp"+String.valueOf(rupiahFormat.format(_sisa)));
                        metodepembayaran.setText(jObj.getString("jbayar"));
                        statuspembayaran.setText(jObj.getString("statusbayar"));


                    } else {
                        Toast.makeText(TransaksiCetakActivity.this, "Kosong", Toast.LENGTH_SHORT).show();
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

                params.put("idtransaksi", idtransaksi);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jArr, tag_json_obj);
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
        for (MpendingDetail order : arraylist) {
            int totalprinttt=Integer.parseInt(order.getHargajual())*Integer.parseInt(order.getQty());
            a.append(order.getNama()+"\n").append(order.getQty()).append(" x ").append(rupiahFormat.format(Double.parseDouble(order.getHargajual()))).append("[R]").append(""+rupiahFormat.format(totalprinttt)).append("\n\n");
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
                "[L]Total[R]" + total.getText().toString() + "\n" +
                "[L]Bayar[R]" + bayar.getText().toString() + "\n" +
                "[L]Kembali[R]" + kembali.getText().toString()  + "\n" +

                "[C]--------------------------------\n" +

                "[C]Terimakasih Sudah Berbelanja\n" +
                "[C]Norek a/n Femina anik sri utami \n" +
                "[C]Bca : 2019707339  \n" +
                "[C]Bri : 058201000213562";
        return printer.addTextToPrint(text);
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), ProdukTransaksi.class));

        finish();
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
}
