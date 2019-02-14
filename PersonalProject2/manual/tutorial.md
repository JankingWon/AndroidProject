# 个人项目2
# 中山大学智慧健康服务平台应用开发

---  

---
## 第七周任务
## Broadcast 使用
---
以下部分内容Android 8.0 以上不适用。
### 静态广播部分 

#### 1. 接收器  
在静态广播类StaticReceiver 中重写onReceive 方法，当接收到对应广播时进行数据处理，产生通知。
```java
public class StaticReceiver extends BroadcastReceiver {
    private static final String STATICACTION = "com.example.hasee.myapplication2.MyStaticFilter";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(STATICACTION)){
            Bundle bundle = intent.getExtras();            
              //TODO:添加Notification部分
        }
    }
}
```
#### 2. 注册
在AndroidMainfest.xml 中进行注册。
```java
        <receiver android:name=".StaticReceiver">
            <intent-filter>
                <action android:name="com.example.hasee.myapplication2.MyStaticFilter" />
            </intent-filter>
        </receiver>
```
#### 2. 发送广播
在主界面的create生命周期发送静态广播。
使用随机数：
```java
    Random random = new Random();
    random nextInt(n); //返回一个0到n-1的整数
```

利用bundle 和intent 将图片与文字内容发送出去
```java
    Intent intentBroadcast = new Intent(STATICACTION); //定义Intent
    intentBroadcast.putExtras(bundles);
    sendBroadcast(intentBroadcast);
```
参考代码中的STATICATION 为自己设定的广播名称。


---
### 动态广播部分 
点击收藏按钮时，注册并发送广播。
#### 1. 接收器
实现BroadcastReceiver子类（这里命名为DynamicReceiver），并且重写onReceive方法，修改方法与静态广播类中类似。
```java
public class DynamicReceiver extends BroadcastReceiver {
    private static final String DYNAMICACTION = "com.example.hasee.myapplication2.MyDynamicFilter";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DYNAMICACTION)) {    //动作检测
            Bundle bundle = intent.getExtras();             
            //TODO:添加Notification部分
        }    
    }
}
```

#### 2. 注册广播

```java
   IntentFilter dynamic_filter = new IntentFilter();
   dynamic_filter.addAction(DYNAMICACTION);    //添加动态广播的Action
   DynamicReceiver dynamicReceiver = new DynamicReceiver();
   registerReceiver(dynamicReceiver, dynamic_filter);    //注册自定义动态广播消息
```

#### 3. 注销广播
```java
   unregisterReceiver(dynamicReceiver);
```
其中dynamicReceiver 为我们之前创建的DynamicReceiver 类。用registerReceiver与unregisterReceiver 分别对其进行注册与注销。

#### 4. 发送广播
```java
   Intent intentBroadcast = new Intent();   //定义Intent
   intentBroadcast.setAction(DYNAMICACTION);
   intentBroadcast.putExtras(bundle);
   sendBroadcast(intentBroadcast);
```
注意在 Android 主界面中将 launchMode 设置为 singleInstance，使得点击Notification 后不会另外新建一个收藏列表。
```java
       <activity
            android:name=".MainActivity"
            android:launchMode="singleInstance">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
```
---
### Notification 的使用
Notification 可以提供持久的通知，位于手机最上层的状态通知栏中。用手指按下状态栏，并从手机上方向下滑动，就可以打开状态栏查看提示消息。开发Notification 主要涉及以下3个类：

#### 1. Notification.Builder
用于动态的设置Notification 的一些属性
```java
   Notification.Builder builder = new Notification.Builder(context);
   //对Builder进行配置，此处仅选取了几个
   builder.setContentTitle("动态广播")   //设置通知栏标题：发件人            
              .setContentText(bundle.getString("name"))   //设置通知栏显示内容：短信内容           
              .setTicker("您有一条新消息")   //通知首次出现在通知栏，带上升动画效果的
              .setSmallIcon(R.mipmap.dynamic)   //设置通知小ICON（通知栏），可以用以前的素材，例如空星
              .setContentIntent(contentIntent)   //传递内容
              .setAutoCancle(true);   //设置这个标志当用户单击面板就可以让通知将自动取消
```
点击notification，就可以跳转到我们intent 中指定的activity。主要使用到setContentIntent 与PendingIntent。

