package com.janking.sysuhealth;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MyListViewAdapter extends BaseAdapter {
    private List<Food> list;

    public MyListViewAdapter() {

        list = new ArrayList<Food>();
        list.add(new Food("收藏夹", "*", "*" , "#000000"));
    }
    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Food getItem(int i) {
        if (list == null) {
            return null;
        }
        return list.get(i);
    }

    public void addNewItem(Food f) {
        if(list == null) {
            list = new ArrayList<>();
        }
        list.add(f);
        notifyDataSetChanged();
    }

    public void deleteItem(int pos) {
        if(list == null || list.isEmpty()) {
            return;
        }
        list.remove(pos);
        notifyDataSetChanged();
    }

    public void deleteItemByName(String name) {
        if(list == null || list.isEmpty()) {
            return;
        }
        for(Food i : list){
            if(i.getName().equals(name)){
                list.remove(i);
                System.out.print("successful");
                break;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        // 新声明一个ViewHoleder变量
        ViewHolder viewHolder;
        // 当view为空时才加载布局，否则，直接修改内容
        if (convertView == null) {
            // 通过inflate的方法加载布局，context需要在使用这个Adapter的Activity中传入。
            convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_single_recyclerview, null);
            viewHolder = new ViewHolder();
            viewHolder.mTv = (TextView) convertView.findViewById(R.id.rv_text);
            viewHolder.mIcon= (TextView) convertView.findViewById(R.id.rv_icon);
            convertView.setTag(viewHolder); // 用setTag方法将处理好的viewHolder放入view中
        } else { // 否则，让convertView等于view，然后从中取出ViewHolder即可
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 从viewHolder中取出对应的对象，然后赋值给他们
        viewHolder.mTv.setText(list.get(i).getName());
        viewHolder.mIcon.setText(list.get(i).getCategory().subSequence(0,1));
        // 将这个处理好的view返回
        return convertView;
    }

    private class ViewHolder {
        public TextView mTv, mIcon;
    }
}
