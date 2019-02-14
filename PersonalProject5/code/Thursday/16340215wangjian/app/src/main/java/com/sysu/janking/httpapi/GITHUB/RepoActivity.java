package com.sysu.janking.httpapi.GITHUB;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.sysu.janking.httpapi.R;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RepoActivity extends AppCompatActivity {
    private final String baseURL = "https://api.github.com";
    private GithubRepoRecyclerAdapter githubRecyclerAdapter;
    private RecyclerView recycler_view_list;
    private EditText editText;
    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo);
        //recyclerView的设置
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler_view_list = findViewById(R.id.second_recycler_view_list);
        githubRecyclerAdapter = new GithubRepoRecyclerAdapter();
        recycler_view_list.setAdapter(githubRecyclerAdapter);
        recycler_view_list.setLayoutManager(mLayoutManager);
        githubRecyclerAdapter.setOnItemClickListener(new GithubRepoRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(RepoActivity.this, IssuesActivity.class);
                intent.putExtra("username",username);
                intent.putExtra("repo",githubRecyclerAdapter.getRepoName(position));
                startActivity(intent);
            }
        });
        //搜索用户点击事件
        editText = findViewById(R.id.second_search_id);
        Button search = findViewById(R.id.second_search_button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = editText.getText().toString();
                //清空搜索框
                editText.setText("");
                getGithubRepos();
            }
        });
    }
    //获取用户所有仓库的方法
    public void getGithubRepos(){
        //先声明OkHttpClient，因为retrofit时基于okhttp的，在这可以设置一些超时参数等
        OkHttpClient build = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                // 设置json数据解析器
                .addConverterFactory(GsonConverterFactory.create())
                //!!!不是下面这样
                //.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                //这是RXJAVA2的版本
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(build)
                .build();
        DisposableObserver<List<GithubRepoObj>> disposableObserver = new DisposableObserver<List<GithubRepoObj>>() {
            @Override
            public void onNext(List<GithubRepoObj> githubRepoObjs) {
                //清空之前的搜索结果
                githubRecyclerAdapter.reset();

                //用户仓库为空
                if(githubRepoObjs.isEmpty()){
                    Toast.makeText(RepoActivity.this, "该用户不存在任何Repository", Toast.LENGTH_SHORT).show();
                    return;
                }
                //添加到显示结果的列表中
                for(GithubRepoObj g : githubRepoObjs){
                    //过滤掉fork他人的项目
                    if(g.isHas_issues())
                        githubRecyclerAdapter.addItem(g);
                }
            }
            @Override
            public void onComplete() {
                Toast.makeText(RepoActivity.this, "搜索完成", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                if(e instanceof UnknownHostException)
                    Toast.makeText(RepoActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                //404错误
                else if(e.toString().equals("retrofit2.adapter.rxjava2.HttpException: HTTP 404 Not Found")){
                    Toast.makeText(RepoActivity.this, "不存在该用户", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(RepoActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                }
                e.printStackTrace();
            }

        };
        GitHubService gitHubService = retrofit.create(GitHubService.class);
        gitHubService.getRepo(username)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                //相当重要，rxjava:2.2.4不是Subscriber而是DisposableObserver
                .subscribe(disposableObserver);
    }

}
