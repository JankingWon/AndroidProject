package com.sysu.janking.httpapi.GITHUB;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sysu.janking.httpapi.R;

import java.util.ArrayList;

public class GithubIssueRecyclerAdapter extends RecyclerView.Adapter<GithubIssueRecyclerAdapter.ViewHolder>{
    private ArrayList<GithubIssueObj> issues;

    public GithubIssueRecyclerAdapter(){
        issues = new ArrayList<>();
    }
    @Override
    public GithubIssueRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.issue_recycler_item, parent, false);
        // 实例化viewholder
        GithubIssueRecyclerAdapter.ViewHolder viewHolder = new GithubIssueRecyclerAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final GithubIssueRecyclerAdapter.ViewHolder holder, final int position){
        // 绑定数据
        holder.issue_body.setText(issues.get(position).getBody());
        holder.issue_create_date.setText(issues.get(position).getCreated_at());
        holder.issue_state.setText(issues.get(position).getState());
        holder.issue_title.setText(issues.get(position).getTitle());
    }

    public void addItem(GithubIssueObj githubIssueObj){
        if(issues != null){
            issues.add(githubIssueObj);
            notifyItemInserted(issues.size() - 1);
        }
    }
    //同样有个清空列表的操作
    public void reset(){
        int count = issues.size();
        issues.clear();
        notifyItemRangeRemoved(0,count);
    }

    @Override
    public int getItemCount() {
        return issues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView issue_title, issue_create_date, issue_body, issue_state;
        public ViewHolder(View itemView) {
            super(itemView);
            issue_title = itemView.findViewById(R.id.issue_title);
            issue_create_date = itemView.findViewById(R.id.issue_create_date);
            issue_body = itemView.findViewById(R.id.issue_body);
            issue_state = itemView.findViewById(R.id.issue_state);
        }
    }
}
