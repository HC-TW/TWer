package com.hc.twer;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.takt.Audience;
import jp.wasabeef.takt.Seat;
import jp.wasabeef.takt.Takt;

public class MediaApplication extends Application {

    // 用于传递的图片数据
    private List mPhotoList;
    private static MediaApplication mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public synchronized static MediaApplication getInstance() {
        return mInstance;
    }

    public void setPhotoList(ArrayList<Bitmap> list){
        this.mPhotoList = list;
    }

    public List getPhotoList(){
        return this.mPhotoList;
    }
}
