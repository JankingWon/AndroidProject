package com.team1.kingofhonor.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.team1.kingofhonor.R;
import com.team1.kingofhonor.adapter.MyViewPagerAdapter;
import com.team1.kingofhonor.adapter.gridviewAdapter;
import com.team1.kingofhonor.equipment_detail;
import com.team1.kingofhonor.model.Equipment;
import com.team1.kingofhonor.sqlite.HeroSQLiteHelper;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class Fragment2 extends Fragment {
    private View view;
    private android.support.v7.widget.SearchView searchView;
    private ViewPager viewPager;
    private ViewGroup group;
    private ImageView[] ivPoints;
    private int totalPage;//总页数
    private int mPageSize = 16;//每页容量
    private int currentPage;
    private ArrayList<Equipment> equipmentArrayList;
    private ArrayList<View> viewPagerList;
    private HeroSQLiteHelper heroSQLiteHelper;
    private ListView equip_search_listview;
    private ArrayAdapter<String> searchHistoryAdapter;
    private AutoCompleteTextView autoCompleteTextView;
    private RadioGroup radioGroup;
    private MyViewPagerAdapter viewPagerAdapter;
    private gridviewAdapter mgridviewAdapter;
    public LayoutInflater myinflater;
    private AutoCompleteTextView searchtext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment2, container, false);
        initView();

        initData();
        totalPage = (int) Math.ceil(equipmentArrayList.size() * 1.0 / mPageSize);
        return view;
    }

    public void initView(){
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        group = (LinearLayout) view.findViewById(R.id.points);
        equip_search_listview = (ListView) view.findViewById(R.id.equip_search_listview);
        searchView = (android.support.v7.widget.SearchView) view.findViewById(R.id.search_equipment);
        searchtext = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        radioGroup = (RadioGroup) view.findViewById(R.id.equip_category);
        searchtext.setTextColor(Color.parseColor("#ffffff"));
        searchtext.setHintTextColor(Color.parseColor("#ffffff"));
    }

    public void initData(){
        equipmentArrayList = new ArrayList<Equipment>();
        heroSQLiteHelper = new HeroSQLiteHelper(getContext());
        equipmentArrayList = heroSQLiteHelper.getAllEquipment();
        myinflater = LayoutInflater.from(getContext());
        setViewPagerWithGridview();

        //设置小圆点
        ivPoints = new ImageView[totalPage];
        for (int i = 0; i < totalPage; i++){
            ImageView imageView = new ImageView(getActivity());
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(30,30);//布局参数,point的布局宽与高
            params.rightMargin = 40;//右边距
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

        //搜索框
        searchView.setIconified(false);//设置searchView处于展开状态
        searchView.setIconifiedByDefault(false);//搜索图标是否显示在搜索框内
        searchView.setSubmitButtonEnabled(true);//设置搜索框展开时是否显示提交按钮，可不显示
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);//让键盘的回车键设置成搜索
        searchView.setQueryHint("请输入装备名称");
        searchView.clearFocus();//清除焦点

        autoCompleteTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        autoCompleteTextView.setThreshold(1);//补全触发需要的字符
        autoCompleteTextView.setCompletionHint("最近5条记录");
        String[] mSearchHistory = getHistoryArray(SP_KEY_SEARCH);
        searchHistoryAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,mSearchHistory);
        autoCompleteTextView.setAdapter(searchHistoryAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                searchView.setQuery(searchHistoryAdapter.getItem(i),true);
            }
        });

        //搜索触发
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                for (int i = 0; i < equipmentArrayList.size(); i++){
                    if (equipmentArrayList.get(i).getName().equals(s)){
                        Equipment equip = equipmentArrayList.get(i);
                        Intent intent = new Intent(getContext(), equipment_detail.class);
                        intent.putExtra("equipment_data", equip);
                        startActivity(intent);
                        searchView.clearFocus();
                        saveSearchHistory();
                        searchHistoryAdapter.add(s);
                        return false;
                    }
                }
                Toast.makeText(getContext(),"没有此装备",Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        //切换类别
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                viewPagerList.clear();
                if (((RadioButton)view.findViewById(i)).getText().toString().equals("全部")){
                    equipmentArrayList = heroSQLiteHelper.getAllEquipment();
                    totalPage = 6;
                }
                else if(((RadioButton)view.findViewById(i)).getText().toString().equals("攻击") || ((RadioButton)view.findViewById(i)).getText().toString().equals("法术") || ((RadioButton)view.findViewById(i)).getText().toString().equals("防御")){
                    equipmentArrayList = heroSQLiteHelper.getEquipmentsWithCategory(((RadioButton)view.findViewById(i)).getText().toString());
                    totalPage = 2;
                }

                else{
                    equipmentArrayList = heroSQLiteHelper.getEquipmentsWithCategory(((RadioButton)view.findViewById(i)).getText().toString());
                    totalPage = 1;
                }
                group.removeAllViews();
                for (int t = 0; t < totalPage; t++){
                    ImageView imageView = new ImageView(getActivity());
                    LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(30,30);//布局参数,point的布局宽与高
                    params.rightMargin = 40;//右边距
                    imageView.setLayoutParams(params);
                    imageView.setBackgroundResource(R.drawable.point_bg);
                    if (t == 0){
                        imageView.setEnabled(true);
                    }
                    else {
                        imageView.setEnabled(false);
                    }
                    ivPoints[t] = imageView;
                    group.addView(imageView);
                }

                setViewPagerWithGridview();
            }
        });
    }

    //圆点设置
    private void setImageBackground(int selectItems) {
        for (int i = 0; i < ivPoints.length; i++) {
            if (i == selectItems) {
                ivPoints[i].setBackgroundResource(R.drawable.point_focus);
            } else {
                ivPoints[i].setBackgroundResource(R.drawable.point_normal);
            }
        }
    }

    //关于搜索记录的api
    private final String SP_NAME = "equip_history";
    private final String SP_KEY_SEARCH = "equip_search";

    private String getHistoryFromSharedPreferences(String key){
        SharedPreferences sp = getContext().getSharedPreferences(SP_NAME, MODE_PRIVATE);
        return sp.getString(SP_NAME, "");
    }

    private void saveHistoryToSharedPreferences(String key, String history){
        SharedPreferences sp = getContext().getSharedPreferences(SP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key,history);
        editor.apply();
    }

    private void clearHistoryInSharedPreferences(){
        SharedPreferences sp = getContext().getSharedPreferences(SP_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    private String[] getHistoryArray(String key){
        String[] array = getHistoryFromSharedPreferences(key).split(",");
        if(array.length > 5){
            String[] newArray = new String[5];
            System.arraycopy(array, 0 , newArray, 0, 10);
            return newArray;
        }
        return array;
    }

    private void saveSearchHistory(){
        String text = autoCompleteTextView.getText().toString().trim();
        if(TextUtils.isEmpty(text)){
            Toast.makeText(getContext(),"请输入查找字符",Toast.LENGTH_SHORT).show();
            return;
        }

        String old_text = getHistoryFromSharedPreferences(SP_KEY_SEARCH);
        StringBuilder sb;
        if(old_text.equals("")){
            sb = new StringBuilder();
        }else {
            sb = new StringBuilder(old_text);
        }
        sb.append(text + ",");

        if(!old_text.contains(text + ",")){
            saveHistoryToSharedPreferences(SP_KEY_SEARCH, sb.toString());
        }
    }

    private void setViewPagerWithGridview(){
        totalPage = (int) Math.ceil(equipmentArrayList.size() * 1.0 / mPageSize);
        viewPagerList = new ArrayList<View>();
        for (int i = 0; i < totalPage; i++){
            mgridviewAdapter = new gridviewAdapter(getContext(),equipmentArrayList, i, mPageSize);
            final GridView gridView = (GridView) myinflater.inflate(R.layout.gridview,viewPager,false);
            gridView.setAdapter(mgridviewAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Equipment equipment = (Equipment) adapterView.getItemAtPosition(i);
                    Intent intent = new Intent(getContext(),equipment_detail.class);
                    intent.putExtra("equipment_data",equipment);
                    startActivity(intent);
                }
            });
            //每一个GridView作为一个View对象添加到ViewPager集合中
            viewPagerList.add(gridView);
        }
        //设置ViewPager适配器
        viewPagerAdapter = new MyViewPagerAdapter(viewPagerList);
        viewPager.setAdapter(viewPagerAdapter);
    }

}
