package com.bospintar.cashier.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bospintar.cashier.R;
import com.bospintar.cashier.app.AppController;
import com.bospintar.cashier.server.URL_SERVER;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginSplash extends AppCompatActivity {

    private boolean passwordVisible = false;
    SwipeRefreshLayout swipeLayout;
    private static final String TAG = LoginSplash.class.getSimpleName();
    private static final String TAG_SUCCESS = "sukses";
//    private static final String TAG_MESSAGE = "message";
    String tag_json_obj = "json_obj_req";
    String xidpetugas,xnama_petugas,xalamat_petugas,xnohp,xlevel,xidtoko,xnama_toko,xalamat_toko,xstatus_toko,xketnota,xnohp_toko;
    int success;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bacaPreferensi();
        DialogFragment dialogFragment = new DialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "DialogFragment");

        TextView Lupapass = findViewById(R.id.lupapassword);
        TextView login = findViewById(R.id.btlogin);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        EditText usernameEditText=findViewById(R.id.usernameEditText);
        ImageView passwordVisibilityToggle = findViewById(R.id.passwordVisibilityToggle);
        swipeLayout = findViewById(R.id.refresh_layout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                    }
                }, 5000);
            }
        });
        swipeLayout.setColorScheme(new int[]{android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light});
        // Menggunakan animasi slide up.


        //visible password
        passwordVisibilityToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordVisible = !passwordVisible;
                int inputType = passwordVisible ?
                        android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                        android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
                passwordEditText.setInputType(inputType);
                passwordEditText.setSelection(passwordEditText.getText().length()); // Agar kursor tetap di akhir teks
                passwordVisibilityToggle.setImageResource(
                        passwordVisible ? R.drawable.ic_unvisible_pass : R.drawable.ic_visible_pass);
            }
        });

        //intent lupa password
        Lupapass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buat Intent untuk memulai activity kedua
              /*  Intent intent = new Intent(LoginActivity.this, LupasPasswordActivity.class);

                // (Opsional) Kirim data ke activity kedua jika diperlukan
                String dataToSend = "Ini adalah data dari FirstActivity";
                intent.putExtra("KEY_DATA", dataToSend);

                // Memulai activity kedua
                startActivity(intent);*/
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (username.equals("")) {
                    usernameEditText.setError("Belum diisi");
                    usernameEditText.requestFocus();
                } else if (password.equals("")) {
                    passwordEditText.setError("Belum diisi");
                    passwordEditText.requestFocus();
                } else {
                    checkLogin(username, password);

                }
            }
        });
        if (xidpetugas.equals("0")) {

        } else {
            startActivity(new Intent(getApplicationContext(), Menu.class));
            finish();
        }
    }
    private void checkLogin(final String username, final String password) {
        swipeLayout.setRefreshing(true);

        StringRequest strReq = new StringRequest(Request.Method.POST, URL_SERVER.CLOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response.toString());
                swipeLayout.setRefreshing(false);
                try {
                    JSONObject object = new JSONObject(response);
                    success = object.getInt(TAG_SUCCESS);
                    // Check for error node in json

                    if (success == 1) {

                        String idpetugas = object.getString("idpetugas");
                        String nama_petugas = object.getString("nama_petugas");
                        String alamat_petugas = object.getString("alamat_petugas");
                        String nohp = object.getString("nohp");
                        String level = object.getString("level");
                        String idtoko = object.getString("idtoko");
                        String nama_toko = object.getString("nama_toko");
                        String alamat_toko = object.getString("alamat_toko");
                        String status_toko = object.getString("status_toko");
                        String ketnota = object.getString("ketnota");
                        String nohp_toko = object.getString("nohp_toko");
                        Log.e("Successfully Login!", object.toString());
                        // menyimpan login ke session
//                        startActivity(new Intent(getApplicationContext(), Activity_MenuUtama.class));
                        SharedPreferences pref = getSharedPreferences("akun", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();

                        editor.putString("idpetugas", idpetugas.toString());
                        editor.putString("nama_petugas", nama_petugas.toString());
                        editor.putString("alamat_petugas", alamat_petugas.toString());
                        editor.putString("nohp", nohp.toString());
                        editor.putString("level", level.toString());
                        editor.putString("idtoko", idtoko.toString());
                        editor.putString("nama_toko", nama_toko.toString());
                        editor.putString("alamat_toko", alamat_toko.toString());
                        editor.putString("status_toko", status_toko.toString());
                        editor.putString("ketnota", ketnota.toString());
                        editor.putString("nohp_toko", nohp_toko.toString());
                        editor.commit();
                        Toast.makeText(LoginSplash.this, "Berhasil Login", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), Menu.class));
                        finish();

                    } else {
                        Toast.makeText(LoginSplash.this, "Gagal Login", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());

                Toast.makeText(LoginSplash.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

                swipeLayout.setRefreshing(false);

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("nohp", username);
                params.put("password", password);
                return params;
            }

        };

        // Adding request to request queue
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
}