#### 2. NotificationManager
负责将Notification 在状态显示出来和取消
```java
   //获取状态通知栏管理
   NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
```

#### 3. Notification
设置Notification 的相关属性
```java
   //绑定Notification，发送通知请求
   Notification notify = builder.build();
   manager.notify(0,notify);
```

关于Notification，不同版本的API 显示可能会有所不同。
图片的使用方面请尽量使用mipmap 目录下的image asset。否则在某些API 中可能会出现Icon 过大的情况。

---
### Eventbus的使用  
Eventbus可以简化组件之间的沟通。  

#### 1. 添加依赖
File -> Project Structure -> Dependancies -> "+" -> Library dependency 
输入：
```java
   org.greenrobot:eventbus:3.0.0
```

#### 2. 声明一个事件类(传递食品信息)
```java
   public static class MessageEvent { /* Additional fields if needed */ }
```

#### 3. 准备订阅者
(1)声明并注释您的订阅方法，可选地指定线程模式(在收藏列表所在Activity声明这个方法)
```java
   @Subscribe(threadMode = ThreadMode.MAIN)  
   public void onMessageEvent(MessageEvent event) {/* Do something */};
```
(2)注册订阅者(注册收藏列表所在Activity为订阅者)
```java
   EventBus.getDefault().register(this); 
```
(3)注销订阅者(退出时要注销订阅者)
```java
   EventBus.getDefault().unregister(this); 
```

#### 4. 传递事件(点击收藏图标时候，传递食品信息)
```java
   EventBus.getDefault().post(new MessageEvent());
```


---

---

