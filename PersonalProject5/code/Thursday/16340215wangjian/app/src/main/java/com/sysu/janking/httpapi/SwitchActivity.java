package com.sysu.janking.httpapi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sysu.janking.httpapi.BILIBILI.MainActivity;
import com.sysu.janking.httpapi.GITHUB.RepoActivity;

public class SwitchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch);

        Button lab10 = findViewById(R.id.lab10);
        Button lab11 = findViewById(R.id.lab11);
        //跳转到BILIBILIAPI
        lab10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SwitchActivity.this, MainActivity.class));
            }
        });
        //跳转到GITHUBAPI
        lab11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SwitchActivity.this, RepoActivity.class));
            }
        });
    }
}
