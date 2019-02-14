package com.example.jason.finalproj;

import android.graphics.Bitmap;

public class Users {
    private int id; // id in the database
    private Bitmap photo;// the portrait of the user
    private String name;// username
    private int sex;// 1 and 0
    private Integer[] emblems; // attribute and hobbies
    private String Githubname;// github account(unused in the final version)
    private String description;// personal introduction
    private String email;// email address
    private String account;// user account
    private int best_jump;
    public Users(Bitmap bitmap){
        photo = bitmap;
        name = "";
        sex = 0;
        emblems = new Integer[]{0,0,0};
        Githubname = "";
        description ="";
        email ="";
        account = "";
        best_jump = -1;//可以用来标识是不是无意义的User
    }
    public Users(int id,Bitmap photo,String name,int sex,Integer[] emblems,String Githubname,String description,String email,int best_jump){
        this.id = id;
        this.photo = photo;
        this.name = name;
        this.sex = sex;
        this.emblems = emblems;
        this.Githubname = Githubname;
        this.description = description;
        this.email = email;
        this.best_jump = best_jump;
    }

    public int getId() {
        return id;
    }

    public Bitmap getPhoto(){
        return photo;
    }
    public String getName(){
        return name;
    }

    public int getSex() {
        return sex;
    }

    public Integer[] getEmblems() {
        return emblems;
    }

    public String getGithubname() {
        return Githubname;
    }

    public String getDescription() {
        return description;
    }

    public String getEmail() {
        return email;
    }

    public int getBest_jump() {
        return best_jump;
    }

}
