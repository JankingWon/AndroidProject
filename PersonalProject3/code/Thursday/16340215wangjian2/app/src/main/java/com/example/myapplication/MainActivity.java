package com.example.myapplication;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.net.Uri;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Boolean status = true;//false表示注册，true表示登录
    ImageButton register_image;
    String default_image = "android.resource://com.example.myapplication/" + R.mipmap.me;
    UserSQLiteHelper userSQLiteHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //userSQLiteHelper.dropComment();
        userSQLiteHelper = new UserSQLiteHelper(this);
        final RadioGroup radioGroup = findViewById(R.id.radio_group);
        final EditText login_username = findViewById(R.id.login_username);
        final EditText login_password = findViewById(R.id.login_password);
        final EditText register_username = findViewById(R.id.register_username);
        final EditText register_password = findViewById(R.id.register_password);
        final EditText register_confirm_password = findViewById(R.id.register_confirm_password);
        register_image = findViewById(R.id.register_image);
        Button button_ok = findViewById(R.id.button_ok);
        Button button_clear = findViewById(R.id.button_clear);

        register_image.setImageResource(R.mipmap.add);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(((RadioButton)findViewById(checkedId)).getText().toString().equals("Login")){
                    register_confirm_password.setVisibility(View.GONE);
                    register_image.setVisibility(View.GONE);
                    register_password.setVisibility(View.GONE);
                    register_username.setVisibility(View.GONE);

                    login_username.setVisibility(View.VISIBLE);
                    login_password.setVisibility(View.VISIBLE);
                    status = true;
                }else{
                    register_confirm_password.setVisibility(View.VISIBLE);
                    register_image.setVisibility(View.VISIBLE);
                    register_password.setVisibility(View.VISIBLE);
                    register_username.setVisibility(View.VISIBLE);

                    login_username.setVisibility(View.GONE);
                    login_password.setVisibility(View.GONE);
                    status = false;
                }
            }
        });

        register_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!status){
                    if(register_username.getText().toString().isEmpty()){
                        Toast.makeText(MainActivity.this, "Username cannot be empty.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(register_password.getText().toString().isEmpty()){
                        Toast.makeText(MainActivity.this, "Password cannot be empty.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!register_password.getText().toString().equals(register_confirm_password.getText().toString())){
                        Toast.makeText(MainActivity.this, "Password Mismatch.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(userSQLiteHelper.addUser(register_username.getText().toString(), register_password.getText().toString(), default_image)){
                        Toast.makeText(MainActivity.this, "Register Successfully.",Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        Toast.makeText(MainActivity.this, "Username already existed.",Toast.LENGTH_SHORT).show();
                        return;
                    }

                }else{
                    if(login_username.getText().toString().isEmpty()){
                        Toast.makeText(MainActivity.this, "Username cannot be empty.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(login_password.getText().toString().isEmpty()){
                        Toast.makeText(MainActivity.this, "Password cannot be empty.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    switch (userSQLiteHelper.loginVerify(login_username.getText().toString(), login_password.getText().toString())){
                        case 0:
                            Toast.makeText(MainActivity.this, "Login Successfully.",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, CommentActivity.class);
                            intent.putExtra("username", login_username.getText().toString());
                            intent.putExtra("image",userSQLiteHelper.getImage(login_username.getText().toString()));
                            startActivity(intent);
                            break;
                        case 1:
                            Toast.makeText(MainActivity.this, "Username not existed.",Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(MainActivity.this, "Invalid Password.",Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });
        button_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status){
                    login_username.setText("");
                    login_password.setText("");
                }else{
                    register_username.setText("");
                    register_password.setText("");
                    register_confirm_password.setText("");
                }
            }
        });

    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            // 得到图片的全路径
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            //存储副本
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                //通过UUID生成字符串文件名
                String image_name = UUID.randomUUID().toString() + ".jpg";
                //存储图片
                FileOutputStream out = openFileOutput(image_name, MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                //获取复制后文件的uri
                Uri image_file_uri = Uri.fromFile(getFileStreamPath(image_name));
                //图片预览
                this.register_image.setImageURI(uri);
                //保存该URI
                default_image = image_file_uri.toString();
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                Log.e("FileNotFoundException", e.getMessage(),e);
            } catch (IOException e) {
                Log.w("IOException", e.getMessage(), e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
