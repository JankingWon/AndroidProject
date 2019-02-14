package com.team1.kingofhonor.adapter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.team1.kingofhonor.R;
import com.team1.kingofhonor.model.Hero;
import com.team1.kingofhonor.sqlite.HeroSQLiteHelper;

import java.util.ArrayList;
import java.util.List;


public class HeroAdapter extends RecyclerView.Adapter<HeroAdapter.ViewHolder> {

    private static List<Hero> mDatas;
    private HeroAdapter.OnItemClickListener onItemClickListener;

    public HeroAdapter(List<Hero> list) {
        mDatas = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.hero_list, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // 绑定数据
        //holder.hero_name.setText(mDatas.get(position).getName());
        holder.hero_image.setImageURI(Uri.parse(mDatas.get(position).getIcon()));
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

    public void addNewItem(Hero f) {
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

    public Hero getItem(int pos){
        if(mDatas == null || mDatas.isEmpty())
            return null;
        return mDatas.get(pos);
    }

    public Hero getItemByName(String name){
        if(mDatas == null || mDatas.isEmpty())
            return null;
        for(Hero h : mDatas){
            if(h.getName().equals(name))
                return h;
        }
        return null;
    }
    //修改某个数据，实现方法是删除再添加
    public void updateSingleHero(int pos, Hero hero){
        if(mDatas == null || mDatas.isEmpty())
            return;
        if(!mDatas.get(pos).getName().equals(hero.getName())){
            Log.d("Update Error", "updateSingleHero: "+ hero.getName());
            return;
        }
        mDatas.remove(pos);
        mDatas.add(0, hero);
        notifyItemRemoved(pos);
        notifyItemInserted(0);
        return;
    }

    //通过英雄职业改变显示的数据
    public void updateWithCategory(List<Hero> total, String category){
        mDatas.clear();
        if(category.equals("全部")){
            mDatas = total;
            notifyDataSetChanged();
        }
        for(Hero i: total){
            if(i.getCategory().equals(category))
                mDatas.add(i);
        }
        notifyDataSetChanged();
    }
    //返回所有的英雄数据
    public ArrayList<String> getAllNames(){
        ArrayList<String> data = new ArrayList<>();
        for(Hero temp : mDatas){
            data.add(temp.getName());
        }
        return data;
    }
    //返回所有已收藏HERO图片
    public String[] getAllFavoriteHeroes(HeroSQLiteHelper heroSQLiteHelper){
        ArrayList<String> favoriteHeroes = new ArrayList<>();
        for(Hero h : heroSQLiteHelper.getAllHeroes()){
            if(h.getFavorite())
                favoriteHeroes.add(h.getImage());
        }
        return favoriteHeroes.toArray(new String[favoriteHeroes.size()]);
    }


    /**
     * 设置回调监听
     *
     * @param listener
     */
    public void setOnItemClickListener(HeroAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView hero_name;
        ImageView hero_image;
        public ViewHolder(View itemView) {
            super(itemView);
            //hero_name = itemView.findViewById(R.id.hero_name);
            hero_image = itemView.findViewById(R.id.hero_image);
        }
    }
}
