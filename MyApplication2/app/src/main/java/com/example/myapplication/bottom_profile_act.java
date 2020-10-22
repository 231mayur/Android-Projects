package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class bottom_profile_act extends AppCompatActivity {
    private BottomNavigationView view;
    private Button btn_logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_profile_act);
        this.getSupportActionBar().hide();
        view=(BottomNavigationView)findViewById(R.id.bottom_chat_act);
        view.setSelectedItemId(R.id.bottom_profile);
        btn_logout=(Button)findViewById(R.id.logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(bottom_profile_act.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.bottom_home:
                        startActivity(new Intent(bottom_profile_act.this,after_login.class));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.bottom_chat:
                        startActivity(new Intent(bottom_profile_act.this,chat_activity.class));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;

                }

                return true;
            }
        });

    }
}