package com.example.jason.finalproj.MainInterface;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jason.finalproj.Article;
import com.example.jason.finalproj.DB;
import com.example.jason.finalproj.MyApplication.Application;
import com.example.jason.finalproj.MyApplication.Application;
import com.example.jason.finalproj.MyArticle.ArticleActivity;
import com.example.jason.finalproj.MyArticle.ArticleAdapter;
import com.example.jason.finalproj.MyPersonalCenter.MyListAdapter;
import com.example.jason.finalproj.MyRecruit.Recruit;
import com.example.jason.finalproj.MyRecruit.Recruit;
import com.example.jason.finalproj.MySearch.Search;
import com.example.jason.finalproj.R;
import com.example.jason.finalproj.Users;
import com.example.jason.finalproj.Utils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Attention extends Fragment {

    /* 主活动实例 */
    private MainActivity mainActivity;

    private CardView cardView;

    private DB db = new DB(); //数据库操作实例
    private Connection con;

    /* 用户 */
    private Users users;
    private int user_id;

    /* 文章列表 */
    boolean article_set = false;
    RecyclerView recyclerView;
    AttentionAdapter attentionAdapter;
    private List<Integer> a_idList = new ArrayList<>();
    private List<Integer> u_idList = new ArrayList<>();
    private List<Article> articleList = new ArrayList<>();
    private List<Users> usersList = new ArrayList<>();
    private List<Map<String, Object>> article_datalist = new ArrayList<>();
    private List<Map<String, Bitmap>> article_imagelist = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attention, container, false);

        cardView = (CardView) view.findViewById(R.id.attention_cardview);
        recyclerView = (RecyclerView) view.findViewById(R.id.attention_recyclerview);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();

        //用户
        user_id = mainActivity.getUser_id();
        cardView.setVisibility(View.GONE);
        setRecyclerView();
        setAttentionArticle();
        Log.d("test", "onA");
    }

    public void setRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(mainActivity, 1);
        recyclerView.setLayoutManager(layoutManager);
        attentionAdapter = new AttentionAdapter(mainActivity, R.layout.attention_item, article_datalist, article_imagelist);
        recyclerView.setAdapter(attentionAdapter);
        attentionAdapter.setonItemClickListener(new AttentionAdapter.OnItemClickListener() {
            @Override
            public void OnClick(View view, int position) {
                Intent intent = new Intent(mainActivity, ArticleActivity.class);
                intent.putExtra("a_id", a_idList.get(position));
                intent.putExtra("u_id", user_id);
                startActivityForResult(intent,1);
                Toast.makeText(mainActivity, String.valueOf(article_datalist.get(position).get("title")) , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void LongClick(View view, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setTitle("是否删除？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // 删除数据
                                deleteAttention(user_id, a_idList.get(position));
                                articleList.remove(position);
                                usersList.remove(position);
                                u_idList.remove(position);
                                a_idList.remove(position);
                                article_datalist.remove(position);
                                article_imagelist.remove(position);
                                attentionAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(mainActivity, "取消", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create()
                        .show();
            }
        });
    }

    public void setAttentionArticle() {
        articleList.clear();
        usersList.clear();
        u_idList.clear();
        a_idList.clear();
        article_datalist.clear();
        article_imagelist.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    con = db.getConnection();
                    a_idList = db.geta_idFromAttentionByu_id(user_id, con);
                    Log.d("Attention a_idList Size", String.valueOf(a_idList.size()));
                    for (int i = 0; i < a_idList.size(); i++) {
                        articleList.add(db.getArticleById(a_idList.get(i), con));
                        u_idList.add(db.getU_idByA_id(a_idList.get(i), con ));
                        usersList.add(db.getUserById(u_idList.get(i), con));
                    }
                    handler.obtainMessage(100).sendToTarget();
                } catch (Exception e) {
                    Log.d("fail to show Attention", e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void deleteAttention(final int u_id, final int a_id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    con = db.getConnection();
                    boolean delete = db.deleteattention(u_id, a_id, con);
                } catch (Exception e) {
                    Log.d("delete Attention fail", e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100: //添加文章
                    if(!(articleList.isEmpty())) {
                        cardView.setVisibility(View.GONE);
                        for(int i = 0 ; i < articleList.size(); i++) {
                           // Log.d("Attention Useres", usersList.get(i).getName());
                           // Log.d("Attention Articles", articleList.get(i).getBody());
                            a_idList.add(articleList.get(i).getId());
                            addArticleList(usersList.get(i), articleList.get(i));
                        }
                        attentionAdapter.notifyDataSetChanged();
                    } else cardView.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 为文章列表添加数据
     */
    public void addArticleList(Users users, Article article) {
        Map<String, Object> data_temp = new LinkedHashMap<>();
        //类型
        if(article.getType() == 1) data_temp.put("type", "室外");
        else if(article.getType() == 2) data_temp.put("type", "室内");
        else if(article.getType() == 3) data_temp.put("type", "个人");
        data_temp.put("name", users.getName());     //姓名
        data_temp.put("date", Utils.getStringByDate(article.getDate()));   //日期
        data_temp.put("title", article.getTitle());     //标题
        data_temp.put("body", article.getBody());       //正文
        article_datalist.add(data_temp); //将数据项加进文章数据列表中

        Map<String, Bitmap> image_temp = new LinkedHashMap<>();
        image_temp.put("photo", users.getPhoto());  //头像
        //徽章
        Bitmap bitmap_badge = Utils.IdToBitmap(mainActivity, R.drawable.badge);
        image_temp.put("badge", bitmap_badge);
        //状态
        if(article.getState() == 1) image_temp.put("state", Utils.IdToBitmap(mainActivity, R.drawable.recruting));
        else if(article.getState() == 0) image_temp.put("state", Utils.IdToBitmap(mainActivity, R.drawable.ending));
        article_imagelist.add(image_temp);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == 2) {
            setAttentionArticle();
            mainActivity.getHome_fragment().update();
            Log.d("test", "onR");
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.closeConnection(con);
    }
}
