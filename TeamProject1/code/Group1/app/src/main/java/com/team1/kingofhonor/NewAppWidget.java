package com.team1.kingofhonor;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RemoteViews;

import com.team1.kingofhonor.model.Equipment;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for(int m = -0; m < N; ++m){
            RemoteViews updateView = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);//实例化RemoteView,其对应相应的Widget布局
            Intent i = new Intent(context, MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(context, 200, i, PendingIntent.FLAG_UPDATE_CURRENT);
            updateView.setOnClickPendingIntent(R.id.shorticon, pi); //设置点击事件
            ComponentName me = new ComponentName(context, NewAppWidget.class);
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

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            Log.e("NewAppWidget", "call-static!");
            Equipment food = (Equipment) intent.getSerializableExtra("equipment");
            Log.e("NewAppWidget", food.getName());
            RemoteViews updateView = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);//实例化RemoteView,其对应相应的Widget布局
            updateView.setTextViewText(R.id.appwidget_text, "今日推荐 " + food.getName());
            Intent ListIntent = new Intent(context, equipment_detail.class);
            ListIntent.putExtra("equip", food);
            PendingIntent myPendingIntent = PendingIntent.getActivity(context, 0, ListIntent, 0);
            updateView.setOnClickPendingIntent(R.id.appwidget_text,myPendingIntent);
            ComponentName cn = new ComponentName(context, NewAppWidget.class);
            int[]appWidgetIds = appWidgetManager.getAppWidgetIds(cn);
            for(int appwidget : appWidgetIds){
                appWidgetManager.updateAppWidget(cn, updateView);
            }

    }
}

