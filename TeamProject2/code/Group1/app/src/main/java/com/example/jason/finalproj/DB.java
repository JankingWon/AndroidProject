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
     * 获取数据库的连接
     *
     * @return conn
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
     * 往数据库里加入一个新的用户，这里的Bitmap没有做压缩处理,这里没做重名判断
     * @param image
     * @param name
     * @param sex
     * @param Githubname
     * @param description
     * @param email
     * @param account
     * @param password
     * @param conn
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
     * 判断account已存在
     * @param account
     * @param conn
     * @return
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
     *
     * @param username
     * @param conn
     * @return 判断用户名已存在
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
     *
     * @param account
     * @param password
     * @param conn
     * @return 判断密码是否正确
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
     *
     * @param username
     * @param conn
     * @return 返回名为username的一个User(包括所有信息,除了已发文章),查不到的话返回的User的best_jump就会为-1,不然的话会是个大于零的整数
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
     *
     * @param id
     * @param conn
     * @return
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
     *
     * @param id
     * @param conn
     * @return
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
     *
     * @param u_id
     * @return
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
     *
     * @param username
     * @param conn
     * @return 该User的Id,为-1说明查不到
     */
    public int getUidByName(String username ,Connection conn){
        int i = -1;
        try{
            Statement stmt = conn.createStatement();
            String sql = "SELECT u_id FROM "+TABLE_USERS + " WHERE name = '"+username+"'";
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
     *
     * @param account
     * @param conn
     * @return u_id
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

//    /**
//     *
//     * @param username
//     * @param name
//     * @param sex
//     * @param Githubname
//     * @param description
//     * @param email
//     * @param conn
//     * 修改名为username的除了头像之外的用户信息
//     */
//    public boolean updateUserByName(String username,String name,int sex,String Githubname,String description,String email,Connection conn){
//        try {
//            Statement stmt = conn.createStatement();
//            String sql = "update "+ TABLE_USERS + " set name = '"+name+"', sex ='"+sex+"', Githubname='"+Githubname+"', description='"+description+"', email='"+email+"' " +
//                    "where name = '"+username+"'";
//            stmt.executeUpdate(sql);
//            stmt.close();
//            return true;
//        }catch (SQLException e){
//            Log.v("w", "fail to connect!"+"  "+e.getMessage());
//            return false;
//        }
//    }
    /**
     *
     * @param account
     * @param name
     * @param sex
     * @param Githubname
     * @param description
     * @param email
     * @param conn
     * 修改名为username的除了头像之外的用户信息
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

//    /**
//     *
//     * @param username
//     * @param bitmap
//     * @param conn
//     */
//    public boolean updateUphotoByName(String username,Bitmap bitmap,Connection conn){
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        byte[] img = baos.toByteArray();
//        try {
//            String sql = "update "+ TABLE_USERS + " set photo = ? where name = '"+username+"'";
//            PreparedStatement pstmt = conn.prepareStatement(sql);
//            pstmt.setBytes(1,img);
//            pstmt.executeUpdate();
//            pstmt.close();
//            return true;
//        }catch (SQLException e){
//            Log.v("w", "fail to connect!"+"  "+e.getMessage());
//            return false;
//        }
//    }

    /**
     *
     * @param username
     * @param bitmap
     * @param conn
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
     * 修改用户密码
     * @param username
     * @param password
     * @param conn
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
     *
     * @param u_id
     * @param conn
     * 这里同时关联删除了徽章表和申请表相关条目,没有删文章表，因为你删文章徽章表也还要再关联删除
     * 可以先找到关于该用户的所有文章，执行删除文章，再执行这个删除用户
     */
    public boolean deleteUserById(Integer u_id,Connection conn){
        try {
            conn.setAutoCommit(false);
            String sql1 = "delete from "+ TABLE_USERS + " where u_id = '"+u_id.toString()+"'";
            String sql2 = "delete from "+ TABLE_EMBLEM + " where u_id = '"+u_id.toString()+"'";
            PreparedStatement ps = conn.prepareStatement(sql1);
            ps.executeUpdate();
            ps = conn.prepareStatement(sql2);
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
     *  @param u_id
     * @param conn
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
     * 修改某个用户的徽章，没错每次都传全部
     * @param id
     * @param emblem
     */
    public boolean updateEmblemById(Integer id,Integer[] emblem,Connection conn){
        try {
            String sql = "update "+ TABLE_EMBLEM + " set web=?,android=?,ios=? where u_id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,emblem[0]);
            pstmt.setInt(2,emblem[1]);
            pstmt.setInt(3,emblem[2]);
            pstmt.setInt(4,id);
            pstmt.executeUpdate();
            pstmt.close();
            return true;
        }catch (SQLException e){
            Log.v("w", "fail to connect!---updateEmblemById"+"  "+e.getMessage());
            return false;
        }
    }

    /**
     * 该文章或者用户的徽章
     * @param id
     * @return
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
     *  文章表相关的函数
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
     *
     * @param title
     * @param conn
     * @return获取文章ID
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
     *
     * @param title 标题
     * @param body 正文
     * @param type 类型（官方？个人？）
     * @param state 状态（招募中，已结束）
     * @param date 时间（最近更新时间）
     * @param emblems 招募范围
     * @param u_id 发布者id
     * 新建一篇文章,这里没做标题重复判断，传进来的时候务必保证不能重复
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
     *
     * @param u_id
     * @return 找到一个用户发布的所有文章id，查文章先可以先查id再查具体内容。
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
     *
     * @return 所有文章
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
     *
     * @param a_id
     * @return 文章
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
     *
     * @param a_id
     * @param title
     * @param body
     * @param type
     * @param state
     * @param date
     * @param emblems
     * 更新一篇文章
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
     *
     * @param a_id
     * 删除一篇文章,关联删除了徽章表和申请表
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
     *
     * @param a_id
     * @param u_id
     * 申请
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
     *
     * @param a_id
     * @param u_id
     * 取消申请
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
     * 用用户id获取申请的所有文章
     * @param u_id
     * @param conn
     * @return
     */
    public ArrayList<Integer> getAllA_IdFromApplyByU_Id(Integer u_id,Connection conn){
        ArrayList<Integer> rt=new ArrayList<Integer>();
        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT a_id FROM "+TABLE_APPLY+" WHERE u_id='"+u_id+"'";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                rt.add(rs.getInt("a_id"));
            }
            closeResources1(stmt,rs);
        }catch (SQLException e){
            Log.v("w", "fail to connect!--getAllArticleFromApplyByU_Id"+"  "+e.getMessage());
        }
        return rt;
    }

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
     * 添加关注
     * @param u_id
     * @param a_id
     * @param conn
     * @return
     */
    public boolean addattention(int u_id,int a_id, Connection conn){
        try {
            /*
            String sql2 = "update " + TABLE_ATTENTION + " set u_id = ?,a_list_id=?" where;
            PreparedStatement pstmt = conn.prepareStatement(sql2);
            pstmt.setInt(1,u_id);
            pstmt.setInt(2,u_id);
            pstmt.executeUpdate();
            */
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
     * 根据u_id获得关注的文章
     * @param u_id
     * @param conn
     * @return
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
     * 取消关注
     * @param u_id
     * @param a_id
     * @param conn
     * @return
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
