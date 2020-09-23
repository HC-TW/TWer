package com.hc.twer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView title;

    private ArrayList<Bitmap> bitmaps = new ArrayList<>();

    private MyHorizontalScrollView myHorizontalScrollView;
    private ImageView gallery_image;
    private GalleryImageAdapter galleryImageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        init();
        getBitmapData();
        setGallery();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.noslide, R.anim.slide_out_bottom);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init()
    {
        toolbar = findViewById(R.id.toolBar);
        title = findViewById(R.id.toolbar_title);
        title.setText("相片");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void getBitmapData() {
        bitmaps = (ArrayList<Bitmap>) MediaApplication.getInstance().getPhotoList();// 解决异常:JavaBinder: !!! FAILED BINDER TRANSACTION !!!
    }

    private void setGallery()
    {
        myHorizontalScrollView = findViewById(R.id.horizontal_scrollview);
        gallery_image = findViewById(R.id.gallery_image);

        Glide.with(this).load(bitmaps.get(0)).into(gallery_image);
        galleryImageAdapter = new GalleryImageAdapter(this, bitmaps);
        myHorizontalScrollView.setOnItemClickListener(new MyHorizontalScrollView.OnItemClickListener() {
            @Override
            public void onClick(View view, int pos) {
                Glide.with(GalleryActivity.this).load(bitmaps.get(pos)).into(gallery_image);
            }
        });
        myHorizontalScrollView.initDatas(galleryImageAdapter);
    }


}
