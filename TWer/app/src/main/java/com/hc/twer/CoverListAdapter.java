package com.hc.twer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class CoverListAdapter extends RecyclerView.Adapter<CoverListAdapter.CoverViewHolder> {

    private Context context;
    private List<String> covers;
    private int schedulePos;

    public CoverListAdapter(Context context, List<String> covers, int schedulePos)
    {
        this.context = context;
        this.covers = covers;
        this.schedulePos = schedulePos;
    }

    public class CoverViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView cover;

        private CoverViewHolder(View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.cover);
        }
    }

    @NonNull
    @Override
    public CoverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.cover_layout, parent, false);

        return new CoverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoverViewHolder holder, int position) {
        final int imageId = context.getResources().getIdentifier(covers.get(position), "drawable", context.getPackageName());
        Glide.with(context).load("").apply(RequestOptions.placeholderOf(imageId)).into(holder.cover);
        holder.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("確定更換此封面嗎？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreference sharedPreference = new SharedPreference();
                                ArrayList<MySchedule> schedules = sharedPreference.getMyScheduleList(context);
                                schedules.get(schedulePos).setScheduleCover(Integer.toString(imageId));
                                sharedPreference.saveMyScheduleList(context, schedules);

                                AppCompatActivity appCompatActivity = (AppCompatActivity) context;
                                appCompatActivity.setResult(RESULT_OK);
                                appCompatActivity.finish();
                            }
                        }).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return covers.size();
    }
}
