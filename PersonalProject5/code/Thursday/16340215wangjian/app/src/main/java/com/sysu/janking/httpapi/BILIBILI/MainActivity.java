package com.sysu.janking.httpapi.BILIBILI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sysu.janking.httpapi.BuildConfig;
import com.sysu.janking.httpapi.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private String baseURL = "https://space.bilibili.com/ajax/top/";
    private EditText editText;
    private Button button;
    private RecyclerView recycler_view_list;
    private RecyclerAdapter recyclerAdapter;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private Observable<RecyclerObj> observable = Observable.create(new ObservableOnSubscribe<RecyclerObj>() {
        @Override
        public void subscribe(ObservableEmitter<RecyclerObj> observableEmitter) throws Exception {
            URL url = new URL(baseURL + "showTop?mid=" + editText.getText().toString());
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            //InputStream转String
            if(conn.getResponseCode() == 200){
                BufferedInputStream bis = new BufferedInputStream((InputStream)conn.getContent());
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int result = bis.read();
                while(result != -1) {
                    byteArrayOutputStream.write((byte) result);
                    result = bis.read();
                }
                observableEmitter.onNext(new Gson().fromJson(byteArrayOutputStream.toString(), RecyclerObj.class));
            }
            observableEmitter.onComplete();
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.search_button);
        editText = findViewById(R.id.search_id);
        //about RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler_view_list = findViewById(R.id.recycler_view_list);
        recyclerAdapter = new RecyclerAdapter(this);
        recycler_view_list.setAdapter(recyclerAdapter);
        recycler_view_list.setLayoutManager(mLayoutManager);
        //点击搜索事件
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(BuildConfig.DEBUG && Integer.parseInt(editText.getText().toString()) > 40)
                        throw new AssertionError();
                    getUser();
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, "输入格式错误!",Toast.LENGTH_SHORT).show();
                }catch (AssertionError assertionError){
                    Toast.makeText(MainActivity.this, "用户id不在前40!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    void getUser(){
        DisposableObserver<RecyclerObj> disposableObserver = new DisposableObserver<RecyclerObj>() {
            @Override
            public void onNext(RecyclerObj recyclerObj) {
                if(recyclerObj.getStatus()){
                    //添加到显示结果的列表中
                    recyclerAdapter.addItem(recyclerObj);
                }
                else//这句理论上一直不会执行
                    Toast.makeText(MainActivity.this, "数据库中不存在记录", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onComplete() {
                Toast.makeText(MainActivity.this, "搜索完成", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                if(e instanceof UnknownHostException)
                    Toast.makeText(MainActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                else if(e instanceof com.google.gson.JsonSyntaxException){
                    Toast.makeText(MainActivity.this, "数据库中不存在记录", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                }
                Log.d("onError", "onError: " + e.toString());
            }
        };
        //在新线程监听
        observable.subscribeOn(Schedulers.newThread())
                //在主线程更新
                .observeOn(AndroidSchedulers.mainThread())
                //绑定
                .subscribe(disposableObserver);
        //管理DisposableObserver的容器
        mCompositeDisposable.add(disposableObserver);
    }
}
