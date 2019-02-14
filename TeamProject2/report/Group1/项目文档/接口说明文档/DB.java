package com.example.jason.finalproj;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DB {
    private static String ip = "202.182.103.117";
    private static int port = 3306;
    private static String dbName = "songxt";
    private static String url = "jdbc:mysql://" + ip + ":" + port + "/" + dbName;
    private static String user = "root";
    private static String psw = "981228";
    private Connection conn;
    private Bitmap initPhoto;
    private static final String DB_NAME = "Final.db";
    private static final String TABLE_USERS = "users";//用户表
    private static final String TABLE_EMBLEM = "emblems";//徽章表
    private static final String TABLE_ARTICLE = "article";//文章表
    private static final String TABLE_APPLY = "apply";
    private static final String TABLE_ATTENTION = "attention";

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public DB() {

    }

    /**
 * @api {GET} /connect
 * @apiGroup Database
 * @apiVersion 0.0.1
 * @apiDescription 获取数据库的连接
 * @apiSuccess (200) {Connection} conn
 */
    public Connection getConnection() {
        if (null == conn) {
            try {
                conn = DriverManager.getConnection(url, user, psw);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return conn;
    }

    /**
 * @api {POST} /register 注册用户
 * @apiGroup Users
 * @apiVersion 0.0.1
 * @apiDescription 往数据库里加入一个新的用户，这里的Bitmap没有做压缩处理,这里没做重名判断
 * @apiParam {Bitmap} image 用户头像 
 * @apiParam {String} name 用户名
 * @apiParam {int} sex 0 女 1 男
 * @apiParam {String} githubname github名
 * @apiParam {String} description 个人简介
 * @apiParam {String} email 邮箱
 * @apiParam {String} account 用户名
 * @apiParam {String} password 密码
 * @apiParam {Connection} conn 连接
 * @apiSuccess (201) {String} msg 信息
 * @apiSuccess (201) {boolean} code 1 代表无错误 0代表有错误
 * @apiSuccessExample {json} 返回样例:
 *                {"code":"0","msg":"注册成功"}
 */
    public boolean addUser(Bitmap image,String name,int sex,String Githubname,String description,String email,String account,String password,Connection conn) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] img = baos.toByteArray();
        try{
            String sql = "insert into " + TABLE_USERS + " set photo = ?,name=?,sex=?,Githubname=?,description=?,email=?,account=?,password=? ";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setBytes(1,img);
            pstmt.setString(2,name);
            pstmt.setInt(3,sex);
            pstmt.setString(4,Githubname);
            pstmt.setString(5,description);
            pstmt.setString(6,email);
            pstmt.setString(7,account);
            pstmt.setString(8,password);
            pstmt.executeUpdate();
            int id = getUidByAccount(account,conn);
            addEmblemById(id,conn);
            pstmt.close();
            return true;
        }catch (SQLException e){
            Log.v("w", "fail to connect!"+"  "+e.getMessage());
            return false;
        }
    }

/**
 * @api {GET} /check/account 检查用户名
 * @apiGroup Users
 * @apiVersion 0.0.1
 * @apiDescription 检查用户名是否已经注册
 * @apiParam {String} account 用户名
 * @apiParam {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {boolean} code 1 代表不存在 0代表存在
 * @apiSuccessExample {json} 返回样例:
 *                {"code":"0","msg":"已存在该用户名"}
 */

    public boolean isaccount(String account,Connection conn){
        try{
            Statement stmt = conn.createStatement();
            String sql = "select * from "+TABLE_USERS +" where account = '"+account+"'";
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                closeResources1(stmt,rs);
                return true;
            }
            closeResources1(stmt,rs);
        }catch (SQLException e){
            Log.v("w", "fail to connect!"+"  "+e.getMessage());
        }
        return false;
    }

/**
 * @api {GET} /check/username 检查昵称
 * @apiGroup Users
 * @apiVersion 0.0.1
 * @apiDescription 检查昵称是否已经注册
 * @apiParam {String} username 昵称
 * @apiParam {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {boolean} code 1 代表不存在 0代表存在
 * @apiSuccessExample {json} 返回样例:
 *                {"code":"0","msg":"已存在该昵称"}
 */

    public boolean search(String username,Connection conn){
        try{
            Statement stmt = conn.createStatement();
            String sql = "select * from "+TABLE_USERS +" where name = '"+username+"'";
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                closeResources1(stmt,rs);
                return true;
            }
            closeResources1(stmt,rs);
        }catch (SQLException e){
            Log.v("w", "fail to connect!"+"  "+e.getMessage());
        }
        return false;
    }

