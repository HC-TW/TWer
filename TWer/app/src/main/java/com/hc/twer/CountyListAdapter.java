package com.hc.twer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class CountyListAdapter extends RecyclerView.Adapter<CountyListAdapter.CountyViewHolder> {

    private Context context;
    private List<String> countyNames;
    private List<Integer> countyImageIds;

    public CountyListAdapter(Context context, List<String> countyNames, List<Integer> countyImageIds)
    {
        this.context = context;
        this.countyNames = countyNames;
        this.countyImageIds = countyImageIds;
    }

    public static class CountyViewHolder extends RecyclerView.ViewHolder {

        private ImageView countyImage;
        private TextView countyName;

        private CountyViewHolder(View itemView) {
            super(itemView);

            countyImage = itemView.findViewById(R.id.county_image);
            countyName = itemView.findViewById(R.id.county_name);
        }
    }


    @NonNull
    @Override
    public CountyListAdapter.CountyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.county_layout, parent, false);

        return new CountyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CountyViewHolder holder, final int position) {

        Glide.with(context).load("").apply(RequestOptions.placeholderOf(countyImageIds.get(position))).into(holder.countyImage);

        holder.countyName.setText(countyNames.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) context;
                Bundle bundle = new Bundle();
                bundle.putString("keyword", countyNames.get(position));
                TravelsSecondFragment travelsSecondFragment = new TravelsSecondFragment();
                travelsSecondFragment.setArguments(bundle);
                appCompatActivity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full, R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.fragment_container, travelsSecondFragment).addToBackStack("travelsFirst").commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return countyNames.size();
    }
}
