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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class TravelsListAdapter extends RecyclerView.Adapter<TravelsListAdapter.TravelsViewHolder> {

    private Context context;
    private JSONArray results;

    public TravelsListAdapter(Context context, JSONArray results)
    {
        this.context = context;
        this.results = results;
    }

    public static class TravelsViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView travels_name;
        private TextView travels_writer;

        private TravelsViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.travels_album);
            travels_name = itemView.findViewById(R.id.travels_name);
            travels_writer = itemView.findViewById(R.id.travels_writer);
        }
    }

    @NonNull
    @Override
    public TravelsListAdapter.TravelsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.travels_layout, parent, false);

        return new TravelsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelsViewHolder holder, int position) {

        try
        {
            final JSONObject result = results.getJSONObject(position);
            final String title = result.getString("title");
            // travels' name
            holder.travels_name.setText(title);
            // travels' writer
            if ( result.getJSONObject("pagemap").getJSONArray("metatags").getJSONObject(0).getString("author") != null )
            {
                holder.travels_writer.setText(result.getJSONObject("pagemap").getJSONArray("metatags").getJSONObject(0).getString("author"));
            }
            // travels' cover
            String image = "tc" + (position+1);
            int imageId = context.getResources().getIdentifier(image, "drawable", context.getPackageName());
            Glide.with(context).load("").apply(RequestOptions.placeholderOf(imageId)).into(holder.imageView);
            // travels' website
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try
                    {
                        AppCompatActivity appCompatActivity = (AppCompatActivity) context;
                        Bundle bundle = new Bundle();
                        // webAddress
                        bundle.putString("webAddress", result.getString("link"));
                        // toolbar_title
                        bundle.putString("title", title);
                        TravelsThirdFragment travelsThirdFragment = new TravelsThirdFragment();
                        travelsThirdFragment.setArguments(bundle);
                        appCompatActivity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full, R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.fragment_container, travelsThirdFragment).addToBackStack("travelsSecond").commit();
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return results.length();
    }
}
