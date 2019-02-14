package com.team1.kingofhonor;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.audiofx.DynamicsProcessing;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.telecom.Call;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.sackcentury.shinebuttonlib.ShineButton;
import com.team1.kingofhonor.model.Equipment;
import com.team1.kingofhonor.model.Hero;
import com.team1.kingofhonor.sqlite.HeroSQLiteHelper;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.greenrobot.eventbus.EventBus;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class HeroDetail extends AppCompatActivity {
    EditText heroName, heroAlias, heroCategory;
    DiscreteSeekBar heroViability, heroAttackDamage, heroSkillDamage, heroDifficulty;
    TextView heroViabilityValue, heroAttackDamageValue, heroSkillDamageValue, heroDifficultyValue, skillDescription;
    ImageButton heroImage, heroIcon, skill1, skill2, skill3,skill4,equip1,equip2,equip3,equip4,equip5,equip6;
    ShineButton heroFavorite;
    String image_uri, icon_uri;
    private Hero displayHero;
    MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hero_detail);
        //虚拟按键遮挡
        //hideBottomUIMenu();
        //获取数据
        displayHero = (Hero)getIntent().getSerializableExtra("Click_Hero");
        //获取英雄装备
        HeroSQLiteHelper sqLiteHelper = new HeroSQLiteHelper(this);
        final Equipment equipment1 = sqLiteHelper.getEquipmentsWithName(displayHero.getEquip1());
        final Equipment equipment2 = sqLiteHelper.getEquipmentsWithName(displayHero.getEquip2());
        final Equipment equipment3 = sqLiteHelper.getEquipmentsWithName(displayHero.getEquip3());
        final Equipment equipment4 = sqLiteHelper.getEquipmentsWithName(displayHero.getEquip4());
        final Equipment equipment5 = sqLiteHelper.getEquipmentsWithName(displayHero.getEquip5());
        final Equipment equipment6 = sqLiteHelper.getEquipmentsWithName(displayHero.getEquip6());
        //获取控件
        Toolbar toolbar = findViewById(R.id.hero_detail_toolbar);
        toolbar.inflateMenu(R.menu.hero_detail_menu);//设置右上角的填充菜单
        heroFavorite = findViewById(R.id.hero_detail_favorite);
        heroName = findViewById(R.id.hero_detail_name);
        heroAlias = findViewById(R.id.hero_detail_alias);
        heroImage = findViewById(R.id.hero_detail_image);
        heroIcon = findViewById(R.id.hero_detail_icon);
        heroCategory = findViewById(R.id.hero_detail_category);
        heroViability = findViewById(R.id.hero_viability);
        heroAttackDamage = findViewById(R.id.hero_attack_damage);
        heroSkillDamage = findViewById(R.id.hero_skill_damage);
        heroDifficulty = findViewById(R.id.hero_difficulty);
        heroViabilityValue = findViewById(R.id.hero_viability_value);
        heroAttackDamageValue = findViewById(R.id.hero_attack_damage_value);
        heroSkillDamageValue = findViewById(R.id.hero_skill_damage_value);
        heroDifficultyValue = findViewById(R.id.hero_difficulty_value);
        skillDescription = findViewById(R.id.hero_detail_skill_description);
        skill1 = findViewById(R.id.hero_detail_skill1);
        skill2 = findViewById(R.id.hero_detail_skill2);
        skill3 = findViewById(R.id.hero_detail_skill3);
        skill4 = findViewById(R.id.hero_detail_skill4);
        equip1 = findViewById(R.id.hero_equip1);
        equip2 = findViewById(R.id.hero_equip2);
        equip3 = findViewById(R.id.hero_equip3);
        equip4 = findViewById(R.id.hero_equip4);
        equip5 = findViewById(R.id.hero_equip5);
        equip6 = findViewById(R.id.hero_equip6);
        //设置宽度固定，高度自适应
        //获取屏幕宽度
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        ViewGroup.LayoutParams lp = heroImage.getLayoutParams();
        //宽度为屏幕宽度
        lp.width = screenWidth;
        //高度自适应
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        heroImage.setLayoutParams(lp);
        //最大允许宽度和高度
        heroImage.setMaxWidth(screenWidth);
        heroImage.setMaxHeight(2 * screenWidth / 3);
        //图片点击事件
        heroImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,1);
            }
        });
        heroIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,2);
            }
        });
        //设置不可编辑
        uneditMode();
        //设置内容
        heroName.setText(displayHero.getName());
        heroAlias.setText(displayHero.getAlias());
        heroCategory.setText(displayHero.getCategory());
        heroImage.setImageURI(Uri.parse(displayHero.getImage()));
        heroIcon.setImageURI(Uri.parse(displayHero.getIcon()));
        image_uri = displayHero.getImage();
        icon_uri = displayHero.getIcon();
        heroViability.setProgress(displayHero.getViability());
        heroAttackDamage.setProgress(displayHero.getAttack_damage());
        heroSkillDamage.setProgress(displayHero.getSkill_damage());
        heroDifficulty.setProgress(displayHero.getDifficulty());
        heroViabilityValue.setText(String.valueOf(displayHero.getViability()));
        heroAttackDamageValue.setText(String.valueOf(displayHero.getAttack_damage()));
        heroSkillDamageValue.setText(String.valueOf(displayHero.getSkill_damage()));
        heroDifficultyValue.setText(String.valueOf(displayHero.getDifficulty()));
        skill1.setImageURI(Uri.parse(displayHero.getSkill1_icon()));
        skill1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skillDescription.setText(displayHero.getSkill1_description());
            }
        });
        skill2.setImageURI(Uri.parse(displayHero.getSkill2_icon()));
        skill2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skillDescription.setText(displayHero.getSkill2_description());
            }
        });
        skill3.setImageURI(Uri.parse(displayHero.getSkill3_icon()));
        skill3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skillDescription.setText(displayHero.getSkill3_description());
            }
        });
        skill4.setImageURI(Uri.parse(displayHero.getSkill4_icon()));
        skill4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skillDescription.setText(displayHero.getSkill4_description());
            }
        });
        //默认显示一技能
        skillDescription.setText(displayHero.getSkill1_description());
        //技能图标
        equip1.setImageResource(equipment1.getImage());
        equip2.setImageResource(equipment2.getImage());
        equip3.setImageResource(equipment3.getImage());
        equip4.setImageResource(equipment4.getImage());
        equip5.setImageResource(equipment5.getImage());
        equip6.setImageResource(equipment6.getImage());
        equip1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HeroDetail.this, equipment_detail.class);
                intent.putExtra("equipment_data", equipment1);
                startActivity(intent);
            }
        });
        equip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HeroDetail.this, equipment_detail.class);
                intent.putExtra("equipment_data", equipment2);
                startActivity(intent);
            }
        });
        equip3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HeroDetail.this, equipment_detail.class);
                intent.putExtra("equipment_data", equipment3);
                startActivity(intent);
            }
        });
        equip4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HeroDetail.this, equipment_detail.class);
                intent.putExtra("equipment_data", equipment4);
                startActivity(intent);
            }
        });
        equip5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HeroDetail.this, equipment_detail.class);
                intent.putExtra("equipment_data", equipment5);
                startActivity(intent);
            }
        });
        equip6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HeroDetail.this, equipment_detail.class);
                intent.putExtra("equipment_data", equipment6);
                startActivity(intent);
            }
        });


        //关于收藏
        heroFavorite.setChecked(displayHero.getFavorite());
        //监听数值改变
        heroViability.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                heroViabilityValue.setText(String.valueOf(value));
            }
            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
            }
        });
        heroAttackDamage.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                heroAttackDamageValue.setText(String.valueOf(value));
            }
            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
            }
        });
        heroSkillDamage.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                heroSkillDamageValue.setText(String.valueOf(value));
            }
            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
            }
        });
        heroDifficulty.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                heroDifficultyValue.setText(String.valueOf(value));
            }
            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
            }
        });
        //设置菜单点击事件
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                        //编辑
                    case R.id.action_hero_edit:
                        editMode();
                        break;
                        //保存
                    case R.id.action_hero_save:
                        displayHero.setImage(image_uri);
                        displayHero.setAlias(heroAlias.getText().toString());
                        displayHero.setCategory(heroCategory.getText().toString());
                        displayHero.setViability(heroViability.getProgress());
                        displayHero.setAttack_damage(heroAttackDamage.getProgress());
                        displayHero.setSkill_damage(heroSkillDamage.getProgress());
                        displayHero.setDifficulty(heroDifficulty.getProgress());
                        displayHero.setIcon(icon_uri);
                        displayHero.setFavorite(heroFavorite.isChecked());
                        //不可编辑
                        uneditMode();
                        displayHero.setModified(true);
                        Toast.makeText(HeroDetail.this, "保存成功", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(displayHero);
                        finish();
                        break;
                        //删除
                    case R.id.action_hero_delete:
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(HeroDetail.this);
                        alertDialog.setTitle("提示").setMessage("是否确定删除英雄: " + displayHero.getName() + "？").setPositiveButton("确认",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        displayHero.setDeleted(true);
                                        EventBus.getDefault().post(displayHero);
                                        finish();

                                    }
                                }).setNegativeButton("取消",null).create().show();
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
        //音效
        mp = MediaPlayer.create(HeroDetail.this, Uri.parse(displayHero.getVoice()));
        mp.start();

    }

    void uneditMode(){
        //姓名一直不可更改
        //取消焦点
        heroName.setFocusable(false);
        heroName.clearFocus();
        heroName.setFocusableInTouchMode(false);
        heroAlias.setFocusable(false);
        heroAlias.setFocusableInTouchMode(false);
        heroCategory.setFocusable(false);
        heroCategory.setFocusableInTouchMode(false);
        heroImage.setClickable(false);
        heroIcon.setClickable(false);
        heroDifficulty.setEnabled(false);
        heroSkillDamage.setEnabled(false);
        heroAttackDamage.setEnabled(false);
        heroViability.setEnabled(false);
        //关闭键盘
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    void editMode(){
        heroCategory.setFocusable(true);
        heroCategory.requestFocus();
        heroCategory.setFocusableInTouchMode(true);
        heroAlias.setFocusable(true);
        heroAlias.requestFocus();
        heroAlias.setFocusableInTouchMode(true);
        heroImage.setClickable(true);
        heroIcon.setClickable(true);
        heroDifficulty.setEnabled(true);
        heroSkillDamage.setEnabled(true);
        heroAttackDamage.setEnabled(true);
        heroViability.setEnabled(true);
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
                    heroImage.setImageURI(image_file_uri);
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
        }else  if(requestCode == 2){
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
                    heroIcon.setImageURI(image_file_uri);
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
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.release();
    }
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
