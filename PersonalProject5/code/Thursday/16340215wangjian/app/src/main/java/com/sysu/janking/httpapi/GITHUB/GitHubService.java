package com.sysu.janking.httpapi.GITHUB;

import com.sysu.janking.httpapi.GITHUB.GithubIssueObj;
import com.sysu.janking.httpapi.GITHUB.GithubRepoObj;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

// 不是Class 而是 interface
public interface GitHubService {
    // 这里的List<Repo>即为最终返回的类型，需要保持一致才可解析
    // 之所以使用一个List包裹是因为该接口返回的最外层是一个数组
    @GET("/users/{user_name}/repos")
    Observable<List<GithubRepoObj>> getRepo(@Path("user_name") String user_name);

    @GET("/repos/{user_name}/{repo}/issues")
    Observable<List<GithubIssueObj>> getIssue(@Path("user_name") String user_name, @Path("repo") String repo);

    @Headers("Authorization: token a5d41a7e26ccf8501cb9ffc170fe9ab396f4472a")
    //@FormUrlEncoded
    @POST("/repos/{user_name}/{repo}/issues")
    Observable<GithubPostIssueObj> postIssue(@Path("user_name") String user_name,
                                             @Path("repo") String repo,
                                             @Body GithubIssueJsonObj githubIssueObj);
}