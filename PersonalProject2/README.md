# 个人项目2
# 中山大学智慧健康服务平台应用开发

---  

---  

## 第七周任务  
## Broadcast 使用

---
  
### 实验目的
   1. 掌握 Broadcast 编程基础。  
   2. 掌握动态注册 Broadcast 和静态注册 Broadcast。
   3. 掌握Notification 编程基础。
   4. 掌握 EventBus 编程基础。
   
---

### 实验内容
在第六周任务的基础上，实现静态广播、动态广播两种改变Notification 内容的方法。  

#### 要求  
* 在启动应用时，会有通知产生，随机推荐一个食品。  
 ![preview](https://gitee.com/code_sysu/PersonalProject2/raw/master/manual/images/week7_static_notification.jpg)
* 点击通知跳转到所推荐食品的详情界面。  
 ![preview](https://gitee.com/code_sysu/PersonalProject2/raw/master/manual/images/week7_static_jump.jpg) 
* 点击收藏图标，会有对应通知产生，并通过Eventbus在收藏列表更新数据。  
 ![preview](https://gitee.com/code_sysu/PersonalProject2/raw/master/manual/images/week7_requirement3.jpg) 
* 点击通知返回收藏列表。  
 ![preview](https://gitee.com/code_sysu/PersonalProject2/raw/master/manual/images/week7_requirement4.jpg) 
* 实现方式要求:启动页面的通知由静态广播产生，点击收藏图标的通知由动态广播产生。   

 

---

### 验收内容
* 静态广播：启动应用是否有随机推荐食品的通知产生。点击通知是否正确跳转到所推荐食品的详情界面。
* 动态广播：点击收藏后是否有提示食品已加入收藏列表的通知产生。同时注意设置launchMode。点击通知是否跳转到收藏列表。
* Eventbus:点击收藏列表图标是否正确添加食品到收藏列表。每点击一次,添加对应的一个食品到收藏列表并产生一条通知。
* 代码+实验报告（都在实验课上检查，暂不需要pr）

---

### 完成期限
第八周各班实验课进行检查，未通过者需在下一周进行修改与重新检查，如再次未通过则扣除这一周任务的分数。

---  

---  

## 第八周任务  
## AppWidget 使用

---
  
### 实验目的
   1. 复习 Broadcast 编程基础。  
   2. 复习动态注册 Broadcast 和静态注册 Broadcast 。
   3. 掌握 AppWidget 编程基础。
   
---

### 实验内容
在第七周任务的基础上，实现静态广播、动态广播两种改变widget内容的方法。  

#### 要求 
* widget初始情况如下：      
![preview](https://gitee.com/code_sysu/PersonalProject2/raw/master/manual/images/week8_begin.PNG) 
* 点击widget可以启动应用，并在widget随机推荐一个食品。      
![preview](https://gitee.com/code_sysu/PersonalProject2/raw/master/manual/images/week8_recommendation.PNG)
* 点击widget跳转到所推荐食品的详情界面。     
![preview](https://gitee.com/code_sysu/PersonalProject2/raw/master/manual/images/week8_jump.PNG) 
* 点击收藏图标，widget相应更新。     
![preview](https://gitee.com/code_sysu/PersonalProject2/raw/master/manual/images/week8_update.PNG) 
* 点击widget跳转到收藏列表。     
![preview](https://gitee.com/code_sysu/PersonalProject2/raw/master/manual/images/week8_collection.PNG) 
* 实现方式要求:启动时的widget更新通过静态广播实现，点击收藏图标时的widget更新通过动态广播实现。 

 

---

### 验收内容
* 布局显示是否正常。
* 静态广播：启动应用Widget是否有随机推荐食品。
* 动态广播：点击收藏图标后，Widget是否提示食品已加入收藏列表。
* 点击widget是否正确跳到对应的界面。
* 代码+实验报告（先在实验课上检查，检查后再pr）

---

### 完成期限
第九周各班实验课进行检查，未通过者需在下一周进行修改与重新检查，如再次未通过则扣除这一周任务的分数。

---
---


### 个人项目提交方式
经这周码云张总监技术分享，在同学们对git基本操作掌握后，现确定作业提交步骤如下
1. 布置的个人项目先fork到个人仓库下；
2. clone自己仓库的个人项目到本地目录；
3. 在个人项目中，在code、report目录下，进入自己所在的班别，然后新建个人目录，目录名为“学号+姓名拼音”，例如“**12345678WangXiaoMing**”；
4. 在“code\班别\12345678WangXiaoMing”目录下，新建Android项目，按要求完成界面设计，代码编写等，注意.gitignore的编写，避免添加不必要的中间文件、临时文件到git中，如果在检查时发现提交了不必要的文件，会扣一定的分数；
5. 实验报告按给出的模版(manual中的report_template.md)的内容要求，以md的格式，写在“report\班别\12345678WangXiaoMing”目录下，结果截图也放在该目录下；
6. 完成任务需求后，Pull Request回主项目的master分支，PR标题为“班级+学号+姓名”， 如“**周三班12345678王小明**”；
7. 一定要在deadline前PR。因为批改后，PR将合并到主项目，所有同学都能看到合并的结果，所以此时是不允许再PR提交作业的。