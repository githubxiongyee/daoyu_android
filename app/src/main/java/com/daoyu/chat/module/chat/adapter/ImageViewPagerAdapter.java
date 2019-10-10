package com.daoyu.chat.module.chat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.daoyu.chat.module.im.module.MessageDetailTable;
import com.daoyu.chat.utils.ImageUtils;

import java.util.ArrayList;

public class ImageViewPagerAdapter extends PagerAdapter {
    private ArrayList<MessageDetailTable> arrayList;
    private Context context;

    public ImageViewPagerAdapter(ArrayList<MessageDetailTable> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return arrayList == null ? 0 : arrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        MessageDetailTable table = arrayList.get(position);
        ImageView imageView = new ImageView(context);
        ImageUtils.setNormalImage(context, table.message, imageView);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
