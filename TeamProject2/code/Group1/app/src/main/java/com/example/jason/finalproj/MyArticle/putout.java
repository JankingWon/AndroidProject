package com.example.jason.finalproj.MyArticle;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.TextView;

import com.example.jason.finalproj.DB;
import com.example.jason.finalproj.Login.LoginActivity;
import com.example.jason.finalproj.R;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class putout extends AppCompatActivity {
    private RadioGroup type;
    private CheckBox web;
    private CheckBox andro;
    private CheckBox ios;
    private CheckBox user_defined;
    private EditText title;
    private EditText content;
    private Button putoutbutton;
    private Button endtime;

    private TextView titlename;
    private EditText define;

    private Toolbar toolbar;

    private String inputtitle;
    private String inputbody;
    private int inputtype;
    private int inputstate;
    private java.util.Date inputdate;

    private int inputu_id;
    boolean is;
    private DB db = new DB();
    private String s;

    final int DATE_DIALOG = 1;
    int mYear, mMonth, mDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putout);

        final SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHAREDNAME2,MODE_PRIVATE);
        inputu_id = sharedPreferences.getInt(LoginActivity.SHAREDNAME2,-1);

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbar();

        findview();

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
                    LayoutInflater factor = LayoutInflater.from(putout.this);
                    View view_in = factor.inflate(R.layout.userdefine, null);
                    final AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(putout.this);

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

        /**
         * 发布按钮监听器
         */
        putoutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().toString().length()!=0&&content.getText().toString().length()!=0){
                    setThread1();
                }else {
                    if(title.getText().toString().length()==0)
                    {
                        title.setError("请输入内容");
                    }
                    if(content.getText().toString().length()==0)
                    {
                        content.setError("请输入内容");
                    }
                    if(endtime.getText().toString().length()==0)
                    {
                        endtime.setError("请输入时间");
                    }
                }
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
     * 找出所有view
     */
    private void findview(){
        type = (RadioGroup) findViewById(R.id.typebutton);
        web = (CheckBox) findViewById(R.id.web);
        andro = (CheckBox) findViewById(R.id.android);
        ios = (CheckBox) findViewById(R.id.ios);
        user_defined = (CheckBox) findViewById(R.id.user_defined);
        title = (EditText) findViewById(R.id.inputtitle);
        content = (EditText) findViewById(R.id.inputContent);
        endtime = (Button) findViewById(R.id.inputtime);
        putoutbutton = (Button) findViewById(R.id.putoutbutton);

        titlename = (TextView) findViewById(R.id.title1);
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
                finish();
                break;

            default:
                break;
        }
        return true;
    }

    public void setThread1(){
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
                inputstate = 1;
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
                db.addArticle(inputtitle,inputbody,inputtype,inputstate,inputdate,inputemblems,inputu_id,con);
                handler.obtainMessage(123).sendToTarget();
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
                    titlename.setText("成功");
                    break;
            }
        }
    };

}
