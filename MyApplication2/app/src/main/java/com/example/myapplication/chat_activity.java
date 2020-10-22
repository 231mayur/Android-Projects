package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
/**************************************************************
 * Name: Mayur Chavhan
 * Change id: C1
 * Change: adding recycler view to view all user using search
 * Date: 22/10/2020
 *************************************************************/
public class chat_activity extends AppCompatActivity {
    private BottomNavigationView view;
    private Button search_btn;
    private RecyclerView list_of_user;
    private EditText edt_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_activity);
        this.getSupportActionBar().hide();
        view=(BottomNavigationView)findViewById(R.id.bottom_chat_act);
        view.setSelectedItemId(R.id.bottom_chat);
        //C1 start here
        edt_text=(EditText)findViewById(R.id.edt_search);
        search_btn=(Button)findViewById(R.id.btn_search);
        list_of_user=(RecyclerView)findViewById(R.id.recyclerView);
        
        //C1 end here
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.bottom_home:
                        startActivity(new Intent(chat_activity.this,after_login.class));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.bottom_chat:
                        Toast.makeText(getApplicationContext(),"Chat",Toast.LENGTH_LONG).show();
                        return true;
                    case R.id.bottom_profile:
                        startActivity(new Intent(chat_activity.this,bottom_profile_act.class));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;

                }

                return true;
            }
        });
    }
}