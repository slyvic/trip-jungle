package com.usa.tripjungle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    EditText name, phoneNumber, email, password;
    TextView registerBtn, loginBtn, txtPassValidation, txtNameValidation, txtEmailValidation, txtPhoneValidation;
    LinearLayout googleBtn;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_register);
        Objects.requireNonNull(getSupportActionBar()).hide();
        loadElement();
    }

    private void loadElement(){
        name = findViewById(R.id.register_name);
        phoneNumber = findViewById(R.id.register_phone);
        email = findViewById(R.id.regiter_email);
        password = findViewById(R.id.regiter_password);
        registerBtn = findViewById(R.id.register_register);
        loginBtn = findViewById(R.id.register_login);
        googleBtn = findViewById(R.id.register_google);
        txtPhoneValidation = findViewById(R.id.txtPhoneValidation);
        txtPassValidation = findViewById(R.id.txtPassValidation);
        txtNameValidation = findViewById(R.id.txtNameValidation);
        txtEmailValidation = findViewById(R.id.txtEmailValidation);
        txtEmailValidation.setText("");
        txtNameValidation.setText("");
        txtPassValidation.setText("");
        txtPhoneValidation.setText("");
        registerBtn.setEnabled(false);
        registerBtn.setBackgroundResource(R.drawable.botton_border_disable);

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        loginBtn.setOnClickListener(v -> {
            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(i);
        });

        name.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String sname = name.getText().toString().trim();
                if (sname.length() > 0)
                {
                    txtNameValidation.setTextColor(Color.rgb(0, 255, 0));
                    txtNameValidation.setText("");
                    if (name.getText().toString().trim().length() > 0
                            && phoneNumber.getText().toString().trim().length() > 0
                            && password.getText().toString().trim().length() > 0
                            && email.getText().toString().trim().length() > 0) {
                        registerBtn.setEnabled(true);
                        registerBtn.setBackgroundResource(R.drawable.botton_border);
                    }
                } else {
                    registerBtn.setBackgroundResource(R.drawable.botton_border_disable);
                    registerBtn.setEnabled(false);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
            }
        });

        phoneNumber.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String phone = phoneNumber.getText().toString().trim();
                if (phone.length() > 0)
                {
                    txtPhoneValidation.setTextColor(Color.rgb(0, 255, 0));
                    txtPhoneValidation.setText("");
                    if (name.getText().toString().trim().length() > 0
                            && phoneNumber.getText().toString().trim().length() > 0
                            && password.getText().toString().trim().length() > 0
                            && email.getText().toString().trim().length() > 0) {
                        registerBtn.setEnabled(true);
                        registerBtn.setBackgroundResource(R.drawable.botton_border);
                    }
                } else {
                    registerBtn.setBackgroundResource(R.drawable.botton_border_disable);
                    registerBtn.setEnabled(false);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String semail = email.getText().toString().trim();
                if (semail.length() > 0)
                {
                    txtEmailValidation.setTextColor(Color.rgb(0, 255, 0));
                    txtEmailValidation.setText("");
                    if (name.getText().toString().trim().length() > 0
                            && phoneNumber.getText().toString().trim().length() > 0
                            && password.getText().toString().trim().length() > 0
                            && semail.length() > 0) {
                        registerBtn.setEnabled(true);
                        registerBtn.setBackgroundResource(R.drawable.botton_border);
                    }
                } else {
                    registerBtn.setBackgroundResource(R.drawable.botton_border_disable);
                    registerBtn.setEnabled(false);
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
                    txtPassValidation.setTextColor(Color.rgb(0, 255, 0));
                    txtPassValidation.setText("");
                    if (name.getText().toString().trim().length() > 0
                            && phoneNumber.getText().toString().trim().length() > 0
                            && password.getText().toString().trim().length() > 0
                            && email.getText().toString().trim().length() > 0) {
                        registerBtn.setEnabled(true);
                        registerBtn.setBackgroundResource(R.drawable.botton_border);
                    }
                } else {
                    registerBtn.setBackgroundResource(R.drawable.botton_border_disable);
                    registerBtn.setEnabled(false);
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
            if (name.getText().toString().trim().length() <= 0
                    || phoneNumber.getText().toString().trim().length() <= 0
                    || password.getText().toString().trim().length() <= 0
                    || email.getText().toString().trim().length() <= 0){
                registerBtn.setEnabled(false);
                registerBtn.setBackgroundResource(R.drawable.botton_border_disable);
                return;
            }
            if (name.getText().toString().trim().length() <= 0
                    || phoneNumber.getText().toString().trim().length() != 10
                    || password.getText().toString().trim().length() <= 0
                    || !email.getText().toString().trim().matches(emailPattern)) {
                if (phoneNumber.getText().toString().trim().length() < 9)
                {
                    txtPhoneValidation.setTextColor(Color.rgb(255, 0, 0));
                    txtPhoneValidation.setText("Datos inválidos");
                }
                if (name.getText().toString().trim().length() <= 0)
                {
                    txtNameValidation.setTextColor(Color.rgb(255, 0, 0));
                    txtNameValidation.setText("Datos inválidos");
                }
                if (!email.getText().toString().trim().matches(emailPattern))
                {
                    txtEmailValidation.setTextColor(Color.rgb(255, 0, 0));
                    txtEmailValidation.setText("Dirección de correo electrónico no válida");
                }
                if (password.getText().toString().trim().length() <= 0)
                {
                    txtPassValidation.setTextColor(Color.rgb(255, 0, 0));
                    txtPassValidation.setText("Datos inválidos");
                }
            }
            registrarUsuario(name.getText().toString(),password.getText().toString(), email.getText().toString(), phoneNumber.getText().toString());
        });
    }

    private void registrarUsuario(String nombreUsuario, String passwordUsuario, String stremail, String phone){
        String url = "https://h7ogkdnsd5.execute-api.us-east-1.amazonaws.com/Prod/api/TripJungle/usuarioregistrar";
        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
        JSONObject js = new JSONObject();
        progress = new ProgressDialog(RegisterActivity.this);
        progress.setIndeterminate(true);
        progress.setMessage("Servidor de conexión ...");
        progress.show();
        try {
            js.put("userName",nombreUsuario);
            js.put("password",passwordUsuario);
            js.put("email",stremail);
            js.put("phone",phone);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, js,
                response -> {
                    try {
                        JSONObject dataobject = response.getJSONObject("data");
                        String textoResultado = dataobject.getString("resultado");
                        String textoCode = response.getString("code");
                        String textoErrorMessage = response.getString("message");

                        if("200".equals(textoCode))
                        {
                            Toast.makeText(this, textoResultado, Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                            i.putExtra("nombreUsuarioSesion", nombreUsuario);
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
            Toast.makeText(RegisterActivity.this, "sever error", Toast.LENGTH_LONG).show();
        });
        queue.add(request);
    }
}
