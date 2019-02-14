package com.example.jason.finalproj.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.jason.finalproj.R;

public class StartActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGHT = 2000; // 两秒后进入系统

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        final Intent mainIntent = new Intent(StartActivity.this,
                LoginActivity.class);
        new android.os.Handler().postDelayed(new Runnable() {
            public void run() {
                StartActivity.this.startActivity(mainIntent);
                StartActivity.this.finish();
            }

        }, SPLASH_DISPLAY_LENGHT);
    }
}
