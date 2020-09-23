package com.hc.twer;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.ArrayList;

public class MyScheduleAdapter extends RecyclerView.Adapter<MyScheduleAdapter.ScheduleViewHolder> implements DragHelper.ActionCompletionContract{

    private SharedPreference sharedPreference = new SharedPreference();
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private Context context;
    private ArrayList<MySchedule> schedules;
    private ItemTouchHelper itemTouchHelper;
    private View draggingView;

    public MyScheduleAdapter(Context context, ArrayList<MySchedule> schedules)
    {
        this.context = context;
        this.schedules = schedules;
    }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {

        private SwipeRevealLayout swipeRevealLayout;
        private ImageView schedule_duplicate;
        private ImageView schedule_delete;
        private ImageView schedule_image;
        private TextView schedule_name;
        private TextView schedule_time;

        private ScheduleViewHolder(View itemView) {
            super(itemView);

            swipeRevealLayout = itemView.findViewById(R.id.home_swipe_reveal_layout);
            schedule_duplicate = itemView.findViewById(R.id.schedule_duplicate);
            schedule_delete = itemView.findViewById(R.id.schedule_delete);
            schedule_image = itemView.findViewById(R.id.schedule_image);
            schedule_name = itemView.findViewById(R.id.schedule_name);
            schedule_time = itemView.findViewById(R.id.schedule_time);
        }
    }


    @NonNull
    @Override
    public MyScheduleAdapter.ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_layout, parent, false);

        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, final int position) {
        // Bind Swipe Reveal Layout
        viewBinderHelper.bind(holder.swipeRevealLayout, schedules.get(position).getScheduleName());

        // set up schedule Image, Name, Time
        holder.schedule_name.setText(schedules.get(position).getScheduleName());
        holder.schedule_time.setText(schedules.get(position).getScheduleTime());
        try {
            Glide.with(context).load("").apply(RequestOptions.placeholderOf(Integer.parseInt(schedules.get(position).getScheduleCover()))).into(holder.schedule_image);
        }catch (NumberFormatException e)
        {
            Glide.with(context).load(schedules.get(position).getScheduleCover()).into(holder.schedule_image);
        }

        // link to EditSchedule Activity
        holder.schedule_image.setOnClickListener(v -> {
            AppCompatActivity appCompatActivity = (AppCompatActivity) context;
            Intent intent = new Intent(appCompatActivity, EditScheduleActivity.class);
            intent.putExtra("schedulePos", position);
            appCompatActivity.startActivity(intent);
            appCompatActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        // drag
        holder.schedule_image.setOnLongClickListener(v -> {
            draggingView = holder.itemView;
            AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.schedule_content_animation_raise);
            set.setTarget(draggingView);
            set.start();
            itemTouchHelper.startDrag(holder);
            return true;
        });

        // Swipe Reveal Layout Delete Button
        holder.schedule_delete.setOnClickListener(v -> {
            // temp change
            schedules.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount() - position);
            // backup
            sharedPreference.removeSchedule(context, position);
            holder.swipeRevealLayout.close(true);
            Toast.makeText(context, "已刪除行程", Toast.LENGTH_LONG).show();
        });
        // Swipe Reveal Layout Duplicate Button
        holder.schedule_duplicate.setOnClickListener(v -> {
            // temp change
            schedules.add(position, schedules.get(position));
            notifyItemInserted(position);
            notifyItemRangeChanged(position, getItemCount() - position);
            // backup
            sharedPreference.saveMyScheduleList(context, schedules);

            ArrayList<MyScheduleContent> scheduleContents = sharedPreference.getMyScheduleContentList(context);
            scheduleContents.add(position, scheduleContents.get(position));
            sharedPreference.saveMyScheduleContentList(context, scheduleContents);

            holder.swipeRevealLayout.close(true);
            Toast.makeText(context, "已複製行程", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        notifyItemMoved(oldPosition, newPosition);
    }

    @Override
    public void reallyMoved(int oldPosition, int newPosition) {
        Log.d("reallyMoved", "yes");
        // temp change
        MySchedule mySchedule = schedules.remove(oldPosition);
        schedules.add(newPosition, mySchedule);
        int changePos;
        if (oldPosition <= newPosition)
        {
            changePos = oldPosition;
        }
        else
        {
            changePos = newPosition;
        }
        notifyItemRangeChanged(changePos, Math.abs(newPosition - oldPosition)+1);
        // backup
        sharedPreference.saveMyScheduleList(context, schedules);

        ArrayList<MyScheduleContent> scheduleContents = sharedPreference.getMyScheduleContentList(context);
        MyScheduleContent myScheduleContent = scheduleContents.remove(oldPosition);
        scheduleContents.add(newPosition, myScheduleContent);
        sharedPreference.saveMyScheduleContentList(context, scheduleContents);
    }

    @Override
    public void drop() {
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.schedule_content_animation_drop);
        set.setTarget(draggingView);
        set.start();
    }

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper)
    {
        this.itemTouchHelper = itemTouchHelper;
    }

}
