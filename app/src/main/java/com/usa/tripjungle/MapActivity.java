package com.usa.tripjungle;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.usa.tripjungle.databinding.ActiviyMapBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    Button btn_back;
    private GoogleMap mMap;
    private ActiviyMapBinding binding;
    private String name = "";
    private FusedLocationProviderClient fusedLocationClient;
    FusedLocationProviderClient fusedLocationProviderClient;
    public static final int REQUEST_CODE = 101;
    private float[] postion = {0, 0};
    LatLng currentLocation, markLocation;
    Location test;
    TextView title, distanceView;
    Bundle extras;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activiy_map);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        extras = getIntent().getExtras();
        name = extras.getString("title");

        binding = ActiviyMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getPos(String pos, String key) {

        String url = "https://h7ogkdnsd5.execute-api.us-east-1.amazonaws.com/Prod/api/TripJungle/buscaratractivos";
        RequestQueue queue = Volley.newRequestQueue(MapActivity.this);
        JSONObject js = new JSONObject();
        try {
            js.put("ubicacion", pos);
            js.put("nombre", key);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, js,
                response -> {
                    try {
                        JSONObject dataobject = response.getJSONObject("data");
                        Log.e("ttt", response.toString());
                        JSONArray lugar = dataobject.getJSONArray("lugar");
                        String textoCode = response.getString("code");
                        String textoErrorMessage = response.getString("message");

                        if ("200".equals(textoCode)) {
                            String posStr = "";
                            for (int i = 0; i < lugar.length(); i++) {
                                posStr = lugar.getJSONObject(i).getString("ubicacion");
                            }
                            String[] strarr = posStr.split(",");
                            postion[0] = Float.parseFloat(strarr[0]);
                            postion[1] = Float.parseFloat(strarr[1]);
                            fetchLastLocation();
                        } else if ("500".equals(textoCode)) {
                            Toast.makeText(this, textoErrorMessage, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            Toast.makeText(MapActivity.this, "sever error", Toast.LENGTH_LONG).show();
        });
        queue.add(request);
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        progress = new ProgressDialog(MapActivity.this);
        btn_back = findViewById(R.id.btn_back);
        title = findViewById(R.id.title);
        distanceView = findViewById(R.id.distance);
        title.setText(name);
        btn_back.setOnClickListener(v -> {
            Intent i = new Intent(MapActivity.this, DetailActivity.class);
            i.putExtra("id", extras.getString("id"));
            i.putExtra("images", extras.getStringArray("images"));
            i.putExtra("title", extras.getString("title"));
            i.putExtra("description", extras.getString("description"));
            i.putExtra("ranking", extras.getString("ranking"));
            i.putExtra("uranking", extras.getString("uranking"));
            i.putExtra("like", extras.getInt("like"));
            startActivity(i);
        });
        getPos("", this.name);
    }


    private void fetchLastLocation() {
        progress.setMessage("Servidor de conexión ...");
        progress.show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
//                currentLocation = new LatLng(-51.7539, -69.3337);
                markLocation = new LatLng(postion[0], postion[1]);
                UpdateCurrentLocation();
                Toast.makeText(this, location.getLatitude() + "" + location.getLongitude(), Toast.LENGTH_SHORT).show();
            } else {
                double x = -10.0695;
                double y = -76.1714;
                // Add a marker in Sydney and move the camera
                LatLng sydney = new LatLng(x, y);
                markLocation = new LatLng(postion[0], postion[1]);
                currentLocation = sydney;
                UpdateCurrentLocation();
                Toast.makeText(this, x + "" + y + " Dev Mode", Toast.LENGTH_SHORT).show();
            }

            LatLng origin = currentLocation;

            LatLng dest = markLocation;

            String url = getUrl(origin, dest);

            Log.d("onMapClick", url.toString());

            FetchUrl FetchUrl = new FetchUrl();

            FetchUrl.execute(url);
//move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void UpdateCurrentLocation() {
        MarkerOptions markerOptions = new MarkerOptions().position(currentLocation)
                .title("Here I am!").icon(bitmapDescriptorFromVector(this, R.drawable.ic_trip_origin));
        MarkerOptions markerOptions1 = new MarkerOptions().position(markLocation)
                .title(this.name).icon(bitmapDescriptorFromVector(this, R.drawable.ic_place_black_24dp));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 5));
        mMap.addMarker(markerOptions);
        mMap.addMarker(markerOptions1);
    }

    public String getUrl(LatLng origin, LatLng dest) {

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
//        String mode = "mode=walking";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + "AIzaSyCH7gw0dPSLV0E2ffd4zoREYg_54pR7bfo";
        Log.e("eeee", url);
        return url;
    }

    private class FetchUrl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                iStream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                StringBuffer sb = new StringBuffer();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                data = sb.toString();
                ParserTask parserTask = new ParserTask();
                parserTask.execute(data);
                Log.d("Download URL", data.toString());
                br.close();
            } catch (Exception e) {
                Log.d("E", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }
    }


    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String[] jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                JSONArray jarr = jObject.getJSONArray("routes");
                if (jarr.length() <= 0) {
                    Toast.makeText(MapActivity.this, "Not Find Route", Toast.LENGTH_LONG).show();
                    return new ArrayList<>();
                }
                JSONObject jsonArray = jarr.getJSONObject(0);

                JSONArray legs = jsonArray.getJSONArray("legs");

                JSONObject jsonObject = legs.getJSONObject(0);

                JSONObject distance = jsonObject.getJSONObject("distance");

                distanceView.setText(distance.getString("text"));

                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());
// Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());
            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }


        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
// Traversing through all the routes
            if (result == null) {
                Toast.makeText(MapActivity.this, "Not Find Route", Toast.LENGTH_LONG).show();
                progress.hide();
                return;
            }
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
// Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.BLUE);
                progress.hide();
                Log.d("onPostExecute","onPostExecute lineoptions decoded");
            }

            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }
}