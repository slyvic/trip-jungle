package com.usa.tripjungle;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.usa.tripjungle.Common.Common;
import com.usa.tripjungle.DataBase.DataBaseHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    EditText user, password;
    TextView loginBtn, registerBtn, forgotBtn, txtEmailValidation, txtPassValidation;
    LinearLayout googleLogin;
    private ProgressDialog progress;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_login);

        this.validSignin();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Objects.requireNonNull(getSupportActionBar()).hide();
        loadElement();
    }

    @SuppressLint("Range")
    private void validSignin() {
        SQLiteOpenHelper openHelper = new DataBaseHelper(this);
        db = openHelper.getReadableDatabase();

        String useremail = "", userpass = "";
        final Cursor cursor = db.rawQuery("SELECT *FROM " + DataBaseHelper.TABLE_NAME,  null);

        if(cursor.getCount() > 0 ) {
            if (cursor.moveToFirst()) {
                do {
                    useremail = cursor.getString(cursor.getColumnIndex("Email"));
                    userpass = cursor.getString(cursor.getColumnIndex("Password"));
                    Common.email = useremail;
                    Common.pass = userpass;
                    Common.id = cursor.getInt(cursor.getColumnIndex("ID"));
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    i.putExtra("nombreUsuarioSesion", useremail);
                    startActivity(i);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }


    private void loadElement(){
        user = findViewById(R.id.login_user);
        password = findViewById(R.id.login_password);
//        user.setText("testcorreo1@hotmail.com");
//        password.setText("pass1234");
        user.setText("");
        password.setText("");
        loginBtn = findViewById(R.id.login_login);
        registerBtn = findViewById(R.id.login_register);
        googleLogin = findViewById(R.id.login_google);
        forgotBtn = findViewById(R.id.login_forgot);
        txtEmailValidation = findViewById(R.id.txtEmailValidation);
        txtPassValidation = findViewById(R.id.txtPassValidation);
        txtEmailValidation.setText("");
        txtPassValidation.setText("");
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        loginBtn.setEnabled(false);
        loginBtn.setBackgroundResource(R.drawable.botton_border_disable);

        loginBtn.setOnClickListener(v -> {
            String email = user.getText().toString().trim();
            String pass = password.getText().toString().trim();
            if (!email.matches(emailPattern)) {
                txtEmailValidation.setTextColor(Color.rgb(255, 0, 0));
                txtEmailValidation.setText("Dirección de correo electrónico no válida");
            }
            if (email.matches(emailPattern) && pass.length() > 0)
                validarLogin(email,pass, "");
        });

        user.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String email = user.getText().toString().trim();
                if (email.length() > 0)
                {
                    if (password.getText().toString().trim().length() > 0) {
                        txtPassValidation.setText("");
                        loginBtn.setBackgroundResource(R.drawable.botton_border);
                        loginBtn.setEnabled(true);
                    }
                    txtEmailValidation.setTextColor(Color.rgb(0, 255, 0));
                    txtEmailValidation.setText("");
                } else {
                    loginBtn.setBackgroundResource(R.drawable.botton_border_disable);
                    loginBtn.setEnabled(false);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String pass = password.getText().toString().trim();
                if (pass.length() > 0)
                {
                    txtPassValidation.setText("");
                    if (user.getText().toString().trim().length() > 0) {
                        txtEmailValidation.setText("");
                        loginBtn.setBackgroundResource(R.drawable.botton_border);
                        loginBtn.setEnabled(true);
                    }
                } else {
                    loginBtn.setBackgroundResource(R.drawable.botton_border_disable);
                    loginBtn.setEnabled(false);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
            }
        });

        registerBtn.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
        });


        forgotBtn.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, ForgetActivity.class);
            startActivity(i);
        });

        googleLogin.setOnClickListener(v -> {

        });
    }

    private void validarLogin(String nombreUsuario, String passwordUsuario, String avatarUsuario){
        String url = "https://h7ogkdnsd5.execute-api.us-east-1.amazonaws.com/Prod/api/TripJungle/validarlogindatos";
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        JSONObject js = new JSONObject();
        progress = new ProgressDialog(LoginActivity.this);
        progress.setIndeterminate(true);
        progress.setMessage("Servidor de conexión ...");
        progress.show();
        try {
            js.put("userEmail",nombreUsuario);
            js.put("password",passwordUsuario);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, js,
                response -> {
                    try {
                        JSONObject dataobject = response.getJSONObject("data");
                        Log.e("ttt", response.toString());
                        String textoResultado = dataobject.getString("resultado");
                        String textoCode = response.getString("code");
                        String textoErrorMessage = response.getString("message");

                        if("200".equals(textoCode))
                        {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DataBaseHelper.COL_2,nombreUsuario);
                            contentValues.put(DataBaseHelper.COL_3,passwordUsuario);
                            contentValues.put(DataBaseHelper.COL_4,avatarUsuario);
                            db.insert(DataBaseHelper.TABLE_NAME,null,contentValues);
                            progress.dismiss();
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            i.putExtra("nombreUsuarioSesion", nombreUsuario);
                            Common.pass = passwordUsuario;
                            Common.email = nombreUsuario;
                            startActivity(i);
                        } else if("500".equals(textoCode))
                        {
                            progress.dismiss();
                            Toast.makeText(this, textoErrorMessage, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        progress.dismiss();
                        e.printStackTrace();
                    }
                }, error -> {
            progress.dismiss();
            Toast.makeText(LoginActivity.this, "sever error", Toast.LENGTH_LONG).show();
        });
        queue.add(request);
    }
}
