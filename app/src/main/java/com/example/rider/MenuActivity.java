package com.example.rider;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("종료할까요?"); // 다이얼로그 제목
        builder.setCancelable(false);   // 다이얼로그 화면 밖 터치 방지
        builder.setPositiveButton("예", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //exit();
                finish();
            }
        });

        builder.setNegativeButton("아니요", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

//        builder.setNeutralButton("취소", new AlertDialog.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
        builder.show(); // 다이얼로그 보이기
        //finish();
    }
    public void exit() { // 종료
        super.onBackPressed();
    }

    public void onClick1(View view){
        Intent intent = new Intent(this, SettingActivity .class);
        startActivity(intent);
    }
    public void onClick3(View view){
        Intent intent = new Intent(this, ContactActivity.class);
        startActivity(intent);
    }

    public void onClick4(View view){
        Intent intent = new Intent(this, DriveActivity.class);
        startActivity(intent);
    }

}