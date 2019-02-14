package com.team1.kingofhonor.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.team1.kingofhonor.R;
import com.team1.kingofhonor.adapter.InscriptionAdapter;
import com.team1.kingofhonor.adapter.MySpinnerAdapter;
import com.team1.kingofhonor.model.Inscription;
import com.team1.kingofhonor.sqlite.HeroSQLiteHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class Fragment3 extends Fragment {
    private View view;
    private ListView listView;
    private Spinner colorChoose;
    private Spinner levelChoose;
    private Spinner typeChoose;
    private MySpinnerAdapter colorAdpter;
    private MySpinnerAdapter levelAdpter;
    private MySpinnerAdapter typeAdpter;
    private InscriptionAdapter inscriptionAdapter;
    private List<Inscription> data;
    private ImageView[] red;
    private ImageView[] green;
    private ImageView[] blue;
    private List<Inscription> redList;
    private List<Inscription> greenList;
    private List<Inscription> blueList;
    private List<Inscription> chooseList;
    private Button property;
    private Button cancel;
    private String color;
    private String type;
    private int level;
    private HeroSQLiteHelper sqLiteHelper;
    private ArrayAdapter<String> searchAdapter;
    private int show;
    private Button isShow;
    private ConstraintLayout inscriptionPic;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment3, container, false);
        listView = (ListView) view.findViewById(R.id.inscriptionList);
        data = new ArrayList<Inscription>();
        sqLiteHelper = new HeroSQLiteHelper(view.getContext());
        initData();
        Log.d("test", String.valueOf(data.size()));
        List<Inscription> inscriptions = new ArrayList<Inscription>();
        inscriptions.addAll(data);
        inscriptionAdapter = new InscriptionAdapter(inscriptions, view.getContext());
        redList = new ArrayList<Inscription>();
        greenList = new ArrayList<Inscription>();
        blueList = new ArrayList<Inscription>();
        chooseList = new ArrayList<Inscription>();
        red = new ImageView[10];
        green = new ImageView[10];
        blue = new ImageView[10];
        property = (Button)view.findViewById(R.id.allProperty);
        cancel = (Button)view.findViewById(R.id.inscriptionCancel);
        color = "all";
        type = "all";
        level = 0;
        show = 0;
        isShow = (Button)view.findViewById(R.id.inscriptionPicShow);
        inscriptionPic = (ConstraintLayout)view.findViewById(R.id.inscriptionPic);


        listView.setAdapter(inscriptionAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String color = inscriptionAdapter.getList().get(i).getColor();
                if(color.equals("red")) {
                    if(redList.size() < 10) {
                        redList.add(inscriptionAdapter.getList().get(i));
                        chooseList.add(inscriptionAdapter.getList().get(i));
                        red[redList.size()-1].setImageResource(data.get(i).getImage());
                    }
                }
                else if(color.equals("green")) {
                    if(greenList.size() < 10) {
                        greenList.add(inscriptionAdapter.getList().get(i));
                        chooseList.add(inscriptionAdapter.getList().get(i));
                        green[greenList.size()-1].setImageResource(inscriptionAdapter.getList().get(i).getImage());
                    }
                }
                else if(color.equals("blue")) {
                    if(blueList.size() < 10) {
                        blueList.add(inscriptionAdapter.getList().get(i));
                        chooseList.add(inscriptionAdapter.getList().get(i));
                        blue[blueList.size()-1].setImageResource(inscriptionAdapter.getList().get(i).getImage());
                    }
                }
            }
        });

        property.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String propertyString = "";
                Map<String, Double> allPro = new HashMap<String, Double>();
                for(Inscription inscription:chooseList) {
                    Map<String, Double> pro = inscription.getProperty();
                    Iterator<Map.Entry<String, Double>> iterator = pro.entrySet().iterator();
                    while(iterator.hasNext()) {
                        Map.Entry<String, Double> entry = iterator.next();
                        if(allPro.containsKey(entry.getKey())) {
                            Double newNum = allPro.get(entry.getKey()) + entry.getValue();
                            allPro.put(entry.getKey(), newNum);
                        }
                        else {
                            allPro.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
                Iterator<Map.Entry<String, Double>> iterator = allPro.entrySet().iterator();
                DecimalFormat df = new DecimalFormat("0.00");
                while(iterator.hasNext()) {
                    Map.Entry<String, Double> entry = iterator.next();
                    propertyString += entry.getKey()+": "+df.format(entry.getValue())+"\n";
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("所选铭文的属性").setMessage(propertyString).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redList.clear();
                blueList.clear();
                greenList.clear();
                chooseList.clear();
                for(int i = 0; i < 10; i++) {
                    red[i].setImageResource(R.mipmap.red);
                    green[i].setImageResource(R.mipmap.green);
                    blue[i].setImageResource(R.mipmap.blue);
                }
            }
        });

        colorChoose = (Spinner)view.findViewById(R.id.inscriptionColorChoose);
        levelChoose = (Spinner)view.findViewById(R.id.inscriptionLevelChoose);
        typeChoose = (Spinner)view.findViewById(R.id.inscriptionTypeChoose);

        final ArrayList<String> colorString = new ArrayList<String>();
        colorString.add("全部");
        colorString.add("红");
        colorString.add("绿");
        colorString.add("蓝");
        colorAdpter = new MySpinnerAdapter(view.getContext(), android.R.layout.simple_spinner_item, colorString);
        colorAdpter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        colorChoose.setAdapter(colorAdpter);
        colorChoose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0) {
                    if(!color.equals("all")) {
                        color = "all";
                        /*data.clear();
                        data = sqLiteHelper.getInscriptionWithCondition(level, type, color);
                        inscriptionAdapter.notifyDataSetChanged();*/
                        List<Inscription> inscriptions = new ArrayList<Inscription>();
                        for(Inscription inscription:data) {
                            inscriptions.add(inscription);
                        }
                        inscriptionAdapter.updateWithCategory(inscriptions, level, color, type);
                    }
                }
                else {
                    color = colorString.get(i);
                    if(color.equals("红")) {
                        color = "red";
                    }
                    else if(color.equals("绿")) {
                        color = "green";
                    }
                    else {
                        color = "blue";
                    }
                    /*data.clear();
                    data = sqLiteHelper.getInscriptionWithCondition(level, type, color);
                    inscriptionAdapter.notifyDataSetChanged();*/
                    List<Inscription> inscriptions = new ArrayList<Inscription>();
                    for(Inscription inscription:data) {
                        inscriptions.add(inscription);
                    }
                    inscriptionAdapter.updateWithCategory(inscriptions, level, color, type);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayList<String> levelString = new ArrayList<String>();
        levelString.add("全部");
        levelString.add("一级");
        levelString.add("二级");
        levelString.add("三级");
        levelString.add("四级");
        levelString.add("五级");
        levelAdpter = new MySpinnerAdapter(view.getContext(), android.R.layout.simple_spinner_item, levelString);
        levelAdpter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        levelChoose.setAdapter(levelAdpter);
        levelChoose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(level != i) {
                    level = i;
                   /*data.clear();
                   data = sqLiteHelper.getInscriptionWithCondition(level, type, color);
                   Log.d("test1", String.valueOf(data.size()));*/
                    List<Inscription> inscriptions = new ArrayList<Inscription>();
                    for(Inscription inscription:data) {
                        inscriptions.add(inscription);
                    }
                    inscriptionAdapter.updateWithCategory(inscriptions, level, color, type);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final ArrayList<String> typeString = new ArrayList<String>();
        typeString.add("全部");
        typeString.add("攻击");
        typeString.add("生命");
        typeString.add("防御");
        typeString.add("功能");
        typeString.add("吸血");
        typeString.add("攻速");
        typeString.add("暴击");
        typeString.add("穿透");
        typeAdpter = new MySpinnerAdapter(view.getContext(), android.R.layout.simple_spinner_item, typeString);
        typeAdpter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        typeChoose.setAdapter(typeAdpter);
        typeChoose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0) {
                    if(!type.equals("all")) {
                        type = "all";
                        /*data.clear();
                        data = sqLiteHelper.getInscriptionWithCondition(level, type, color);
                        inscriptionAdapter.notifyDataSetChanged();*/
                        List<Inscription> inscriptions = new ArrayList<Inscription>();
                        for(Inscription inscription:data) {
                            inscriptions.add(inscription);
                        }
                        inscriptionAdapter.updateWithCategory(inscriptions, level, color, type);
                    }
                }
                else {
                    type = typeString.get(i);
                    /*data.clear();
                    data = sqLiteHelper.getInscriptionWithCondition(level, type, color);
                    inscriptionAdapter.notifyDataSetChanged();*/
                    List<Inscription> inscriptions = new ArrayList<Inscription>();
                    for(Inscription inscription:data) {
                        inscriptions.add(inscription);
                    }
                    inscriptionAdapter.updateWithCategory(inscriptions, level, color, type);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        blue[0] = (ImageView)view.findViewById(R.id.blue1);
        blue[1] = (ImageView)view.findViewById(R.id.blue2);
        blue[2] = (ImageView)view.findViewById(R.id.blue3);
        blue[3] = (ImageView)view.findViewById(R.id.blue4);
        blue[4] = (ImageView)view.findViewById(R.id.blue5);
        blue[5] = (ImageView)view.findViewById(R.id.blue6);
        blue[6] = (ImageView)view.findViewById(R.id.blue7);
        blue[7] = (ImageView)view.findViewById(R.id.blue8);
        blue[8] = (ImageView)view.findViewById(R.id.blue9);
        blue[9] = (ImageView)view.findViewById(R.id.blue10);
        red[0]= (ImageView)view.findViewById(R.id.red1);
        red[1]= (ImageView)view.findViewById(R.id.red2);
        red[2]= (ImageView)view.findViewById(R.id.red3);
        red[3]= (ImageView)view.findViewById(R.id.red4);
        red[4]= (ImageView)view.findViewById(R.id.red5);
        red[5]= (ImageView)view.findViewById(R.id.red6);
        red[6]= (ImageView)view.findViewById(R.id.red7);
        red[7]= (ImageView)view.findViewById(R.id.red8);
        red[8]= (ImageView)view.findViewById(R.id.red9);
        red[9] = (ImageView)view.findViewById(R.id.red10);
        green[0]= (ImageView)view.findViewById(R.id.green1);
        green[1]= (ImageView)view.findViewById(R.id.green2);
        green[2]= (ImageView)view.findViewById(R.id.green3);
        green[3]= (ImageView)view.findViewById(R.id.green4);
        green[4]= (ImageView)view.findViewById(R.id.green5);
        green[5]= (ImageView)view.findViewById(R.id.green6);
        green[6]= (ImageView)view.findViewById(R.id.green7);
        green[7]= (ImageView)view.findViewById(R.id.green8);
        green[8]= (ImageView)view.findViewById(R.id.green9);
        green[9] = (ImageView)view.findViewById(R.id.green10);

        final SearchView searchView = view.findViewById(R.id.inscription_edit_search);
        searchView.setIconified(false);//设置searchView处于展开状态
        searchView.onActionViewExpanded();// 当展开无输入内容的时候，没有关闭的图标
        searchView.setQueryHint("输入查找的铭文");//设置默认无内容时的文字提示
        searchView.setIconifiedByDefault(false);//默认为true在框内，设置false则在框外
        searchView.setIconified(false);//展开状态
        searchView.clearFocus();//清除焦点
        searchView.isSubmitButtonEnabled();//键盘上显示搜索图标

        AutoCompleteTextView completeText = searchView.findViewById(R.id.search_src_text) ;
        completeText.setTextColor(getResources().getColor(android.R.color.white));//设置内容文字颜色
        completeText.setHintTextColor(getResources().getColor(R.color.gainsboro));//设置提示文字颜色
        completeText.setThreshold(0);
        searchAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, inscriptionAdapter.getAllNames());
        completeText.setAdapter(searchAdapter);
        completeText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position,long id){
                searchView.setQuery(searchAdapter.getItem(position),true);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query){
                List<Inscription> inscriptions = new ArrayList<Inscription>();
                for(Inscription inscription:data) {
                    inscriptions.add(inscription);
                }
                inscriptionAdapter.updateWithName(inscriptions, query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText){
                if(newText.length() == 0) {
                    List<Inscription> inscriptions = new ArrayList<Inscription>();
                    for(Inscription inscription:data) {
                        inscriptions.add(inscription);
                    }
                    inscriptionAdapter.updateWithName(inscriptions, newText);
                }
                return false;
            }
        });

        isShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(show == 0) {
                    isShow.setText("显示");
                    inscriptionPic.setVisibility(View.GONE);
                    show = 1;
                }
                else {
                    isShow.setText("隐藏");
                    inscriptionPic.setVisibility(View.VISIBLE);
                    show = 0;
                }

            }
        });




        return view;
    }


    public void initData() {
        data = sqLiteHelper.getAllInscription();
    }
}
