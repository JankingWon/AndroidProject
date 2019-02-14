package com.example.myapplication;

import android.text.method.SingleLineTransformationMethod;

import java.util.ArrayList;

public class CommentInfo {
    private String username, date, comment, image;
    private int praise_count;
    private ArrayList<String> praise_users;
    public CommentInfo(String date,String username, String comment, String image, ArrayList<String> praise_users){
        this.username = username;
        this.date = date;
        this.comment = comment;
        this.image = image;
        this.praise_count = praise_users.size();
        this.praise_users = praise_users;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }

    public String getImage() {
        return image;
    }

    public int getPraise_count() {
        return praise_count;
    }

    public String getUsername() {
        return username;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPraise_count(int praise_count) {
        this.praise_count = praise_count;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPraise_users(ArrayList<String> praise_users) {
        this.praise_users = praise_users;
    }

    public ArrayList<String> getPraise_users() {
        return praise_users;
    }
    public Boolean praise_count_change(String current_user){
        for(String u : getPraise_users()){
            if(u.equals(current_user)){
                praise_users.remove(u);
                praise_count--;
                return false;
            }
        }
        praise_users.add(current_user);
        praise_count++;
        return true;
    }
}
