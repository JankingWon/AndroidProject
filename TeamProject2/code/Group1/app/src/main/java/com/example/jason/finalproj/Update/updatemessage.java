package com.example.jason.finalproj.Update;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.jason.finalproj.Article;
import com.example.jason.finalproj.DB;
import com.example.jason.finalproj.R;
import com.kyleduo.switchbutton.SwitchButton;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class updatemessage extends AppCompatActivity {
    private RadioGroup type;
    private CheckBox web;
    private CheckBox andro;
    private CheckBox ios;
    private CheckBox user_defined;
    private EditText title;
    private EditText content;
    private Button updatebutton;
    private Button deletebutton;
    private SwitchButton changeswitch;
    private EditText define;
    private Button endtime;

    private String inputtitle;
    private String inputbody;
    private int inputtype;
    private int inputstate = 0 ;
    private java.util.Date inputdate;
    private DB db = new DB();
    private int inputu_id;
    private Toolbar toolbar;

    private Article myarticle = new Article();
    private boolean ret;
    private Integer[] emblem = new Integer[]{};
    private String originaltime;

    private int a_id = 1;

    final int DATE_DIALOG = 1;
    int mYear, mMonth, mDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updatemessage);

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbar();

        findview();

        Intent intent = this.getIntent();
        if(intent.getExtras()!=null) {
            a_id = intent.getIntExtra("a_id",-1);
        }
        setThread3();

        endtime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG);
            }
        });

        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        /**
         * 自定义box的监听器
         */
        user_defined.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    LayoutInflater factor = LayoutInflater.from(updatemessage.this);
                    View view_in = factor.inflate(R.layout.userdefine, null);
                    final AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(updatemessage.this);

                    define = (EditText) view_in.findViewById(R.id.define);

                    alertDialog1.setView(view_in);
                    alertDialog1.setTitle("根据您的需要自定义范围");
                    alertDialog1.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(define.getText().toString().length()!=0){
                                user_defined.setText(define.getText().toString());
                            }
                            else {
                                user_defined.setError("请输入内容");
                            }
                        }
                    });
                    alertDialog1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog1.show();
                }
            }
        });

        changeswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    inputstate = 1;
                }
                else {
                    inputstate = 0;
                }
            }
        });
        /**
         * 发布按钮监听器
         */
        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().toString().length()!=0&&content.getText().toString().length()!=0){
                    setThread();
                }else {
                    if(title.getText().toString().length()==0)
                    {
                        title.setError("请输入内容");
                    }
                    if(content.getText().toString().length()==0)
                    {
                        content.setError("请输入内容");
                    }
                }
            }
        });

        deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setThread2();
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG:
                return new DatePickerDialog(this, mdateListener, mYear, mMonth, mDay);
        }
        return null;
    }

    public void display() {
        endtime.setText(new StringBuffer().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay).append(" "));
    }

    private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            display();
        }
    };

    /**
     * 更新ui
     * @param myarticle
     * @param emblem
     * @param ret
     */
    public void updateUI(Article myarticle,Integer[] emblem,boolean ret){
        Toast.makeText(updatemessage.this,myarticle.getTitle(),Toast.LENGTH_SHORT).show();
        title.setText(myarticle.getTitle());
        content.setText(myarticle.getBody());
        changeswitch.setChecked(ret);
        switch (myarticle.getType()){
            case 1:
                type.check(R.id.match_update);
                break;
            case 2:
                type.check(R.id.proposal_update);
                break;

        }
        if(emblem[0]==1){
            web.setChecked(true);
        }
        if(emblem[1]==1){
            andro.setChecked(true);
        }
        if(emblem[2]==1){
            ios.setChecked(true);
        }
        endtime.setText(originaltime);
    }

    private void findview(){
        type = (RadioGroup) findViewById(R.id.typebutton_update);
        web = (CheckBox) findViewById(R.id.web_update);
        andro = (CheckBox) findViewById(R.id.android_update);
        ios = (CheckBox) findViewById(R.id.ios_update);
        user_defined = (CheckBox) findViewById(R.id.user_defined_update);
        title = (EditText) findViewById(R.id.inputtitle_update);
        content = (EditText) findViewById(R.id.inputContent_update);
        updatebutton = (Button) findViewById(R.id.button_updatemessage);
        deletebutton = (Button) findViewById(R.id.button_delete);
        changeswitch = (SwitchButton) findViewById(R.id.status_change);
        endtime = (Button) findViewById(R.id.inputtime_update);
    }

    //toolbar设置函数
    public void setToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationIcon(R.drawable.previous);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }


    //重写项目选择函数
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(2);
                finish();
                break;

            default:
                break;
        }
        return true;
    }

    /**
     * 更新线程
     */
    public void setThread(){
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Connection con = db.getConnection();
                inputtitle = title.getText().toString();
                inputbody = content.getText().toString();
                RadioButton rb = (RadioButton)findViewById(type.getCheckedRadioButtonId());
                if(rb.getText().toString().equals("室外"))
                    inputtype = 1;
                else if(rb.getText().toString().equals("室内"))
                    inputtype = 2;
                else if(rb.getText().toString().equals("个人招募"))
                    inputtype = 3;
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                String time = endtime.getText().toString();
                try {
                    inputdate = sdf.parse(time);
                }catch (ParseException e){
                    Log.i("ss","bbb");
                }
                int isweb=(web.isChecked()==true)?1:0;
                int isandroid = (andro.isChecked()==true)?1:0;
                int isios = (ios.isChecked()==true)?1:0;
                Integer[] inputemblems = {isweb,isandroid,isios};
                db.updateArticleById(a_id,inputtitle,inputbody,inputtype,inputstate,inputdate,inputemblems,con);
                db.closeConnection(con);
                setResult(2);
                finish();
            }
        }
        );
        thread.start();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 123:
                    Toast.makeText(updatemessage.this,"成功",Toast.LENGTH_SHORT).show();
                    ret=(myarticle.getState()==1)?true:false;
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                    originaltime=sdf.format(myarticle.getDate());
                    updateUI(myarticle,myarticle.getEmblems(),ret);
                    break;
            }
        }
    };

    /**
     * 删除线程
     */
    public void setThread2(){
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Connection con = db.getConnection();
                db.deleteArticleById(a_id,con);
                db.closeConnection(con);
                setResult(3);
                finish();
            }
        }
        );
        thread.start();
    }

    public void setThread3(){
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Connection conn = db.getConnection();
                myarticle = db.getArticleById(a_id,conn);
                handler.obtainMessage(123).sendToTarget();
            }
        }
        );
        thread.start();
    }


}
