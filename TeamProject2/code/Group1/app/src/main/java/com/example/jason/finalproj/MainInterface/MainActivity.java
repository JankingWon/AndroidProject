package com.example.jason.finalproj.MainInterface;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jason.finalproj.Article;
import com.example.jason.finalproj.DB;
import com.example.jason.finalproj.Login.LoginActivity;
import com.example.jason.finalproj.MyApplication.Application;
import com.example.jason.finalproj.MyArticle.putout;
import com.example.jason.finalproj.MyRecruit.Recruit;
import com.example.jason.finalproj.MySearch.Search;
import com.example.jason.finalproj.R;
import com.example.jason.finalproj.Update.updatedata;
import com.example.jason.finalproj.Users;
import com.example.jason.finalproj.Utils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.System.exit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Home Home_fragment; //主页碎片
    private Attention Attention_fragment; //我碎片
    RelativeLayout home_button; //主页按钮
    RelativeLayout me_button; //我按钮

    private DB db = new DB(); //数据库操作实例
    private Connection con;

    /* 用户Id */
    private int user_id;
    Users users;
    Drawable drawable;

    /* 文章列表 */
    boolean article_set = false;
    private List<Integer> a_idList = new ArrayList<>();
    private List<Article> articleList = new ArrayList<>();
    private List<Users> usersList = new ArrayList<>();
    private List<Map<String, Object>> article_datalist = new ArrayList<>();
    private List<Map<String, Bitmap>> article_imagelist = new ArrayList<>();


    /* 滑动菜单 */
    DrawerLayout drawerLayout; //滑动布局
    NavigationView navigationView; //导航布局
    Toolbar toolbar;
    private CircleImageView nav_photo;
    private TextView nav_name;
    private TextView nav_sex;
    private TextView nav_mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //startActivity(new Intent(this, Main2Activity.class));

        initial();//私有成员初始化
        setDrawerLayout(); //设置滑动菜单
        setHome_fragment(); //添加并设置碎片

    }

    @Override
    public void onBackPressed() {
        SlidingUpPanelLayout slidingUpPanelLayout = Home_fragment.getSlidingUpPanelLayout();
        if (slidingUpPanelLayout != null &&
                (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    /**
     * 点击监听事件
     * 1.显示“主页”碎片
     * 2.显示“我”碎片
     * @param v
     */
    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideAllFragment(transaction);
        switch(v.getId()){
            case R.id.home_layout:
                //Toast.makeText(MainActivity.this,"test",Toast.LENGTH_SHORT).show();
                resetSelected();
                home_button.setSelected(true);
                if(Home_fragment == null){
                    Home_fragment = new Home();
                    transaction.add(R.id.fragment_layout, Home_fragment);
                }else{
                    transaction.show(Home_fragment);
                }
                break;
            case R.id.me_layout:
                resetSelected();
                me_button.setSelected(true);
                if(Attention_fragment == null){
                    Attention_fragment = new Attention();
                    transaction.add(R.id.fragment_layout, Attention_fragment);
                }else{
                    transaction.show(Attention_fragment);
                }
                break;
            default:break;
        }
        transaction.commit();
    }

    public Attention getAttention_fragment() {
        return Attention_fragment;
    }

    public Home getHome_fragment(){
        return Home_fragment;
    }

    public List<Map<String, Object>> getArticle_datalist() {
        return article_datalist;
    }

    public List<Map<String, Bitmap>> getArticle_imagelist() {
        return article_imagelist;
    }

    public List<Integer> getA_idList() {
        return a_idList;
    }

    public int getUser_id() {
        return user_id;
    }

    public boolean getArticle_set() {
        return  article_set;
    }

    /**
     * 初始化私有成员
     * 添加并显示“主页”碎片
     */
    private void initial() {
        //初始化私有成员
        home_button = (RelativeLayout) findViewById(R.id.home_layout);
        me_button = (RelativeLayout) findViewById(R.id.me_layout);
        home_button.setOnClickListener(this);
        me_button.setOnClickListener(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        View nav_header = navigationView.inflateHeaderView(R.layout.nav_header);
        nav_photo = (CircleImageView) nav_header.findViewById(R.id.nav_photo);
        nav_name = (TextView) nav_header.findViewById(R.id.nav_name);
        nav_sex = (TextView) nav_header.findViewById(R.id.nav_sex);
        nav_mail = (TextView) nav_header.findViewById(R.id.nav_mail);

        //用户id
        final SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHAREDNAME2,MODE_PRIVATE);
        user_id = sharedPreferences.getInt(LoginActivity.SHAREDNAME2,-1);

        setNavHeader(); //设置侧边栏个人信息
        setAllArticle(); //添加所有文章



        hideAllNotice(); //隐藏申请、招募、消息的红点提示
    }

    /**
     * 添加并显示主页碎片
     */
    public void setHome_fragment() {
        //不断给自身发消息处理，直到文章设置好之后才进入主页碎片
        final Handler mHandler = new Handler();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (article_set) {
                    article_set = false;
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    if(Attention_fragment == null) {
                        Attention_fragment = new Attention();
                        transaction.add(R.id.fragment_layout, Attention_fragment);
                    }
                    if(Home_fragment == null){
                        Log.d("test", "null");
                        Home_fragment = new Home();
                        transaction.add(R.id.fragment_layout, Home_fragment);

                    }else{
                        Log.d("test", "not null");
                        //transaction.show(Home_fragment);
                        transaction.remove(Home_fragment);
                        Home_fragment = new Home();
                        transaction.add(R.id.fragment_layout, Home_fragment);
                    }
                    transaction.commit();

                    home_button.setSelected(true); //主页按钮状态设为点击
                } else {
                    // 一直给自己发消息
                    mHandler.postDelayed(this, 10); //10ms
                }
            }
        });
    }

    /**
     * 重置按钮的所有选中状态
     */
    public void resetSelected(){
        home_button.setSelected(false);
        me_button.setSelected(false);
    }

    /**
     * 隐藏所有Fragment
     */
    public void hideAllFragment(FragmentTransaction transaction){
        if(Home_fragment != null){
            transaction.hide(Home_fragment);
        }
        if(Attention_fragment!=null){
            transaction.hide(Attention_fragment);
        }
        Log.d("test", "hide");
    }
    /**
     * 设置滑动菜单
     */
    public void setDrawerLayout() {
        /* 设置toolbar */
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null ){
            actionBar.setDisplayHomeAsUpEnabled(true);
            // 设置toolbar左侧的头像
            setToolbarPhoto();
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.nav_edit:
                        startActivityForResult(new Intent(MainActivity.this, updatedata.class), 1);
                        Toast.makeText(MainActivity.this, "编辑资料", Toast.LENGTH_SHORT).show();
                        article_set = false;

                        break;
                    case R.id.nav_apply:
                        Intent intent_apply = new Intent(MainActivity.this, Application.class);
                        intent_apply.putExtra("u_id", user_id);
                        startActivityForResult(intent_apply, 1);
                        Toast.makeText(MainActivity.this, "申请", Toast.LENGTH_LONG).show();
                        article_set = false;
                        break;
                    case R.id.nav_recruit:
                        Intent intent_recruit = new Intent(MainActivity.this, Recruit.class);
                        intent_recruit.putExtra("u_id", user_id);
                        startActivityForResult(intent_recruit, 1);
                        Toast.makeText(MainActivity.this, "招募", Toast.LENGTH_LONG).show();
                        article_set = false;
                        break;
                    case R.id.nav_badge:
                        Toast.makeText(MainActivity.this, "考核邮件已发到您的邮箱", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.exit:
                        db.closeConnection(con);
                        finish();
                        exit(0);
                        break;
                    default: break;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        item.setChecked(false);
                        item.setCheckable(true);
                    }
                }, 100);
                return true;
            }
        });
    }

    //创建toolbar项目menu
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_home, menu);
        return true;
    }
    /**
     * toolbar的item选择监听器
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.toolbar_search:
                startActivity(new Intent(MainActivity.this, Search.class));
                break;
            case R.id.toolbar_edit:
                startActivityForResult(new Intent(MainActivity.this, putout.class), 1);
                break;

            default:break;
        }

        return true;
    }



    @SuppressLint("HandlerLeak")
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100: //设置Tollbar左边的头像
                    if(users.getPhoto() != null) drawable = Utils.BitmapToDrawable(users.getPhoto());
                    else drawable = Utils.IdToDrawable(MainActivity.this, R.drawable.me);
                    ActionBar actionBar = getSupportActionBar();
                    drawable = Utils.setDrawableSize(drawable, Utils.dipTopx(MainActivity.this, 150), Utils.dipTopx(MainActivity.this, 150));
                    RoundCircleDrawable circledrawable = new RoundCircleDrawable(Utils.DrawableToBitmap(drawable));
                    circledrawable.setRound(Utils.dipTopx(MainActivity.this, 75));
                    actionBar.setHomeAsUpIndicator(circledrawable);
                    break;
                case 101: //设置滑动菜单Header的个人信息
                    if(users.getPhoto() != null) nav_photo.setImageBitmap(users.getPhoto());
                    else nav_photo.setImageResource(R.drawable.me);
                    nav_name.setText(users.getName());
                    if(users.getSex() == 1) nav_sex.setText("女");
                    else nav_sex.setText("男");
                    nav_mail.setText(users.getEmail());
                    break;
                case 102:  //添加所有文章
                    for(int i = 0 ; i < articleList.size(); i++) {
                        Log.d("Useres", usersList.get(i).getName());
                        Log.d("Articles", articleList.get(i).getBody());
                        a_idList.add(articleList.get(i).getId());
                        addArticleList(usersList.get(i), articleList.get(i));
                    }
                    article_set = true;
                    break;
                default:
                    break;
            }
        }
    };
    /**
     * 设置Toolbar左侧头像
     */
    public void setToolbarPhoto() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    con = db.getConnection();
                    users = db.getUserById(user_id, con);
                    handler.obtainMessage(100).sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 设置滑动菜单个人信息
     */
    public void setNavHeader() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    con = db.getConnection();
                    users = db.getUserById(user_id, con);
                    handler.obtainMessage(101).sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 添加所有文章
     */
    public void setAllArticle() {
        articleList.clear();
        usersList.clear();
        a_idList.clear();
        article_datalist.clear();
        article_imagelist.clear();
        article_set = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    con = db.getConnection();
                    articleList = db.getAllarticle(con);
                    Log.d("Size", String.valueOf(articleList.size()));
                    for(int i = 0 ; i < articleList.size(); i++) {
                        usersList.add(db.getUserById(articleList.get(i).getU_id(), con));
                    }
                    handler.obtainMessage(102).sendToTarget();

                } catch (Exception e) {
                    Log.d("Fail", e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

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
        Bitmap bitmap_badge = Utils.IdToBitmap(this, R.drawable.badge);
        image_temp.put("badge", bitmap_badge);
        //状态
        if(article.getState() == 1) image_temp.put("state", Utils.IdToBitmap(this, R.drawable.recruting));
        else if(article.getState() == 0) image_temp.put("state", Utils.IdToBitmap(this, R.drawable.ending));
        //收藏
        Bitmap bitmap_collection = Utils.IdToBitmap(this, R.drawable.collection);
        image_temp.put("collection", bitmap_collection);
        article_imagelist.add(image_temp);
        Log.d("data", article_datalist.get(0).get("body").toString());
    }

    public void hideAllNotice() {
        navigationView.getMenu().findItem(R.id.nav_apply).getActionView().setVisibility(View.GONE);
        navigationView.getMenu().findItem(R.id.nav_recruit).getActionView().setVisibility(View.GONE);
    }

    public void setNotice(int resource_id, int num) {
        LinearLayout gallery = (LinearLayout) navigationView.getMenu().findItem(resource_id).getActionView();
        TextView msg= (TextView) gallery.findViewById(resource_id);
        msg.setText(num);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        Log.d("requestCode", String.valueOf(requestCode));
        Log.d("resultCode", String.valueOf(resultCode));
        if(requestCode == 1 && resultCode == 2){
            setNavHeader();
            setToolbarPhoto();
            setAllArticle();
            setHome_fragment();
            //Home_fragment.update();
            Log.d("MainActivity", "更新主界面");
        }
    }
}