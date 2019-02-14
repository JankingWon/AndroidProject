package com.example.myapplication;

import android.content.Context;
import android.icu.text.IDNA;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentAdapter extends BaseAdapter{
    private ArrayList<CommentInfo> data;
    private String current_user;
    private Context mContext = null;
    private UserSQLiteHelper userSQLiteHelper;


    public CommentAdapter(Context context, String current_user)
    {
        userSQLiteHelper = new UserSQLiteHelper(context);
        data= userSQLiteHelper.getAllComments();
        mContext = context;
        this.current_user= current_user;
    }

    @Override
    public int getCount()
    {
        int count = 0;
        if (data != null)
        {
            count = data.size();
        }
        return count;
    }

    @Override
    public CommentInfo getItem(int position)
    {
        CommentInfo item = null;

        if (null != data)
        {
            item = data.get(position);
        }

        return item;
    }

    public void removeItem(int position){

        userSQLiteHelper.removeComment(data.get(position));
        if(data!=null && !data.isEmpty()){
            data.remove(position);
            notifyDataSetChanged();
        }

    }

    public void addItem(CommentInfo commentInfo){
        if(data != null && commentInfo!= null){
            data.add(commentInfo);
            notifyDataSetChanged();
        }
        userSQLiteHelper.addComment(commentInfo);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = null;
        if (null == convertView)
        {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.comment_item, null);

            viewHolder.item_image= convertView.findViewById(R.id.item_image);
            viewHolder.item_username= convertView.findViewById(R.id.item_username);
            viewHolder.item_date= convertView.findViewById(R.id.item_date);
            viewHolder.item_comment = convertView.findViewById(R.id.item_comment);
            viewHolder.item_praise_count = convertView.findViewById(R.id.item_praise_count);
            viewHolder.praise_button = convertView.findViewById(R.id.item_praise_icon);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // set item values to the viewHolder:

        final CommentInfo commentInfo = getItem(position);
        if (null != commentInfo)
        {
            viewHolder.item_image.setImageURI(Uri.parse(commentInfo.getImage()));
            viewHolder.item_username.setText(commentInfo.getUsername());
            viewHolder.item_date.setText(commentInfo.getDate());
            viewHolder.item_comment.setText(commentInfo.getComment());
            viewHolder.item_praise_count.setText(String.valueOf(commentInfo.getPraise_count()));

            viewHolder.praise_button.setImageResource(R.mipmap.white);
            for(String user : commentInfo.getPraise_users()){
                if(user.equals(current_user)){
                    viewHolder.praise_button.setImageResource(R.mipmap.red);
                    break;
                }
            }
            viewHolder.praise_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageButton imageButton = v.findViewById(R.id.item_praise_icon);
                    if(commentInfo.praise_count_change(current_user)) {
                        imageButton.setImageResource(R.mipmap.red);
                    }else{
                        imageButton.setImageResource(R.mipmap.white);
                    }
                    notifyDataSetChanged();
                    userSQLiteHelper.updateComment(commentInfo.getDate(), commentInfo.getPraise_users());
                }
            });
        }

        return convertView;
    }

    private static class ViewHolder
    {
        ImageView item_image;
        TextView item_username, item_date, item_comment, item_praise_count;
        ImageButton praise_button;
    }

}
