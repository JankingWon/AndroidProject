package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class UserSQLiteHelper extends SQLiteOpenHelper {
    private static final String DB_NAME= "db_project3";
    private static final String TABLE_NAME = "user";
    private static final String TABLE_COMMENT_NAME = "comment";
    private static final int DB_VERSION = 1;

    private static final String[] COLUMNS = {"username","password","image"};

    public UserSQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE if not exists "
                + TABLE_NAME
                + " ( username TEXT PRIMARY KEY, password TEXT, image TEXT)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
        String CREATE_COMMENT = "CREATE TABLE if not exists "
                + TABLE_COMMENT_NAME
                + " ( date TEXT PRIMARY KEY, username TEXT, comment TEXT, image TEXT, praise_users TEXT)";
        sqLiteDatabase.execSQL(CREATE_COMMENT);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int ii) {

    }
    public Boolean addUser(String name, String password, String image) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor =
                db.query(TABLE_NAME, // a. table
                        COLUMNS, // b. column names
                        " username = ?", // c. selections
                        new String[] { name }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor.moveToFirst()){
            db.close();
            return false;
        }
        ContentValues cv = new ContentValues();
        cv.put("username", name);
        cv.put("password", password);
        cv.put("image", image);
        db.insert(TABLE_NAME, null, cv);
        db.close();
        return true;
    }
    public int loginVerify(String name, String password){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor =
                db.query(TABLE_NAME, // a. table
                        COLUMNS, // b. column names
                        " username = ?", // c. selections
                        new String[] { name }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        if (!cursor.moveToFirst()){
            db.close();
            return 1;//username not found
        }

        if(!password.equals(cursor.getString(1))){
            db.close();
            return 2;//user password not correct
        }
        db.close();
        return 0;
    }
    public String getImage(String name){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor =
                db.query(TABLE_NAME, // a. table
                        COLUMNS, // b. column names
                        " username = ?", // c. selections
                        new String[] { name }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        if (!cursor.moveToFirst()){
            db.close();
            return null;//username not found
        }else {
            String image =  cursor.getString(2);
            cursor.close();
            db.close();
            return  image;
        }
    }

    public void addComment(CommentInfo commentInfo){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("date", commentInfo.getDate());
        cv.put("username", commentInfo.getUsername());
        cv.put("comment", commentInfo.getComment());
        cv.put("image", commentInfo.getImage());
        cv.put("praise_users", new Convert().array2String(commentInfo.getPraise_users()));
        db.insert(TABLE_COMMENT_NAME, null, cv);
        db.close();
    }

    public void removeComment(CommentInfo commentInfo){
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "date = ?";
        String[] whereArgs = new String[] { commentInfo.getDate() };
        db.delete(TABLE_COMMENT_NAME, whereClause, whereArgs);
        db.close();
    }

    public void updateComment(String date, ArrayList<String> new_users){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("praise_users", new Convert().array2String(new_users));
        String whereClause = "date = ?";
        String[] whereArgs = new String[] { date };
        db.update(TABLE_COMMENT_NAME, cv, whereClause, whereArgs );
        db.close();
    }

    public ArrayList<CommentInfo> getAllComments(){
        ArrayList<CommentInfo> arrayList = new ArrayList<>();

        String query = "SELECT  * FROM " + TABLE_COMMENT_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                arrayList.add(new CommentInfo(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        new Convert().string2Array( cursor.getString(4))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return arrayList;
    }

    public void dropUser(){
//删除表的SQL语句
        String sql ="DROP TABLE user";
//执行SQL
        this.getWritableDatabase().execSQL(sql);
    }
    public void dropComment(){
//删除表的SQL语句
        String sql ="DROP TABLE comment";
//执行SQL
        this.getWritableDatabase().execSQL(sql);
    }
}
