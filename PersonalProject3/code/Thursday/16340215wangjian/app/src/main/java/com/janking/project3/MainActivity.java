package com.janking.project3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button_ok = findViewById(R.id.ok);
        final Button button_clear = findViewById(R.id.clear);
        final TextView pw1 = findViewById(R.id.new_password);
        final TextView pw2 = findViewById(R.id.confirm_password);
        final TextView pw = findViewById(R.id.login_password);

        pw1.setVisibility(View.VISIBLE);
        pw2.setVisibility(View.VISIBLE);
        pw.setVisibility(View.INVISIBLE);
        //读取状态
        SharedPreferences sp = MainActivity.this.getSharedPreferences("MY_PASSWORD", Context.MODE_PRIVATE);
        if(sp != null){
            String last_status = sp.getString("STATUS", null);
            if(last_status != null){
                pw1.setVisibility(View.INVISIBLE);
                pw2.setVisibility(View.INVISIBLE);
                pw.setVisibility(View.VISIBLE);
            }
        }


        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //还没注册密码成功
                if(pw.getVisibility() != View.VISIBLE){
                    if(pw1.getText().toString().isEmpty()){
                        Toast.makeText(MainActivity.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
                        //清空文本框中内容
                        //模拟点击按钮
                        button_clear.performClick();
                    }else if(! pw1.getText().toString().equals(pw2.getText().toString())){
                        Toast.makeText(MainActivity.this, "Password mismatch.", Toast.LENGTH_SHORT).show();
                        button_clear.performClick();
                    }else{
                        //getActivity()得到的就是MainActivity.this，不需要用这个函数了
                        SharedPreferences sharedPref = MainActivity.this.getSharedPreferences("MY_PASSWORD", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        //保存注册密码
                        editor.putString("NEW_PASSWORD", pw1.getText().toString());
                        //保存已注册状态
                        editor.putString("STATUS", "LOGIN");
                        //系统提醒我将commit改成apply的
                        editor.apply();
                        Toast.makeText(MainActivity.this, "Successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                        startActivity(intent);
                    }
                }else{
                    SharedPreferences sharedPref = MainActivity.this.getSharedPreferences("MY_PASSWORD", Context.MODE_PRIVATE);
                    String real_password = sharedPref.getString("NEW_PASSWORD", null);
                    //if(!pw.getText().equals(real_password))我之前这样写是错误的
                    if(!pw.getText().toString().equals(real_password)){
                        Toast.makeText(MainActivity.this, "Password incorrect", Toast.LENGTH_SHORT).show();
                        button_clear.performClick();
                    }else{
                        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                        startActivity(intent);
                    }
                }

            }
        });

        button_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空文本框中内容
                pw1.setText("");
                pw2.setText("");
                pw.setText("");
            }
        });


    }
    //当此Acticity暂停时直接结束（移出栈）
    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.this.finish();
    }
}
