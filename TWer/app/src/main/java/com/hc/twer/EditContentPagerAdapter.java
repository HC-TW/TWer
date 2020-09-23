package com.hc.twer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditContentPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mFragmentTitleList = new ArrayList<>();
    private int schedulePos;

    private String[] nums = {"", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
    private String[] base = {"", "十"};

    public EditContentPagerAdapter(FragmentManager fm, int schedulePos) {
        super(fm);
        this.schedulePos = schedulePos;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = mFragmentList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("schedulePos", schedulePos);
        bundle.putInt("datePos", position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        Fragment fragment = (Fragment) object;
        int position = mFragmentList.indexOf(fragment);

        if (position >= 0)
        {
            return position;
        }
        else
        {
            return POSITION_NONE;
        }
    }

    public void addFragment(Fragment fragment, String title)
    {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    public void removeFragment(int position, ArrayList<Date> scheduleDates)
    {
        if (position == 0)
        {
            mFragmentTitleList.clear();
            for (int i = 0; i < scheduleDates.size(); i++)
            {
                mFragmentTitleList.add(transformDate(scheduleDates.get(i)) + "\n" + "第" + getOrdinalNumber(i+1) + "天");
            }
        }
        else
        {
            mFragmentTitleList.remove(getCount()-1);
        }
        mFragmentList.remove(position);
    }

    public void moveFragment(int oldPosition, int newPosition)
    {
        Fragment fragment = mFragmentList.remove(oldPosition);
        //fragment.getArguments().putInt("datePos", newPosition);
        mFragmentList.add(newPosition, fragment);
    }

    // get real world date
    private String transformDate(Date date)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");
        String dateString = simpleDateFormat.format(date);

        return dateString;
    }

    // get ordinal number
    private String getOrdinalNumber(int n)
    {
        String ordinalNum = "";

        if (n < 20)
        {
            ordinalNum += base[n/10] + nums[n%10];
        }
        else
        {
            ordinalNum += nums[n/10] + "十" + nums[n%10];
        }

        return ordinalNum;
    }
}
