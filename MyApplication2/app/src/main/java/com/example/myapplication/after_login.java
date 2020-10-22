package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class after_login extends AppCompatActivity {
private BottomNavigationView view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_login);
        this.getSupportActionBar().hide();
        view=(BottomNavigationView)findViewById(R.id.after_login_home);
        view.setSelectedItemId(R.id.bottom_home);
        Log.d("Start","StartActiviti");

    view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId())
        {
            case R.id.bottom_home:
                Toast.makeText(getApplicationContext(),"Home",Toast.LENGTH_LONG).show();
                Log.d("after","Afterloginactivity");
                return true;
            case R.id.bottom_chat:
                startActivity(new Intent(after_login.this,chat_activity.class));
                finish();
                overridePendingTransition(0, 0);
                return true;
            case R.id.bottom_profile:
                startActivity(new Intent(after_login.this,bottom_profile_act.class));
                finish();
                overridePendingTransition(0, 0);
                return true;
        }

        return true;
    }
});
    }
}