/**
 * @api {GET} /check/password 检查用户密码
 * @apiGroup Users
 * @apiVersion 0.0.1
 * @apiDescription 检查密码是否正确从而完成登录
 * @apiParam {String} account 用户名
 * @apiParam {String} password 密码
 * @apiParam {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {boolean} code 1 代表正确 0代表错误
 * @apiSuccessExample {json} 返回样例:
 *                {"code":"0","msg":"密码错误"}
 */
    public boolean confirmPassword(String account,String password,Connection conn){
        try{
            Statement stmt = conn.createStatement();
            String sql = "select password from "+TABLE_USERS +" where account = '"+account+"'";
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                String pwd = rs.getString("password");
                closeResources1(stmt,rs);
                return pwd.equals(password)?true:false;
            }
            closeResources1(stmt,rs);
        }catch (SQLException e){
            Log.v("w", "fail to connect!"+"  "+e.getMessage());
        }
        return false;
    }

    /**
 * @api {GET} /get/user 获取用户
 * @apiGroup Users
 * @apiVersion 0.0.1
 * @apiDescription 使用usename查询用户
 * @apiParam {String} username 昵称
 * @apiParam {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {Users} user 用户所有信息
 */

    public Users getUserByName(String username, Connection conn){
        Users user = new Users(initPhoto);
        try {
            Statement stmt = conn.createStatement();
            String sql = "select * from " + TABLE_USERS +" WHERE name ='"+username+"'";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("u_id");
                Integer[] emblem_temp = {0, 0, 0};
                byte[] photo_temp = blobToBytes(rs.getBlob("photo"));
                Bitmap photo = BitmapFactory.decodeByteArray(photo_temp, 0, photo_temp.length);
                String name = rs.getString("name");
                int sex = rs.getInt("sex");
                String Githubname = rs.getString("Githubname");
                String description = rs.getString("description");
                String email = rs.getString("email");
                int best_jump = rs.getInt("best_jump");
                user = new Users(id, photo, name, sex, emblem_temp, Githubname, description, email, best_jump);
                Log.i("test", "查询成功");
            }
            closeResources1(stmt, rs);
        } catch (SQLException e) {
            Log.v("w", "fail to connect!"+"  "+e.getMessage());
        }
        return user;
    }

/**
 * @api {GET} /get/account 获取用户名
 * @apiGroup Users
 * @apiVersion 0.0.1
 * @apiDescription 根据id获取用户信息
 * @apiParam {int} id 用户id
 * @apiParam {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {String} account 用户名
 */
    public String getAccountByUid(int id, Connection conn) {
        String account = "";
        try {
            Statement stmt = conn.createStatement();
            String sql = "select * from " + TABLE_USERS +" where u_id = "+ id;
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                account = rs.getString("account");
                Log.i("test--getUserById", "查询成功");
            }
            closeResources1(stmt, rs);
        } catch (SQLException e) {
            Log.v("w", "fail to connect!--getUserById"+"  "+e.getMessage());
        }
        return account;
    }

/**
 * @api {GET} /get/user 获取用户信息
 * @apiGroup Users
 * @apiVersion 0.0.1
 * @apiDescription 根据id获取用户信息
 * @apiParam {int} id 用户id
 * @apiParam {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {String} user 用户信息
 */
    public Users getUserById(int id, Connection conn) {
        Users user = new Users(initPhoto);
        try {
            Statement stmt = conn.createStatement();
            String sql = "select * from " + TABLE_USERS +" where u_id = "+ id;
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Integer[] emblem_temp = getEmblemById(id,conn);
                byte[] photo_temp = blobToBytes(rs.getBlob("photo"));
                Bitmap photo = BitmapFactory.decodeByteArray(photo_temp, 0, photo_temp.length);
                String name = rs.getString("name");
                int sex = rs.getInt("sex");
                String Githubname = rs.getString("Githubname");
                String description = rs.getString("description");
                String email = rs.getString("email");
                int best_jump = rs.getInt("best_jump");
                user = new Users(id, photo, name, sex, emblem_temp, Githubname, description, email, best_jump);
                Log.i("test--getUserById", "查询成功");
            }
            closeResources1(stmt, rs);
        } catch (SQLException e) {
            Log.v("w", "fail to connect!--getUserById"+"  "+e.getMessage());
        }
        return user;
    }

