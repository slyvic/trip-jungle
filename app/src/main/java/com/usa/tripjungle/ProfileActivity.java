package com.usa.tripjungle;

import static com.usa.tripjungle.DataBase.DataBaseHelper.bitmapToBase64;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.makeramen.roundedimageview.RoundedImageView;
import com.usa.tripjungle.Common.Common;
import com.usa.tripjungle.DataBase.DataBaseHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    ImageView sideMenu;
    CircleImageView picture;
    EditText nameEdit, phoneEdit, emailEdit;
    LabeledSwitch menuSwitch;
    RadioGroup radioGroup;
    RoundedImageView camera;
    ImageView set_profile_name;
    ImageView set_profile_number;
    private SQLiteDatabase db;
    AlertDialog.Builder builder;
    private ProgressDialog progress;
    boolean isImageUpdate = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activiy_profile);
        getUserProfileData();
        loadElement();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==100 && resultCode==RESULT_OK)
        {
            SQLiteOpenHelper openHelper = new DataBaseHelper(this);
            db = openHelper.getReadableDatabase();
            Uri uri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                Common.avatar = bitmap;
//                updateUserAvatar();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ContentValues cv = new ContentValues();
            cv.put(DataBaseHelper.COL_4, bitmapToBase64(bitmap));
            int update = db.update(DataBaseHelper.TABLE_NAME, cv, "ID = ?", new String[]{String.valueOf(Common.id)});
            Common.avatar = bitmap;
            picture.setImageBitmap(bitmap);
            db.close();
        }
    }

    private void loadElement(){
        sideMenu = findViewById(R.id.profile_menu);
        nameEdit = findViewById(R.id.profile_name);
        phoneEdit = findViewById(R.id.profile_number);
        emailEdit = findViewById(R.id.profile_email);
        menuSwitch = findViewById(R.id.profile_switch);
        radioGroup = findViewById(R.id.profile_radioGroup);
        picture = findViewById(R.id.profile_picture);
        camera = findViewById(R.id.profile_camera);
        builder = new AlertDialog.Builder(this);
        set_profile_name = findViewById(R.id.set_profile_name);
        set_profile_name.setOnClickListener(v -> {
            LayoutInflater inflater = getLayoutInflater();
            View myLayout = inflater.inflate(R.layout.input_view, null, false);
            EditText input_data = myLayout.findViewById(R.id.input_data);
            input_data.setText(nameEdit.getText());
            builder.setTitle("Editar nombre de usuario");
            builder.setView(myLayout);
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (input_data.getText().toString().trim().length() > 0) {
//                        updateUserNameAndPhone(input_data.getText().toString(), null);
                    } else {
                        Toast.makeText(ProfileActivity.this, "Datos inválidos", Toast.LENGTH_LONG).show();
                    }
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        set_profile_number = findViewById(R.id.set_profile_number);
        set_profile_number.setOnClickListener(v -> {
            LayoutInflater inflater = getLayoutInflater();
            View myLayout = inflater.inflate(R.layout.input_phone_data, null, false);
            EditText input_data = myLayout.findViewById(R.id.input_data);
            input_data.setText(phoneEdit.getText());

            builder.setTitle("Editar número de teléfono");
            builder.setView(myLayout);
            builder
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (input_data.getText().toString().trim().length() == 9) {
//                        updateUserNameAndPhone(null, input_data.getText().toString());
                    } else {
                        Toast.makeText(ProfileActivity.this, "Datos inválidos", Toast.LENGTH_LONG).show();
                    }
                    // User clicked OK button
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        sideMenu.setOnClickListener(v -> {
            Intent i = new Intent(ProfileActivity.this, MenuActivity.class);
            i.putExtra("sideMenu", "profile");
            startActivity(i);
        });
        menuSwitch.setColorOn(getColor(R.color.yellow));
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> Log.e("checked", String.valueOf(checkedId) ));
        camera.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(intent,100);
        });
    }

    private void getUserProfileData(){
        progress = new ProgressDialog(ProfileActivity.this);
        progress.setIndeterminate(true);
        progress.setMessage("Servidor de conexión ...");
        progress.show();
        String url = "https://h7ogkdnsd5.execute-api.us-east-1.amazonaws.com/Prod/api/TripJungle/usuariodatos";
        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
        JSONObject js = new JSONObject();
        try {
            js.put("userEmail",Common.email);
            js.put("userPassword",Common.pass);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, js,
                response -> {
                    try {
                        String code = response.getString("code");
                        String message = response.getString("message");
                        if (message.isEmpty()) {
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        }
                        if (code.equals("200")) {
                            Common.userName = response.getJSONObject("data").getString("userName");
                            Common.userPhone = response.getJSONObject("data").getString("userPhone");
                            Common.email = response.getJSONObject("data").getString("userEmail");
                            nameEdit.setText(Common.userName);
                            phoneEdit.setText(Common.userPhone);
                            emailEdit.setText(Common.email);
                            if (isImageUpdate) {
                                try {
                                    URL url1 = new URL(response.getJSONObject("data").getString("userProfileImgUrl"));
                                    Log.e("1111", response.getJSONObject("data").getString("userProfileImgUrl"));
                                    Bitmap image = BitmapFactory.decodeStream(url1.openConnection().getInputStream());
                                    Common.avatar = image;
                                    SQLiteOpenHelper openHelper = new DataBaseHelper(this);
                                    db = openHelper.getReadableDatabase();
                                    ContentValues cv = new ContentValues();
                                    cv.put(DataBaseHelper.COL_4, bitmapToBase64(Common.avatar));
                                    int update = db.update(DataBaseHelper.TABLE_NAME, cv, "ID = ?", new String[]{String.valueOf(Common.id)});
                                    db.close();
                                    if (Common.avatar != null) {
                                        picture.setImageBitmap(Common.avatar);
                                    }
                                } catch(IOException e) {
                                    System.out.println(e);
                                }
                            }
                        }
                        progress.dismiss();
                    } catch (JSONException e) {
                        progress.dismiss();
                        e.printStackTrace();
                    }
                }, error -> {
            progress.dismiss();
            Toast.makeText(ProfileActivity.this, "sever error", Toast.LENGTH_LONG).show();
        });
        queue.add(request);
    }

//    private void updateUserAvatar() {
//        progress = new ProgressDialog(ProfileActivity.this);
//        progress.setIndeterminate(true);
//        progress.setMessage("Servidor de conexión ...");
//        progress.show();
//        String url = "https://h7ogkdnsd5.execute-api.us-east-1.amazonaws.com/Prod/api/TripJungle/actfotousuario";
//        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
//        JSONObject js = new JSONObject();
//        try {
//            js.put("userEmail",Common.email);
//            js.put("userPassword",Common.pass);
//            js.put("userPhoto",bitmapToBase64(Common.avatar));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, js,
//                response -> {
//                    try {
//                        String code = response.getString("code");
//                        String message = response.getString("message");
//                        if (message.isEmpty()) {
//                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
//                        }
//                        if (code.equals("200")) {
//                            String result = response.getJSONObject("data").getString("resultado");
//                            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
//                        }
//                        progress.dismiss();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        progress.dismiss();
//                    }
//                }, error -> {
//            Toast.makeText(ProfileActivity.this, "sever error", Toast.LENGTH_LONG).show();
//            progress.dismiss();
//        });
//        queue.add(request);
//    }
//    private void updateUserNameAndPhone(String userName, String phone){
//        progress = new ProgressDialog(ProfileActivity.this);
//        progress.setIndeterminate(true);
//        progress.setMessage("Servidor de conexión ...");
//        progress.show();
//        String url = "https://h7ogkdnsd5.execute-api.us-east-1.amazonaws.com/Prod/api/TripJungle/actualizarusuario";
//        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
//        JSONObject js = new JSONObject();
//        try {
//            js.put("userEmail",Common.email);
//            js.put("userPassword",Common.pass);
//            js.put("userName",userName);
//            js.put("phone",phone);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, js,
//                response -> {
//                    try {
//                        String code = response.getString("code");
//                        String message = response.getString("message");
//                        if (code.equals("200")) {
//                            String result = response.getJSONObject("data").getString("resultado");
//                            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
//                            progress.dismiss();
//                            isImageUpdate = false;
//                            getUserProfileData();
//                        } else {
//                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
//                            progress.dismiss();
//                        }
//                    } catch (JSONException e) {
//                        progress.dismiss();
//                        e.printStackTrace();
//                    }
//                }, error -> {
//            Toast.makeText(ProfileActivity.this, "sever error", Toast.LENGTH_LONG).show();
//            progress.dismiss();
//        });
//        queue.add(request);
//    }
}
