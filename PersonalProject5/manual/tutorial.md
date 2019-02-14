
# 个人项目5
# WEB API应用

# 第十四周任务
## WEB API 应用
---
## 布局知识
### 1. CardView介绍
引入CardView依赖
```java
compile 'com.android.support:cardview-v7:28.+'
```
便可以如同使用正常属性一样使用CardView了
```xml
<android.support.v7.widget.CardView  
  app:cardCornerRadius="8dp"  
  android:layout_width="match_parent"  
  android:layout_height="wrap_content"  
  android:layout_margin="10dp"  
  app:contentPadding="5dp">
</android.support.v7.widget.CardView>
```

### 2. Progressbar介绍
最简单的设置即可，我们只需要更新其visibility属性即可，这里需要使用到上周的知识，子线程更新UI。
```xml
<!--设置progressbar的格式-->
style="?android:attr/progressBarStyleLarge"
<!--也可以使用其他格式-->
style="?android:attr/progressBarStyleInverse" 
style="?android:attr/progressBarStyleLargeInverse" 
style="?android:attr/progressBarStyleSmall" 
style="?android:attr/progressBarStyleSmallInverse" 
style="?android:attr/progressBarStyleSmallTitle" 
```

### 3. 网络图片使用

图片的更新方法参考课件，图片的url需要提前通过其他接口得到，需要明确以下几点
* 开新线程发送GET请求
* GET成功后利用handler传递消息
* 主线程更新UI，隐藏progressBar

## 网络请求知识
### 1. 学习使用HttpURLConnection
* 创建新线程，在线程中进行网络访问防止阻塞主线程
* 创建一个新的URL对象
* 为这个URL资源打开Connection
* 读取数据
* 访问网络时需要捕获异常

本次基础内容需要访问的接口是
`https://space.bilibili.com/ajax/top/showTop?mid=<user_id>`
返回的数据结构可以自己用postman或浏览器访问即可获得。


### 2. 线程更新UI
线程更新UI存在多种方法，最简单的是handler。
在这建议使用RxJava，具体使用方法在上次作业已经讲过了。

### 3.Json解析
这次得到的数据如下：
```json
 {
 	"status": true,
 	"data": {
 		"aid":30087657,
 		"state":0,
 		"cover":"……",
 		"title":"华农兄弟：这只竹鼠打架受内伤，农村小伙只能把它煮了，味道鲜嫩可口"
 		……
 	}
 }
```
首先我们需要声明一个类，这个类包含了我们需要显示的信息，比如
```java
public class RecyclerObj {
    private Boolean status;
    private Data data;
	public static class Data  {
		private int aid;
		……//省略get set等
	}
	……//省略get，set
}
```
之后直接使用这个解析即可。
```java
RecyclerObj recyclerObj = new Gson().fromJson((String)jsonString, RecyclerObj.class);
```
解析需要用到Gson，需要引入依赖
`compile 'com.squareup.retrofit2:converter-gson:2.1.0'`

### 4.加分项
本次加分项旨在提高网络访问以及对Android线程机制的认知。
需要额外进行两次网络请求以及更新UI的操作。
需要根据上述接口得到的aid向
`https://api.bilibili.com/pvideo?aid=<aid>`
请求数据，格式如下，我们需要根据里面的image或者简单点使用pvdata得到预览图的信息。
```json
{
    "code": 0,
    "message": "0",
    "ttl": 1,
    "data": {
        "pvdata": "http://i3.hdslb.com/bfs/videoshot/3648617.bin?vsign=aa201bcb43f2eec2b7d4cc3a56f63e0252e6fca7&ver=31532705",
        "img_x_len": 10,
        "img_y_len": 10,
        "img_x_size": 160,
        "img_y_size": 90,
        "image": [
            "http://i3.hdslb.com/bfs/videoshot/3648617.jpg?vsign=2a581a23de86cfa280b00948f6c16e814f5e1c13&ver=31532705"
        ],
	}
}
```
再向上述预览图请求数据即可，最后更新BitMap即可完成。