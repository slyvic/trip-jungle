package com.usa.tripjungle;

import static com.usa.tripjungle.CustomArFragment.arImageInfo;
import static com.usa.tripjungle.MapActivity.REQUEST_CODE;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.PixelCopy;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.usa.tripjungle.model.ImageInfoModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class ARActivity extends AppCompatActivity {
    private ExternalTexture texture;
    private MediaPlayer mediaPlayer;
    private CustomArFragment arFragment;
    private Scene scene;
    private ModelRenderable renderable;
    private boolean isImageDetected = false;
    private boolean[] isImageDetectedClose;
    ImageView imageView;
    ImageButton btnBack, image_close;
    ImageButton btnCapture;
    ProgressDialog progress;
    String dataView = "", dataHeader = "";
    JSONArray imageInfo = null;

    double pos_x = 0, pos_y = 0;
    int idx = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_ar);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        texture = new ExternalTexture();
        imageView = findViewById(R.id.imageView);
        btnBack = findViewById(R.id.btn_back);
        btnCapture = findViewById(R.id.btn_capture);
        image_close = findViewById(R.id.image_close);
        imageView.setVisibility(View.GONE);
        progress = new ProgressDialog(this);
        btnBack.setOnClickListener(v -> {
            Intent i = new Intent(ARActivity.this, MainActivity.class);
            startActivity(i);
        });


        btnCapture.setOnClickListener(v -> {
            takePhoto();
        });

        mediaPlayer = MediaPlayer.create(this, R.raw.scuba01);
        mediaPlayer.setSurface(texture.getSurface());
        mediaPlayer.setLooping(true);

        arFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        scene = arFragment.getArSceneView().getScene();

        image_close.setOnClickListener(v -> {
            image_close.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            btnCapture.setEnabled(true);
            btnBack.setEnabled(true);
        });
        fetchLastLocation();
    }
    private void onUpdate(FrameTime frameTime){
        idx ++;
        if (idx >= isImageDetectedClose.length) {
            idx = 0;
        }
        if (isImageDetected)
            return;

        Frame frame = arFragment.getArSceneView().getArFrame();
        Collection<AugmentedImage> updatedAugmentedImages =
                frame.getUpdatedTrackables(AugmentedImage.class);

        for (AugmentedImage augmentedImage : updatedAugmentedImages) {
//            Toast.makeText(this, "image" + idx + ", " + augmentedImage.getName(), Toast.LENGTH_SHORT).show();
            if (augmentedImage.getTrackingState() == TrackingState.TRACKING && augmentedImage.getName().contains("image" + idx)) {
                // Check camera image matches our reference image
                isImageDetected = true;
                Node node =  new AugmentedWebViewNode(this, arImageInfo.get(idx).descripcion, new Vector3(augmentedImage.getCenterPose().tx(), augmentedImage.getCenterPose().ty() + .5f,augmentedImage.getCenterPose().tz()), arImageInfo.get(idx).nombres);
                arFragment.getArSceneView().getScene().addChild(node);
            }
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 0) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                btnCapture.setVisibility(View.GONE);
//                btnCapture.setEnabled(false);
//            }
//        }
//    }

    private void playVideo(Anchor anchor, float extentX, float extentZ){
        mediaPlayer.start();

        AnchorNode anchorNode = new AnchorNode(anchor);

        texture.getSurfaceTexture().setOnFrameAvailableListener(surfaceTexture -> {
            anchorNode.setRenderable(renderable);
            texture.getSurfaceTexture().setOnFrameAvailableListener(null);
        });

        anchorNode.setWorldScale(new Vector3(extentX, 1, extentZ));
        scene.addChild(anchorNode);
    }

    private File generateFilename() {
        String date =
                new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
        File mFolder = new File("/sdcard/TripJungle");
        if (!mFolder.canRead()) {
            mFolder.mkdir();
        }
        File imgFile = new File(mFolder.getAbsolutePath() + "/Image_" + date + ".jpg");
        if (!imgFile.canRead()) {
            try {
                imgFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imgFile;
    }

    private void takePhoto() {
        ArSceneView view = arFragment.getArSceneView();

        final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);
        image_close.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        btnCapture.setEnabled(false);
        btnBack.setEnabled(false);
        imageView.setImageBitmap(bitmap);

        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to WRITE_EXTERNAL_STORAGE - requesting it");
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, 1);
            }

            PixelCopy.request(view, bitmap, (copyResult) -> {
                if (copyResult == PixelCopy.SUCCESS) {
                        FileOutputStream fos = null;
                        try {
                            File imgFile = generateFilename();
                            fos = new FileOutputStream(imgFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG,70, fos);
                            fos.flush();
                            fos.close();

//                            this.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                            this.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                                    "saved picture.", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                } else {
                    Toast toast = Toast.makeText(ARActivity.this,
                            "not saved!: " + copyResult, Toast.LENGTH_LONG);
                    toast.show();
                }
                handlerThread.quitSafely();
            }, new Handler(handlerThread.getLooper()));
        }
    }

    FusedLocationProviderClient fusedLocationProviderClient;

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE);
        }
        getARInfo();
