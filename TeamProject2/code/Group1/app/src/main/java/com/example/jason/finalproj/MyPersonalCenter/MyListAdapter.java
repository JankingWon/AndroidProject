package com.example.jason.finalproj.MyPersonalCenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jason.finalproj.R;

import java.util.List;


/**
 * “我”功能列表适配器
 */
public class MyListAdapter extends BaseAdapter {
    private Context mcontext;
    private List<Integer> image_list;//图片id数组
    private List<String> func_list; //功能名称数组
    private int resourceId;
    private class ViewHolder {
        public ImageView imageView;
        public TextView function;
    }
    public MyListAdapter(Context context, int ResourceId, List<Integer> Image_list, List<String> Func_list) {
        mcontext = context;
        resourceId = ResourceId;
        image_list = Image_list;
        func_list = Func_list;
    }

    @Override
    public int getCount() {
        if(func_list == null) {
            return 0;
        }
        return func_list.size();
    }
    @Override
    public Object getItem(int i) {
        if(func_list == null) {
            return null;
        }
        return func_list.get(i);
    }
    @Override
    public long getItemId(int i ) {
        return i;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View convertView;
        ViewHolder holder;
        if(view == null) {
            convertView = LayoutInflater.from(mcontext).inflate(resourceId, viewGroup, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.item_image);
            holder.function = (TextView) convertView.findViewById(R.id.item_function);
            convertView.setTag(holder);
        } else {
            convertView = view;
            holder = (ViewHolder) convertView.getTag();
        }
        holder.imageView.setImageResource(image_list.get(i));
        holder.function.setText(func_list.get(i));
        return convertView;
    }
}
