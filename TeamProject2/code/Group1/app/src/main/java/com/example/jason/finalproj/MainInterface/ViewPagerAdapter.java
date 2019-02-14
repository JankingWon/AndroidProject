package com.example.jason.finalproj.MainInterface;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ViewPagerAdapter extends PagerAdapter {
    private Context mcontext;
    private int[] images;//图片ID数组

    ViewPagerAdapter(Context context, int[] Images) {
        mcontext = context;
        images = Images;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object o){
        //container.removeViewAt(position);
    }

    //设置ViewPager指定位置要显示的view
    @Override
    public Object instantiateItem(ViewGroup container, int position){
        ImageView im = new ImageView(mcontext);
        im.setScaleType(ImageView.ScaleType.CENTER_CROP);
        position = position % images.length;
        Glide.with(mcontext).load(images[position]).into(im);
        //im.setImageResource(images[position]);
        container.addView(im);
        return im;
    }
}
