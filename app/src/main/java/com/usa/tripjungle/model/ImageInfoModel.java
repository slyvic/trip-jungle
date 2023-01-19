package com.usa.tripjungle.model;

import android.graphics.Bitmap;

public class ImageInfoModel {
    public int atractivoID = 0;
    public String nombres = "";
    public String descripcion = "";
    public Bitmap imagenes;

    public ImageInfoModel(int atractivoID, String nombres, String descripcion, Bitmap imagenes) {
        this.atractivoID = atractivoID;
        this.nombres = nombres;
        this.descripcion = descripcion;
        this.imagenes = imagenes;
    }
}
