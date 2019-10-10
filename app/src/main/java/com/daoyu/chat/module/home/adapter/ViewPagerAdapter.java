package com.daoyu.chat.module.home.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.daoyu.chat.base.BaseFragment;

import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> data;

    public ViewPagerAdapter(FragmentManager fm, List<BaseFragment> list) {
        super(fm);
        this.data = list;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }
}
