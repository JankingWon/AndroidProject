package com.example.jason.finalproj.MyApplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jason.finalproj.R;
import com.example.jason.finalproj.Users;

import java.util.List;


public class RC_adapter extends RecyclerView.Adapter<RC_adapter.VH> {
    Context mContext;
    private List<Users> list_;

    public RC_adapter(Context context) {
        this.mContext = context;
        this.list_ = null;
    }

    public void addiItem(Users h) {
        list_.add(h);
        notifyDataSetChanged();
    }

    public void deleteItem(int posititon) {
        list_.remove(posititon);
        notifyDataSetChanged();
    }

    public void setNewList(List<Users> newlist) {
        list_ = newlist;
        notifyDataSetChanged();
    }

    public Users getItem(int position) {
        return list_.get(position);
    }

    public static class VH extends RecyclerView.ViewHolder {
        ImageView user_photo;
        TextView user_name;
        TextView weblv;
        TextView ASlv;
        TextView ioslv;
        TextView email;

        public VH(View itemView) {
            super(itemView);
            user_photo = (ImageView) itemView.findViewById(R.id.applicant_photo);
            user_name = (TextView) itemView.findViewById(R.id.applicant_name);
            weblv = (TextView) itemView.findViewById(R.id.web1);
            ASlv = (TextView) itemView.findViewById(R.id.AS1);
            ioslv = (TextView) itemView.findViewById(R.id.IOS1);
            email=(TextView) itemView.findViewById(R.id.email);
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        VH h = new VH(LayoutInflater
                .from(mContext)
                .inflate(R.layout.cardview_item, parent, false));
        return h;
    }

    /*创建Item的点击接口*/
    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public void onBindViewHolder(final VH holder, final int position) {
        final Users g = ((Users) list_.get(position));
        holder.user_photo.setImageBitmap(g.getPhoto());
        holder.user_name.setText( g.getName());
        Integer[] elem=g.getEmblems();
        holder.weblv.setText(String.valueOf(elem[0]));
        holder.ASlv.setText(String.valueOf(elem[1]));
        holder.ioslv.setText(String.valueOf(elem[2]));
        holder.email.setText( g.getEmail());

        if (mOnItemClickLitener != null) {//自定义触发事件
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list_.size();
    }
}
