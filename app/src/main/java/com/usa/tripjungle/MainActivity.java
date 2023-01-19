package com.usa.tripjungle;

import static com.usa.tripjungle.Common.Common.email;
import static com.usa.tripjungle.DataBase.DataBaseHelper.base64ToBitmap;
import static com.usa.tripjungle.MapActivity.REQUEST_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.usa.tripjungle.Common.Common;
import com.usa.tripjungle.DataBase.DataBaseHelper;
import com.usa.tripjungle.adapter.MainAdapter;
import com.usa.tripjungle.model.MainModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static int typeId = 0;
    ImageView sideMenu;
    de.hdodenhof.circleimageview.CircleImageView profileImg;
    EditText searchEdit;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextInputLayout textInputLayout;

    private List<MainModel> mainList = new ArrayList<>();
    private static List<MainModel> allMainList = new ArrayList<>();
    private List list = new ArrayList<>();
    private final List<Integer> idlist = new ArrayList<>();

    private MainAdapter mainAdapter;
    private ArrayAdapter<String> listAdapter;
    private SQLiteDatabase db;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }

        loadElement();
    }

    private void getSearchData(String key) {
        progressBar.setVisibility(View.VISIBLE);
        mainList.clear();
        for (int i = 0; i < allMainList.size(); i ++) {
            if (allMainList.get(i).getTitle().contains(key)) {
                mainList.add(allMainList.get(i));
            }
        }
        mainAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }

    public void getDataFromServer(){
        mainList.clear();
        allMainList.clear();
        mainAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.VISIBLE);
        String url = "https://h7ogkdnsd5.execute-api.us-east-1.amazonaws.com/Prod/api/TripJungle/atractivosdatosusuario";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        JSONObject js = new JSONObject();
        try {
            js.put("usuarioEmail",email);
            js.put("tipoTurismoID",typeId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, js,
                response -> {
                    try {
                        JSONObject dataobject = response.getJSONObject("data");
                        String code = response.getString("code");
                        String message = response.getString("message");
                        if (code.equals("200")) {
                            JSONArray lugarobject = dataobject.getJSONArray("lugar");
                            for(int i = 0; i < lugarobject.length(); i++){
                                JSONObject indiviobject = (JSONObject) lugarobject.get(i);
                                JSONArray indiviobjectJSONArray = indiviobject.getJSONArray("imagenes");
                                JSONObject imageObject = indiviobjectJSONArray.getJSONObject(0);
                                preparemaindData(
                                        indiviobject.getString("atractivoID"),
                                        indiviobject.getString("nombres"),
                                        indiviobject.getString("descripcion"),
                                        indiviobject.getString("puntajeGeneral"),
                                        indiviobjectJSONArray,imageObject.getString("rutaImagen"),
                                        indiviobject.getBoolean("usuarioFavorito"),
                                        indiviobject.getString("puntajeUsuario"));
                            }
                            progressBar.setVisibility(View.GONE);
                            mainAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }
                }, error -> {
            Toast.makeText(MainActivity.this, "sever error", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        });
        queue.add(request);
    }

    private void getlISTDataFromServer(){
        String url = "https://h7ogkdnsd5.execute-api.us-east-1.amazonaws.com/Prod/api/TripJungle/listartiposturismo";
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        JSONObject js = new JSONObject();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, js,
                response -> {
                    try {
                        JSONObject dataobject = response.getJSONObject("data");
                        String code = response.getString("code");
                        String message = response.getString("message");
                        if (message.isEmpty()) {
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        }
                        if (code.equals("200")) {
                            JSONArray jsonArray = dataobject.getJSONArray("tipos");
                            list.clear();
                            for (int i = 0; i < jsonArray.length(); i ++) {
                                idlist.add(jsonArray.getJSONObject(i).getInt("tipoTurismoID"));
                                list.add(jsonArray.getJSONObject(i).getString("nombre"));
                            }
                            ((MaterialAutoCompleteTextView) textInputLayout.getEditText()).setText(listAdapter.getItem(0),false);
                            listAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            Toast.makeText(MainActivity.this, "sever error", Toast.LENGTH_LONG).show();
        });
        queue.add(request);
    }

    private void preparemaindData(String atractivoID, String title, String description,
                                  String ranking, JSONArray iamgeUrl, String image, boolean loadlike, String ranking1) {
        MainModel movie = new MainModel(atractivoID, title, description, ranking, iamgeUrl, image, loadlike, R.drawable.ic_bookmark_empty, ranking1);
        mainList.add(movie);
        allMainList.add(movie);
    }

    @SuppressLint("Range")
    public void getUserProfile() {
        SQLiteOpenHelper openHelper = new DataBaseHelper(this);
        db = openHelper.getReadableDatabase();

        String userAvatar = null;
        String userEmail = "";
        final Cursor cursor = db.rawQuery("SELECT *FROM " + DataBaseHelper.TABLE_NAME,  null);

        if(cursor.getCount() > 0 ) {
            if (cursor.moveToFirst()) {
                do {
                    userAvatar = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COL_4));
                    Common.avatar = base64ToBitmap(userAvatar);
                    userEmail = cursor.getString(cursor.getColumnIndex("Email"));
                    Common.email = userEmail;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
    }
    private void loadElement(){
        sideMenu = findViewById(R.id.main_menu);
        profileImg = findViewById(R.id.main_profile);
        searchEdit = findViewById(R.id.main_search);
        recyclerView = findViewById(R.id.main_recycle);
        progressBar = findViewById(R.id.progressBar);
        getUserProfile();
        if (Common.avatar != null) {
            profileImg.setImageBitmap(Common.avatar);
        }
        sideMenu.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, MenuActivity.class);
            i.putExtra("sideMenu", "main");
            startActivity(i);
        });
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getSearchData(searchEdit.getText().toString());
            }
        });
        mainAdapter = new MainAdapter(mainList);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getDataFromServer();
        LinearLayoutManager HLayoutManager = new LinearLayoutManager(this);
        HLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(HLayoutManager);
        recyclerView.setAdapter(mainAdapter);
        AutoCompleteTextView datesFilterSpinner = findViewById(R.id.datesFilterSpinner);
        datesFilterSpinner.setDropDownBackgroundResource(R.drawable.ic_bookmark_empty);
        listAdapter = new ArrayAdapter<>(this, R.layout.menu_spinner_item, list);
        textInputLayout = findViewById(R.id.dateFilterContainer);
        ((MaterialAutoCompleteTextView) textInputLayout.getEditText()).setAdapter(listAdapter);
        getlISTDataFromServer();
        profileImg.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(i);
        });
        ((AutoCompleteTextView)textInputLayout.getEditText()).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                typeId = idlist.get(position);
                getDataFromServer();
            }
        });
    }
}