package com.example.jason.finalproj.MyPersonalCenter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jason.finalproj.DB;
import com.example.jason.finalproj.MyApplication.Application;
import com.example.jason.finalproj.MyRecruit.Recruit;
import com.example.jason.finalproj.R;
import com.example.jason.finalproj.Users;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;


/**
 * 个人中心
 * 1.编辑信息
 * 2.我的申请
 * 3.我的招募
 * 4.徽章考核
 */
public class     PersonalCenter extends AppCompatActivity {

    private Toolbar toolbar;
    /* 头像、名称 */
    private ImageView photo;
    private TextView name;
    private TextView sex;
    private TextView mail;


    private DB db = new DB(); //数据库操作实例

    /* 用户Id */
    private int user_id;
    Users users;
    Drawable drawable;

    /* 功能列表 */
    private ListView listView;
    /* 功能列表数组 */
    List<Integer> image_list;
    List<String> func_list;
    /* 功能列表适配器 */
    MyListAdapter myListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        initial();
        setListView();
        setHeader(); //设置Header中的个人信息
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
                finish();
                break;
            default:break;
        }
        return true;
    }

    /**
     * 私有变量初始化
     */
    private void initial() {
        /* 头像、名称 */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        photo = (ImageView) findViewById(R.id.center_photo);
        name = (TextView) findViewById(R.id.center_name);
        sex = (TextView) findViewById(R.id.center_sex);
        mail = (TextView) findViewById(R.id.center_mail);
        //setToolbar();

        /* 功能列表 */
        listView = (ListView) findViewById(R.id.center_list);

        Intent mintent=getIntent();
        user_id = mintent.getIntExtra("u_id",1);
    }

    /**
     * 设置功能列表ListView
     */
    public void setListView() {
        /* 设置图片列表 */
        image_list = new ArrayList<>();
        image_list.add(R.drawable.apply);
        image_list.add(R.drawable.recruit_me);
        /* 设置功能名称列表 */
        func_list = new ArrayList<>();
        func_list.add("TA的申请");
        func_list.add("TA的招募");
        /* 设置适配器 */
        myListAdapter = new MyListAdapter(this, R.layout.me_listitem, image_list, func_list);
        listView.setAdapter(myListAdapter);

        /* 菜单项点击监听事件 */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        Intent intent_apply = new Intent(PersonalCenter.this, Application.class);
                        intent_apply.putExtra("u_id", user_id);
                        startActivityForResult(intent_apply, 1);
                        Toast.makeText(PersonalCenter.this, "申请", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Intent intent_recruit = new Intent(PersonalCenter.this, Recruit.class);
                        intent_recruit.putExtra("u_id", user_id);
                        startActivityForResult(intent_recruit, 1);
                        Toast.makeText(PersonalCenter.this, "招募", Toast.LENGTH_LONG).show();
                        break;
                    default: break;

                }
            }
        });

    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100: //设置Toolbar左边的头像
                    if(users.getPhoto() != null) photo.setImageBitmap(users.getPhoto());
                    else photo.setImageResource(R.drawable.me);
                    name.setText(users.getName());
                    if(users.getSex() == 1) sex.setText("女");
                    else sex.setText("男");
                    mail.setText(users.getEmail());
                    break;
                default:
                    break;
            }
        }
    };

    public void setHeader() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection con = db.getConnection();
                    users = db.getUserById(user_id, con);
                    handler.obtainMessage(100).sendToTarget();
                    db.closeConnection(con);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
