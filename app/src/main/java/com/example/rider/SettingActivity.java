package com.example.rider;

import static java.sql.DriverManager.println;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;


public class SettingActivity extends AppCompatActivity {

    private setting_sound setSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setSound = new setting_sound();

    }
    public void onClick_sound(View view){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentFrame_set, setSound);
        fragmentTransaction.commit();
    }
    public void onClick_back(View view){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(setSound);
        fragmentTransaction.commit();
    }
    public void onClick_camera(View view){

    }

}