/**
 * @api {GET} /get/username 获取昵称
 * @apiGroup Users
 * @apiVersion 0.0.1
 * @apiDescription 根据id获取昵称
 * @apiParam {int} id 用户id
 * @apiParam {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {String} name 昵称
 */
    public String getUnameById(int u_id,Connection conn){
        String name = "";
        try{
            Statement stmt = conn.createStatement();
            String sql = "SELECT name FROM "+TABLE_USERS + " WHERE u_id = '"+ u_id+"'";
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                name = rs.getString("name");
            }
            closeResources1(stmt,rs);
        }catch (SQLException e){
            Log.v("w", "fail to connect!--getUnameById"+"  "+e.getMessage());
        }
        return name;
    }

/**
 * @api {GET} /get/id 获取信息
 * @apiGroup Users
 * @apiVersion 0.0.1
 * @apiDescription 根据用户名获取数据库内id
 * @apiParam {String} account 用户名
 * @apiParam {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {int} id 用户id
 */

    public int getUidByAccount(String account,Connection conn){
        int i = -1;
        try{
            Statement stmt = conn.createStatement();
            String sql = "SELECT u_id FROM "+TABLE_USERS + " WHERE account = '"+account+"'";
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                i = rs.getInt("u_id");
            }
            closeResources1(stmt,rs);
        }catch (SQLException e){
            Log.v("w", "fail to connect!"+"  "+e.getMessage());
        }
        return i;
    }
/**
 * @api {POST} /users/:acount 修改(完善)用户信息
 * @apiGroup Users
 * @apiVersion 0.0.1
 * @apiDescription 修改(完善)用户信息
 * @apiParam {String} account 用户名
 * @apiParam (200) {String} [name] 昵称
 * @apiParam (200) {int} [sex] 性别
 * @apiParam (200) {String} [githubname] github名
 * @apiParam (200) {String} [description] 个人简介
 * @apiParam (200) {String} [email] 邮箱
 * @apiParam (200) {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {int} code 1代表无错误 0代表有错误
 * @apiSuccessExample {json} 返回样例:
 *                {"code":"1","msg":"修改成功"}
 */
    public boolean updateUserByAccount(String account,String name,int sex,String Githubname,String description,String email,Connection conn){
        try {
            Statement stmt = conn.createStatement();
            String sql = "update "+ TABLE_USERS + " set name = '"+name+"', sex ='"+sex+"', Githubname='"+Githubname+"', description='"+description+"', email='"+email+"' " +
                    "where account = '"+account+"'";
            stmt.executeUpdate(sql);
            stmt.close();
            return true;
        }catch (SQLException e){
            Log.v("w", "fail to connect!"+"  "+e.getMessage());
            return false;
        }
    }

/**
 * @api {POST} /users/:account 修改(完善)用户头像
 * @apiGroup Users
 * @apiVersion 0.0.1
 * @apiDescription 修改(完善)用户头像
 * @apiParam (200) {String} account 用户名
 * @apiParam (200) {Bitmap} bitmap 新头像
 * @apiParam (200) {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {int} code 1代表无错误 0代表有错误
 * @apiSuccessExample {json} 返回样例:
 *                {"code":"1","msg":"修改成功"}
 */

    public boolean updateUphotoByAccount(String username,Bitmap bitmap,Connection conn){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] img = baos.toByteArray();
        try {
            String sql = "update "+ TABLE_USERS + " set photo = ? where account = '"+username+"'";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setBytes(1,img);
            pstmt.executeUpdate();
            pstmt.close();
            return true;
        }catch (SQLException e){
            Log.v("w", "fail to connect!"+"  "+e.getMessage());
            return false;
        }
    }


/**
 * @api {PATCH} /users/:password 修改密码
 * @apiGroup Users
 * @apiVersion 0.0.1
 * @apiDescription 修改密码
 * @apiParam (200) {String} username 用户名
 * @apiParam (200) {String} password 新头像
 * @apiParam (200) {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {int} code 1代表无错误 0代表有错误
 * @apiSuccessExample {json} 返回样例:
 *                {"code":"1","msg":"修改成功"}
 */
    public boolean updateUpasswordByName(String username,String password,Connection conn){
        try {
            Statement stmt = conn.createStatement();
            String sql = "update "+ TABLE_USERS + " set password = '"+password+"' "+
                    "where name = '"+username+"'";
            stmt.executeUpdate(sql);
            stmt.close();
            return true;
        }catch (SQLException e){
            Log.v("w", "fail to connect!"+"  "+e.getMessage());
            return false;
        }
    }


