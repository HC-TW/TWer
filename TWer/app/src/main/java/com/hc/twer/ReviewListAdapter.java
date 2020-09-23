package com.hc.twer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewViewHolder> {

    private Context context;
    private JSONArray reviews;

    public ReviewListAdapter(Context context, JSONArray reviews)
    {
        this.context = context;
        this.reviews = reviews;
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView reviewPhoto;
        private TextView reviewAuthor;
        private RatingBar reviewRating;
        private TextView reviewTime;
        private TextView reviewContent;

        private ReviewViewHolder(View itemView) {
            super(itemView);

            reviewPhoto = itemView.findViewById(R.id.review_photo);
            reviewAuthor = itemView.findViewById(R.id.review_author);
            reviewRating = itemView.findViewById(R.id.review_rating);
            reviewTime = itemView.findViewById(R.id.review_time);
            reviewContent = itemView.findViewById(R.id.review_content);
        }
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.review_layout, parent, false);

        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {

        try
        {
            final JSONObject result = reviews.getJSONObject(position);
            Uri uri = Uri.parse(result.getString("profile_photo_url"));
            // author's photo
            Glide.with(context).load(uri).into(holder.reviewPhoto);
            // author's name
            holder.reviewAuthor.setText(result.getString("author_name"));
            // review's rating
            holder.reviewRating.setRating(result.getInt("rating"));
            // review's time
            holder.reviewTime.setText(result.getString("relative_time_description"));
            // review's content
            holder.reviewContent.setText(result.getString("text"));

        } catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return reviews.length();
    }
}
