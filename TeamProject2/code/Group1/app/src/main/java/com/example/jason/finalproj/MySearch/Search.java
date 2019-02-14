package com.example.jason.finalproj.MySearch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.jason.finalproj.Article;
import com.example.jason.finalproj.MainInterface.AttentionAdapter;
import com.example.jason.finalproj.MyArticle.ArticleActivity;
import com.example.jason.finalproj.MyArticle.ArticleAdapter;
import com.example.jason.finalproj.DB;
import com.example.jason.finalproj.R;
import com.example.jason.finalproj.Users;
import com.example.jason.finalproj.Utils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Search extends AppCompatActivity {
    // 初始化变量
    private Toolbar toolbar;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private TextView textView;
    private RelativeLayout have_result,no_result;
    private AttentionAdapter attentionAdapter;


    private DB db = new DB(); //数据库操作实例
    private Connection con;

    /* 用户Id */
    private int user_id;
    Users users;
    Drawable drawable;

    /* 文章列表 */
    boolean article_set = false;
    private List<Integer> u_idList = new ArrayList<>();
    private List<Integer> a_idList = new ArrayList<>();
    private List<Article> articleList = new ArrayList<>();
    private List<Users> usersList = new ArrayList<>();
    private List<Map<String, Object>> article_datalist = new ArrayList<>();
    private List<Map<String, Bitmap>> article_imagelist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        textView =(TextView) findViewById(R.id.text_in_Search);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_in_Search);
        have_result = (RelativeLayout) findViewById(R.id.have_result);
        no_result = (RelativeLayout) findViewById(R.id.dh_result);
        handleRCV();
        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setToolbar();

        //绑定组件
        searchView = (SearchView) findViewById(R.id.search_view);

        //设置点击搜索按键后的操作（通过回调接口）
        // 参数 = 搜索框输入的内容
        searchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String info) {
                InputMethodManager imm = (InputMethodManager) Search.this.getSystemService(Context.INPUT_METHOD_SERVICE);//软键盘降下
                if (imm != null) {
                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                }
                Log.d("Search info", info);
                Toast.makeText(Search.this, info, Toast.LENGTH_SHORT).show();
                SearchInfo(info);
            }
        });
    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    if(!(articleList.isEmpty())) {
                        for(int i = 0 ; i < articleList.size(); i++) {
                            Log.d("Search Useres", usersList.get(i).getName());
                            Log.d("Search Articles", articleList.get(i).getBody());
                            a_idList.add(articleList.get(i).getId());
                            addArticleList(usersList.get(i), articleList.get(i));
                        }
                        article_set = true;
                        have_result.setVisibility(View.VISIBLE);
                        no_result.setVisibility(View.INVISIBLE);
                        textView.setText("与关键字相关的文章有");
                        attentionAdapter.notifyDataSetChanged();
                    }
                    else{
                        have_result.setVisibility(View.INVISIBLE);
                        no_result.setVisibility(View.VISIBLE);
                        if(!usersList.isEmpty()){
                            usersList.clear();
                            articleList.clear();
                        }
                        attentionAdapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public void SearchInfo(final String info) {
        articleList.clear();
        usersList.clear();
        a_idList.clear();
        article_datalist.clear();
        article_imagelist.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    con = db.getConnection();
                    Log.d("Search info", info);
                    a_idList = db.getA_idByTitleLike(info, con);
                    Log.d("Search a_idList Size", String.valueOf(a_idList.size()));
                    for (int i = 0; i < a_idList.size(); i++) {
                        articleList.add(db.getArticleById(a_idList.get(i), con));
                        u_idList.add(db.getU_idByA_id(a_idList.get(i), con ));
                        usersList.add(db.getUserById(u_idList.get(i), con));
                    }
                    handler.obtainMessage(100).sendToTarget();
                } catch (Exception e) {
                    Log.d("fail to search", e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
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
    //toolbar标题栏项目选择函数
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                db.closeConnection(con);
                finish();
                overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
                break;
            default:
                break;
        }
        return true;
    }
    public void handleRCV(){
        GridLayoutManager layoutManager = new GridLayoutManager(Search.this, 1);
        recyclerView.setLayoutManager(layoutManager);
        attentionAdapter = new AttentionAdapter(this, R.layout.attention_item, article_datalist, article_imagelist);
        recyclerView.setAdapter(attentionAdapter);
        attentionAdapter.setonItemClickListener(new AttentionAdapter.OnItemClickListener() {
            @Override
            public void OnClick(View view, int position) {
                Intent intent = new Intent(Search.this, ArticleActivity.class);
                intent.putExtra("a_id", a_idList.get(position));
                intent.putExtra("u_id", user_id);
                startActivityForResult(intent,1);
                Toast.makeText(Search.this, String.valueOf(article_datalist.get(position).get("title")) , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void LongClick(View view, int position) {

            }
        });
    }
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
        Bitmap bitmap_badge = Utils.IdToBitmap(this, R.drawable.badge);
        image_temp.put("badge", bitmap_badge);
        //状态
        if(article.getState() == 1) image_temp.put("state", Utils.IdToBitmap(this, R.drawable.recruting));
        else if(article.getState() == 0) image_temp.put("state", Utils.IdToBitmap(this, R.drawable.ending));
        article_imagelist.add(image_temp);
        Log.d("data", article_datalist.get(0).get("body").toString());
    }

}