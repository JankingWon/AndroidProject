package com.janking.sysuhealth;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //消息提示
    String message = new String("查询失败");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        event();
    }

    public void event(){
        //获取输入文本
        final EditText editText = (EditText)findViewById(R.id.edit_text);

        //获取单选内容
        final RadioGroup radioGroup = (RadioGroup)findViewById(R.id.rg);

        //
        final ImageButton toSecond = findViewById(R.id.funny_image);

        //按钮点击事件
        final Button btn = (Button) findViewById(R.id.button);
        if(btn != null){
            btn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){

                    message = (editText.getText().toString().isEmpty() ? "搜索内容不能为空" :
                            (editText.getText().toString().equals("Health") ?((RadioButton)findViewById(radioGroup.getCheckedRadioButtonId())).getText() + "搜索成功": "搜索失败"));
                    //对话框
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setTitle("提示").setMessage(message).setPositiveButton("确认",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(),"对话框“确定”按钮被点击",Toast.LENGTH_SHORT).show();
                                }
                            }).setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(),"对话框“取消”按钮被点击",Toast.LENGTH_SHORT).show();
                                }
                            }).create().show();
                }
            });
        }

        //单选按钮更改事件
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
                Toast.makeText(getApplicationContext(),((RadioButton)findViewById(radioGroup.getCheckedRadioButtonId())).getText() + "被选中",Toast.LENGTH_SHORT).show();

            } });

        //更换页面
        toSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 给bnt1添加点击响应事件
                Intent intent =new Intent(MainActivity.this,SecondActivity.class);
                //启动
                startActivity(intent);
            }
        });
    }


}