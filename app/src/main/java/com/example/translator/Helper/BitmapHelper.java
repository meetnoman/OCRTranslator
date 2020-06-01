package com.example.translator.Helper;

import android.graphics.Bitmap;

/**
 * Created by M Noman on 05-Nov-19.
 */


public class BitmapHelper {

    private Bitmap bitmap=null;
    private static final BitmapHelper instance=new BitmapHelper();

    BitmapHelper(){

    }
    public static BitmapHelper getInstance(){
        return instance;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap){
        this.bitmap=bitmap;
    }


}