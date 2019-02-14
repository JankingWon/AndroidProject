package com.janking.sysuhealth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.Random;

public class Detail extends AppCompatActivity {
    private RelativeLayout top_layout;
    private TextView food_name, food_category, food_nutrition;
    private View top_view;
    private Boolean star_empty;
    //记录favorite属性改变次数
    private int change;
    private ImageButton food_if_favorite;
    private ImageButton collect;
    private Food display_food;
    private ImageButton back_bt;
    private OperationListViewAdapter mOperationAdapter;
    private ListView mListView;
    DynamicReceiver dynamicReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //注册广播
        IntentFilter dynamic_filter = new IntentFilter();
        dynamic_filter.addAction("com.janking.sysuhealth.myapplication2.MyDynamicFilter");    //添加动态广播的Action
        dynamic_filter.addAction("android.appwidget.action.APPWIDGET_UPDATE");
        dynamicReceiver = new DynamicReceiver();
        registerReceiver(dynamicReceiver, dynamic_filter);    //注册自定义动态广播消息

        //set 1/3
        DisplayMetrics dm = getResources().getDisplayMetrics();
        top_layout = findViewById(R.id.top_layout);
        ViewGroup.LayoutParams lp = top_layout.getLayoutParams();
        lp.height = dm.heightPixels / 3;
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        top_layout.setLayoutParams(lp); //设置

        //find view
        top_view = findViewById(R.id.top_view);
        food_name = findViewById(R.id.food_name);
        food_category = findViewById(R.id.category);
        food_nutrition = findViewById(R.id.nutrition);
        food_if_favorite = findViewById(R.id.food_if_favorite);
        collect = findViewById(R.id.collect);
        back_bt = findViewById(R.id.back_button);

        //获取显示的食物
        Intent intent = getIntent();
        display_food = (Food)intent.getSerializableExtra("Click_Food");
        change = 0;
        star_empty = true;

        if (display_food != null) {
            food_name.setText(display_food.getName());
            food_category.setText(display_food.getCategory());
            food_nutrition.setText("富含 " +display_food.getNutrition());
            top_view.setBackgroundColor(Color.parseColor(display_food.getColor()));
            back_bt.setBackgroundColor(Color.parseColor(display_food.getColor()));
            food_if_favorite.setBackgroundColor(Color.parseColor(display_food.getColor()));
        }

        food_if_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(star_empty){
                    food_if_favorite.setImageDrawable(getResources().getDrawable(R.mipmap.empty_star));
                    star_empty = false;
                }else{
                    food_if_favorite.setImageDrawable(getResources().getDrawable(R.mipmap.full_star));
                    star_empty = true;
                }
            }
        });

        collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!display_food.getFavorite()){
                    change++;
                    Toast.makeText(Detail.this, "已收藏", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(Detail.this, "重复收藏", Toast.LENGTH_SHORT).show();
                display_food.setFavorite(true);
                //发送广播
                Bundle bundle = new Bundle();
                bundle.putSerializable("broadcast_favorite", display_food);//返回一个0到n-1的整数
                Intent intentBroadcast = new Intent("com.janking.sysuhealth.myapplication2.MyDynamicFilter"); //定义Intent
                intentBroadcast.putExtras(bundle);
                sendBroadcast(intentBroadcast);

                Bundle bundleWidget = new Bundle();
                bundleWidget.putSerializable("widget_favorite", display_food);//返回一个0到n-1的整数
                Intent intentBroadcastWidget = new Intent("android.appwidget.action.APPWIDGET_UPDATE"); //定义Intent
                intentBroadcastWidget.putExtras(bundleWidget);
                sendBroadcast(intentBroadcastWidget);

            }
        });

        //listview create
        mListView = findViewById(R.id.operation_list);
        mOperationAdapter = new OperationListViewAdapter();
        mListView.setAdapter(mOperationAdapter);
        event();
    }

    private void event(){
        //0 表示没有更改，1表示更改过了
        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                Bundle bundle=new Bundle();
                if(change > 0){
                    bundle.putSerializable("display",display_food);
                    setResult(1, intent);
                    //Toast.makeText(Detail.this,"change:" + String.valueOf(change), Toast.LENGTH_SHORT).show();
                }
                else{
                    setResult(0, intent);
                }
                intent.putExtras(bundle);
                finish();//此处一定要调用finish()方法
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(dynamicReceiver);
    }


}
