package com.janking.sysuhealth;

import android.graphics.Color;

import java.io.Serializable;

public class Food implements Serializable {
    private String name;
    private String category;
    private String nutrition;
    private Boolean favorite;
    private String color;

    public Food(String a, String b, String c, String d){
        name = a;
        category = b;
        nutrition = c;
        favorite = false;
        color = d;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setNutrition(String nutrition) {
        this.nutrition = nutrition;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getNutrition() {
        return nutrition;
    }

    public String getColor() {
        return color;
    }
}