//        Task<Location> task = fusedLocationProviderClient.getLastLocation();
//        task.addOnSuccessListener(location -> {
////            if (location != null) {
////                pos_x = location.getLatitude();
////                pos_y = location.getLongitude();
////                getARInfo();
////            } else {
////                pos_x = -6.417917;
////                pos_y = -77.923333;
////                getARInfo();
////                Toast.makeText(this, "ubicación no encontrada", Toast.LENGTH_SHORT).show();
////            }
//        });
    }

    private void getARInfo() {
        pos_x = -6.417917;
        pos_y = -77.923333;
        String url = "https://h7ogkdnsd5.execute-api.us-east-1.amazonaws.com/Prod/api/TripJungle/buscarimagenesar";
        RequestQueue queue = Volley.newRequestQueue(ARActivity.this);
        JSONObject js = new JSONObject();
        progress = new ProgressDialog(ARActivity.this);
        progress.setIndeterminate(true);
        progress.setMessage("Servidor de conexión ...");
        progress.show();
        try {
            js.put("latitude", pos_x);
            js.put("longitude", pos_y);
            js.put("altitude", 0);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, js,
                response -> {
                    try {
                        JSONObject dataobject = response.getJSONObject("data");
                        String textoCode = response.getString("code");
                        String textoErrorMessage = response.getString("message");

                        if("200".equals(textoCode))
                        {
                            imageInfo = dataobject.getJSONArray("lugar");
                            arImageInfo.clear();
                            for (int i = 0; i < imageInfo.length(); i ++) {
                                JSONObject e = imageInfo.getJSONObject(i);
                                Bitmap[] images = new Bitmap[e.getJSONArray("imagenes").length()];
                                isImageDetectedClose = new boolean[e.getJSONArray("imagenes").length()];
                                for (int j = 0; j < e.getJSONArray("imagenes").length(); j ++) {
                                    Bitmap image = null;
                                    try {
                                        URL imageUrl = new URL(e.getJSONArray("imagenes").getJSONObject(j).getString("rutaImagen"));
                                        image = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                                    } catch (MalformedURLException ex) {
                                        ex.printStackTrace();
                                    } catch (IOException ioException) {
                                        ioException.printStackTrace();
                                    }
                                    isImageDetectedClose[j] = false;
                                    arImageInfo.add(new ImageInfoModel(e.getInt("atractivoID"), e.getString("nombres"), e.getString("descripcion"), image));
                                }
                            }
                            Config config = new Config(CustomArFragment.session);
                            config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
                            AugmentedImageDatabase aid = new AugmentedImageDatabase(CustomArFragment.session);
                            for (int i = 0; i < arImageInfo.size(); i ++) {
                                    Bitmap bitmap = arImageInfo.get(i).imagenes;
                                    aid.addImage("image" + i, bitmap);
                            }
                            config.setAugmentedImageDatabase(aid);
//                            arFragment.getArSceneView().setupSession(CustomArFragment.session);
                            CustomArFragment.session.configure(config);
                            CustomArFragment.session.resume();
                            scene.addOnUpdateListener(this::onUpdate);
                        } else if("500".equals(textoCode))
                        {
                            Toast.makeText(this, textoErrorMessage, Toast.LENGTH_SHORT).show();
                        }
                        progress.dismiss();

                    } catch (JSONException e) {
                        progress.dismiss();
                        e.printStackTrace();
                    } catch (CameraNotAvailableException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            progress.dismiss();
            Toast.makeText(ARActivity.this, "sever error", Toast.LENGTH_LONG).show();
        });
        queue.add(request);
    }
}
