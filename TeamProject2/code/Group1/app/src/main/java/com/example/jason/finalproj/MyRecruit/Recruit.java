package com.example.jason.finalproj.MyRecruit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.jason.finalproj.Article;
import com.example.jason.finalproj.DB;
import com.example.jason.finalproj.R;
import com.example.jason.finalproj.Utils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Recruit extends AppCompatActivity {

    private Toolbar toolbar;
    private CardView cardView;
    private RecyclerView recyclerView;
    private RecruitAdapter recruitAdapter;

    /* 数据库操作实例 */
    private DB db = new DB();

    /* 用户Id */
    private int user_id;

    /*  申请列表 */
    private List<Integer> a_ids = new ArrayList<>();
    private List<Article> articleList = new ArrayList<>();
    private List<Map<String, Object>> recruit_datalist = new ArrayList<>();

    /* 申请是否查找完毕 */
    boolean recruit_set = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recruit);

        initial();
        setRecyclerView();
    }

    private void initial() {
        cardView = (CardView) findViewById(R.id.recruit_cardview);
        recyclerView = (RecyclerView) findViewById(R.id.recruit_recyclerview);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbar();
        cardView.setVisibility(View.GONE);

        Intent mintent=getIntent();
        user_id = mintent.getIntExtra("u_id",1);
    }

    //toolbar设置函数
    public void setToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationIcon(R.drawable.previous);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
    /**
     * toolbar的item选择监听器
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(2);
                finish();
                break;
            default:break;
        }
        return true;
    }

    public void setRecyclerView() {
        /* applicationAdapter适配器 */
        recruitAdapter = new RecruitAdapter(this, R.layout.recruit_item, recruit_datalist);
        /* 设置适配器 */
        recyclerView.setAdapter(recruitAdapter);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);//网格布局，每行1列
        recyclerView.setLayoutManager(layoutManager);
        /* 单项点击监听器 */
        recruitAdapter.setonItemClickListener(new RecruitAdapter.OnItemClickListener() {
            @Override
            public void OnClick(View view, int position) {

            }

            @Override
            public void LongClick(View view, int position) {

            }
        });

        /* 文章列表数据 */
        setRecruit();

    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100: //设置所有申请
                    for(int i = 0; i < articleList.size(); i++) {
                        Log.d("ApplicationArticles", articleList.get(i).getBody());
                        addRecruitList(articleList.get(i));
                    }
                    recruit_set = true;
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 设置所有申请
     */
    public void setRecruit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection con = db.getConnection();
                    a_ids = db.getA_idByU_id(user_id, con);
                    Log.d("a_idsSize", String.valueOf(a_ids.size()));
                    for (int i = 0; i < a_ids.size(); i++) {
                        articleList.add(db.getArticleById(a_ids.get(i), con));
                    }
                    Log.d("RecruitArticleSize", String.valueOf(articleList.size()));
                    handler.obtainMessage(100).sendToTarget();
                    db.closeConnection(con);
                } catch (Exception e) {
                    Log.d("Fail", e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
        //不断给自身发消息处理，直到所有申请设置好之后才更新UI
        final Handler mHandler = new Handler();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (recruit_set) {
                    recruitAdapter.notifyDataSetChanged();
                    if(recruit_datalist.size() == 0) cardView.setVisibility(View.VISIBLE);
                    else cardView.setVisibility(View.GONE);
                    Log.d("ApplicationDatalist", String.valueOf(recruit_datalist.size()));
                } else {
                    // 一直给自己发消息
                    mHandler.postDelayed(this, 10); //10ms
                }
            }
        });
    }

    /**
     * 为申请列表添加数据
     */
    public void addRecruitList(Article article) {
        Map<String, Object> data_temp = new LinkedHashMap<>();
        //类型
        if(article.getType() == 1) data_temp.put("type", "室外");
        else if(article.getType() == 2) data_temp.put("type", "室内");
        else if(article.getType() == 3) data_temp.put("type", "个人");
        data_temp.put("date", Utils.getStringByDate(article.getDate()));   //日期
        data_temp.put("title", article.getTitle());     //标题
        data_temp.put("body", article.getBody());       //正文
        data_temp.put("state", article.getState());     //状态

        recruit_datalist.add(data_temp); //将数据项加进文章数据列表中
    }

}
