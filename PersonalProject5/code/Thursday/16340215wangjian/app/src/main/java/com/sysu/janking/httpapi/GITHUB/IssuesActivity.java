package com.sysu.janking.httpapi.GITHUB;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.sysu.janking.httpapi.R;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class IssuesActivity extends AppCompatActivity {
    private final String baseURL = "https://api.github.com";
    private RecyclerView recycler_view_list;
    private GithubIssueRecyclerAdapter githubIssueRecyclerAdapter;
    private EditText issues_title, issues_body;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues);
        //recyclerView的设置
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler_view_list = findViewById(R.id.issues_recycler_view_list);
        githubIssueRecyclerAdapter = new GithubIssueRecyclerAdapter();
        recycler_view_list.setAdapter(githubIssueRecyclerAdapter);
        recycler_view_list.setLayoutManager(mLayoutManager);
        //提交ISSUE
        issues_title = findViewById(R.id.issues_title);
        issues_body = findViewById(R.id.issues_body);
        Button submit = findViewById(R.id.issues_commit_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postGithubIssue(issues_title.getText().toString(), issues_body.getText().toString());
            }
        });

        //获取ISSUES
        getGithubIssues(getIntent().getStringExtra("username"),
                getIntent().getStringExtra("repo"));

    }
    //获取用户某一个仓库所有问题的方法
    public void getGithubIssues(String username, String repo) {
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
        GitHubService gitHubService = retrofit.create(GitHubService.class);
            DisposableObserver<List<GithubIssueObj>> disposableObserver = new DisposableObserver<List<GithubIssueObj>>() {
                @Override
                public void onNext(List<GithubIssueObj> githubIssueObjs) {
                    //清除掉之前的列表
                    githubIssueRecyclerAdapter.reset();
                    if(githubIssueObjs.isEmpty()){
                        Toast.makeText(IssuesActivity.this, "该仓库不存在任何Issue", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //添加到显示结果的列表中
                    for(GithubIssueObj g : githubIssueObjs){
                        githubIssueRecyclerAdapter.addItem(g);
                    }
                }
                @Override
                public void onComplete() {
                    Toast.makeText(IssuesActivity.this, "搜索完成", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                }

            };
            gitHubService.getIssue(username, repo)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    //相当重要，rxjava:2.2.4不是Subscriber而是DisposableObserver
                    .subscribe(disposableObserver);


    }
    //提交issue的方法
    public void postGithubIssue(String title, String body){
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
        GitHubService gitHubService = retrofit.create(GitHubService.class);
        DisposableObserver<GithubPostIssueObj> disposableObserver = new DisposableObserver<GithubPostIssueObj>() {
            @Override
            public void onNext(GithubPostIssueObj response) {
                //重新显示ISSUES，更新界面
                getGithubIssues(getIntent().getStringExtra("username"),
                        getIntent().getStringExtra("repo"));
            }
            @Override
            public void onComplete() {
                Toast.makeText(IssuesActivity.this, "提交完成", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(IssuesActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        };
        gitHubService.postIssue(getIntent().getStringExtra("username"),
                getIntent().getStringExtra("repo"),new GithubIssueJsonObj(title, body))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                //相当重要，rxjava:2.2.4不是Subscriber而是DisposableObserver
                .subscribe(disposableObserver);
    }
}