## 第八周任务
## Widget 使用
---
### Widget widget的添加
#### 1. 在 Android Studio 中创建Widget 类: File -> New -> Widget。
#### 2. 在界面空白处长按，在弹出的界面中点击widgets 选项。找到对应的widget 将其拖入桌面。对于不同的API版本显示会稍有不同。    
![preview](https://gitee.com/code_sysu/PersonalProject2/raw/master/manual/images/week8_tutorial1.PNG)    
![preview](https://gitee.com/code_sysu/PersonalProject2/raw/master/manual/images/week8_tutorial2.PNG)    
典型的Android Widget 有三个主要组件，一个边框、一个框架和图形控件以及其他元素。在 Android Studio 中创建Widget 类后，会直接生成相关文件。     
![preview](https://gitee.com/code_sysu/PersonalProject2/raw/master/manual/images/week8_tutorial3.PNG)


### Widget widget的布局文件

#### 1. new_app_widget.xml
布局中有一个ImageView，一个TextView。要求：文字颜色为白色，大小为15sp，整体背景为透明。最后效果如下：     
![preview](https://gitee.com/code_sysu/PersonalProject2/raw/master/manual/images/week8_tutorial4.PNG)

#### 1. new_app_widget_info.xml
编辑该文件，设置其大小属性和布局，如下：
```java
    <appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
            android:initialKeyguardLayout="@layout/new_app_widget"
            android:initialLayout="@layout/new_app_widget"
            android:minWidth="300dp"
            android:minHeight="50dp"
            android:previewImage="@mipmap/full_star"
            android:resizeMode="horizontal|vertical"
            android:updatePeriodMillis="86400000"
            android:widgetCategory="home_screen|keyguard"></appwidget-provider>
```
其中，minWidth 为最小宽度，minHeight 为最小高度，initialLayout 为初始布局。


### Java文件
#### 1. 修改NewAppWidget.java 代码，重写 onUpdate 方法，为 Widget 添加事件，使得点击能够启动应用。

这里需要使用到一种用户程序访问主屏幕和修改特定区域内容的方法：RemoteView 架构 。 RemoteView 架构允许用户程序更新主屏幕的View，点击Widget 激活点击事件， Android 会将其转发给用户程序，由AppWidgetProviders类处理，使得用户程序可更新主屏幕Widget。
```java
    RemoteViews updateView = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);//实例化RemoteView,其对应相应的Widget布局
    Intent i = new Intent(context, MainActivity.class);
    PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
    updateView.setOnClickPendingIntent(R.id.widget_image, pi); //设置点击事件
    ComponentName me = new ComponentName(context, NewAppWidget.class);
    appWidgetManager.updateAppWidget(me, updateView);
```

pendingIntent 是一种特殊的Intent。主要的区别在于Intent 的执行立刻的，而 pendingIntent 的执行不是立刻的。本次使用方法类的静态方法为 getActivity(Context,int, Intent, int)，对应Intent 的跳转到一个activity组件的操作。

#### 2. 静态广播 

(1) 注册    
在 AndroidMainfest.xml 中进行注册。
```java
    <receiver android:name=".NewAppWidget">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
                <action android:name="com.example.hasee.myapplication2.MyWidgetStaticFilter" />
            </intent-filter>

            <meta-data
                android:name="android.appwidget.provider"
                android:resource="@xml/new_app_widget_info" />
        </receiver>
```
(2) 接收器    
在NewAppWidget.java 文件的Widget 类中重写onReceive 方法，这里需要使用到RemoteView 以及Bundle。当接收到对应广播时进行数据处理。
```java
    @Override
    public void onReceive(Context context, Intent intent ){
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        Bundle bundle = intent.getExtras();
        if(intent.getAction().equals(WIDGETSTATICACTION)){
        }
    }
```
if条件语句中主要用到的函数为：setTextViewText、setImageViewResource。之后使用AppWidgetManager类对Widget进行更新。

(3) 发送广播      
使用随机数：
```java
    Random random = new Random();
    random nextInt(n); //返回一个0到n-1的整数
```
利用bundle 和intent 将图片与文字内容发送出去，建议在Restart生命周期发送该广播:
```java
    Intent widgetIntentBroadcast = new Intent();
    widgetIntentBroadcast.setAction(WIDGETSTATICACTION);
    widgetIntentBroadcast.putExtras(bundle);
    sendBroadcast(widgetIntentBroadcast);
```


#### 3. 动态广播     
点击收藏按钮时，注册并发送广播。  
(1) 注册
```java
    IntentFilter widget_dynamic_filter = new IntentFilter();
    widget_dynamic_filter.addAction(WIDGETDYNAMICACTION);
    DynamicReceiver widgetDynamicReceiver = new DynamicReceiver(); //添加动态广播的Action
    registerReceiver(widgetDynamicReceiver, widget_dynamic_filter); //注册自定义动态广播信息
```
(2) 接收器    
实现 BroadcastReceiver子类（这里命名为DynamicReceiver），并且重写onReceive方法，修改方法与静态广播类中类似。

```java
    public class DynamicReceiver extends BroadcastReceiver {
    private static final String WIDGETDYNAMICACTION = "com.example.hasee.myapplication2.MyWidgetDynamicFilter";  //动态广播的Action字符串

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WIDGETDYNAMICACTION)){
            Bundle bundle = intent.getExtras();
        }
    }
}
```
(3) 发送广播
```java
    Intent widgetIntentBroadcast = new Intent();   //定义Intent
    widgetIntentBroadcast.setAction(WIDGETDYNAMICACTION);
    widgetIntentBroadcast.putExtras(bundle);
    sendBroadcast(widgetIntentBroadcast);
```
(4) 注销广播
```java
    unregisterReceiver(widgetDynamicReceiver);
```




