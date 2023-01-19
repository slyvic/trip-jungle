package com.usa.tripjungle;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amazon.identity.auth.device.api.workflow.RequestContext;
import com.usa.tripjungle.connect.AvsItem;
import com.usa.tripjungle.connect.AvsSpeakItem;
import com.usa.tripjungle.connect.AvsTemplateItem;
import com.usa.tripjungle.connect.ConnectManager;
import com.usa.tripjungle.util.AudioPlayer;
import com.usa.tripjungle.util.DateTimeUtil;
import com.usa.tripjungle.util.LoginManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ee.ioc.phon.android.speechutils.Log;
import ee.ioc.phon.android.speechutils.RawAudioRecorder;


public class VChatActivity extends AppCompatActivity {

    private RequestContext mRequestContext;
    private ConnectManager mConnectManager;

    ImageView recode;
    TextView txtStatus;
    private RawAudioRecorder mRecorder;
    private AudioPlayer mAudioPlayer;

    boolean isRecording = false;
    private static final int AUDIO_RATE = 16000;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_chat);
        Objects.requireNonNull(getSupportActionBar()).hide();

        LoginManager.init(this);
        mRequestContext = RequestContext.create(this);
        mConnectManager = new ConnectManager(this);
        mAudioPlayer = new AudioPlayer(this);

        LoginManager.doLogin(mRequestContext, new LoginManager.LoginCallback() {
            @Override
            public void onSuccess() {
//                setLoginStatus();

                long expireTime = LoginManager.getExpireTime();
                Toast.makeText(VChatActivity.this, "Login Success, Token Expires At " + DateTimeUtil.getDateString(expireTime), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail() {
                Toast.makeText(VChatActivity.this, "Login Fail", Toast.LENGTH_LONG).show();
            }
        });

        txtStatus = findViewById(R.id.txtStatus);
        recode = findViewById(R.id.recode);
        recode.setLongClickable(true);

        recode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //long press to listen
                        txtStatus.setText("hablar...");
                        startListening();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_UP:
                        txtStatus.setText("escuchando...");
                        //release to stop recording and send request to alexa
                        stopListening();
                        break;

                    default:
                        break;
                }

                return true;
            }
        });

    }


    private void startListening() {
        if (!isRecording) {
            if (mRecorder == null) {
                mRecorder = new RawAudioRecorder(AUDIO_RATE);
            }
            mRecorder.start();
            isRecording = true;
        }
    }

    private void stopListening() {
//        mPulseView.setVisibility(View.GONE);
//        mProcessingView.setVisibility(View.VISIBLE);
        Log.e("test");
        if (mRecorder != null) {
            new Thread(() -> {
                byte[] recordBytes = mRecorder.getCompleteRecording();
//                byte[] recordBytes = hexStringToByteArray("e04fd020ea3a6910a2d808002b30309d");
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
                isRecording = false;
                mConnectManager.sendRequest(recordBytes, res -> runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.getResponseCode() == 400) {
                            Toast.makeText(VChatActivity.this, res.getResponseCode() + " " + res.getMessage(), Toast.LENGTH_LONG).show();
                        } else if (res.getResponseCode() != 200) {
                            Toast.makeText(VChatActivity.this, res.getResponseCode() + " " + res.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            onAlexaResponse(res.getAvsItems());
                        }
                    }
                }));
            }).start();
        }
    }
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    private void onAlexaResponse(List<AvsItem> res) {
        if (res != null) {
            for (AvsItem item : res) {
                if (item instanceof AvsSpeakItem) {
                    mAudioPlayer.play((AvsSpeakItem) item);
                }
                if (item instanceof AvsTemplateItem) {
                    final AvsTemplateItem templateItem = (AvsTemplateItem) item;
                    if (templateItem.isBodyType()) {
                        new Handler().postDelayed(() -> {
                        }, 100);
                    }
                    if (templateItem.isWeatherType()) {
                        new Handler().postDelayed(() -> {
                        }, 100);
                    }

                }
            }
        }
    }

    private void checkAndRequestPermissions() {
        int permission1 = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO);
        int permission2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission3 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permission3 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permission2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRequestContext.onResume();
        checkAndRequestPermissions();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            trimCache();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void trimCache() {
        try {
            File dir = getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
        }

        // The directory is now empty so delete it
        if (dir != null) {
            return dir.delete();
        }
        return false;
    }
}
