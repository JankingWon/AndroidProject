package com.team1.kingofhonor.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.team1.kingofhonor.R;
import com.team1.kingofhonor.adapter.MyViewPagerAdapter;
import com.team1.kingofhonor.adapter.SkillAdapter;
import com.team1.kingofhonor.model.Skill;
import com.team1.kingofhonor.sqlite.HeroSQLiteHelper;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Fragment4 extends Fragment {
    private View view;
    private GridView skill_gridView;
    private ImageView[] ivPoints;
    private ViewGroup group;
    private int currentPage;
 //   private List<Map<String,Object>> dataList;
    private ArrayList<Skill> skillArrayList;
    private HeroSQLiteHelper heroSQLiteHelper;
    private ArrayList<View> viewPagerList;
    private SimpleAdapter adapter;
    private ImageView skillDetailImage;
    private TextView skillNameText;
    private TextView skillDetail1;
    private TextView skillDetail2;
    private SkillAdapter mSkillAdapter;
    private ViewPager viewPager;
    private MyViewPagerAdapter viewPagerAdapter;
    private int totalPage;
    private int mPageSize = 12;
    public LayoutInflater myinflater;

    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment4, container, false);
        initView();
        initData();
        totalPage = (int) Math.ceil(skillArrayList.size() * 1.0 / mPageSize);

        return view;
    }
    public void initView(){
        skillDetailImage = (ImageView) view.findViewById(R.id.skill_detail_image);
        skillNameText = (TextView)view.findViewById(R.id.skill_name_text);
        skillDetail1 = (TextView)view.findViewById(R.id.skill_detail1);
        skillDetail2 = (TextView)view.findViewById(R.id.skill_detail2);
        skill_gridView = (GridView)view.findViewById(R.id.skill_gridview);
        group = (LinearLayout) view.findViewById(R.id.points);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        skillNameText.setText("惩击");
        skillDetailImage.setImageResource(R.mipmap.chengji_detail);
        skillDetail1.setText("LV.1解锁");
        skillDetail2.setText("30秒CD：对身边的野怪和小兵造成真实伤害并眩晕1秒");
    }
    public void initData(){
        skillArrayList = new ArrayList<Skill>();
        heroSQLiteHelper = new HeroSQLiteHelper(getContext());
        heroSQLiteHelper.addSkill(new Skill("惩击",
                R.mipmap.chengji,
                R.mipmap.chengji_detail,
                "LV.1解锁",
                "30秒CD：对身边的野怪和小兵造成真实伤害并眩晕1秒"));
        heroSQLiteHelper.addSkill(new Skill("终结",
                R.mipmap.zhongjie,
                R.mipmap.zhongjie_detail,
                "LV.3解锁",
                "90秒CD：立即对身边敌军英雄造成其已损失生命值14%的真实伤害"));
        heroSQLiteHelper.addSkill(new Skill("狂暴",
                R.mipmap.kuangbao,
                R.mipmap.kuangbao_detail,
                "LV.5解锁",
                "60秒CD：增加攻击速度60%，并增加物理攻击力10%持续5秒"));
        heroSQLiteHelper.addSkill(new Skill("疾跑",
                R.mipmap.jipao,
                R.mipmap.jipao_detail,
                "LV.7解锁",
                "100秒CD：增加30%移动速度持续10秒"));
        heroSQLiteHelper.addSkill(new Skill("治疗术",
                R.mipmap.zhiliaoshu,
                R.mipmap.zhiliaoshu_detail,
                "LV.9解锁",
                "120秒CD：回复自己与附近队友15%生命值，提高附近友军移动速度15%持续2秒"));
        heroSQLiteHelper.addSkill(new Skill("干扰",
                R.mipmap.ganrao,
                R.mipmap.ganrao_detail,
                "LV.11解锁",
                "60秒CD：沉默机关持续5秒"));
        heroSQLiteHelper.addSkill(new Skill("晕眩",
                R.mipmap.yunxuan,
                R.mipmap.yunxuan_detail,
                "LV.13解锁",
                "90秒CD：晕眩身边所有敌人0.75秒，并附带持续1秒的减速效果"));
        heroSQLiteHelper.addSkill(new Skill("净化",
                R.mipmap.jinghua,
                R.mipmap.jinghua_detail,
                "LV.15解锁",
                "120秒CD：解除自身所有负面和控制效果并免疫控制持续1.5秒"));
        heroSQLiteHelper.addSkill(new Skill("弱化",
                R.mipmap.ruohua,
                R.mipmap.ruohua_detail,
                "LV.17解锁",
                "90秒CD：减少身边敌人伤害输出50%持续3秒"));
        heroSQLiteHelper.addSkill(new Skill("闪现",
                R.mipmap.shanxian,
                R.mipmap.shanxian_detail,
                "LV.19解锁",
                "120秒CD：向指定方向位移一段距离"));
        skillArrayList = heroSQLiteHelper.getAllSkill();
        myinflater = LayoutInflater.from(getContext());
        setViewPagerWithGridview();


        ivPoints = new ImageView[totalPage];
        for (int i = 0; i < totalPage; i++){
            ImageView imageView = new ImageView(getActivity());
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(30,30);
            params.rightMargin = 40;
            imageView.setLayoutParams(params);
            imageView.setBackgroundResource(R.drawable.point_bg);
            if (i == 0){
                imageView.setEnabled(true);
            }
            else {
                imageView.setEnabled(false);
            }
            ivPoints[i] = imageView;
            group.addView(imageView);
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                setImageBackground(i);
                currentPage = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void setImageBackground(int selectItems) {
        for (int i = 0; i < ivPoints.length; i++) {
            if (i == selectItems) {
                ivPoints[i].setBackgroundResource(R.drawable.point_focus);
            } else {
                ivPoints[i].setBackgroundResource(R.drawable.point_normal);
            }
        }
    }
    private void setViewPagerWithGridview(){
        totalPage = (int) Math.ceil(skillArrayList.size() * 1.0 / mPageSize);
        viewPagerList = new ArrayList<View>();
        for (int i = 0; i < totalPage; i++){
            mSkillAdapter = new SkillAdapter(getContext(),skillArrayList, i, mPageSize);
            final GridView gridView = (GridView) myinflater.inflate(R.layout.gridview,viewPager,false);
            gridView.setAdapter(mSkillAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Skill skill = (Skill) adapterView.getItemAtPosition(i);
                    skillDetailImage.setImageResource(skill.getImage_detail());
                    skillNameText.setText(skill.getName());
                    skillDetail1.setText(skill.getDetail1());
                    skillDetail2.setText(skill.getDetail2());

                }
            });

            viewPagerList.add(gridView);
        }

        viewPagerAdapter = new MyViewPagerAdapter(viewPagerList);
        viewPager.setAdapter(viewPagerAdapter);
    }
 /*   public void setSkillDetail(String skill){
        skillNameText.setText(skill);
        if (skill.equals("惩击")){
            skillDetailImage.setImageResource(R.mipmap.chengji_detail);
            skillDetail1.setText("LV.1解锁");
            skillDetail2.setText("30秒CD：对身边的野怪和小兵造成真实伤害并眩晕1秒");
        }
        else if(skill.equals("终结")){
            skillDetailImage.setImageResource(R.mipmap.zhongjie_detail);
            skillDetail1.setText("LV.3解锁");
            skillDetail2.setText("90秒CD：立即对身边敌军英雄造成其已损失生命值14%的真实伤害");
        }
        else if(skill.equals("狂暴")){
            skillDetailImage.setImageResource(R.mipmap.kuangbao_detail);
            skillDetail1.setText("LV.5解锁");
            skillDetail2.setText("60秒CD：增加攻击速度60%，并增加物理攻击力10%持续5秒");
        }
        else if(skill.equals("疾跑")){
            skillDetailImage.setImageResource(R.mipmap.jipao_detail);
            skillDetail1.setText("LV.7解锁");
            skillDetail2.setText("100秒CD：增加30%移动速度持续10秒");
        }
        else if (skill.equals("治疗术")){
            skillDetailImage.setImageResource(R.mipmap.zhiliaoshu_detail);
            skillDetail1.setText("LV.9解锁");
            skillDetail2.setText("120秒CD：回复自己与附近队友15%生命值，提高附近友军移动速度15%持续2秒");
        }
        else if (skill.equals("干扰")){
            skillDetailImage.setImageResource(R.mipmap.ganrao_detail);
            skillDetail1.setText("LV.11解锁");
            skillDetail2.setText("60秒CD：沉默机关持续5秒");
        }
        else if (skill.equals("晕眩")){
            skillDetailImage.setImageResource(R.mipmap.yunxuan_detail);
            skillDetail1.setText("LV.13解锁");
            skillDetail2.setText("90秒CD：晕眩身边所有敌人0.75秒，并附带持续1秒的减速效果");
        }
        else if (skill.equals("净化")){
            skillDetailImage.setImageResource(R.mipmap.jinghua_detail);
            skillDetail1.setText("LV.15解锁");
            skillDetail2.setText("120秒CD：解除自身所有负面和控制效果并免疫控制持续1.5秒");
        }
        else if (skill.equals("弱化")){
            skillDetailImage.setImageResource(R.mipmap.ruohua_detail);
            skillDetail1.setText("LV.17解锁");
            skillDetail2.setText("90秒CD：减少身边敌人伤害输出50%持续3秒");
        }
        else if (skill.equals("闪现")){
            skillDetailImage.setImageResource(R.mipmap.shanxian_detail);
            skillDetail1.setText("LV.19解锁");
            skillDetail2.setText("120秒CD：向指定方向位移一段距离");
        }
    }*/
}
