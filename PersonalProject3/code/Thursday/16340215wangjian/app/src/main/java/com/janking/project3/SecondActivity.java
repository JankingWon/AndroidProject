package com.janking.project3;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        final String FILE_NAME = "data.txt";
        final EditText et = findViewById(R.id.tv);
        Button bt_load = findViewById(R.id.button_load);
        Button bt_save = findViewById(R.id.button_save);
        Button bt_clear = findViewById(R.id.button_clear);

        //清除文本
        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et.setText("");
            }
        });

        //读取文件
        bt_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try (FileInputStream fileInputStream = openFileInput(FILE_NAME)) {
                    byte[] contents = new byte[fileInputStream.available()];
                    fileInputStream.read(contents);
                    //et.setText(contents.toString())竟然不行
                    et.setText(new String(contents));
                    Toast.makeText(SecondActivity.this, "Load successfully.", Toast.LENGTH_SHORT).show();
                } catch (IOException ex) {
                    Toast.makeText(SecondActivity.this, "Fail to load file.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //保存文件
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try (FileOutputStream fileOutputStream = openFileOutput(FILE_NAME, MODE_PRIVATE)) {
                    fileOutputStream.write(et.getText().toString().getBytes());
                    Toast.makeText(SecondActivity.this, "Save successfully.", Toast.LENGTH_SHORT).show();
                } catch (IOException ex) {
                    Toast.makeText(SecondActivity.this, "Fail to save file.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //test
        createExternalStoragePrivateFile();
    }
    void createExternalStoragePrivateFile(){
        File file = new File(getExternalFilesDir(null), "DemoFile.jpg");
        try {
            @SuppressLint("ResourceType") InputStream is = getResources().openRawResource(R.mipmap.face);
            OutputStream os = new FileOutputStream(file);
            byte[] data = new byte[is.available()];
            is.read(data);
            os.write(data);
            is.close();
            os.close();
        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.w("ExternalStorage", "Error writing " + file, e);
        }
    }
}