/**
 * @api {POST} /Emblem/:id 初始化用户兴趣
 * @apiGroup Emblem
 * @apiVersion 0.0.1
 * @apiDescription 初始化用户兴趣分类
 * @apiParam (200) {int} id 用户id
 * @apiParam (200) {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {int} code 1代表无错误 0代表无类别或有错误
 * @apiSuccessExample {json} 返回样例:
 *                {"code":"1","msg":"初始化成功"}
 */

    public boolean addEmblemById(int u_id, Connection conn){
        if(u_id>=1)
        {
            try {
                String sql = "insert into "+ TABLE_EMBLEM + " values("+u_id+",0,0,0)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.executeUpdate();
                pstmt.close();
                return true;
            }catch (SQLException e){
                Log.v("w", "fail to connect!"+"  "+e.getMessage());
                return false;
            }
        }
        return false;
    }


/**
 * @api {GET} /Emblem/:id 获取兴趣分类
 * @apiGroup Emblem
 * @apiVersion 0.0.1
 * @apiDescription 获取用户兴趣分类
 * @apiParam (200) {int} id 用户id
 * @apiParam (200) {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {int[]} code 三个二进制码分别代表用户的三个兴趣类型
 * @apiSuccessExample {json} 返回样例:
 *                {"code":"000","msg":"无兴趣"}
 */
    public Integer[] getEmblemById(int id,Connection conn){
        Integer[] emblem_temp = new Integer[]{};
        try{
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM "+TABLE_EMBLEM + " WHERE u_id = '"+id+"'";
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                emblem_temp = new Integer[]{rs.getInt("web"), rs.getInt("android"), rs.getInt("ios")};
            }
            closeResources1(stmt,rs);
        }catch (SQLException e){
            Log.v("w", "fail to connect!--getEmblemById"+"  "+e.getMessage());
        }
        return emblem_temp;
    }

/**
 * @api {GET} /Article/:title 搜索活动
 * @apiGroup Article
 * @apiVersion 0.0.1
 * @apiDescription 获取活动
 * @apiParam (200) {String} title 活动名
 * @apiParam (200) {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {int} code 1代表无错误 0代表有错误
 * @apiSuccessExample {json} 返回样例:
 *                {"code":"1","msg":"查询成功"}
 */
    public boolean searchArticle(String title,Connection conn){
        try{
            Statement stmt = conn.createStatement();
            String sql = "SELECT title FROM "+TABLE_ARTICLE + " WHERE title = '"+title+"'";
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                closeResources1(stmt,rs);
                return true;
            }
            closeResources1(stmt,rs);
        }catch (SQLException e){
            Log.v("w", "fail to connect!"+"  "+e.getMessage());
        }
        return false;
    }

/**
 * @api {GET} /Article/:title 获取活动id
 * @apiGroup Article
 * @apiVersion 0.0.1
 * @apiDescription 获取搜索关键字有关的活动
 * @apiParam (200) {String} title 活动名
 * @apiParam (200) {Connection} conn 连接
 * @apiSuccess (200) {String} msg 活动名
 * @apiSuccess (200) {int} id 活动的库内编号
 * @apiSuccessExample {json} 返回样例:
 *                {"id":"1","msg":"活动1"}
 */
    public int getA_IdByTitle(String title,Connection conn){
        int rt = -1;
        try{
            Statement stmt = conn.createStatement();
            String sql = "SELECT a_id FROM "+TABLE_ARTICLE + " WHERE title = '"+title+"'";
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                rt = rs.getInt("a_id");
                closeResources1(stmt,rs);
            }
            closeResources1(stmt,rs);
        }catch (SQLException e){
            Log.v("w", "fail to connect!"+"  "+e.getMessage());
        }
        return rt;
    }

