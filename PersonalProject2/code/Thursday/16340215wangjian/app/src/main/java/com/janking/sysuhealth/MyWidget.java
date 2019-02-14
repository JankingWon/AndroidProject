package com.janking.sysuhealth;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

/**
 * Implementation of App Widget functionality.
 */
public class MyWidget extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        if(intent.getAction().equals("android.appwidget.action.APPWIDGET_STARTUP")){//初始化
            Bundle bundle = intent.getExtras();
            Food food = (Food)bundle.getSerializable("widget_startup");
            //获取pendingIntent
            //这里继续使用Click_Food关键词，就不用再Detail里加一个判断语句了
            bundle.putSerializable("Click_Food", food);
            Intent mainIntent = new Intent(context, Detail.class);
            mainIntent.putExtras(bundle);
            PendingIntent myPendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            RemoteViews updateView = new RemoteViews(context.getPackageName(), R.layout.my_widget);//实例化RemoteView,其对应相应的Widget布局
            updateView.setOnClickPendingIntent(R.id.widget_image, myPendingIntent); //设置点击事件
            updateView.setTextViewText(R.id.appwidget_text, "今日推荐 " + food.getName());
            ComponentName cn = new ComponentName(context, MyWidget.class);
            appWidgetManager.updateAppWidget(cn, updateView);
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
            Intent i = new Intent(context, SecondActivity.class);
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

