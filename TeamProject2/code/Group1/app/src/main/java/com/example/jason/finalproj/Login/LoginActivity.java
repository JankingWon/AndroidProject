package com.example.jason.finalproj.Login;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class LoginActivity extends AppCompatActivity {
    private LoginFragment loginFragment;
    private SignFragment signFragment;
    public final static String SHAREDNAME2 = "now u_id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.jason.finalproj.R.layout.activity_login);
        showLogin();
    }


    public void showLogin(){
        Log.i("test","showLogin");
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if(loginFragment==null){
            loginFragment = new LoginFragment();
        }
        transaction.replace(com.example.jason.finalproj.R.id.fragmelayout,loginFragment);
        transaction.commit();

    }
    public void showSign(){
        Log.i("test","showSign");
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if(signFragment==null){
            signFragment = new SignFragment();
        }
        transaction.replace(com.example.jason.finalproj.R.id.fragmelayout,signFragment);
        transaction.addToBackStack(null).commit();
    }

    public void loadsuccessed(int u_id){
        Log.i("test","loadsuccessed");
        final SharedPreferences sharedPreferences =getSharedPreferences(SHAREDNAME2,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("now u_id",u_id);
        editor.commit();
        Intent intent = new Intent(LoginActivity.this,com.example.jason.finalproj.MainInterface.MainActivity.class);
        startActivity(intent);
    }
}
