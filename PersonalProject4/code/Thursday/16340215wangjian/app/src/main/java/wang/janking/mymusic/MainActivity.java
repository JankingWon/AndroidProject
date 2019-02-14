package wang.janking.mymusic;

import android.Manifest;


import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.reactivestreams.Subscriber;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    //
    private IBinder mBinder;
    private final int PLAY_CODE = 1, STOP_CODE = 2, SEEK_CODE = 3, NEWMUSIC_CODE = 4, CURRENTDURATION_CODE = 5, TOTALDURATION = 6;
    private int total_duration = 0;
    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;
    ImageView imageView;
    SeekBar seekbar;
    ImageButton play_pause, stop, select, quit;
    TextView current_time, total_time, music_title, music_singer;
    boolean isPlay = false;
    boolean isStop = false;
    private SimpleDateFormat time = new SimpleDateFormat("mm:ss");
    private String time_format = "mm:ss";
    //RxJAVA变量
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
        @Override
        public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                while (true) {
                    //与服务通信
                    Parcel data = Parcel.obtain();
                    Parcel reply = Parcel.obtain();
                    try {
                        //获取信息
                        mBinder.transact(CURRENTDURATION_CODE, data, reply, 0);
                        //每隔一毫秒
                        Thread.sleep(1);

                    } catch (Exception exception){
                        Log.d("SERVICE CONNECTION", "onServiceConnected: " + exception.toString());
                    }
                    //读取当前进度
                    observableEmitter.onNext(reply.readInt());
                    //读取 isFinish
                    if(reply.readInt() == 1 || isStop)
                        break;
                }
                observableEmitter.onComplete();
        }
    });

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mBinder = service;
            //与服务通信
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                mBinder.transact(TOTALDURATION, data, reply, 0);
            }catch (Exception e){
                Log.d("SERVICE CONNECTION", "onServiceConnected: " + e.toString());
            }
            total_duration = reply.readInt();
            seekbar.setProgress(0);
            seekbar.setMax(total_duration);
            //两种方法实现毫秒转时间
            //total_time.setText(time.format(new Date(ms.mp.getDuration())));
            total_time.setText(DateFormat.format(time_format, total_duration));
            current_time.setText(time.format(new Date(0)));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    /*public Runnable  myThread = new Runnable() {
        @Override
        public void run() {
            Message msg = handler.obtainMessage();
            if(isStop){
                msg.what = -1;
                handler.sendMessage(msg);
                return;
            }
            try{
                //与服务通信
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                mBinder.transact(CURRENTDURATION_CODE, data, reply, 0);
                msg.arg1 = reply.readInt();
            }catch (Exception e){
                Log.d("Run", "run: " + e.toString());
                return;
            }
            handler.sendMessage(msg);
        }
    };
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) { // 根据消息类型进行操作
                case -1:
                    handler.removeCallbacks(myThread);
                    seekbar.setProgress(0);
                    current_time.setText(time.format(0));
                    imageView.setRotation(0);
                    break;
                default:
                    if(msg.arg1 >= total_duration)
                        stop.performClick();
                    seekbar.setProgress(msg.arg1);
                    current_time.setText(time.format(new Date(msg.arg1)));
                    imageView.setPivotX(imageView.getWidth()/2);
                    imageView.setPivotY(imageView.getHeight()/2);//支点在图片中心
                    imageView.setRotation(msg.arg1/30);
                    handler.postDelayed(myThread, 1);
            }
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
        play_pause = findViewById(R.id.play_pause);
        stop = findViewById(R.id.stop);
        select = findViewById(R.id.select_music);
        quit = findViewById(R.id.quit);
        seekbar = findViewById(R.id.seekbar);
        imageView = findViewById(R.id.profile_image);
        current_time = findViewById(R.id.current_time);
        total_time = findViewById(R.id.total_time);
        music_singer = findViewById(R.id.music_singer);
        music_title = findViewById(R.id.music_title);
        imageView.setPivotX(imageView.getWidth()/2);
        imageView.setPivotY(imageView.getHeight()/2);//支点在图片中心
        //设置一些UI
        setSomething();
    }
    void setSomething(){
        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //与服务通信
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try{
                    mBinder.transact(PLAY_CODE, data, reply, 0);
                }catch (RemoteException e){
                    Log.e("STOP:", "onClick: " + e.toString() );
                }
                if(isPlay){
                    isPlay = false;
                    play_pause.setImageResource(R.mipmap.play);
                }
                else {
                    isPlay = true;
                    isStop = false;
                    play_pause.setImageResource(R.mipmap.pause);
                    //开始监听
                    //myThread.run();
                    //订阅观察者
                    DisposableObserver<Integer> disposableObserver = new DisposableObserver<Integer>() {
                        @Override
                        public void onNext(Integer integer) {
                            Log.d("onNext", "" + integer);
                            seekbar.setProgress(integer);
                            current_time.setText(time.format(new Date(integer)));
                            imageView.setPivotX(imageView.getWidth()/2);
                            imageView.setPivotY(imageView.getHeight()/2);//支点在图片中心
                            imageView.setRotation(integer/30);
                        }

                        @Override
                        public void onComplete() {
                            stop.performClick();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("onError", "" + e.toString());
                        }
                    };
                    observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
                    mCompositeDisposable.add(disposableObserver);
                }

            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               isPlay = false;
               isStop = true;
               play_pause.setImageResource(R.mipmap.play);
                seekbar.setProgress(0);
                current_time.setText(time.format(0));
                imageView.setRotation(0);
                //与服务通信
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try{
                    mBinder.transact(STOP_CODE, data, reply, 0);
                }catch (RemoteException e){
                    Log.e("STOP:", "onClick: " + e.toString() );
                }

            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    imageView.setPivotX(imageView.getWidth()/2);
                    imageView.setPivotY(imageView.getHeight()/2);//支点在图片中心
                    imageView.setRotation(progress/30);
                    current_time.setText(time.format(progress));
                    //与服务通信
                    Parcel data = Parcel.obtain();
                    Parcel reply = Parcel.obtain();
                    data.writeInt(progress);
                    try{
                        mBinder.transact(SEEK_CODE, data, reply, 0);
                    }catch (RemoteException e){
                        Log.e("STOP:", "onClick: " + e.toString() );
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,1);
            }
        });

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        //结束进程
        //handler.removeCallbacks(myThread);
        mCompositeDisposable.clear();
        if(sc != null){
            unbindService(sc);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            Intent intent = new Intent(this, MusicService.class);
            bindService(intent, sc, BIND_AUTO_CREATE);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            try{
                //与服务通信
                Parcel send_data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                send_data.writeString(data.getData().toString());
                try{
                    mBinder.transact(NEWMUSIC_CODE, send_data, reply, 0);
                }catch (RemoteException e){
                    Log.e("STOP:", "onClick: " + e.toString() );
                }
                //设置信息
                total_duration = reply.readInt();
                seekbar.setProgress(0);
                seekbar.setMax(total_duration);
                total_time.setText(DateFormat.format(time_format, total_duration));
                current_time.setText(time.format(new Date(0)));
                //设置歌曲信息
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(MainActivity.this,data.getData());
                music_title.setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                music_singer.setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                byte[] picture = mmr.getEmbeddedPicture();
                if(picture.length!=0){
                    Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                    imageView.setImageBitmap(bitmap);
                }
                mmr.release();
                //模拟停止
                stop.performClick();
            }catch (Exception e){
                Log.d("Open file", "onActivityResult: " + e.toString());
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    //点击返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
