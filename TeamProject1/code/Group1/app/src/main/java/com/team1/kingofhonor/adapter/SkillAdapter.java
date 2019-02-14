package com.team1.kingofhonor.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.team1.kingofhonor.R;
import com.team1.kingofhonor.model.Skill;


import java.util.ArrayList;

public class SkillAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Skill> list;
    private int mIndex;
    private int mPagerSize;

    public SkillAdapter(Context context, ArrayList<Skill> list, int mIndex, int mPagerSize){
        this.context = context;
        this.list = list;
        this.mIndex = mIndex;
        this.mPagerSize = mPagerSize;
    }


    /**
     * 先判断数据及的大小是否显示满本页lists.size() > (mIndex + 1)*mPagerSize
     * 如果满足，则此页就显示最大数量lists的个数
     * 如果不够显示每页的最大数量，那么剩下几个就显示几个
     */
    @Override
    public int getCount() {
        return list.size() > (mIndex + 1) * mPagerSize ? mPagerSize : (list.size() - mIndex * mPagerSize);
    }

    @Override
    public Object getItem(int i) {
        return list.get(i + mPagerSize*mIndex);
    }

    @Override
    public long getItemId(int i) {
        return i + mIndex*mPagerSize;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null){
            holder = new ViewHolder();
            view = View.inflate(context, R.layout.skill_detail, null);
            holder.skill_name = (TextView) view.findViewById(R.id.skill_name);
            holder.skill_image = (ImageView) view.findViewById(R.id.skill_image);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        final int pos = i + mIndex*mPagerSize;
        holder.skill_image.setImageResource(list.get(pos).getImage());
        holder.skill_name.setText(list.get(pos).getName());
        return view;
    }

    class ViewHolder{
        private TextView skill_name;
        private ImageView skill_image;
    }


    public void updateEquipment(ArrayList<Skill> all, String categoryId){
        list.clear();
        list = all;
        notifyDataSetChanged();
        notifyDataSetChanged();
    }

}

