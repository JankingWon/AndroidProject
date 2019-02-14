package com.janking.sysuhealth;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.style.UpdateLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInRightAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FlipInTopXAnimator;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;


public class SecondActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ListView mListView;
    private MyListViewAdapter myListViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private MyAdapter mAdapter;
    private FloatingActionButton mButton;
    private boolean isHome;

    //EventBus
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Food f) {
        //change RecyclerView
        mAdapter.updateData(f.getName(), f.getFavorite());
        //change listview
        myListViewAdapter.addNewItem(f);
        //change floatbutton
        isHome = false;
        mRecyclerView.setVisibility(View.INVISIBLE);
        mListView.setVisibility(View.VISIBLE);
        mButton.setImageResource(R.mipmap.mainpage);

    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        //about FloatingActionButton
        isHome = true;
        //订阅
        EventBus.getDefault().register(this);
        //about RecyclerView
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new MyAdapter();
        mRecyclerView = (RecyclerView) findViewById(R.id.id_recyclerview);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //broadcast
        Bundle bundle = new Bundle();
        bundle.putSerializable("broadcast_startup", mAdapter.getItem(new Random().nextInt(mAdapter.getItemCount())));//返回一个0到n-1的整数
        Intent intentBroadcast = new Intent("com.janking.sysuhealth.myapplication2.MyStaticFilter"); //定义Intent
        intentBroadcast.setComponent(new ComponentName("com.janking.sysuhealth", "com.janking.sysuhealth.StaticReceiver"));
        intentBroadcast.putExtras(bundle);
        sendBroadcast(intentBroadcast);

        //animation general
        SlideInRightAnimationAdapter slideInRightAnimationAdapter = new SlideInRightAnimationAdapter(mAdapter);
        slideInRightAnimationAdapter.setFirstOnly(false);
        mRecyclerView.setAdapter(new SlideInBottomAnimationAdapter(slideInRightAnimationAdapter));

        //animation remove and add
        SlideInRightAnimator animator = new SlideInRightAnimator(new OvershootInterpolator(1f));
        mRecyclerView.setItemAnimator(animator);

        //Click events
        mAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //go to detail
                Intent intent=new Intent(SecondActivity.this, Detail.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("Click_Food", mAdapter.getItem(position));
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);//这里采用startActivityForResult来做跳转，此处的0为一个依据，可以写其他的值，但一定要>=0

            }
            @Override
            public void onItemLongClick(View view, int position) {
                //delete
                String deleteName = mAdapter.getItem(position).getName();
                //顺序很重要，先删除收藏夹中的，再删除总列表中的
                myListViewAdapter.deleteItemByName(deleteName);
                mAdapter.deleteItem(position);

                Toast.makeText(SecondActivity.this,"删除 " + deleteName, Toast.LENGTH_SHORT).show();
            }
        });

        //about ListView
        mListView = (ListView) findViewById(R.id.listView);
        myListViewAdapter = new MyListViewAdapter();
        mListView.setAdapter(myListViewAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0)
                    return;
                // 处理单击事件
                Intent intent=new Intent(SecondActivity.this, Detail.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("Click_Food", myListViewAdapter.getItem(i));
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);//这里采用startActivityForResult来做跳转，此处的0为一个依据，可以写其他的值，但一定要>=0
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view,final int i, long l) {
                if(i == 0)
                    return false;
                // 处理长按事件
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SecondActivity.this);
                alertDialog.setTitle("提示").setMessage("是否确定删除" + myListViewAdapter.getItem(i).getName() + "？").setPositiveButton("确认",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //先更新总列表中元素的数据，再删除
                                mAdapter.updateData(myListViewAdapter.getItem(i).getName(), false);
                                myListViewAdapter.deleteItem(i);
                            }
                        }).setNegativeButton("取消",null).create().show();
                return true;
            }
        });
        //do something
        event();
    }

    public void event(){
        /* 为FloatingActionButton设置点击事件 */
        mButton = (FloatingActionButton) findViewById(R.id.float_btn);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isHome){
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    mListView.setVisibility(View.VISIBLE);
                    mButton.setImageResource(R.mipmap.mainpage);
                    isHome = false;
                }else{
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.INVISIBLE);
                    mButton.setImageResource(R.mipmap.collect);
                    isHome = true;
                }
            }
        });

    }
    //get result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {//表示来自RecyclerView跳转的
            if (resultCode == 1) {//表示内容更改过
                Food f = (Food) data.getSerializableExtra("display");
                //change RecyclerView
                mAdapter.updateData(f.getName(), f.getFavorite());
                myListViewAdapter.addNewItem(f);

            }

        }
        isHome = true;
        mRecyclerView.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart(){
        super.onStart();
        //broadcast
        Bundle bundle = new Bundle();
        bundle.putSerializable("widget_startup", mAdapter.getItem(new Random().nextInt(mAdapter.getItemCount())));//返回一个0到n-1的整数
        Intent widgetIntentBroadcast = new Intent();
        widgetIntentBroadcast.setAction("android.appwidget.action.APPWIDGET_STARTUP");
        widgetIntentBroadcast.setComponent(new ComponentName("com.janking.sysuhealth", "com.janking.sysuhealth.MyWidget"));
        widgetIntentBroadcast.putExtras(bundle);
        sendBroadcast(widgetIntentBroadcast);

        //很关键，因为不知道为什么有时候会调用onCreate函数,然后一切都变了
        Food f = (Food)getIntent().getSerializableExtra("widget_favorite");
        if(f != null )
            EventBus.getDefault().post(f);
    }

}
