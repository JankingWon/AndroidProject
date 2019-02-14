package com.janking.sysuhealth;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class OperationListViewAdapter extends BaseAdapter {

    private List<String> list;

    public OperationListViewAdapter() {

        list = new ArrayList<String>();
        list.add(new String("分享信息"));
        list.add(new String("不感兴趣"));
        list.add(new String("查看更多信息"));
        list.add(new String("出错反馈"));
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
    public String getItem(int i) {
        if (list == null) {
            return null;
        }
        return list.get(i);
    }

    public void addNewItem(String f) {
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


    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        // 新声明一个ViewHoleder变量
        OperationListViewAdapter.ViewHolder viewHolder;
        // 当view为空时才加载布局，否则，直接修改内容
        if (convertView == null) {
            // 通过inflate的方法加载布局，context需要在使用这个Adapter的Activity中传入。
            convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listview_detail, null);
            viewHolder = new OperationListViewAdapter.ViewHolder();
            viewHolder.mTv = (TextView) convertView.findViewById(R.id.operation_tv);
            convertView.setTag(viewHolder); // 用setTag方法将处理好的viewHolder放入view中
        } else { // 否则，让convertView等于view，然后从中取出ViewHolder即可
            viewHolder = (OperationListViewAdapter.ViewHolder) convertView.getTag();
        }
        // 从viewHolder中取出对应的对象，然后赋值给他们
        viewHolder.mTv.setText(list.get(i));
        // 将这个处理好的view返回
        return convertView;
    }

    private class ViewHolder {
        public TextView mTv;
    }
}
