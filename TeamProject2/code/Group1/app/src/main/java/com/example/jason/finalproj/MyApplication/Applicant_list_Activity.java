package com.example.jason.finalproj.MyApplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.jason.finalproj.MyArticle.ArticleActivity;
import com.example.jason.finalproj.DB;
import com.example.jason.finalproj.MyPersonalCenter.PersonalCenter;
import com.example.jason.finalproj.R;
import com.example.jason.finalproj.Users;

import java.sql.Connection;
import java.util.ArrayList;


public class Applicant_list_Activity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressBar progressBar1;
    RecyclerView apply_recyclerView;
    RC_adapter rc_adapter;
    ArrayList<Integer> allapply_id=new ArrayList<Integer>();
    ArrayList<Users> apply_list=new ArrayList<Users>();
    ArrayList<Users> apply_list_temp=new ArrayList<Users>();
    private Handler handler;
    int a_id;
    int u_id;
    int position_temp;
    boolean is_publisher;
    boolean is_delete=false;
    DB mydb=new DB();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_list_);
        init();
        setToolbar();
        getapply();
        even();
    }

    /**
     * 初始化
     */
    private void init(){
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        progressBar1=(ProgressBar)findViewById(R.id.progressBar1);
        progressBar1.setVisibility(View.VISIBLE);
        apply_recyclerView=(RecyclerView) findViewById(R.id.RCview);
        //动画
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, resId);
        apply_recyclerView.setLayoutAnimation(animation);
        rc_adapter = new RC_adapter(this);
        rc_adapter.setNewList(apply_list);
        apply_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        apply_recyclerView.setAdapter(rc_adapter);
    }
    /*
    设置顶部工具栏
     */
    private void setToolbar() {
        toolbar.setTitle("申请人列表");   //设置标题
        setSupportActionBar(toolbar);   //必须使用
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.previous);
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.gray));
        //添加左边图标点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 获取申请人列表
     */
    private void getapply(){
        Intent mintent=getIntent();
        a_id=mintent.getIntExtra("article_id",1);
        is_publisher=mintent.getBooleanExtra("is_publisher",false);
        u_id=mintent.getIntExtra("u_id",1);
        handler=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 123:
                        for(int i=0;i<apply_list_temp.size();i++)
                        {
                            rc_adapter.addiItem(apply_list_temp.get(i));
                        }
                        progressBar1.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        };
        final Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                Connection con=mydb.getConnection();
                allapply_id=mydb.getAllapplyById(a_id,con);
                Log.d("test", String.valueOf(allapply_id.size()));
                Log.d("test1", String.valueOf(a_id));
                for(int i=0;i<allapply_id.size();i++)
                {
                    apply_list_temp.add(mydb.getUserById(allapply_id.get(i),con));
                }
                handler.obtainMessage(123).sendToTarget();
                mydb.closeConnection(con);
            }
        });
        thread.start();
    }
    /**
     * 列表点击事件
     */
    private void even(){
        rc_adapter.setOnItemClickLitener(new RC_adapter.OnItemClickLitener(){
            @Override
            public void onItemClick(View view, int position) {
                Intent newIntent = new Intent(Applicant_list_Activity.this, PersonalCenter.class);
                newIntent.putExtra("u_id",
                        rc_adapter.getItem(position).getId());
                startActivity(newIntent);
                overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                if(is_publisher){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Applicant_list_Activity.this);
                    dialog.setMessage("从申请列表中移除"+apply_list.get(position).getName());
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final Context context = apply_recyclerView.getContext();
                            final LayoutAnimationController controller =
                                    AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);
                            apply_recyclerView.setLayoutAnimation(controller);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    DB db=new DB();
                                    Connection con=db.getConnection();
                                    db.cancleapply(a_id,u_id,con);
                                    db.closeConnection(con);
                                }
                            }).start();
                            rc_adapter.deleteItem(position);
                        }
                    });
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(Applicant_list_Activity.this, "你选择了： [取消]", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.show();
                }
                else {
                    AlertDialog.Builder mdialog = new AlertDialog.Builder(Applicant_list_Activity.this);
                    mdialog.setMessage("你不是发布人，无法删除申请人"+apply_list.get(position).getName());
                    mdialog.setCancelable(false);
                    mdialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    mdialog.show();
                }
            }
        });
    }
}
