package com.team1.kingofhonor.model;

import java.util.HashMap;
import java.util.Map;

public class Inscription {
    private String name;
    private String type;
    private int image;
    private int level;
    private Map<String, Double> property;
    private String color;

    public Inscription(String name, String type, int image, int level, String color) {
        this.name = name;
        this.type = type;
        this.image = image;
        this.level = level;
        this.color = color;
        this.property = new HashMap<String, Double>();
    }
    public void addProperty(String name, double num) {
        property.put(name, num);
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public int getImage() {
        return image;
    }
    public int getLevel() {
        return level;
    }
    public String getColor() {
        return color;
    }
    public Map<String, Double> getProperty() {
        return property;
    }

}
