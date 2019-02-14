package com.example.jason.finalproj.MyApplication;

import android.content.Intent;
import android.graphics.Bitmap;
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
import com.example.jason.finalproj.Users;
import com.example.jason.finalproj.Utils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Application extends AppCompatActivity {

    private Toolbar toolbar;
    private CardView cardView;
    private RecyclerView recyclerView;
    private ApplicationAdapter applicationAdapter;

    /* 数据库操作实例 */
    private DB db = new DB();

    /* 用户Id */
    private int user_id;

    /*  申请列表 */
    private List<Integer> applicationList = new ArrayList<>();
    private List<Article> articleList = new ArrayList<>();
    private List<Integer> a_userList = new ArrayList<>();
    private List<Users> usersList = new ArrayList<>();
    private List<Map<String, Object>> application_datalist = new ArrayList<>();
    private List<Map<String, Bitmap>> application_imagelist = new ArrayList<>();

    /* 申请是否查找完毕 */
    boolean applcattion_set = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_application);

        initial();
        setRecyclerView();
    }

    private void initial() {
        cardView = (CardView) findViewById(R.id.application_cardview);
        recyclerView = (RecyclerView) findViewById(R.id.application_recyclerview);
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
        applicationAdapter = new ApplicationAdapter(this, R.layout.application_item, application_datalist, application_imagelist);
        /* 设置适配器 */
        recyclerView.setAdapter(applicationAdapter);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);//网格布局，每行1列
        recyclerView.setLayoutManager(layoutManager);
        /* 单项点击监听器 */
        applicationAdapter.setonItemClickListener(new ApplicationAdapter.OnItemClickListener() {
            @Override
            public void OnClick(View view, int position) {

            }

            @Override
            public void LongClick(View view, int position) {

            }
        });

        /* 文章列表数据 */
        setApplication();
        Log.d("ApplicationDatalist", String.valueOf(application_datalist.size()));
        Log.d("ApplicationImagelist", String.valueOf(application_imagelist.size()));
    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100: //设置所有申请
                    for(int i = 0; i < articleList.size(); i++) {
                        Log.d("ApplicationUseres", usersList.get(i).getName());
                        Log.d("ApplicationArticles", articleList.get(i).getBody());
                        addApplicationList(usersList.get(i), articleList.get(i));
                    }
                    applcattion_set = true;
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 设置所有申请
     */
    public void setApplication() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection con = db.getConnection();
                    applicationList = db.getAllA_IdFromApplyByU_Id(user_id, con);
                    Log.d("ApplicationSize", String.valueOf(applicationList.size()));
                    for(int i = 0 ; i < applicationList.size(); i++) {
                        articleList.add(db.getArticleById(applicationList.get(i), con));
                        a_userList.add(db.getU_idByA_id(applicationList.get(i), con));
                        usersList.add(db.getUserById(a_userList.get(i), con));
                    }
                    Log.d("ArticleSize", String.valueOf(articleList.size()));
                    Log.d("A_userSize", String.valueOf(a_userList.size()));
                    Log.d("UsersSize", String.valueOf(usersList.size()));
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
                if (applcattion_set) {
                    applicationAdapter.notifyDataSetChanged();
                    if(application_datalist.size() == 0) cardView.setVisibility(View.VISIBLE);
                    else cardView.setVisibility(View.GONE);
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
    public void addApplicationList(Users users, Article article) {
        Map<String, Object> data_temp = new LinkedHashMap<>();
        //类型
        if(article.getType() == 1) data_temp.put("type", "室外");
        else if(article.getType() == 2) data_temp.put("type", "室内");
        else if(article.getType() == 3) data_temp.put("type", "个人");
        data_temp.put("name", users.getName());     //姓名
        data_temp.put("date", Utils.getStringByDate(article.getDate()));   //日期
        data_temp.put("title", article.getTitle());     //标题
        data_temp.put("body", article.getBody());       //正文
        application_datalist.add(data_temp); //将数据项加进文章数据列表中

        Map<String, Bitmap> image_temp = new LinkedHashMap<>();
        image_temp.put("photo", users.getPhoto());  //头像
        //徽章
        Bitmap bitmap_badge = Utils.IdToBitmap(this, R.drawable.badge);
        image_temp.put("badge", bitmap_badge);
        //申请状态 （应加入数据库）
        if(article.getState() == 2) image_temp.put("state", Utils.IdToBitmap(this, R.drawable.access));
        else if(article.getState() == 1) image_temp.put("state", Utils.IdToBitmap(this, R.drawable.auditing));
        else if(article.getState() == 0) image_temp.put("state", Utils.IdToBitmap(this, R.drawable.refuse));
        application_imagelist.add(image_temp);
    }
}
