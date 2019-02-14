package com.example.jason.finalproj.MyArticle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jason.finalproj.MyApplication.Applicant_list_Activity;
import com.example.jason.finalproj.Article;
import com.example.jason.finalproj.DB;
import com.example.jason.finalproj.MyPersonalCenter.PersonalCenter;
import com.example.jason.finalproj.R;
import com.example.jason.finalproj.Update.updatemessage;

import java.sql.Connection;
import java.util.ArrayList;

public class ArticleActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ScrollView scrollView;
    private TextView title;
    private TextView publisher;
    private TextView content;
    private TextView type;
    private TextView time;
    private Handler handler;
    private ProgressBar progressBar;
    int u_id;
    int a_id;
    String u_name;
    ArrayList<Integer> alluer_id=new ArrayList<Integer>();
    DB mydb=new DB();
    Article mArticle=new Article();
    private Connection con;
    private Boolean isDirt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        init();
        getarticle();//更新UI
        setToolbar();//顶部toolbar
    }

    /**
     * 初始化
     */
    private void init(){
        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        scrollView=(ScrollView)findViewById(R.id.scroll);
        title=(TextView)findViewById(R.id.title1);
        publisher=(TextView)findViewById(R.id.publisher1);
        content=(TextView)findViewById(R.id.content1);
        type=(TextView)findViewById(R.id.type1);
        time=(TextView)findViewById(R.id.time1);
        scrollView.setVisibility(View.INVISIBLE);
        progressBar=(ProgressBar)findViewById(R.id.progressBar2);
        isDirt = false;
    }

    /**
     * 得到文章详情，并更新界面
     * */
    private void getarticle(){

        Intent mintent=getIntent();
        a_id=mintent.getIntExtra("a_id",1);
        u_id=mintent.getIntExtra("u_id",1);
         handler=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 123:
                        if(mArticle!=null){
                            Log.d("test", "mArticle not null");
                            title.setText(mArticle.getTitle());
                            publisher.setText(u_name);
                            content.setText(mArticle.getBody());
                            if(mArticle.getType()==1) type.setText("室外");
                            else if(mArticle.getType()==2)type.setText("室内");
                            else type.setText("个人招募");
                            String rag="";
                            if(mArticle.getEmblems()[0]==1){
                                rag+="web前端";
                            }
                            if(mArticle.getEmblems()[1]==1){
                                rag+=" 安卓";
                            }
                            if(mArticle.getEmblems()[2]==1){
                                rag+=" IOS";
                            }
                            time.setText(mArticle.getDate().toString());
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        scrollView.setVisibility(View.VISIBLE);
                        break;
                    case 124:

                        Toast.makeText(ArticleActivity.this,"你已成功申请该项目", Toast.LENGTH_SHORT).show();
                        break;
                    case 125:

                        Toast.makeText(ArticleActivity.this,"你已成功取消申请该项目", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        final Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                con=mydb.getConnection();
                mArticle=mydb.getArticleById(a_id,con);
                u_name=mydb.getUnameById(mArticle.getU_id(),con);
                Log.d("id", String.valueOf(a_id));
                handler.obtainMessage(123).sendToTarget();
            }
        });
        thread.start();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.toolbar, menu); //解析menu布局文件到menu
       return true;
    }

    /**
     * 设置顶部工具栏
     */
    private void setToolbar() {
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.gray));
        toolbar.setTitle("招募详情");   //设置标题
       setSupportActionBar(toolbar);   //必须使用
       ActionBar actionBar = getSupportActionBar();
       actionBar.setDisplayHomeAsUpEnabled(true);
       //添加左边图标点击事件
        toolbar.setNavigationIcon(R.drawable.previous);
       toolbar.setNavigationOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               mydb.closeConnection(con);
               if(isDirt) {
                   setResult(2);
               }
               else {
                   setResult(3);
               }
               Log.d("setresult", "2");
               finish();
           }
       });
       //添加menu项点击事件
       toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
           @Override
           public boolean onMenuItemClick(MenuItem item) {
               switch (item.getItemId()) {
                   case R.id.toolbar_r_img:
                       Log.e("Test---->","点击了右边图标");
                       break;
                   case R.id.toolbar_edit:
                       Log.e("Test---->","点击了编辑");
                       if(u_id==mArticle.getU_id()){
                           Intent newintent=new Intent(ArticleActivity.this,updatemessage.class);
                           newintent.putExtra("a_id",a_id);
                           startActivityForResult(newintent,1);
                           isDirt = true;
                       }
                       else Toast.makeText(ArticleActivity.this,"你没有权限编辑此文章", Toast.LENGTH_SHORT).show();
                       break;
                   case R.id.toolbar_add:
                       Log.e("Test---->","点击了申请");
                       if(u_id==mArticle.getU_id()){
                           Toast.makeText(ArticleActivity.this,"你是文章发布人，无需申请", Toast.LENGTH_SHORT).show();
                       }
                       else{
                           new Thread(new Runnable() {
                               @Override
                               public void run() {
                                   DB db=new DB();
                                   Connection con=db.getConnection();
                                   alluer_id=mydb.getAllapplyById(a_id,con);
                                   boolean flag=true;
                                   int code=124;
                                   for(int i=0;i<alluer_id.size();i++){
                                       if(u_id==alluer_id.get(i)){
                                           flag=false;
                                           break;
                                       }
                                   }
                                   Log.d("test", String.valueOf(flag));
                                   Log.d("test1", String.valueOf(a_id));
                                   Log.d("test2", String.valueOf(u_id));
                                   if(flag){
                                       db.apply(a_id,u_id,con);
                                       code=124;
                                   }
                                   else {
                                       db.cancleapply(a_id,u_id,con);
                                       code=125;
                                   }
                                   handler.obtainMessage(code).sendToTarget();
                                   db.closeConnection(con);
                               }
                           }).start();
                       }
                       break;
                   case R.id.toolbar_publisher:
                       Log.e("Test---->","点击了查看发布人");
                       Intent n_intent=new Intent(ArticleActivity.this,PersonalCenter.class);
                       n_intent.putExtra("u_id",mArticle.getU_id());
                       startActivity(n_intent);
                       overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
                       break;
                   case R.id.toolbar_applicant:
                       Boolean is_publisher=false;
                       if(u_id==mArticle.getU_id()){
                           is_publisher=true;
                       }
                       Intent intent=new Intent(ArticleActivity.this,Applicant_list_Activity.class);
                       intent.putExtra("article_id",a_id);
                       Log.d("test3", String.valueOf(a_id));
                       intent.putExtra("is_publisher",is_publisher);
                       intent.putExtra("u_id",u_id);
                       startActivity(intent);
                       overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
                       Log.e("Test---->","点击了查看申请列表");
                       break;
               }
               return true;
           }
       });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == 1 && resultCode == 2){
            getarticle();
        }
        else if(requestCode == 1 && resultCode == 3){
            setResult(2);
            finish();
        }
    }
}
