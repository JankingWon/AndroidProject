package com.team1.kingofhonor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.team1.kingofhonor.R;
import com.team1.kingofhonor.model.Inscription;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class InscriptionAdapter extends BaseAdapter {
    private List<Inscription> list;
    Context context;


    public InscriptionAdapter(List<Inscription> list, Context context) {
        this.list = list;
        this.context = context;
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
    public Object getItem(int i) {
        if (list == null) {
            return null;
        }
        return list.get(i);
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // 新声明一个View变量和ViewHoleder变量,ViewHolder类在下面定义。
        View convertView;
        ViewHolder viewHolder;
        // 当view为空时才加载布局，否则，直接修改内容
        if (view == null) {
            // 通过inflate的方法加载布局，context需要在使用这个Adapter的Activity中传入。
            convertView = LayoutInflater.from(context).inflate(R.layout.inscription_view, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.inscriptionItemName);
            viewHolder.level = (TextView) convertView.findViewById(R.id.inscriptionItemLevel);
            viewHolder.pro1 = (TextView) convertView.findViewById(R.id.inscriptionItemPro1);
            viewHolder.pro2 = (TextView)convertView.findViewById(R.id.inscriptionItemPro2);
            viewHolder.pro3 = (TextView)convertView.findViewById(R.id.inscriptionItemPro3);
            viewHolder.pic = (ImageView)convertView.findViewById(R.id.inscriptionItemPic);
            convertView.setTag(viewHolder); // 用setTag方法将处理好的viewHolder放入view中
        } else { // 否则，让convertView等于view，然后从中取出ViewHolder即可
            convertView = view;
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 从viewHolder中取出对应的对象，然后赋值给他们
        viewHolder.name.setText(list.get(i).getName());
        viewHolder.level.setText(String.valueOf(list.get(i).getLevel()) + "级");
        Map<String, Double> pro = list.get(i).getProperty();
        if(pro.size() == 1) {
            viewHolder.pro2.setVisibility(View.INVISIBLE);
            viewHolder.pro3.setVisibility(View.INVISIBLE);
            Iterator iterator = pro.entrySet().iterator();
            Map.Entry<String, Double> entry=(Map.Entry<String, Double>) iterator.next();
            String proString = entry.getKey()+": "+String.valueOf(entry.getValue());
            viewHolder.pro1.setText(proString);
        }
        else if(pro.size() == 2) {
            viewHolder.pro3.setVisibility(View.INVISIBLE);
            Iterator iterator = pro.entrySet().iterator();
            Map.Entry<String, Double> entry=(Map.Entry<String, Double>) iterator.next();
            String proString = entry.getKey()+": "+String.valueOf(entry.getValue());
            viewHolder.pro1.setText(proString);
            entry=(Map.Entry<String, Double>) iterator.next();
            proString = entry.getKey()+": "+String.valueOf(entry.getValue());
            viewHolder.pro2.setText(proString);
        }
        else {
            Iterator iterator = pro.entrySet().iterator();
            Map.Entry<String, Double> entry=(Map.Entry<String, Double>) iterator.next();
            String proString = entry.getKey()+": "+String.valueOf(entry.getValue());
            viewHolder.pro1.setText(proString);
            entry=(Map.Entry<String, Double>) iterator.next();
            proString = entry.getKey()+": "+String.valueOf(entry.getValue());
            viewHolder.pro2.setText(proString);
            entry=(Map.Entry<String, Double>) iterator.next();
            proString = entry.getKey()+": "+String.valueOf(entry.getValue());
            viewHolder.pro3.setText(proString);
        }
        viewHolder.pic.setImageResource(list.get(i).getImage());
        // 将这个处理好的view返回
        return convertView;
    }

    public void updateWithCategory(List<Inscription> inscriptions, int level, String color, String type) {
        list.clear();
        if(level == 0) {
            if(type.equals("all")) {
                if(color.equals("all")) {
                    list = inscriptions;
                }
                else {
                    for(Inscription inscription : inscriptions) {
                        if(inscription.getColor().equals(color)) {
                            list.add(inscription);
                        }
                    }
                }
            }
            else {
                if(color.equals("all")) {
                    for(Inscription inscription : inscriptions) {
                        if(inscription.getType().equals(type)) {
                            list.add(inscription);
                        }
                    }
                }
                else {
                    for(Inscription inscription : inscriptions) {
                        if(inscription.getColor().equals(color) && inscription.getType().equals(type)) {
                            list.add(inscription);
                        }
                    }
                }
            }
        }
        else {
            if(type.equals("all")) {
                if(color.equals("all")) {
                    for(Inscription inscription : inscriptions) {
                        if(inscription.getLevel()==level) {
                            list.add(inscription);
                        }
                    }
                }
                else {
                    for(Inscription inscription : inscriptions) {
                        if(inscription.getLevel()==level && inscription.getColor().equals(color)) {
                            list.add(inscription);
                        }
                    }
                }
            }
            else {
                if(color.equals("all")) {
                    for(Inscription inscription : inscriptions) {
                        if(inscription.getLevel()==level && inscription.getType().equals(type)) {
                            list.add(inscription);
                        }
                    }
                }
                else {
                    for(Inscription inscription : inscriptions) {
                        if(inscription.getLevel()==level && inscription.getColor().equals(color) && inscription.getType().equals(type)) {
                            list.add(inscription);
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public ArrayList<String> getAllNames() {
        ArrayList<String> result = new ArrayList<String>();
        for(Inscription inscription:list) {
            result.add(inscription.getName());
        }
        return result;
    }

    public void updateWithName(List<Inscription> inscriptions, String name) {
        if(name.length() != 0) {
            list.clear();
            for(Inscription inscription:inscriptions) {
                if(inscription.getName().equals(name)) {
                    list.add(inscription);
                }
            }
            notifyDataSetChanged();
        }
        else {
            list.clear();
            list.addAll(inscriptions);
            notifyDataSetChanged();
        }
    }

    public List<Inscription> getList() {
        return list;
    }

    private class ViewHolder {
        public TextView name;
        public TextView level;
        public TextView pro1;
        public TextView pro2;
        public TextView pro3;
        public ImageView pic;
    }
}
