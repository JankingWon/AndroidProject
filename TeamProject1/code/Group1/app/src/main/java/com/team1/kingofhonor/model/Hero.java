package com.team1.kingofhonor.model;

import android.net.Uri;

import com.team1.kingofhonor.R;

import java.io.Serializable;

//英雄的属性
//还没修改好

public class Hero implements Serializable{
    //保存的是image的uri
    //英雄海报
    private String image = "android.resource://com.team1.kingofhonor/" +R.mipmap.hero;
    //英雄语音
    private String voice = "android.resource://com.team1.kingofhonor/" +R.raw.pentakill;
    //英雄图标
    private String icon = "android.resource://com.team1.kingofhonor/" +R.mipmap.hero_icon;
    //英雄技能图标
    private String skill1_icon = "android.resource://com.team1.kingofhonor/" +R.mipmap.juyoujing1;
    private String skill2_icon = "android.resource://com.team1.kingofhonor/" +R.mipmap.juyoujing2;
    private String skill3_icon = "android.resource://com.team1.kingofhonor/" +R.mipmap.juyoujing3;
    private String skill4_icon = "android.resource://com.team1.kingofhonor/" +R.mipmap.juyoujing4;
    //英雄技能描述
    private String skill1_description = "技能1";
    private String skill2_description = "技能2";
    private String skill3_description = "技能3";
    private String skill4_description = "技能4";
    //推荐装备
    private String equip1 = "破军";
    private String equip2 = "破军";
    private String equip3 = "破军";
    private String equip4 = "破军";
    private String equip5 = "破军";
    private String equip6 = "破军";
    //英雄名字
    private String name = "名字未设置";
    //英雄称号
    private String alias = "王者小兵";
    //英雄职业
    private String category;//1.法师 2.刺客 3.射手 4.辅助 5.战士 6.坦克
    //生存能力
    private int viability = 1;
    //攻击伤害
    private int attack_damage= 1;
    //技能伤害
    private int skill_damage = 1;
    //上手难度
    private int difficulty = 1;

    private Boolean favorite = false;//收藏
    private Boolean deleted = false;//true表示已经删除
    private Boolean added = false;//true表示是新加的
    private Boolean modified = false;//true表示已经修改

    public Hero(){}

    public Hero(String name, String image, String alias, String category, int viability, int attack_damage, int skill_damage, int difficulty, String voice, String icon, boolean favorite,
                String skill1_icon, String skill1_description, String skill2_icon, String skill2_description, String skill3_icon, String skill3_description, String skill4_icon, String skill4_description,
                String equip1,String equip2, String equip3, String equip4, String equip5, String  equip6) {
        super();
        this.name = name;
        if(!alias.isEmpty())
            this.alias = alias;
        this.image = image;
        this.category = category;
        this.viability = viability;
        this.attack_damage = attack_damage;
        this.skill_damage = skill_damage;
        this.difficulty = difficulty;
        this.voice = voice;
        this.icon = icon;
        this.favorite = favorite;
        this.skill1_icon = skill1_icon;
        this.skill2_icon = skill2_icon;
        this.skill3_icon = skill3_icon;
        this.skill4_icon = skill4_icon;
        this.skill1_description = skill1_description;
        this.skill2_description = skill2_description;
        this.skill3_description = skill3_description;
        this.skill4_description = skill4_description;
        this.equip1 = equip1;
        this.equip2 = equip2;
        this.equip3 = equip3;
        this.equip4 = equip4;
        this.equip5 = equip5;
        this.equip6 = equip6;

    }
    public Hero(String name, String image, String alias, String category, int viability, int attack_damage, int skill_damage, int difficulty, String voice, String icon, boolean favorite) {
        super();
        this.name = name;
        if(!alias.isEmpty())
            this.alias = alias;
        this.image = image;
        this.category = category;
        this.viability = viability;
        this.attack_damage = attack_damage;
        this.skill_damage = skill_damage;
        this.difficulty = difficulty;
        this.voice = voice;
        this.icon = icon;
        this.favorite = favorite;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        if(image!=null&&!image.isEmpty())
        this.image = image;
    }

    public void setVoice(String voice) {
        if(voice!=null&&!voice.isEmpty())
        this.voice = voice;
    }

    public String getVoice() {
        return voice;
    }

    public void setName(String name) {
        if(name==null || name.isEmpty())
            this.name = new String("名字未设置");
        else
            this.name = name;
    }

    public void setAlias(String alias) {
        if(alias==null || alias.isEmpty())
            this.alias = new String("称号未设置");
        else
            this.alias = alias;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if(category!=null && !category.isEmpty())
        this.category = category;
    }

    public void setIcon(String icon) {
        if(icon!=null && !icon.isEmpty())
        this.icon = icon;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public String getIcon() {
        return icon;
    }

    public int getAttack_damage() {
        return attack_damage;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getSkill_damage() {
        return skill_damage;
    }

    public int getViability() {
        return viability;
    }

    public void setAttack_damage(int attack_damage) {
        this.attack_damage = attack_damage;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setSkill_damage(int skill_damage) {
        this.skill_damage = skill_damage;
    }

    public void setViability(int viability) {
        this.viability = viability;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setAdded(Boolean added) {
        this.added = added;
    }

    public Boolean getAdded() {
        return added;
    }

    public void setModified(Boolean modified) {
        this.modified = modified;
    }

    public Boolean getModified() {
        return modified;
    }

    public String getSkill1_description() {
        return skill1_description;
    }

    public String getSkill1_icon() {
        return skill1_icon;
    }

    public String getSkill2_description() {
        return skill2_description;
    }

    public String getSkill2_icon() {
        return skill2_icon;
    }

    public String getSkill3_description() {
        return skill3_description;
    }

    public String getSkill3_icon() {
        return skill3_icon;
    }

    public String getSkill4_description() {
        return skill4_description;
    }

    public String getSkill4_icon() {
        return skill4_icon;
    }

    public void setSkill1_description(String skill1_description) {
        this.skill1_description = skill1_description;
    }

    public void setSkill1_icon(String skill1_icon) {
        this.skill1_icon = skill1_icon;
    }

    public void setSkill2_description(String skill2_description) {
        this.skill2_description = skill2_description;
    }

    public void setSkill2_icon(String skill2_icon) {
        this.skill2_icon = skill2_icon;
    }

    public void setSkill3_description(String skill3_description) {
        this.skill3_description = skill3_description;
    }

    public void setSkill3_icon(String skill3_icon) {
        this.skill3_icon = skill3_icon;
    }

    public void setSkill4_description(String skill4_description) {
        this.skill4_description = skill4_description;
    }

    public void setSkill4_icon(String skill4_icon) {
        this.skill4_icon = skill4_icon;
    }

    public String getEquip1() {
        return equip1;
    }

    public String getEquip2() {
        return equip2;
    }

    public String getEquip3() {
        return equip3;
    }

    public String getEquip4() {
        return equip4;
    }

    public void setEquip1(String equip1) {
        this.equip1 = equip1;
    }

    public void setEquip2(String equip2) {
        this.equip2 = equip2;
    }

    public void setEquip3(String equip3) {
        this.equip3 = equip3;
    }

    public void setEquip4(String equip4) {
        this.equip4 = equip4;
    }

    public String getEquip5() {
        return equip5;
    }

    public String getEquip6() {
        return equip6;
    }

    public void setEquip5(String equip5) {
        this.equip5 = equip5;
    }

    public void setEquip6(String equip6) {
        this.equip6 = equip6;
    }
}