/**
 * @api {POST} /register 创建活动
 * @apiGroup Article
 * @apiVersion 0.0.1
 * @apiDescription 创建一个新活动
 * @apiParam {String} title 标题
 * @apiParam {String} body 正文
 * @apiParam {int} type 活动类型
 * @apiParam {int} state 活动状态
 * @apiParam {Date} date 日期
 * @apiParam {int[]} emblem 活动级别
 * @apiParam {int} u_id 发布者id
 * @apiParam {Connection} conn 连接
 * @apiSuccess (201) {String} msg 信息
 * @apiSuccess (201) {boolean} code 1代表无错误 0代表有错误
 * @apiSuccessExample {json} 返回样例:
 *                {"code":"1","msg":"创建成功"}
 */
   
    public boolean addArticle(String title,String body,int type,int state,Date date,Integer[] emblems,int u_id,Connection conn){
        try {
            String sql = "insert into "+ TABLE_ARTICLE + " set title = ?,body = ?,type = ?,state = ?,date = ?,u_id= ?,emblem= ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,title);
            pstmt.setString(2,body);
            pstmt.setInt(3,type);
            pstmt.setInt(4,state);
            java.sql.Date sqlDate=new java.sql.Date(date.getTime());
            pstmt.setDate(5,sqlDate);
            pstmt.setInt(6,u_id);
            int e = 0;
            if(emblems[0]==1)
            {
                e+=100;
            }
            if(emblems[1]==1)
            {
                e+=10;
            }
            if(emblems[2]==1)
            {
                e+=1;
            }
            pstmt.setInt(7,e);
            pstmt.executeUpdate();
            pstmt.close();
            return true;
        }catch (SQLException e){
            Log.v("w", "fail to connect!"+"  "+e.getMessage());
            return false;
        }
    }

/**
 * @api {GET} /Article/:u_id 查看某用户所有活动
 * @apiGroup Article
 * @apiVersion 0.0.1
 * @apiDescription 查看某个用户创建的所有活动
 * @apiParam {int} u_id 发布者id
 * @apiParam {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {boolean} code 1代表无错误 0代表有错误
 * @apiSuccess (200) {int[]} a_ids 活动文章的序列号
 * @apiSuccessExample {json} 返回样例:
 *                {"code":"1","msg":"搜索成功"，"a_id":"[1,3,4]"}
 */
    public List<Integer> getA_idByU_id(int u_id,Connection conn){
        List<Integer> rt = new ArrayList<>();
        try{
            Statement stmt = conn.createStatement();
            String sql = "SELECT a_id FROM "+TABLE_ARTICLE + " WHERE u_id = '"+u_id+"'";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                rt.add(rs.getInt("a_id"));
            }
            closeResources1(stmt,rs);
        }catch (SQLException e){
            Log.v("w", "fail to connect!"+"  "+e.getMessage());
        }
        return rt;
    }

    /**
 * @api {GET} /Article 查看所有活动
 * @apiGroup Article
 * @apiVersion 0.0.1
 * @apiDescription 查看所有活动
 * @apiParam {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {boolean} code 1代表无错误 0代表有错误
 * @apiSuccess (200) {int[]} a_ids 活动文章的序列号
 * @apiSuccessExample {json} 返回样例:
 *                {"code":"1","msg":"搜索成功"，"a_id":"[1,3,4]"}
 */
    public List<Article> getAllarticle(Connection conn){
        List<Article> list = new ArrayList<Article>();
        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM "+TABLE_ARTICLE;
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                int a_id = rs.getInt("a_id");
                String title = rs.getString("title");
                String body = rs.getString("body");
                int type = rs.getInt("type");
                int state = rs.getInt("state");
                java.sql.Date date = rs.getDate("date");
                java.util.Date d=new java.util.Date (date.getTime());
                int u_id = rs.getInt("u_id");
                Integer[] emblems =new Integer[] {0,0,0};
                int e = rs.getInt("emblem");
                if(e/100==1)
                {
                    emblems[0]= 1;
                }
                if((e%100)/10==1)
                {
                    emblems[1]=1;
                }
                if(e%10==1)
                {
                    emblems[2]=1;
                }
                String name = getUnameById(u_id,conn);
                Article article = new Article(a_id,title,body,type,state,d,emblems,u_id);
                article.setU_name(name);
                list.add(article);
            }
            closeResources1(stmt,rs);
        }catch (SQLException e){
            Log.v("w", "fail to connect!"+"  "+e.getMessage());
        }
        return list;
    }

