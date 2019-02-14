package com.team1.kingofhonor.model;

import java.io.Serializable;

public class Equipment implements Serializable {
    private int image;
    private String name;
    private int price;
    private String property;
    private String skill;
    private String process;
    private String category;

    public Equipment(){}

    public Equipment(String name, int price, String property, String skill, String process, int image, String category){
        this.image = image;
        this.name = name;
        this.price = price;
        this.property = property;
        this.skill = skill;
        this.process = process;
        this.category = category;
    }

    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getProcess() {
        return process;
    }

    public String getProperty() {
        return property;
    }

    public int getPrice() {
        return price;
    }

    public String getSkill() {
        return skill;
    }

    public String getCategory() {
        return category;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
