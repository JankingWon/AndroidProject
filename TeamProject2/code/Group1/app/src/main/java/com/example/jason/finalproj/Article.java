package com.example.jason.finalproj;

import java.util.Date;


public class Article {
    int id;
    String title;
    String body;// introduction
    int type;//0 and 1
    int state;//0 and 1
    Date date;// the ddl
    Integer[] emblems;// attribution
    int u_id;
    String u_name;
    public Article(){
        this.id = -1;
    }
    public Article(int id,String title,String body,int type,int state,Date date,Integer[] emblems,int u_id){
        this.id = id;
        this.title = title;
        this.body = body;
        this.type = type;
        this.state = state;
        this.date = date;
        this.emblems = emblems;
        this.u_id = u_id;
        u_name = "";
    }

    public void setU_name(String name){
        u_name = name;
    }

    public int getId() {
        return id;
    }

    public Integer[] getEmblems() {
        return emblems;
    }

    public int getState() {
        return state;
    }

    public int getType() {
        return type;
    }

    public int getU_id() {
        return u_id;
    }

    public String getBody() {
        return body;
    }

    public Date getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getU_name() {
        return u_name;
    }
}