/**
 * @api {GET} /Article/:a_id 使用id查看活动
 * @apiGroup Article
 * @apiVersion 0.0.1
 * @apiDescription 使用活动id查看该活动
 * @apiParam {int} a_id 活动id
 * @apiParam {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {boolean} code 1代表无错误 0代表有错误
 * @apiSuccess (200) {Article} article 活动内容
 * @apiSuccessExample {json} 返回样例:
 *                {"code":"1","msg":"搜索成功"，"a_id":"xxx"}
 */
    public Article getArticleById(int a_id, Connection conn){
        Article article = new Article();
        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM "+TABLE_ARTICLE+" WHERE a_id="+a_id+"";
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                String title = rs.getString("title");
                String body = rs.getString("body");
                int type = rs.getInt("type");
                int state = rs.getInt("state");
                java.sql.Date date = rs.getDate("date");
                java.util.Date d=new java.util.Date (date.getTime());
                int u_id = rs.getInt("u_id");
                Integer[] emblems =new Integer[3];
                int e = rs.getInt("emblem");
                if(e/100==1)
                {
                    emblems[0]= 1;
                }
                else {
                    emblems[0]= 0;
                }
                if((e%100)/10==1)
                {
                    emblems[1]=1;
                }
                else
                {
                    emblems[1]=0;
                }
                if(e%10==1)
                {
                    emblems[2]=1;
                }
                else {
                    emblems[2]=0;
                }
                String name = getUnameById(u_id,conn);
                article = new Article(a_id,title,body,type,state,d,emblems,u_id);
                article.setU_name(name);
            }
        }catch (SQLException e){
            Log.v("w", "fail to connect!--getArticleById"+"  "+e.getMessage());
        }
        return article;
    }
/**
 * @api {POST} /Article/:a_id 修改活动信息
 * @apiGroup Article
 * @apiVersion 0.0.1
 * @apiDescription 通过id修改相应活动的信息
 * @apiParam (200) {int} a_id 活动序列号
 * @apiParam (200) {String} [title] 标题
 * @apiParam (200) {String} [body] 正文
 * @apiParam (200) {int} [type] 类别
 * @apiParam (200) {int} [state] 状态
 * @apiParam (200) {Date} [date] 日期
 * @apiParam (200) {int[]} [emblem] 级别
 * @apiParam (200) {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {int} code 1代表无错误 0代表有错误
 * @apiSuccessExample {json} 返回样例:
 *                {"code":"1","msg":"修改成功"}
 */
    public boolean updateArticleById(Integer a_id, String title, String body, int type, int state, Date date, Integer[] emblems, Connection conn){
        try {
            String sql = "update "+ TABLE_ARTICLE + " set title=?,body=?,type=?,state=?,date=?, emblem=? where a_id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,title);
            pstmt.setString(2,body);
            pstmt.setInt(3,type);
            pstmt.setInt(4,state);
            java.sql.Date sqlDate=new java.sql.Date(date.getTime());
            pstmt.setDate(5,sqlDate);
            int e = 0;
            if(emblems[0]==1)
            {
                e+=100;
            }
            if(emblems[1]==1)
            {
                e+=10;
            }
            if(emblems[2]==1)
            {
                e+=1;
            }
            pstmt.setInt(6,e);
            pstmt.setInt(7,a_id);
            pstmt.executeUpdate();
            pstmt.close();
            return true;
        }catch (SQLException e){
            Log.v("w", "fail to connect!"+"  "+e.getMessage());
            return false;
        }
    }

/**
 * @api {DELETE} /Article/:a_id 删除活动
 * @apiGroup Article
 * @apiVersion 0.0.1
 * @apiDescription 删除相应id的活动发布
 * @apiParam (200) {int} a_id 活动序列号
 * @apiParam (200) {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {int} code 1代表无错误 0代表有错误
 * @apiSuccessExample {json} 返回样例:
 *                {"code":"1","msg":"删除成功"}
 */

    public boolean deleteArticleById(Integer a_id,Connection conn){
        try {
            conn.setAutoCommit(false);
            String sql1 = "delete from "+ TABLE_ARTICLE + " where a_id = "+a_id.toString();
            String sql3 = "delete from "+ TABLE_APPLY + "  where a_id = "+a_id.toString();
            PreparedStatement ps = conn.prepareStatement(sql1);
            ps.executeUpdate();
            ps = conn.prepareStatement(sql3);
            ps.executeUpdate();
            conn.commit();
            ps.close();
            return true;
        }catch (SQLException e){
            try {
                conn.rollback();
            }catch (SQLException e1){
                e1.printStackTrace();
            }
            Log.v("w", "fail to connect!"+"  "+e.getMessage());
            return false;
        }
    }

