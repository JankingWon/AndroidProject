# 个人项目5
# WEB API

# 第十四周任务
## WEB API
---

### 第十四周实验目的
1. 学会使用HttpURLConnection请求访问Web服务
2. 学习Android线程机制，学会线程更新UI
3. 学会解析JSON数据
4. 学习CardView布局技术

---
### 实验内容
#### 实现一个bilibili的用户视频信息获取软件
<table>
    <tr>
        <td ><img src="/manual/images/img1.png" >打开程序主页面</td>
        <td ><img src="/manual/images/img2.png" >输入用户id，要求正整数int类型，不满足的弹Toast提示即可</td>
    </tr>
    <tr>
        <td ><img src="/manual/images/img3.png" >输入用户id，点击搜索，网络没打开则弹Toast提示网络连接失败</td>
        <td ><img src="/manual/images/img4.png" >网络打开情况下，输入用户id，不存在相应数据的弹Toast提示</td>
    </tr>
    <tr>
        <td ><img src="/manual/images/img5.png" >输入用户id = 2，点击搜索，展示图片/播放数/评论/时长/创建时间/标题/简介内容</td>
        <td ><img src="/manual/images/img6.png" >再次输入用户id = 7，接着上次结果继续展示以上内容</td>
    </tr>
</table>

* 搜索框只允许正整数int类型，不符合的需要弹Toast提示
*  当手机处于飞行模式或关闭wifi和移动数据的网络连接时，需要弹Toast提示
*  由于bilibili的API返回状态有很多，这次我们特别的限制在以下几点
    * 基础信息API接口为： `https://space.bilibili.com/ajax/top/showTop?mid=<user_id>`
    * 图片信息API接口为基础信息API返回的URL，cover字段
    * 只针对前40的用户id进行处理，即`user_id <= 40`
    * [2,7,10,19,20,24,32]都存在数据，需要正确显示
* **在图片加载出来前需要有一个加载条，不要求与加载进度同步**
* 布局和样式没有强制要求，只需要展示图片/播放数/评论/时长/创建时间/标题/简介的内容即可，可以自由发挥
* **布局需要使用到CardView和RecyclerView**
* 每个item最少使用2个CardView，布局怎样好看可以自由发挥，不发挥也行
* 不完成加分项的同学可以不显示SeekBar
* 输入框以及按钮需要一直处于顶部

---
### 验收内容
1. 图片/播放数/评论/时长/创建时间/标题/简介 显示是否齐全正确，
2. 是否存在加载条
3. Toast信息是否准确，特别地，针对用户网络连接状态和数据不存在情况的Toast要有区别
4. 多次搜索时是否正常
5. 代码+实验报告
6. 好看的界面会酌情加分，不要弄得像demo那么丑= =

---
### 加分项
<table>
    <tr>
        <td ><img src="/manual/images/img7.png" >拖动SeekBar，显示相应位置的预览图</td>
        <td ><img src="/manual/images/img8.png" >拖动SeekBar，显示相应位置的预览图</td>
    </tr>
</table>

* 拖动前后均显示原图片
* 模拟bilibili网页PC端，完成可拖动的预览功能
* 拖动seekBar，预览图会相应改变
* 前40的用户id中，32不存在预览图，可以忽略也可以跟demo一样将seekbar的enable设置为false
* 需要额外使用两个API接口，分别为
    * 利用之前API获得的信息，得到aid传入`https://api.bilibili.com/pvideo?aid=<aid>`
    * 利用`api.bilibili.com`得到的信息，解析image字段得到`"http://i3.hdslb.com/bfs/videoshot/3668745.jpg` 的图片
    * 分割该图片即可完成预览功能
* 加分项存在一定难度，需要不少额外编码，**可不做**。
* 32不存在预览图，可忽略或处理该异常情况


---

**demo测试于API28**

---
### 完成期限
第十五周各班实验课进行检查，未通过者需在下一周进行修改与重新检查。


# 第十五周任务
---

### 第十五周实验目的
1. 理解Restful接口
2. 学会使用Retrofit2
3. 复习使用RxJava
4. 学会使用OkHttp

---
### 基础实验内容
#### 实现一个github用户repos以及issues应用
<table>
    <tr>
        <td ><img src="/manual/images/img9.png" >主界面有两个跳转按钮分别对应两次作业</td>
        <td ><img src="/manual/images/img10.png" >github界面，输入用户名搜索该用户所有可提交issue的repo，每个item可点击</td>
    </tr>
    <tr>
        <td ><img src="/manual/images/img11.png" >repo详情界面，显示该repo所有的issues</td>
        <td ><img src="/manual/images/img12.png" >加分项：在该用户的该repo下增加一条issue，输入title和body即可</td>
    </tr>
</table>

* 教程位于`./manual/tutorial_retrofit.md`
* 每次点击搜索按钮都会清空上次搜索结果再进行新一轮的搜索
* 获取repos时需要处理以下异常：HTTP 404 以及 用户没有任何repo
* 只显示 has_issues = true 的repo（即fork的他人的项目不会显示）
* repo显示的样式自由发挥，显示的内容可以自由增加（不能减少）
* repo的item可以点击跳转至下一界面
* 该repo不存在任何issue时需要弹Toast提示
* 不完成加分项的同学只需要显示所有issues即可，样式自由发挥，内容可以增加

### 加分项
* 加分项旨在学习Retrofit2的POST请求，加深Rest API的理解
* demo需要你们额外提供TOKEN参数，这点不必实现，实现了反而不利于检查
* 提交的代码可以删除掉你的token等授权信息
* 仅处理提交成功与提交失败两种状态
* issue提交成功后需要刷新展示出来

* **加分项注意事项**
* **不要在他人repo下随意提交issue测试，自己新建一个repo即可**
* **管理好自己的github授权（token，client id等），验收结束后请删除对应授权**

---

**demo为app-retrofit.apk**
**demo测试于API28**
**过于频繁的请求会造成403 demo暂时失效的现象**

---
### 完成期限
第十六周各班实验课进行检查，未通过者需在下一周进行修改与重新检查。