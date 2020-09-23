package com.hc.twer;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GalleryImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Bitmap> bitmaps;

    public GalleryImageAdapter(Context context, ArrayList<Bitmap> bitmapArrayList)
    {
        mContext = context;
        this.bitmaps = bitmapArrayList;
    }

    @Override
    public int getCount() {
        return bitmaps.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView = new ImageView(mContext);

        Glide.with(mContext).load(bitmaps.get(position)).into(imageView);
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(200, 200));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        return imageView;
    }
}
