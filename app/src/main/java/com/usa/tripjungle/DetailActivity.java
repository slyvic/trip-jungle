package com.usa.tripjungle;

import static com.usa.tripjungle.Common.Common.email;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.usa.tripjungle.Common.Common;
import com.usa.tripjungle.carousel.The_Slide_Items_Model_Class;
import com.usa.tripjungle.carousel.The_Slide_items_Pager_Adapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class DetailActivity extends AppCompatActivity {
    ImageView backBtn, mark, rating_1, rating_2, rating_3, rating_4, rating_5, profileImg;
    TextView title, ranking, description, goBtn;
    private List<The_Slide_Items_Model_Class> listItems;
    private ViewPager page;
    private TabLayout tabLayout;
    boolean markStatus = false;
    String ratingCount = "";
    ProgressDialog progress;
    int id = 0;
    String name = "";
    SQLiteDatabase db;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_detail);
        getSupportActionBar().hide();
        loadElement();

        extras = getIntent().getExtras();
        page = findViewById(R.id.my_pager) ;
        tabLayout = findViewById(R.id.my_tablayout);

        String[] images = extras.getStringArray("images");
        this.id = Integer.parseInt(extras.getString("id"));

        listItems = new ArrayList<>() ;
        for (int i = 0; i < images.length; i ++) {
            listItems.add(new The_Slide_Items_Model_Class(images[i],"Slider 1 Title"));
        }
        The_Slide_items_Pager_Adapter itemsPager_adapter = new The_Slide_items_Pager_Adapter(this, listItems);
        page.setAdapter(itemsPager_adapter);

        if (extras.getInt("like") == 0) {
            mark.setImageResource(R.drawable.ic_bookmark_empty);
        } else {
            mark.setImageResource(R.drawable.ic_bookmark);
        }
        markStatus = extras.getInt("like") == 0 ? false : true;
        ratingCount = extras.getString("uranking");
        mark.setOnClickListener(v -> {

            progress.setMessage("Servidor de conexión ...");
            progress.show();
            String url = "https://h7ogkdnsd5.execute-api.us-east-1.amazonaws.com/Prod/api/TripJungle/usuarioxatractivoreg";
            RequestQueue queue = Volley.newRequestQueue(DetailActivity.this);
            JSONObject js = new JSONObject();
            markStatus = !markStatus;
            try {
                js.put("usuarioEmail",email);
                js.put("AtractivoID",this.id);
                js.put("puntajeUsuario", ratingCount);
                js.put("usuarioFavorito", markStatus);

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
                                Toast.makeText(DetailActivity.this, dataobject.getString("resultado"), Toast.LENGTH_LONG).show();

                                if (!markStatus) {
                                    mark.setImageResource(R.drawable.ic_bookmark_empty);
                                } else {
                                    mark.setImageResource(R.drawable.ic_bookmark);
                                }
                            } else {
                                Toast.makeText(DetailActivity.this, message, Toast.LENGTH_LONG).show();
                                markStatus = !markStatus;
                            }
                            progress.hide();
                        } catch (JSONException e) {
                            markStatus = !markStatus;
                            e.printStackTrace();
                            progress.hide();
                        }
                    }, error -> {
                Toast.makeText(DetailActivity.this, "sever error", Toast.LENGTH_LONG).show();
                markStatus = !markStatus;
                progress.hide();
            });
            queue.add(request);
        });
        name = extras.getString("title");
        title.setText(name);
        description.setText(extras.getString("description"));
        ranking.setText(extras.getString("ranking"));

        switch (Integer.parseInt(Math.round(Float.parseFloat(extras.getString("uranking"))) + "")) {
            case 1:
                rating_1.setImageResource(R.drawable.ic_star_fill);
                rating_2.setImageResource(R.drawable.ic_star_empty);
                rating_3.setImageResource(R.drawable.ic_star_empty);
                rating_4.setImageResource(R.drawable.ic_star_empty);
                rating_5.setImageResource(R.drawable.ic_star_empty);
                break;
            case 2:
                rating_1.setImageResource(R.drawable.ic_star_fill);
                rating_2.setImageResource(R.drawable.ic_star_fill);
                rating_3.setImageResource(R.drawable.ic_star_empty);
                rating_4.setImageResource(R.drawable.ic_star_empty);
                rating_5.setImageResource(R.drawable.ic_star_empty);
                break;
            case 3:
                rating_1.setImageResource(R.drawable.ic_star_fill);
                rating_2.setImageResource(R.drawable.ic_star_fill);
                rating_3.setImageResource(R.drawable.ic_star_fill);
                rating_4.setImageResource(R.drawable.ic_star_empty);
                rating_5.setImageResource(R.drawable.ic_star_empty);
                break;
            case 4:
                rating_1.setImageResource(R.drawable.ic_star_fill);
                rating_2.setImageResource(R.drawable.ic_star_fill);
                rating_3.setImageResource(R.drawable.ic_star_fill);
                rating_4.setImageResource(R.drawable.ic_star_fill);
                rating_5.setImageResource(R.drawable.ic_star_empty);
                break;
            case 5:
                rating_1.setImageResource(R.drawable.ic_star_fill);
                rating_2.setImageResource(R.drawable.ic_star_fill);
                rating_3.setImageResource(R.drawable.ic_star_fill);
                rating_4.setImageResource(R.drawable.ic_star_fill);
                rating_5.setImageResource(R.drawable.ic_star_fill);
                break;
            default:
                rating_1.setImageResource(R.drawable.ic_star_empty);
                rating_2.setImageResource(R.drawable.ic_star_empty);
                rating_3.setImageResource(R.drawable.ic_star_empty);
                rating_4.setImageResource(R.drawable.ic_star_empty);
                rating_5.setImageResource(R.drawable.ic_star_empty);
                break;
        }

        java.util.Timer timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new The_slide_timer(),2000,3000);
        tabLayout.setupWithViewPager(page,true);
    }

    public class The_slide_timer extends TimerTask {
        @Override
        public void run() {

            DetailActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (page.getCurrentItem()< listItems.size()-1) {
                        page.setCurrentItem(page.getCurrentItem()+1);
                    }
                    else
                        page.setCurrentItem(0);
                }
            });
        }
    }

    private void loadElement(){
        profileImg = findViewById(R.id.main_profile);
        rating_1 = findViewById(R.id.rating_1);
        rating_2 = findViewById(R.id.rating_2);
        rating_3 = findViewById(R.id.rating_3);
        rating_4 = findViewById(R.id.rating_4);
        rating_5 = findViewById(R.id.rating_5);
        backBtn = findViewById(R.id.detail_back);
        title = findViewById(R.id.detail_title);
        ranking = findViewById(R.id.detail_ranking);
        description = findViewById(R.id.detail_description);
        goBtn = findViewById(R.id.detail_mapbtn);
        mark = findViewById(R.id.detail_mark);
        description.setMovementMethod(new ScrollingMovementMethod());
        if (Common.avatar != null) {
            profileImg.setImageBitmap(Common.avatar);
        }
        rating_1.setOnClickListener(v -> {
            setRating(1, markStatus);
        });
        rating_2.setOnClickListener(v -> {
            setRating(2, markStatus);
        });
        rating_3.setOnClickListener(v -> {
            setRating(3, markStatus);
        });
        rating_4.setOnClickListener(v -> {
            setRating(4, markStatus);
        });
        rating_5.setOnClickListener(v -> {
            setRating(5, markStatus);
        });

        progress = new ProgressDialog(DetailActivity.this);
        progress.setIndeterminate(true);
        backBtn.setOnClickListener(v -> {
            Intent i = new Intent(DetailActivity.this, MainActivity.class);
            startActivity(i);
        });

        goBtn.setOnClickListener(v -> {
            Intent i = new Intent(DetailActivity.this, MapActivity.class);
            i.putExtra("id", extras.getString("id"));
            i.putExtra("images", extras.getStringArray("images"));
            i.putExtra("title", extras.getString("title"));
            i.putExtra("description", extras.getString("description"));
            i.putExtra("ranking", extras.getString("ranking"));
            i.putExtra("uranking", extras.getString("uranking"));
            i.putExtra("like", extras.getInt("like"));
            startActivity(i);
        });
        profileImg.setOnClickListener(v -> {
            Intent i = new Intent(DetailActivity.this, ProfileActivity.class);
            startActivity(i);
        });
    }

    private void setRating(int count, boolean state) {

        progress.setMessage("Servidor de conexión ...");
        progress.show();
        String url = "https://h7ogkdnsd5.execute-api.us-east-1.amazonaws.com/Prod/api/TripJungle/usuarioxatractivoreg";
        RequestQueue queue = Volley.newRequestQueue(DetailActivity.this);
        JSONObject js = new JSONObject();
        try {
            js.put("usuarioEmail",email);
            js.put("AtractivoID",this.id);
            js.put("puntajeUsuario", count);
            js.put("usuarioFavorito", state);

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
                            Toast.makeText(DetailActivity.this, dataobject.getString("resultado"), Toast.LENGTH_LONG).show();
                            ratingCount = String.valueOf(count);
                            switch (count) {
                                case 1:
                                    rating_1.setImageResource(R.drawable.ic_star_fill);
                                    rating_2.setImageResource(R.drawable.ic_star_empty);
                                    rating_3.setImageResource(R.drawable.ic_star_empty);
                                    rating_4.setImageResource(R.drawable.ic_star_empty);
                                    rating_5.setImageResource(R.drawable.ic_star_empty);
                                    break;
                                case 2:
                                    rating_1.setImageResource(R.drawable.ic_star_fill);
                                    rating_2.setImageResource(R.drawable.ic_star_fill);
                                    rating_3.setImageResource(R.drawable.ic_star_empty);
                                    rating_4.setImageResource(R.drawable.ic_star_empty);
                                    rating_5.setImageResource(R.drawable.ic_star_empty);
                                    break;
                                case 3:
                                    rating_1.setImageResource(R.drawable.ic_star_fill);
                                    rating_2.setImageResource(R.drawable.ic_star_fill);
                                    rating_3.setImageResource(R.drawable.ic_star_fill);
                                    rating_4.setImageResource(R.drawable.ic_star_empty);
                                    rating_5.setImageResource(R.drawable.ic_star_empty);
                                    break;
                                case 4:
                                    rating_1.setImageResource(R.drawable.ic_star_fill);
                                    rating_2.setImageResource(R.drawable.ic_star_fill);
                                    rating_3.setImageResource(R.drawable.ic_star_fill);
                                    rating_4.setImageResource(R.drawable.ic_star_fill);
                                    rating_5.setImageResource(R.drawable.ic_star_empty);
                                    break;
                                case 5:
                                    rating_1.setImageResource(R.drawable.ic_star_fill);
                                    rating_2.setImageResource(R.drawable.ic_star_fill);
                                    rating_3.setImageResource(R.drawable.ic_star_fill);
                                    rating_4.setImageResource(R.drawable.ic_star_fill);
                                    rating_5.setImageResource(R.drawable.ic_star_fill);
                                    break;
                                default:
                                    rating_1.setImageResource(R.drawable.ic_star_empty);
                                    rating_2.setImageResource(R.drawable.ic_star_empty);
                                    rating_3.setImageResource(R.drawable.ic_star_empty);
                                    rating_4.setImageResource(R.drawable.ic_star_empty);
                                    rating_5.setImageResource(R.drawable.ic_star_empty);
                                    break;
                            }
                        } else {
                            Toast.makeText(DetailActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                        progress.hide();
                    } catch (JSONException e) {
                        progress.hide();
                        e.printStackTrace();
                    }
                }, error -> {
            progress.hide();
            Toast.makeText(DetailActivity.this, "sever error", Toast.LENGTH_LONG).show();
        });
        queue.add(request);
    }
}
