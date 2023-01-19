package com.usa.tripjungle;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.usa.tripjungle.DataBase.DataBaseHelper;

import java.util.Objects;

public class MenuActivity extends AppCompatActivity {
    ImageView closeBtn;
    TextView homeBtn, asistenteBtn, arbtn, perfilBtn, salirBtn;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_menu);

        Objects.requireNonNull(getSupportActionBar()).hide();
        loadElement();
        Bundle extras = getIntent().getExtras();

        closeBtn.setOnClickListener(v -> {
            String beforeActivity = extras.getString("sideMenu");
            switch (beforeActivity){
                case "main":
                    Intent i = new Intent(MenuActivity.this, MainActivity.class);
                    startActivity(i);
                    break;
                case "profile":
                    Intent intent = new Intent(MenuActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }

        });
    }

    private void loadElement(){
        closeBtn = findViewById(R.id.menu_close);
        homeBtn = findViewById(R.id.menu_home);
        asistenteBtn = findViewById(R.id.menu_asistente);
        arbtn = findViewById(R.id.menu_ar);
        perfilBtn = findViewById(R.id.menu_perfil);
        salirBtn = findViewById(R.id.menu_salir);

        homeBtn.setOnClickListener(v -> {
            Intent i = new Intent(MenuActivity.this, MainActivity.class);
            startActivity(i);
        });

        asistenteBtn.setOnClickListener(v -> {
            Intent i = new Intent(MenuActivity.this, VChatActivity.class);
            startActivity(i);
        });

        arbtn.setOnClickListener(v -> {
            Intent i = new Intent(MenuActivity.this, ARActivity.class);
            startActivity(i);
        });

        perfilBtn.setOnClickListener(v -> {
            Intent i = new Intent(MenuActivity.this, ProfileActivity.class);
            startActivity(i);
        });

        salirBtn.setOnClickListener(v -> {
            onSignOut();
            Intent i = new Intent(MenuActivity.this, LoginActivity.class);
            startActivity(i);
        });
    }
    public void onSignOut() {
        SQLiteOpenHelper openHelper = new DataBaseHelper(this);
        db = openHelper.getReadableDatabase();
        db.execSQL("delete from "+ DataBaseHelper.TABLE_NAME);
    }
}
