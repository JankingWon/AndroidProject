package com.example.jason.finalproj.Login;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.jason.finalproj.DB;
import com.example.jason.finalproj.R;

import java.sql.Connection;

public class SignFragment extends Fragment {
    private com.dd.CircularProgressButton btn;
    private TextInputLayout input_account;
    private TextInputLayout input_pwd;
    private EditText input_pwd2;
    private RadioGroup input_sex;
    private Bitmap initPhoto;
    private boolean isMale;
    private boolean password_ok=false;
    private com.example.jason.finalproj.DB mydb;
    private Handler handler;
    private String pwd;
    private String pwd2;
    private final static int HASACCOUNT = 0;
    private final static int ADDSUCCESSED = 2;
    private final static int ADDFAILED = 3;
    private final static int RUNTIMEERRO = 4;
    private Button toLoginBtn;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.i("test","onCreatedView");
        View view = inflater.inflate(R.layout.fragment_sign,container,false);
        btn = (com.dd.CircularProgressButton) view.findViewById(R.id.signbtn);
        input_account = (TextInputLayout) view.findViewById(R.id.Laccount);
        input_pwd = (TextInputLayout) view.findViewById(R.id.Lpwd);
        input_pwd2 = (EditText) view.findViewById(R.id.pwd2);
        input_sex = (RadioGroup) view.findViewById(R.id.Rg_sex);
        toLoginBtn = (Button) view.findViewById(R.id.tologinbtn);
        clear();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initPhoto = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.user1);
        mydb = new DB();
        setRadiobtn();
        setInputLayout();
        setBtn();
        setHandler();
        toLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity loginActivity = (LoginActivity) getActivity();
                loginActivity.showLogin();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        clear();
    }

    public void setRadiobtn(){
        input_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                isMale = checkedId==R.id.male;
            }
        });
    }

    public void setInputLayout(){
        input_account.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_account.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        input_pwd.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_pwd.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        input_pwd2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_pwd.setErrorEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable s) {
                pwd = input_pwd.getEditText().getText().toString();
                pwd2 = input_pwd2.getText().toString();
                if(pwd2.isEmpty()) password_ok = false;
                else if(!pwd.equals(pwd2)){
                    input_pwd.setErrorEnabled(true);
                    input_pwd.setError("两次输入不一致");
                    password_ok = false;
                }else password_ok = true;
            }
        });
    }


    public void setBtn(){
        btn.setIndeterminateProgressMode(true);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //点击注册 判断所有项目是否合法
                if(btn.getProgress()!=0) btn.setProgress(0);
                else{
                    int sex = isMale?1:0;
                    String account = input_account.getEditText().getText().toString();
                    String pwd = input_pwd.getEditText().getText().toString();
                    String pwd2 = input_pwd2.getText().toString();
                    if(pwd.equals(pwd2)&&!pwd.isEmpty()){password_ok=true;}
                    else {Toast.makeText(getActivity(),"请检检查密码输入",Toast.LENGTH_SHORT).show();}
                    if(password_ok){
                        if(account.isEmpty()){
                            Toast.makeText(getActivity(),"请检查空值",Toast.LENGTH_SHORT).show();
                        }else {
                            btn.setProgress(50);
                            Toast.makeText(getActivity(),"hey",Toast.LENGTH_SHORT).show();
                            submit(account,sex);
                        }
                    }else Toast.makeText(getActivity(),"请检查信息是否正确",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void submit(final String account,final int sex){
        final  Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Connection  con = mydb.getConnection();
                    if(mydb.isaccount(account,con)){
                        handler.obtainMessage(HASACCOUNT).sendToTarget();
                    }
                    else if(mydb.addUser(initPhoto,"unknow",sex,"","","",account,pwd,con)){
                        handler.obtainMessage(ADDSUCCESSED).sendToTarget();
                        mydb.closeConnection(con);
                    }else handler.obtainMessage(ADDFAILED).sendToTarget();
                }catch (RuntimeException e){
                    handler.obtainMessage(RUNTIMEERRO).sendToTarget();
                }
            }
        });
        thread.start();
    }

    public void setHandler(){
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case HASACCOUNT:
                        input_account.setErrorEnabled(true);
                        input_account.setError("账号已存在");
                        input_account.requestFocus();
                        btn.setProgress(-1);
                        break;
                    case ADDSUCCESSED:
                        btn.setProgress(100);
                        new android.os.Handler().postDelayed(new Runnable() {
                            public void run() {
                                ((LoginActivity) getActivity()).showLogin();
                            }

                        }, 1000);
                        break;
                    case ADDFAILED:
                        btn.setProgress(-1);
                        Snackbar.make(btn.getRootView(), "注册失败，请检查网络连接",Snackbar.LENGTH_SHORT)
                                .setDuration(5000)
                                .setActionTextColor(Color.WHITE)
                                .show();
                        break;
                    case RUNTIMEERRO:
                        btn.setProgress(-1);
                        break;
                    default:break;
                }
            }
        };
    }

    public void clear(){
        Log.i("test","clear");
        password_ok = false;
        input_account.getEditText().setText("");
        input_pwd.getEditText().setText("");
        input_pwd2.setText("");
        btn.setProgress(0);
    }
}
