package com.example.newtesting;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    EditText e,p;
    private static final String TAG = "MainActivity";
    private static final int ERROR_LOG=9001;
    Button signup,login;
    private RadioGroup radioGroup;
    private RadioButton select;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        signup=(Button)findViewById(R.id.signbtn);
        login=(Button)findViewById(R.id.loginbtn);
        e=(EditText)findViewById(R.id.mail);
        p=(EditText)findViewById(R.id.password);
        radioGroup=(RadioGroup)findViewById(R.id.radioGroup1);
        mAuth = FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SignupPage.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=e.getText().toString();
                String password=p.getText().toString();
                if(TextUtils.isEmpty(email)|TextUtils.isEmpty(password))
                {
                    Toast.makeText(getApplicationContext(),"Details Fail..",Toast.LENGTH_LONG).show();
                }else {
                        loginUser(email,password);
                }
            }
        });

    }

    private void loginUser(String email, String password) {
        progressDialog.setTitle("Logging");
        progressDialog.setMessage("Please Wait..");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (isServiceOk()) {
                                int selectedid=radioGroup.getCheckedRadioButtonId();
                                select=(RadioButton)findViewById(selectedid);
                                if(select.getText().toString().equals("User"))
                                {
                                    startActivity(new Intent(MainActivity.this, AfterLogin.class));
                                }
                                else  if(select.getText().toString().equals("Driver"))
                                {
                                    startActivity(new Intent(MainActivity.this, DriverMainPage.class));
                                }

                            }
                        }else {
                            Toast.makeText(getApplicationContext(),"fail",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }

                        // ...
                    }
                });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
        {

           // startActivity(new Intent(MainActivity.this,MainActivity.class));
        }
    }
    public boolean isServiceOk()
    {
        Log.d(TAG,"isServerOk:checking google service ok");
        int availbe= GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if(availbe== ConnectionResult.SUCCESS)
        {
            //evering ok
            Log.d(TAG,"isServerOk:Working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(availbe))
        {
            Log.d(TAG,"There is error with your google service ");
            Dialog dialog=GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,availbe,ERROR_LOG);
            dialog.show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"You can not access Map",Toast.LENGTH_LONG).show();
        }
        return false;

    }

}
