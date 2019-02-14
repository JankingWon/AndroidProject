# 个人项目5
# WEB API应用

# 第十五周任务
## Rest + Retrofit2 + OkHttp + RxJava 应用
---
## Rest API
### 1.Rest介绍
具体知识参考理论课课件
本次实验我们需要访问GITHUB的restAPI接口。
个人推荐参考： [一篇文章搞定Github API](https://segmentfault.com/a/1190000015144126?utm_source=tag-newest)
不完成加分项的同学可以不考虑权限验证等问题。
但需要注意的是，短时间内的大量请求会造成Github返回403，TA推荐增加请求头解决这个问题，头为Authentication token。
本次我们需要用到以下几个接口：

|接口|方法|需要的返回字段|备注|
|--|--|--|--|
|/users/{用户名}/repos|GET|[{"id", "name","description"，“has_issues”,"open_issues"},{...}]|获得某用户所有repo的简介以及issues数目，非本人项目无法提交issue（比如fork的项目）需要过滤|
|/repos/{用户名}/{repo_name}/issues|GET|[{"title", "state", "created_at", "body"},{...}]|获取某人某项目的所有问题|
|/repos/{用户名}/{repo_name}/issues|POST|提交内容：{"title", "body"}|加分项使用，post一个新问题，需要权限验证|

## Retrofit2
### 1.Retrofit2介绍
课件上已经很详细的给出了教程。
个人推荐参考[Retrofit2详解](https://blog.csdn.net/carson_ho/article/details/73732076)，其基本涵盖了本次实验所需要的所有知识。
我在这再强调几个问题：
### 2.model类
我们最少需要两个model类，以Repo为例，另一个类为Issue
```java
Class Repo {
	String name;
	String description;
	Boolean has_issues;
	int open_issues;
}
```
### 3. Interface类
Interface为API访问接口
使用方法如下，在这提醒几点点
```java
// 不是Class 而是 interface
public interface GitHubService {
	@GET("/users/{user_name}/repos")
	// 这里的List<Repo>即为最终返回的类型，需要保持一致才可解析
	// 之所以使用一个List包裹是因为该接口返回的最外层是一个数组
	Call<List<Repo>> getRepo(@Path("user_name") String user_name);
	// 特别地，使用rxJava时为
	// Observable<List<RepoObj>> getRepo(@Path("user_name") String user_name);
}
```

### 4.构造Retrofit对象访问
根据课件构造即可。
本次实验不需要自定义GsonConverter解析器去获取各种异常返回状态，捕获不同异常弹Toast即可。
```java
Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseURL)

                        // 本次实验不需要自定义Gson
                        .addConverterFactory(GsonConverterFactory.create())
                        
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        
                        // build 即为okhttp声明的变量，下文会讲
                        .client(build)
                        
                        .build();
```

## OkHttp
主要目的是承载Retrofit的请求，本次实验只需要简单的设置超时时间等即可
```java
OkHttpClient build = new OkHttpClient.Builder()
                        .connectTimeout(2, TimeUnit.SECONDS)
                        .readTimeout(2, TimeUnit.SECONDS)
                        .writeTimeout(2, TimeUnit.SECONDS)
                        .build();
```

## RxJava
利用RxJava更新UI，这点我们在之前的几次作业中都有使用到,就不过多解释了

## 加分项
加分项的目的是考察POST请求的使用以及请求头部的设置方法。
做加分项的同学需要注意以下几点：
1. **GITHUB申请token时不需要给太多权限！**
2. 检查时token是写死的，这样方便TA检查。最终提交的代码可以不包含你的token，在实验结束后请务必在github上删除这个token，以防盗用。
3. **不要随便给别人的项目提交issue来测试程序！！！** 自己新建一个repo来测试相关接口
4. fork的项目无法提交issue，但只要过滤操作正确是不需要考虑这个问题的