/**
 * @api {POST} /Apply 添加申请记录
 * @apiGroup Apply
 * @apiVersion 0.0.1
 * @apiDescription 记录用户和活动的id从而记录申请操作
 * @apiParam (200) {int} u_id 用户id
 * @apiParam (200) {int} a_id 活动id
 * @apiParam (200) {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {int} code 1代表无错误 0代表有错误
 * @apiSuccessExample {json} 返回样例:
 *                {"code":"1","msg":"申请成功"}
 */
    public void apply(int a_id,int u_id,Connection conn){
        try {
            String sql = "insert into " + TABLE_APPLY + " set a_id = ?,u_id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,a_id);
            pstmt.setInt(2,u_id);
            pstmt.executeUpdate();
            pstmt.close();
        }catch (SQLException e){
            Log.v("w", "fail to connect!---apply"+"  "+e.getMessage());
        }
    }

   /**
 * @api {DELETE} /Apply 删除申请记录
 * @apiGroup Apply
 * @apiVersion 0.0.1
 * @apiDescription 将申请记录删除
 * @apiParam (200) {int} u_id 用户id
 * @apiParam (200) {int} a_id 活动id
 * @apiParam (200) {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {int} code 1代表无错误 0代表有错误
 * @apiSuccessExample {json} 返回样例:
 *                {"code":"1","msg":"取消成功"}
 */
    public void cancleapply(Integer a_id,Integer u_id,Connection conn){
        try {
            String sql = "delete from " + TABLE_APPLY + " where a_id = ? and u_id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,a_id);
            pstmt.setInt(2,u_id);
            pstmt.executeUpdate();
            pstmt.close();
            Log.v("w", "cancleapply删除成功!");
        }catch (SQLException e){
            Log.v("w", "fail to connect!--cancleapply"+"  "+e.getMessage());
        }
    }


    /**
 * @api {GET} /Apply/:u_id 查询用户所有的申请记录
 * @apiGroup Apply
 * @apiVersion 0.0.1
 * @apiDescription 查询用户所有的申请记录
 * @apiParam (200) {int} u_id 用户id
 * @apiParam (200) {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {int} code 1代表无错误 0代表有错误
 * @apiSuccess (200) {int[]} articles 申请的活动的数组
 * @apiSuccessExample {json} 返回样例:
 *                {"code":"1","msg":"查询成功"，"articles":"[1,2]"}
 */
    public ArrayList<Integer> getAllapplyById(Integer a_id,Connection conn){
        //int[] rt = new int[]{};
        ArrayList<Integer> rt=new ArrayList<Integer>();
        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT u_id FROM "+TABLE_APPLY+" WHERE a_id='"+a_id+"'";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                rt.add(rs.getInt("u_id"));
            }
            closeResources1(stmt,rs);
        }catch (SQLException e){
            Log.v("w", "fail to connect!--getAllapplyById"+"  "+e.getMessage());
        }
        return rt;
    }

 /**
 * @api {GET} /Article/search/:title 搜索活动
 * @apiGroup Article
 * @apiVersion 0.0.1
 * @apiDescription 搜索近似活动
 * @apiParam (200) {String} title 标题
 * @apiParam (200) {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {int} code 1代表无错误 0代表有错误
 * @apiSuccess (200) {int[]} articles 活动的数组
 * @apiSuccessExample {json} 返回样例:
 *                {"code":"1","msg":"搜索成功"，"articles":"[1,2]"}
 */ 
    public List<Integer> getA_idByTitleLike(String title, Connection conn) {
        List<Integer> rt = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT a_id FROM "+TABLE_ARTICLE+" WHERE title LIKE '%"+title+"%'";
            Log.d("exec search before", "true");
            ResultSet rs = stmt.executeQuery(sql);
            Log.d("exec search after", "true");
            while(rs.next()){
                Log.d("getA_idByTitleLike", String.valueOf(rs.getInt("a_id")));
                rt.add(rs.getInt("a_id"));
            }
            closeResources1(stmt,rs);
        } catch (Exception e) {
            Log.v("w", "fail to connect!--getA_idByTitleLike"+"  "+e.getMessage());
        }
        return rt;
    }
 /**
 * @api {GET} /Apply/get/U_id 查看申请人
 * @apiGroup Apply
 * @apiVersion 0.0.1
 * @apiDescription 查看申请记录的申请人
 * @apiParam (200) {int} a_id
 * @apiParam (200) {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {int} code 1代表无错误 0代表有错误
 * @apiSuccess (200) {int} user 申请人
 * @apiSuccessExample {json} 返回样例:
 *                {"code":"1","msg":"搜索成功"，"user":"1"}
 */ 

    public Integer getU_idByA_id(Integer a_id,Connection conn){
        Integer rt = -1;
        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT u_id FROM "+TABLE_ARTICLE+" WHERE a_id='"+a_id+"'";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                rt = rs.getInt("u_id");
            }
            closeResources1(stmt,rs);
        }catch (SQLException e){
            Log.v("w", "fail to connect!--getU_idByA_id"+"  "+e.getMessage());
        }
        return rt;
    }

 /**
 * @api {POST} /Attention/add 添加收藏
 * @apiGroup Attention
 * @apiVersion 0.0.1
 * @apiDescription 添加收藏
 * @apiParam (200) {int} u_id 用户
 * @apiParam (200) {int} a_id 活动
 * @apiParam (200) {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {int} code 1代表无错误 0代表有错误
 * @apiSuccessExample {json} 返回样例:
 *                {"code":"1","msg":"收藏成功"}
 */ 
    public boolean addattention(int u_id,int a_id, Connection conn){
        try {

            String sql3 = "insert into " + TABLE_ATTENTION + " set u_id = ?,a_id=?";
            PreparedStatement pstmt1 = conn.prepareStatement(sql3);
            pstmt1.setInt(1,u_id);
            pstmt1.setInt(2,a_id);
            pstmt1.executeUpdate();
            pstmt1.close();
            return true;
        }catch (SQLException e){
            Log.v("w", "fail to connect!---addattention"+"  "+e.getMessage());
            return false;
        }
    }

 /**
 * @api {GET} /Attention/:id 查找收藏
 * @apiGroup Attention
 * @apiVersion 0.0.1
 * @apiDescription 查看某用户的所有收藏
 * @apiParam (200) {int} u_id 用户
 * @apiParam (200) {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {int} code 1代表无错误 0代表有错误
 * @apiSuccess (200) {int[]} articles 活动的数组
 * @apiSuccessExample {json} 返回样例:
 *                {"code":"1","msg":"查询成功"，"articles":"[1,2]"}
 */ 
    public List<Integer> geta_idFromAttentionByu_id(int u_id,Connection conn){
        List<Integer> rt = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            int attentionlistid = -1;
            String sql = "SELECT a_id FROM "+TABLE_ATTENTION+" WHERE u_id = '"+u_id+"'";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                rt.add(rs.getInt("a_id"));
            }
            rs.close();
            closeResources1(stmt,rs);
        }catch (SQLException e){
            Log.v("w", "fail to connect!--geta_idFromAttentionByu_id"+"  "+e.getMessage());
        }
        return rt;
    }

 /**
 * @api {DELETE} /Attention/cancle 取消收藏
 * @apiGroup Attention
 * @apiVersion 0.0.1
 * @apiDescription 取消收藏
 * @apiParam (200) {int} u_id 用户
 * @apiParam (200) {int} a_id 活动
 * @apiParam (200) {Connection} conn 连接
 * @apiSuccess (200) {String} msg 信息
 * @apiSuccess (200) {int} code 1代表无错误 0代表有错误
 * @apiSuccessExample {json} 返回样例:
 *                {"code":"1","msg":"取关成功"}
 */ 
    public boolean deleteattention(int u_id,int a_id, Connection conn){
        try {
            String sql = "delete from " + TABLE_ATTENTION + " where a_id = ? and u_id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,a_id);
            pstmt.setInt(2,u_id);
            pstmt.executeUpdate();
            pstmt.close();
            Log.v("w", "deleteattention删除成功!");
            return true;
        }catch (SQLException e){
            Log.v("w", "fail to connect!--deleteattention"+"  "+e.getMessage());
            return false;
        }
    }



    /**
     * blob转Bytes
     * @param blob
     * @return
     */
    private static byte[] blobToBytes(Blob blob) {
        BufferedInputStream is = null;
        byte[] bytes = null;
        try {
            is = new BufferedInputStream(blob.getBinaryStream());
            bytes = new byte[(int) blob.length()];
            int len = bytes.length;
            int offset = 0;
            int read = 0;

            while (offset < len
                    && (read = is.read(bytes, offset, len - offset)) >= 0) {
                offset += read;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;

    }

    /**
     * 释放资源
     * @param stmt
     * @param rs
     */
    public static void closeResources1( Statement stmt, ResultSet rs) {
        if(null != rs) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                if(null != stmt) {
                    try {
                        stmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public static void closeConnection(Connection conn) {
        try {
            if(null != conn){
                conn.close();
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

    }
}
