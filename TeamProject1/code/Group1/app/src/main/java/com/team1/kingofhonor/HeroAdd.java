package com.team1.kingofhonor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import com.team1.kingofhonor.adapter.HeroAdapter;
import com.team1.kingofhonor.model.Hero;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class HeroAdd extends AppCompatActivity {
    private List<String> hero_names;
    public static final int FILE_RESULT_CODE = 1;
    private Hero add_hero;
    //保存英雄的图片uri
    private String image_uri,icon_uri,voice_uri;
    private ImageButton imageButton;
    final private String FILE_NAME = "new_image.jpg";
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hero_add);
        image_uri = new Hero().getImage();
        icon_uri = new Hero().getIcon();
        voice_uri = new Hero().getVoice();
        //比较无重复的英雄名称
        hero_names = getIntent().getStringArrayListExtra("hero_names");
        final EditText hero_name = findViewById(R.id.hero_add_name);
        final EditText hero_alias = findViewById(R.id.hero_add_alias);
        final Spinner hero_category = findViewById(R.id.hero_add_category);
        final DiscreteSeekBar hero_viability = findViewById(R.id.hero_add_viability);
        final DiscreteSeekBar hero_attack_damage = findViewById(R.id.hero_add_attack_damage);
        final DiscreteSeekBar hero_skill_damage = findViewById(R.id.hero_add_skill_damage);
        final DiscreteSeekBar hero_difficulty = findViewById(R.id.hero_add_difficulty);
        //获取图片选择按钮
        imageButton = findViewById(R.id.hero_add_image);
        //设置宽度固定，高度自适应
        //获取屏幕宽度
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        ViewGroup.LayoutParams lp = imageButton.getLayoutParams();
        //宽度为屏幕宽度
        lp.width = screenWidth;
        //高度自适应
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        imageButton.setLayoutParams(lp);
        //最大允许宽度和高度
        imageButton.setMaxWidth(screenWidth);
        imageButton.setMaxHeight(2 * screenWidth / 3);
        //设置显示图片
        imageButton.setImageURI(Uri.parse(image_uri));
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,1);
            }
        });
        //填充菜单
        Toolbar toolbar = findViewById(R.id.hero_add_toolbar);
        toolbar.inflateMenu(R.menu.hero_add_menu);
        //设置菜单点击事件
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    //保存
                    case R.id.action_hero_add_save:
                        if(hero_name.getText().toString().isEmpty()){
                            Toast.makeText(HeroAdd.this, "英雄名不能为空!", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        for(String i : hero_names){
                            if(i.equals(hero_name.getText().toString())){
                                Toast.makeText(HeroAdd.this, "英雄名重复!", Toast.LENGTH_SHORT).show();
                                return false;
                            }

                        }
                        add_hero = new Hero(hero_name.getText().toString(), image_uri,  hero_alias.getText().toString(),hero_category.getSelectedItem().toString(),
                                hero_viability.getProgress(),hero_attack_damage.getProgress(),hero_skill_damage.getProgress(), hero_difficulty.getProgress(),voice_uri,icon_uri,false);
                        add_hero.setAdded(true);
                        EventBus.getDefault().post(add_hero);
                        finish();
                        Toast.makeText(HeroAdd.this, "添加成功", Toast.LENGTH_SHORT).show();
                        break;
                        //添加头像
                    case R.id.action_hero_add_icon:
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent,2);
                        break;
                    //添加语音
                    case  R.id.action_hero_add_voice:
                        intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("audio/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent,3);
                        break;
                }
                return true;
            }
        });
        //返回
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                ContentResolver cr = this.getContentResolver();
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
                    imageButton.setImageURI(image_file_uri);
                    //保存该URI
                    image_uri = image_file_uri.toString();
                    out.flush();
                    out.close();
                } catch (FileNotFoundException e) {
                    Log.e("FileNotFoundException", e.getMessage(),e);
                } catch (IOException e) {
                    Log.w("IOException", e.getMessage(), e);
                }
            }
        }else if(requestCode == 2){
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                ContentResolver cr = this.getContentResolver();
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    //通过UUID生成字符串文件名
                    String image_name = UUID.randomUUID().toString() + ".jpg";
                    //存储图片
                    FileOutputStream out = openFileOutput(image_name, MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    //获取复制后文件的uri
                    Uri image_file_uri = Uri.fromFile(getFileStreamPath(image_name));
                    //保存该URI
                    icon_uri = image_file_uri.toString();
                    out.flush();
                    out.close();
                } catch (FileNotFoundException e) {
                    Log.e("FileNotFoundException", e.getMessage(),e);
                } catch (IOException e) {
                    Log.w("IOException", e.getMessage(), e);
                }
            }
        }else  if(requestCode == 3){
            if (resultCode == RESULT_OK) {
                voice_uri = data.getData().toString();
                ContentResolver cr = this.getContentResolver();
                //播放预览
                mediaPlayer = MediaPlayer.create(HeroAdd.this, Uri.parse(voice_uri));
                mediaPlayer.start();
                try {
                    //通过UUID生成字符串文件名
                    String voice_name = UUID.randomUUID().toString() + ".mp3";
                    //存储音频
                    FileOutputStream out = openFileOutput(voice_name, MODE_PRIVATE);
                    InputStream in = cr.openInputStream(data.getData());
                    byte[] newVoice = new byte[in.available()];
                    in.read(newVoice);
                    out.write(newVoice);
                    in.close();
                    out.close();
                    voice_uri = Uri.fromFile(getFileStreamPath(voice_name)).toString();

                } catch (FileNotFoundException e) {
                    Log.e("FileNotFoundException", e.getMessage(),e);
                } catch (IOException e) {
                    Log.w("IOException", e.getMessage(), e);
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null)
            mediaPlayer.release();
    }
}
