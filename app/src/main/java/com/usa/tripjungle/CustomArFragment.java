package com.usa.tripjungle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;
import com.usa.tripjungle.model.ImageInfoModel;

import java.util.ArrayList;

public class CustomArFragment  extends ArFragment {
    static ArrayList<ImageInfoModel> arImageInfo = new ArrayList<>();
    static Session session;

    @Override
    protected Config getSessionConfiguration(Session session){
        this.session = session;
        Config config = new Config(session);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);

        AugmentedImageDatabase aid = new AugmentedImageDatabase(session);

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable._karajia);
//        aid.addImage("image", bitmap);

        config.setAugmentedImageDatabase(aid);

        this.getArSceneView().setupSession(session);

        return config;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout frameLayout = (FrameLayout) super.onCreateView(inflater, container, savedInstanceState);
        getPlaneDiscoveryController().hide();
        getPlaneDiscoveryController().setInstructionView(null);

        return frameLayout;
    }
}
