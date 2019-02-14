package com.example.jason.finalproj.Update;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.jason.finalproj.DB;
import com.example.jason.finalproj.Login.LoginActivity;
import com.example.jason.finalproj.R;
import com.example.jason.finalproj.Users;
import com.example.jason.finalproj.Utils;

import java.sql.Connection;

import de.hdodenhof.circleimageview.CircleImageView;

public class updatedata extends AppCompatActivity {
    private CircleImageView photo;
    private EditText inputname;
    private RadioGroup inputsex;
    private EditText inputgithub;
    private CheckBox web;
    private CheckBox andro;
    private CheckBox ios;
    private EditText inputconnect;
    private EditText inputintroduction;
    private Button button_update;
    private Toolbar toolbar;
    private Utils utils;
    private DB db = new DB();
    private Connection con;
    protected static final int TAKE_PICTURE = 1;
    protected static final int CHOOSE_PHOTO = 0;
    private static final int CROP_SMALL_PICTURE = 2;

    private int u_id = 19;
    private Users users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updatedata);

        final SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHAREDNAME2,MODE_PRIVATE);
        u_id = sharedPreferences.getInt(LoginActivity.SHAREDNAME2,-1);

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbar();

        findview();
        setThread2();
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(updatedata.this);
                builder.setTitle("上传头像");
                final String[] items = new String[]{"拍摄", "从相册选择"};
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            Toast.makeText(updatedata.this, "您选择了[拍摄]", Toast.LENGTH_LONG).show();
                            takepicture();
                        }
                        if (i == 1) {
                            Toast.makeText(updatedata.this, "您选择了[从相册选择]", Toast.LENGTH_LONG).show();
                            openAlbum();
                        }

                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(updatedata.this, "您选择了[取消]", Toast.LENGTH_LONG).show();
                    }
                });
                builder.show();
            }
        });

        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputname.getText().toString().length() != 0 && inputconnect.getText().toString().length() != 0) {
                    setThread();
                } else {
                    if (inputname.getText().toString().length() == 0) {
                        inputname.setError("请输入用户名");
                    }
                    if (inputconnect.getText().toString().length() == 0) {
                        inputconnect.setError("请输入联系方式");
                    }
                }
            }
        });

    }

    private void findview(){
        photo = (CircleImageView) findViewById(R.id.photo);
        inputname = (EditText) findViewById(R.id.InputName);
        inputsex = (RadioGroup) findViewById(R.id.InputSex);
        inputgithub = (EditText) findViewById(R.id.Inputgithub);
        inputconnect = (EditText) findViewById(R.id.Inputconnect);
        inputintroduction = (EditText) findViewById(R.id.InputIntroduction);
        web = (CheckBox) findViewById(R.id.web_regist);
        andro = (CheckBox) findViewById(R.id.android_regist);
        ios = (CheckBox) findViewById(R.id.ios_regist);
        button_update = (Button) findViewById(R.id.button_updatedata);
    }

    public void updateUI(Users users){
        photo.setImageBitmap(users.getPhoto());
        inputname.setText(users.getName());
        if (users.getSex()==0) {
            inputsex.check(R.id.reg_nan);
        } else
            inputsex.check(R.id.reg_nv);
        inputgithub.setText(users.getGithubname());
        inputconnect.setText(users.getEmail());
        inputintroduction.setText(users.getDescription());
        if(users.getEmblems() != null){
            if(users.getEmblems()[0]==1){
                web.setChecked(true);
            }
            if(users.getEmblems()[1]==1){
                andro.setChecked(true);
            }
            if(users.getEmblems()[2]==1){
                ios.setChecked(true);
            }
        }

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
                db.closeConnection(con);
                setResult(2);
                finish();
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode) {
                case TAKE_PICTURE:
                    if (data.getData() != null || data.getExtras() != null) { // 防止没有返回结果
                        Uri uri = data.getData();
                        if (uri != null) {
                            setImageToView(data, photo); // 拿到图片
                        }
                    }
                    setImageToView(data, photo);
                    break;
                case CHOOSE_PHOTO:
                    Uri uri = data.getData();
                    cropPhoto(uri);
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data, photo); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }}
    }

    private void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //设置裁剪
        intent.putExtra("crop",true);

        //aspectX, aspectY：宽高比
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        //outputX, outputY: 裁剪宽高比
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
        overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
    }

    public static void setImageToView(Intent data, ImageView imageView) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            //接受传递的数据
            Bitmap photo = extras.getParcelable("data");
            imageView.setImageBitmap(photo);
        }
    }

    private void takepicture() {
        Intent intentCamera = new Intent();
        intentCamera.setAction("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intentCamera, TAKE_PICTURE);//开启相机，传入上面的Intent对象
        overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
    }

    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        this.startActivityForResult(intent, CHOOSE_PHOTO);
        overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
    }

    public void setThread(){
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    con = db.getConnection();
                    Bitmap bitmap = utils.DrawableToBitmap(photo.getDrawable());
                    String name = inputname.getText().toString();
                    int sex = 0;
                    RadioButton rb = (RadioButton) findViewById(inputsex.getCheckedRadioButtonId());
                    if (rb.getText().toString().equals("男"))
                        sex = 0;
                    else if (rb.getText().toString().equals("女"))
                        sex = 1;
                    String githubname = inputgithub.getText().toString();
                    String description = inputintroduction.getText().toString();
                    String email = inputconnect.getText().toString();
                    String account = "111";
                    String password = "111";
                    int isweb = (web.isChecked()) ? 1 : 0;
                    int isandroid = (andro.isChecked()) ? 1 : 0;
                    int isios = (ios.isChecked()) ? 1 : 0;
                    Integer[] inputemblems = {isweb, isandroid, isios};
                    if (db.search(name, con) && db.getUidByName(name, con) != u_id) {
                        handler.obtainMessage(1).sendToTarget();
                    } else {
                        String curr_account = db.getAccountByUid(u_id, con);
                        db.updateUserByAccount(curr_account, name, sex, githubname, description, email, con);
                        db.updateUphotoByAccount(curr_account, bitmap, con);
                        db.updateEmblemById(u_id, inputemblems, con);
                        handler.obtainMessage(2).sendToTarget();
                        db.closeConnection(con);
                        setResult(2);
                        finish();
                    }
                } catch (Exception e) {
                    Log.d("fail to update", e.getMessage());
                    e.printStackTrace();
                }
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
                case 1:
                    inputname.setError("用户名已存在");
                    break;
                case 2:
                    Toast.makeText(updatedata.this,"成功",Toast.LENGTH_SHORT).show();
                    break;
                case 123:
                    updateUI(users);
            }
        }
    };

    public void setThread2(){
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    con = db.getConnection();
                    users = db.getUserById(u_id, con);
                    handler.obtainMessage(123).sendToTarget();
                } catch (Exception e) {
                    Log.d("fail to update", e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        );
        thread.start();
    }

}

