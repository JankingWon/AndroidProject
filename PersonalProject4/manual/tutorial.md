# 个人项目4
# 简单音乐播放器

# 第十二周任务
## 简单音乐播放器
---
### 基础知识
### 1. MediaPlayer介绍
常用方法：  

| 函数 | 功能 | 使用时机 |
| :------: | :------: | :------: |
| setDataSource(String) | 设置音频文件路径，进入初始化状态 | MediaPlayer对象已创建 |
| prepare() | 进入就绪状态 | 已初始化或停止 |
| start() | 进入播放状态 | 已就绪 |
| pause() | 进入暂停状态 | 正在播放 |
| stop() | 进入停止状态 | 正在播放或暂停 |
| isPlaying() | 检查是否正在播放 | 任意正常状态 |
| getCurrentPosition() | 获取当前已播放的毫秒数 | 已就绪 |
| getDuration() | 获取文件的时间长度（毫秒） | 已就绪 |
| release() | 停止播放并释放资源 | 任何时候 |

### 2. 向虚拟机添加文件
打开Android Device Monitor向虚拟机添加文件，demo的默认歌曲路径是：/data/山高水长.mp3
使用自己手机进行调试的同学，注意把文件拷到内置SD卡而不是外置SD卡会比较方便。要使用外置的SD卡时，注意下文件路径的获取。相关的路径获取方法看[这里](http://blog.sina.com.cn/s/blog_5da93c8f0102vcam.html)

### 3. 使用MediaPlayer
创建对象并初始化（注意文件的路径）：
```java
try {
	mediaPlayer.setDataSource(Environment.getExternalStorageDirectory() + "/data/山高水长.mp3");
	mediaPlayer.prepare();
	...
} catch (IOException e) {
	e.printStackTrace();
}
```
播放/暂停：
```java
if (mediaPlayer.isPlaying()) {
	mediaPlayer.pause();
} else {
	mediaPlayer.start();
}
```
停止：
```java
if (mediaPlayer != null) {
	mediaPlayer.stop();
	try {
		mediaPlayer.prepare();
		mediaPlayer.seekTo(0);
	} catch (Exception e) {
		e.printStackTrack();
	}
}
```

### 4. Service的使用
创建service类，实现MediaPlayer的功能
注意在AndroidManifest.xml文件中注册Service
```xml
<service android:name=".MusicService" android:exported="true"/>
```
通过Binder来保持Activity和Service的通信（写在service类）：
```java
public final IBinder binder = new MyBinder();
public class MyBinder extends Binder {
	@Override
        protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
            switch (code) {
                //service solve
                case PLAY_CODE:
                	...
                    break;
                case STOP_CODE:
                    ...
                    break;
                ...
            }
            return super.onTransact(code, data, reply, flags);
        }
}
```
在Activity中调用bindService保持与Service的通信（写在Activity类）：
Activity启动时绑定Service：
```java
Intent intent = new Intent(this, MusicService.class);
bindService(intent, sc, BIND_AUTO_CREATE);
```
bindService成功后回调onServiceConnected函数，通过IBinder获取Service对象，实现Activity与Service的绑定：
```java
private ServiceConnection sc = new ServiceConnection() {
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		mBinder = service;
	}
}
```
通过IBinder对象控制Service
```
Parcel data = Parcel.obtain();
Parcel reply = Parcel.obtain();
mBinder.transact(STOP_CODE, data, reply, 0);
```
停止服务时，必须解除绑定，写入退出按钮中
```java
mHandler.removeCallbacks(mRunnable);
unbindService(sc);
try {
	MainActivity.this.finish();
	System.exit(0);
} catch (Exception e) {
	e.printStackTrace();
}
```
此时，在Activity的onCreate方法中执行上述与Service通信的方法后，即可实现后台播放。点击退出按钮，程序会退出，音乐停止；按home键返回桌面，音乐继续播放。

### 5. Handler的使用
Handler与UI是同一线程，这里可以通过Handler更新UI上的组件状态，Handler有很多方法，这里使用比较简便的post和postDelayed方法。
使用Seekbar显示播放进度，设置当前值与最大值：
```java
seekBar.setProgress(ms.mp.getCurrentPosition());
seekBar.setMax(ms.mp.getDuration());
```
定义Handler，在run函数中进行更新seekbar的进度，
```java
Handler mHandler = new Handler();
Runnable mRunnable = new Runnable() {
	@Override
	public void run() {
		...
	}
}
```
在类中定义简单日期格式，用来显示播放时间，用time.format来格式所需要的数据，
```java
private SimpleDataFormat time = new SimpleDateFormat("mm:ss");
```
设置监听器来监听进度条的滑动变换：
```java
seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
	...
})
```
### 6. 参考文件目录
![preview](https://gitee.com/code_sysu/PersonalProject4/raw/master/manual/images/catalog.png)

### 7. CircleImageView
[参考](https://github.com/hdodenhof/CircleImageView)

### 8. 使用手机自带的资源管理器选歌
和上一次作业中使用图库选图大同小异
[参考](https://blog.csdn.net/qq_38552744/article/details/78713381)

### 9. 解析MP3文件中的歌曲信息
[参考](https://www.jianshu.com/p/e38178f008ab)

### 10. rxJava的使用
使用时注意rxJava1和rxJava2的差异，
在Observable对象中查询歌曲的播放时间，用onNext方法传递给Observer。Observer对象观察到Observable发送的播放时间后，完成UI的更新。
[参考](http://gank.io/post/560e15be2dca930e00da1083#toc_4)
[参考](https://www.jianshu.com/p/c935d0860186)