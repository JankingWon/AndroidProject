package com.example.jason.finalproj.MyRecruit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jason.finalproj.R;

import java.util.List;
import java.util.Map;

public class RecruitAdapter  extends RecyclerView.Adapter<RecruitAdapter.ViewHolder> {
    protected Context mContext;
    protected int mLayoutid;
    protected List<Map<String, Object>> mData;
    private RecruitAdapter.OnItemClickListener mOnItemClickListener = null;

    public RecruitAdapter (Context context, int layoutid, List<Map<String, Object>> datas ) {
        mContext = context;
        mLayoutid = layoutid;
        mData = datas;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mtype;      //类型（比赛、项目、个人）
        public TextView mdate;      //日期
        public TextView mtitle;     //标题
        public TextView mbody;      //正文
        public ImageView mstate;    //状态（招募中、已结束）
        View mView;     //布局视图

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mtype = (TextView) itemView.findViewById(R.id.recruit_item_type);
            mdate = (TextView) itemView.findViewById(R.id.recruit_item_date);
            mtitle = (TextView) itemView.findViewById(R.id.recruit_item_title);
            mbody = (TextView) itemView.findViewById(R.id.recruit_item_body);
            mstate = (ImageView) itemView.findViewById(R.id.recruit_item_state);
        }
        public static RecruitAdapter.ViewHolder get(Context context, ViewGroup parent, int layoutId) {
            View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
            RecruitAdapter.ViewHolder holder = new RecruitAdapter.ViewHolder(itemView);
            return holder;
        }
    }

    public interface OnItemClickListener {
        void OnClick(View view, int position);
        void LongClick(View view, int position);
    }
    public void setonItemClickListener (RecruitAdapter.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    @Override
    public RecruitAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int ViewType) {
        RecruitAdapter.ViewHolder viewHolder = RecruitAdapter.ViewHolder.get(mContext, parent, mLayoutid);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecruitAdapter.ViewHolder holder, int position) {
        //类型
        TextView type = holder.mtype;
        type.setText(mData.get(position).get("type").toString());
        //日期
        TextView date = holder.mdate;
        date.setText(mData.get(position).get("date").toString());
        //标题
        TextView title = holder.mtitle;
        title.setText(mData.get(position).get("title").toString());
        //正文
        TextView body = holder.mbody;
        body.setText(mData.get(position).get("body").toString());
        //状态
        ImageView state = holder.mstate;
        if(mData.get(position).get("state").toString().equals("1")) state.setImageResource(R.drawable.recruting);
        else state.setImageResource(R.drawable.ending);

        if(mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.OnClick(holder.itemView, holder.getLayoutPosition());
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick (View view) {
                    mOnItemClickListener.LongClick(holder.itemView, holder.getLayoutPosition());
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

}
