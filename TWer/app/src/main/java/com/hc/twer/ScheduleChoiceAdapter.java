package com.hc.twer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import java.util.ArrayList;

public class ScheduleChoiceAdapter extends RecyclerView.Adapter<ScheduleChoiceAdapter.ScheduleChoiceViewHolder> {

    private AlphaAnimation alphaAnimation = new AlphaAnimation(1F, 0.8F);
    private Context context;
    private ArrayList<MySchedule> schedules;
    private String placeId;

    public ScheduleChoiceAdapter(Context context, ArrayList<MySchedule> schedules, String placeId)
    {
        this.context = context;
        this.schedules = schedules;
        this.placeId = placeId;
    }

    public static class ScheduleChoiceViewHolder extends RecyclerView.ViewHolder {

        private TextView scheduleChoice;

        public ScheduleChoiceViewHolder(View itemView) {
            super(itemView);

            scheduleChoice = itemView.findViewById(R.id.schedule_choice);
        }
    }

    @NonNull
    @Override
    public ScheduleChoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.schedule_choice_layout, parent, false);

        return new ScheduleChoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleChoiceViewHolder holder, final int position) {

        holder.scheduleChoice.setText(schedules.get(position).getScheduleName());
        holder.itemView.setOnClickListener(v -> {
            v.startAnimation(alphaAnimation);
            AppCompatActivity appCompatActivity = (AppCompatActivity) context;
            Bundle bundle = new Bundle();
            bundle.putInt("schedulePos", position);
            bundle.putString("placeId", placeId);
            DateChoiceFragment dateChoiceFragment = new DateChoiceFragment();
            dateChoiceFragment.setArguments(bundle);
            if (((AppCompatActivity) context).findViewById(R.id.fragment_container) != null)
            {
                appCompatActivity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full, R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.fragment_container, dateChoiceFragment).addToBackStack("ScheduleChoice").commit();
            }
            else
            {
                appCompatActivity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left_full, R.anim.slide_in_left_full, R.anim.slide_out_right).replace(R.id.edit_fragment_container, dateChoiceFragment).addToBackStack("ScheduleChoice").commit();
            }

        });
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }
}
