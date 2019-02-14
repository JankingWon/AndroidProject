# 个人项目3
# 数据存储（二）应用开发

## 第十一周任务
## 数据存储（二）

### 基础知识

### 1.SQLite数据库使用

**使用SQLiteOpenHelper的子类能更方便实现要求**

1. 创建类
```java
    public class myDB extends SQLiteOpenHelper {
        private static final String DB_NAME= "db_name";
        private static final String TABLE_NAME = "table_name";
        private static final int int DB_VERSION = 1;
        ...
    }
```  
2. 创建数据库：可直接执行创建数据库的SQL语句
```java
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE if not exists "
            + TABLE_NAME
            + " (_id INTEGER PRIMARY KEY, name TEXT, password TEXT)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }
```
3.onUpgrade在本次实验不需要用到，但Android要求重写才能实例化。
```java
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int ii) {

    }
```

4.实现增加、更新和删除这3种操作有两种方法：  
不管是哪种方法，记得先getWritableDatabase()  
(a) 用execSQL方法直接执行相应的SQL语句，比如增加（如下）。  
```java
    SQLiteDatabase db = getWritableDatabase();
    String insert_sql = "INSERT INTO <table_name> (<column 1>, <column 2>, ···) values (<value 1>, <value 2>, ···)";
    db.execSQL(insert_sql);
```
(b) 使用相应的 insert、update 和 delete 方法

* insert 方法需要使用ContentValues来存放要添加的数据，如下。

```java
    public void insert2DB(String name, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("password", password);
        db.insert(TABLE_NAME, null, cv);
        db.close();
    }
```
* update方法需要使用ContentValues和Where语句。（下图只是说明性代码）

```java
    SQLiteDatabase db = getWritableDatabase();
    ContentValues cv = new ContentValues();
    cv.put("<column 1>", "<value 1>");
    cv.put("<column 2>", "<value 2>");
    //···
    String whereClause = "<Primary KEY> = ?";
    String[] whereArgs = {"<key_id>"};

    db.update(TABLE_NAME, cv, whereClause, whereArgs);

    //如果更新的主键列值只有一个，也可以这样写
    String whereClause11 = "<Primary KEY>=<key_id>";
    db.update(TABLE_NAME, cv, whereClause11, null);
    db.close();
```
* delete方法需要使用where语句。（以下只是说明性代码）  
```java
    SQLiteDatabase db = getWritableDatabase();

    String whereClause = "<Primary key>=?";
    String[] whereArgs={"<key_id 1>", "<key_id 1>",...};
    db.close();
```

5. 实现查询操作可以使用 rawQuery 或 query 函数，它们的区别类似于上面，前者直接执行SQL 语句，后者是通过参数组合产生 SQL 语句。（以下只是说明性代码）
```java
    db = getREadableDatabase();
    Cursor cursor = db.query(TABLE_NAME,
        new String[] {"<column 1>", "<column 2>",...},
        null, null, null, null, null);
    Cursor cursor2 = db.rawQuery("SELECT * FROM <TABLE_NAME>", null);
```
rawQuery或者query函数返回的都是Cursor，关于Cursor类的详细介绍请看[这里](http://www.cnblogs.com/TerryBlog/archive/2010/07/05/1771459.html)

### 2.Content Provider使用
1. 在AndroidManifest.xml文件里声明读取通讯录的权限
```xml
<uses-permission android:name="android.permission.READ_CONTACTS"/>
```
2. 使用getContentResolver方法查询相应用户名
```java
Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = \"" + username + "\"", null, null);
```
3. 判断cursor不为空，且查询到至少一个号码
4. 读取查询到的号码
```java
cursor.moveToFirst();
String number = "\nPhone: ";
do {
    number += cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)) + "         ";
} while (cursor.moveToNext());
```
### 3.ListView中的Item内部控件的点击事件
看[这里](https://blog.csdn.net/jzhowe/article/details/54767477)

### 4.读取手机图库
看[这里](https://www.2cto.com/kf/201311/260102.html)

### 5.在数据库中存储图片
看[这里](https://www.cnblogs.com/wxmdevelop/p/6180424.html)

### 参考工程目录结构
![preview](https://gitee.com/code_sysu/PersonalProject3/raw/master/manual/images2/catalog.png)

### 注意事项
1. 关于获取通讯录权限和获取存储权限，在实际操作中，有些手机（比如API19的）是在程勋运行的时候进行询问是否给予权限；有些手机（比如API23的），不会提示，需要你自己到设置界面下开启本应用程序的获取权限。所以，如果你在点击这些相关功能的按钮时闪退，先去看一眼权限有没有开吧。  
2. 在操作图片时，为了防止OOM，请先对图片进行缩小，在演示demo中，我将头像图片缩小为100*100的了。   


