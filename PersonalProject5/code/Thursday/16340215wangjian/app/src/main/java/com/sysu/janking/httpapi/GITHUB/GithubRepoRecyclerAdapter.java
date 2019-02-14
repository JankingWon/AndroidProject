package com.sysu.janking.httpapi.GITHUB;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sysu.janking.httpapi.R;
import java.util.ArrayList;

public class GithubRepoRecyclerAdapter extends RecyclerView.Adapter<GithubRepoRecyclerAdapter.ViewHolder> {
    private ArrayList<GithubRepoObj> repos;
    private GithubRepoRecyclerAdapter.OnItemClickListener onItemClickListener;

    public GithubRepoRecyclerAdapter(){
        repos = new ArrayList<>();
    }
    @Override
    public GithubRepoRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.repo_recycler_item, parent, false);
        // 实例化viewholder
        GithubRepoRecyclerAdapter.ViewHolder viewHolder = new GithubRepoRecyclerAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final GithubRepoRecyclerAdapter.ViewHolder holder, final int position){
        // 绑定数据
        holder.repo_name.setText(repos.get(position).getName());
        //老生常谈！！！int转String
        holder.repo_issues_count.setText(String.valueOf(repos.get(position).getOpen_issues_count()));
        holder.repo_description.setText(repos.get(position).getDescription());
        holder.repo_id.setText(repos.get(position).getId());
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
    }

    public void addItem(GithubRepoObj githubRepoObj){
        if(repos != null){
            repos.add(githubRepoObj);
            notifyItemInserted(repos.size() - 1);
        }
    }

    @Override
    public int getItemCount() {
        return repos.size();
    }

    //清空RecyclerView列表
    public void reset(){
        int count = repos.size();
        repos.clear();
        notifyItemRangeRemoved(0,count);
    }

    public String getRepoName(int position){
        return repos.get(position).getName();
    }

    public void setOnItemClickListener(GithubRepoRecyclerAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView repo_name, repo_id, repo_description, repo_issues_count;
        public ViewHolder(View itemView) {
            super(itemView);
            repo_name = itemView.findViewById(R.id.repo_name);
            repo_id = itemView.findViewById(R.id.repo_id);
            repo_description = itemView.findViewById(R.id.repo_description);
            repo_issues_count = itemView.findViewById(R.id.repo_issues_count);
        }
    }
}
