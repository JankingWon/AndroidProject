package com.janking.sysuhealth;

import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private static List<Food> mDatas;
    private MyAdapter.OnItemClickListener onItemClickListener;

    public MyAdapter(){
        initData();
    }

    public void initData()
    {
        mDatas = new ArrayList<Food>();
        mDatas.add(new Food("大豆", "粮食", "蛋白质", "#BB4C3B"));
        mDatas.add(new Food("十字花科蔬菜","蔬菜","维生素C", "#C48D30"));
        mDatas.add(new Food("牛奶","饮品","钙","#4469B0"));
        mDatas.add(new Food("海鱼","肉食","蛋白质", "#20A17B"));
        mDatas.add(new Food("菌菇类","蔬菜","微量元素", "#BB4C3B"));
        mDatas.add(new Food("番茄","蔬菜","番茄红素","#4469B0"));
        mDatas.add(new Food("胡萝卜","蔬菜","胡萝卜素", "#20A17B"));
        mDatas.add(new Food("荞麦","蔬菜","膳食纤维", "#BB4C3B"));
        mDatas.add(new Food("鸡蛋","杂","几乎包含所有营养物质", "#C48D30"));
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_single_recyclerview, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // 绑定数据
        holder.mTv.setText(mDatas.get(position).getName());
        holder.mIcon.setText(mDatas.get(position).getCategory().subSequence(0,1));
        //listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, pos);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemLongClick(holder.itemView, pos);
                }
                //表示此事件已经消费，不会触发单击事件
                return true;
            }
        });

    }

        @Override
        public int getItemCount()
        {
            return mDatas.size();
        }

        public void addNewItem(Food f) {
            if(mDatas == null) {
                mDatas = new ArrayList<>();
            }
            mDatas.add(0, f);
            notifyItemInserted(0);
        }

        public void deleteItem(int pos) {
            if(mDatas == null || mDatas.isEmpty()) {
                return;
            }
            mDatas.remove(pos);
            notifyItemRemoved(pos);
        }

        public Food getItem(int pos){
            if(mDatas == null || mDatas.isEmpty())
                return null;
            return mDatas.get(pos);
        }


    public boolean updateData(String name, Boolean favorite) {
        for(Food i : mDatas){
            if(i.getName().equals(name)){
                if(i.getFavorite().equals(favorite))
                    return false;
                i.setFavorite(favorite);
                return true;
            }
        }
        return false;

    }

    /**
     * 设置回调监听
     *
     * @param listener
     */
    public void setOnItemClickListener(MyAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTv, mIcon;
        public ViewHolder(View itemView) {
            super(itemView);
            mTv = (TextView) itemView.findViewById(R.id.rv_text);
            mIcon = (TextView)itemView.findViewById(R.id.rv_icon);
        }



    }
}
