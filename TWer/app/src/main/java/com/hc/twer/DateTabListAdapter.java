package com.hc.twer;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nshmura.recyclertablayout.RecyclerTabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DateTabListAdapter extends RecyclerTabLayout.Adapter<DateTabListAdapter.DateTabViewHolder> implements DragHelper.ActionCompletionContract{

    private SharedPreference sharedPreference = new SharedPreference();
    private RelativeLayout.LayoutParams editLayoutParams;
    private RelativeLayout.LayoutParams normalLayoutParams;
    private Context context;
    private ArrayList<Date> scheduleDates;
    private int schedulePos;
    private EditContentPagerAdapter editContentPagerAdapter;
    private ItemTouchHelper itemTouchHelper;
    private View draggingView;

    public DateTabListAdapter(ViewPager viewPager, Context context, ArrayList<Date> scheduleDates, int schedulePos) {
        super(viewPager);
        this.context = context;
        this.scheduleDates = scheduleDates;
        this.schedulePos = schedulePos;
        this.editContentPagerAdapter = (EditContentPagerAdapter) viewPager.getAdapter();

        this.editLayoutParams = new RelativeLayout.LayoutParams(dpToPx(120), dpToPx(59));
        this.normalLayoutParams = new RelativeLayout.LayoutParams(dpToPx(80), dpToPx(59));
    }

    public static class DateTabViewHolder extends RecyclerView.ViewHolder {

        private ImageView deleteDate;
        private ImageView moveDate;
        private TextView dateTitle;

        public DateTabViewHolder(@NonNull View itemView) {
            super(itemView);

            deleteDate = itemView.findViewById(R.id.edit_home_delete_date);
            moveDate = itemView.findViewById(R.id.edit_home_move_date);
            dateTitle = itemView.findViewById(R.id.edit_home_date_title);
        }
    }

    @NonNull
    @Override
    public DateTabViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.edit_home_tab_layout, viewGroup, false);

        return new DateTabViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateTabViewHolder holder, int position) {
        // check if it's in EDIT_MODE
        if (EditHomeFragment.EDIT_MODE)
        {
            holder.itemView.setLayoutParams(editLayoutParams);
            holder.deleteDate.setVisibility(View.VISIBLE);
            holder.moveDate.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.itemView.setLayoutParams(normalLayoutParams);
            holder.deleteDate.setVisibility(View.GONE);
            holder.moveDate.setVisibility(View.GONE);
        }
        // set date tab title
        holder.dateTitle.setText(editContentPagerAdapter.getPageTitle(position));
        // set current tab pager item
        holder.dateTitle.setOnClickListener(v -> {
            getViewPager().setCurrentItem(position);
            EditHomeFragment.datePos = position;
        });
        // delete date
        holder.deleteDate.setOnClickListener(v -> {
            // check if the size of dates > 1
            if (scheduleDates.size() == 1) {
                Toast.makeText(context, "天數必須為一天以上", Toast.LENGTH_LONG).show();
                return;
            }
            new AlertDialog.Builder(context)
                    .setMessage("確定刪除第"+ (position+1) + "天嗎？")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // temp change
                            int removePos;
                            if (position == 0)
                            {
                                removePos = position;
                            }
                            else
                            {
                                removePos = scheduleDates.size()-1;
                            }
                            Date removeDate = scheduleDates.get(position);
                            scheduleDates.remove(removePos);

                            /*
                            editContentPagerAdapter.removeFragment(position, scheduleDates);
                            editContentPagerAdapter.notifyDataSetChanged();
                            */

                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, getItemCount() - position);

                            // backup
                            ArrayList<MyScheduleContent> scheduleContents = sharedPreference.getMyScheduleContentList(context);
                            MyScheduleContent scheduleContent = scheduleContents.get(schedulePos);

                            scheduleContent.getScheduleDates().remove(removePos);
                            scheduleContent.getSchedulePlaces().remove(position);
                            scheduleContent.getSchedulePlaceTimes().remove(position);
                            scheduleContent.getSchedulePlaceTransportWays().remove(position);
                            sharedPreference.saveMyScheduleContentList(context, scheduleContents);

                            // refresh scheduleTime
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                            String scheduleTimeString = simpleDateFormat.format(scheduleDates.get(0)) + " - " + simpleDateFormat.format(scheduleDates.get(scheduleDates.size()-1));
                            EditHomeFragment.scheduleTime.setText(scheduleTimeString);
                            ArrayList<MySchedule> schedules = sharedPreference.getMyScheduleList(context);
                            schedules.get(schedulePos).setScheduleTime(scheduleTimeString);
                            sharedPreference.saveMyScheduleList(context, schedules);

                            Bundle bundle = new Bundle();
                            bundle.putBoolean("EDIT_MODE", true);
                            bundle.putInt("schedulePos", schedulePos);
                            if (position > 0)
                            {
                                bundle.putInt("datePos", position-1);
                            }
                            EditHomeFragment editHomeFragment = new EditHomeFragment();
                            editHomeFragment.setArguments(bundle);
                            AppCompatActivity appCompatActivity = (AppCompatActivity)context;
                            appCompatActivity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.edit_fragment_container, editHomeFragment, null).commit();
                            Toast.makeText(context, "已刪除 " + transformDate(removeDate) + " 行程", Toast.LENGTH_LONG).show();
                        }
                    }).show();
        });


        // move date
        holder.moveDate.setOnTouchListener((v, event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
            {
                getViewPager().setCurrentItem(position);
                EditHomeFragment.datePos = position;

                draggingView = holder.itemView;
                AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.animation_raise);
                set.setTarget(holder.itemView);
                set.start();
                itemTouchHelper.startDrag(holder);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return editContentPagerAdapter.getCount();
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        notifyItemMoved(oldPosition, newPosition);
    }

    @Override
    public void reallyMoved(int oldPosition, int newPosition) {
        Log.d("reallyMoved", "yes");
        Log.d("move", "oldPos" + oldPosition + " newPos" + newPosition);
        /*
        // temp change
        editContentPagerAdapter.moveFragment(oldPosition, newPosition);
        editContentPagerAdapter.notifyDataSetChanged();
        */
        // backup
        ArrayList<MyScheduleContent> scheduleContents = sharedPreference.getMyScheduleContentList(context);
        MyScheduleContent scheduleContent = scheduleContents.get(schedulePos);

        ArrayList<ArrayList<MyPlace>> schedulePlaces = scheduleContent.getSchedulePlaces();
        ArrayList<MyPlace> datePlaces = schedulePlaces.remove(oldPosition);
        schedulePlaces.add(newPosition, datePlaces);

        ArrayList<ArrayList<ArrayList<Integer>>> schedulePlaceTimes = scheduleContent.getSchedulePlaceTimes();
        ArrayList<ArrayList<Integer>> datePlaceTimes = schedulePlaceTimes.remove(oldPosition);
        schedulePlaceTimes.add(newPosition, datePlaceTimes);

        ArrayList<ArrayList<Integer>> schedulePlaceTransportWays = scheduleContent.getSchedulePlaceTransportWays();
        ArrayList<Integer> datePlaceTransportWays = schedulePlaceTransportWays.remove(oldPosition);
        schedulePlaceTransportWays.add(newPosition, datePlaceTransportWays);

        sharedPreference.saveMyScheduleContentList(context, scheduleContents);

        Bundle bundle = new Bundle();
        bundle.putBoolean("EDIT_MODE", true);
        bundle.putInt("schedulePos", schedulePos);
        bundle.putInt("datePos", newPosition);
        EditHomeFragment editHomeFragment = new EditHomeFragment();
        editHomeFragment.setArguments(bundle);
        AppCompatActivity appCompatActivity = (AppCompatActivity)context;
        appCompatActivity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.edit_fragment_container, editHomeFragment, null).commit();
    }

    @Override
    public void drop() {
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.animation_drop);
        set.setTarget(draggingView);
        set.start();
    }

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper)
    {
        this.itemTouchHelper = itemTouchHelper;
    }

    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    // get real world date
    private String transformDate(Date date)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");

        return simpleDateFormat.format(date);
    }
}
