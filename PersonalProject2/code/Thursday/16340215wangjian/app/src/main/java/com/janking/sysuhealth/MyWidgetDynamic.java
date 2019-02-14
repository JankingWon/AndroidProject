package com.janking.sysuhealth;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

public class MyWidgetDynamic extends AppWidgetProvider {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        if(intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE")){//更新
            Bundle bundle = intent.getExtras();
            Food food = (Food)bundle.getSerializable("widget_favorite");
            //获取pendingIntent
            //这里继续使用Click_Food关键词，就不用再Detail里加一个判断语句了
            Intent mainIntent = new Intent(context, SecondActivity.class);
            mainIntent.putExtras(bundle);
            PendingIntent myPendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            RemoteViews updateView = new RemoteViews(context.getPackageName(), R.layout.my_widget);//实例化RemoteView,其对应相应的Widget布局
            updateView.setOnClickPendingIntent(R.id.widget_image, myPendingIntent); //设置点击事件
            updateView.setTextViewText(R.id.appwidget_text, "已收藏 " + food.getName());
            ComponentName cn = new ComponentName(context, MyWidget.class);
            appWidgetManager.updateAppWidget(cn, updateView);
            EventBus.getDefault().post(food);
        }else  if (intent.getAction().equals("com.janking.sysuhealth.myapplication2.MyDynamicFilter")) {    //动作检测
            Bundle bundle = intent.getExtras();
            Food food = (Food)bundle.getSerializable("broadcast_favorite");
            //Event bus
            EventBus.getDefault().post(food);
            //获取pendingIntent
            Intent mainIntent = new Intent(context, SecondActivity.class);
            mainIntent.putExtras(bundle);
            PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            //获取状态通知栏管理
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            //channel
            String CHANNEL_ID = "channel_02";
            NotificationChannel mChannel = null;
            //不用重复赋值
            if (mChannel == null) {
                String name = "my_channel_02";//渠道名字
                String description = "my_package_second_channel"; // 渠道解释说明
                //HIGH或者MAX才能弹出通知到屏幕上
                int importance = NotificationManager.IMPORTANCE_HIGH;
                mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                mChannel.setDescription(description);
                mChannel.enableLights(true); //是否在桌面icon右上角展示小红点
                manager.createNotificationChannel(mChannel);
            }
            //notification
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.full_star)
                    .setContentTitle("已收藏")
                    .setTicker("您有一条新消息")   //通知首次出现在通知栏，带上升动画效果的
                    .setContentText(food.getName())
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)   //设置这个标志当用户单击面板就可以让通知将自动取消
                    .setContentIntent(mainPendingIntent);
            //display
            Notification notification = mBuilder.build();
            manager.notify(0,notification);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            RemoteViews updateView = new RemoteViews(context.getPackageName(), R.layout.my_widget);//实例化RemoteView,其对应相应的Widget布局
            Intent i = new Intent(context, Detail.class);
            PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            updateView.setOnClickPendingIntent(R.id.widget_image, pi); //设置点击事件
            ComponentName me = new ComponentName(context, MyWidget.class);
            appWidgetManager.updateAppWidget(me, updateView);

        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created



    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}
