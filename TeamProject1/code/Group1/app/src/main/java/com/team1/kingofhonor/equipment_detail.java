package com.team1.kingofhonor;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.team1.kingofhonor.model.Equipment;

public class equipment_detail extends AppCompatActivity {

    private static final String EQUIPMENT = "equipment_data";

    private android.support.v7.widget.Toolbar toolbar;
    private ImageView big_image;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView equip_photo_detail;
    private TextView equip_name_detail;
    private TextView equip_price_detail;
    private TextView equip_property_detail;
    private TextView equip_skill_detail;
    private TextView equip_baseequip_detail;

    private Equipment equipment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_detail);
        Intent intent = getIntent();
        equipment =(Equipment) intent.getSerializableExtra(EQUIPMENT);
        initview();
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout.setTitle(equipment.getName());
        equip_photo_detail.setImageResource(equipment.getImage());
        equip_name_detail.setText(equipment.getName());
        equip_price_detail.setText(String.valueOf(equipment.getPrice()));
        equip_property_detail.setText(equipment.getProperty());
        equip_skill_detail.setText(equipment.getSkill());
        equip_baseequip_detail.setText(equipment.getProcess());

    }

    private void initview(){
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.equip_toolbar);
        big_image = (ImageView) findViewById(R.id.imageview_equip);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        equip_name_detail = (TextView) findViewById(R.id.equip_name_detail);
        equip_photo_detail = (ImageView) findViewById(R.id.equip_photo_detail);
        equip_price_detail = (TextView) findViewById(R.id.equip_price_detail);
        equip_property_detail = (TextView) findViewById(R.id.equip_property_detail);
        equip_skill_detail = (TextView) findViewById(R.id.equip_skill_detail);
        equip_baseequip_detail = (TextView) findViewById(R.id.equip_baseequip_detail);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
