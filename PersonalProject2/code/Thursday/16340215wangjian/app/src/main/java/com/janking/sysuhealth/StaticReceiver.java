package com.janking.sysuhealth;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.nio.charset.CharsetEncoder;

public class StaticReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.janking.sysuhealth.myapplication2.MyStaticFilter")){
            Bundle bundle = intent.getExtras();
            Food food = (Food)bundle.getSerializable("broadcast_startup");

            //获取pendingIntent
            //这里继续使用Click_Food关键词，就不用再Detail里加一个判断语句了
            bundle.putSerializable("Click_Food", food);
            Intent mainIntent = new Intent(context, Detail.class);
            mainIntent.putExtras(bundle);
            PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            //获取状态通知栏管理
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            //channel
            String CHANNEL_ID = "channel_01";
            NotificationChannel mChannel = null;
            if (mChannel == null) {
                String name = "my_channel_01";//渠道名字
                String description = "my_package_first_channel"; // 渠道解释说明
                //HIGH或者MAX才能弹出通知到屏幕上
                int importance = NotificationManager.IMPORTANCE_HIGH;
                mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                mChannel.setDescription(description);
                mChannel.enableLights(true); //是否在桌面icon右上角展示小红点
                manager.createNotificationChannel(mChannel);
            }
            //notification
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.empty_star)
                    .setContentTitle("今日推荐")
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

}
