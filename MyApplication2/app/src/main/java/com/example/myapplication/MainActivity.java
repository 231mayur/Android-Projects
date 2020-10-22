package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
private Button next_btn;
private EditText edt_phone_number;

private String str_phone_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        next_btn=(Button) findViewById(R.id.next_btn);
        edt_phone_number=(EditText)findViewById(R.id.phone_number);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               str_phone_number=edt_phone_number.getText().toString();
               if(str_phone_number.isEmpty() || str_phone_number.length() <10)
               {
                   edt_phone_number.setError("Phone number required!");
                   edt_phone_number.requestFocus();
                   return;
               }
                Intent intent=new Intent(MainActivity.this,act_get_otp.class);
                 intent.putExtra("phone_number","+91"+str_phone_number);
                 startActivity(intent);
            }
        });
    }


    public void openOtpDialog()
    {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View view= inflater.inflate(R.layout.dialog_layout,null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        TextView next=(TextView)view.findViewById(R.id.uploaded);
        alertDialog.show();

        /*Dialog dialog=new Dialog(getApplicationContext());
        dialog.setContentView(R.layout.dialog_layout);
        dialog.show();*/
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() !=null)
        {
            Intent intent=new Intent(MainActivity.this,after_login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}