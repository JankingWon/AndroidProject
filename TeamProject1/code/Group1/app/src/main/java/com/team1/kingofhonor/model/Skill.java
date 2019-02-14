package com.team1.kingofhonor.model;

import java.io.Serializable;

public class Skill implements Serializable {
    private int image;
    private String name;
    private int image_detail;
    private String detail1;
    private String detail2;


    public Skill(){

    }

    public Skill(String name,int image, int image_detail, String detail1, String detail2){
        this.image = image;
        this.name = name;
        this.image_detail = image_detail;
        this.detail1 = detail1;
        this.detail2 = detail2;
    }

    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getDetail1() {
        return detail1;
    }

    public int getImage_detail() {
        return image_detail;
    }

    public String getDetail2() {
        return detail2;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setImage_detail(int image_detail) {
        this.image_detail = image_detail;
    }

    public void setDetail1(String detail1) {
        this.detail1 = detail1;
    }

    public void setDetail2(String detail2) {
        this.detail2 = detail2;
    }
}

