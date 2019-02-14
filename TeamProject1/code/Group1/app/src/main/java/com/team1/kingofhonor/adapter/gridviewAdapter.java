package com.team1.kingofhonor.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.team1.kingofhonor.R;
import com.team1.kingofhonor.model.Equipment;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class gridviewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Equipment> list;//数据源
    private int mIndex;// 页数下标，标示第几页，从0开始
    private int mPagerSize;// 每页显示的最大的数量

    public gridviewAdapter(Context context, ArrayList<Equipment> list, int mIndex, int mPagerSize){
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
            view = View.inflate(context, R.layout.equipment_view, null);
            holder.equip_name = (TextView) view.findViewById(R.id.equip_name);
            holder.equip_photo = (ImageView) view.findViewById(R.id.imgUrl);
            holder.getEquip_price = (TextView) view.findViewById(R.id.equip_price);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        final int pos = i + mIndex*mPagerSize;
        holder.equip_photo.setBackgroundResource(list.get(pos).getImage());
        holder.equip_name.setText(list.get(pos).getName());
        holder.getEquip_price.setText(String.valueOf(list.get(pos).getPrice()));
        return view;
    }

    class ViewHolder{
        private TextView equip_name;
        private ImageView equip_photo;
        private TextView getEquip_price;
    }


    public void updateEquipment(ArrayList<Equipment> all, String categoryId){
        list.clear();
        if (categoryId.equals("全部")){
            list = all;
            notifyDataSetChanged();
        }
        for (Equipment i : all){
            if (i.getCategory().equals(categoryId) ){
                list.add(i);
            }
        }
        Log.e("bbbbb",String.valueOf(list.size()));
        notifyDataSetChanged();
    }

}
