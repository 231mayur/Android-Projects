package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class act_get_otp extends AppCompatActivity {
    private String otp_code;
    private FirebaseAuth firebaseAuth;
    private Button btn_register;
    private String phone_number;
    private EditText edt_otp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_get_otp);
        this.getSupportActionBar().hide();
        btn_register=(Button)findViewById(R.id.register);
        edt_otp=(EditText)findViewById(R.id.otp);
        firebaseAuth=FirebaseAuth.getInstance();
        phone_number=getIntent().getStringExtra("phone_number");
        Log.d("phone",phone_number);
        getOTP(phone_number);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code=edt_otp.getText().toString().trim();
                if(code.isEmpty() || code.length()<6)
                {
                    edt_otp.setError("Enter code");
                    edt_otp.requestFocus();
                    return;
                }
                verifyCode(code);
            }
        });


    }
    private void verifyCode(String code)
    {
        PhoneAuthCredential cProvider=PhoneAuthProvider.getCredential(otp_code,code);
        singInWithCredential(cProvider);
    }

    private void singInWithCredential(PhoneAuthCredential cProvider) {
        firebaseAuth.signInWithCredential(cProvider)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Intent intent=new Intent(act_get_otp.this,ask_for_mood.class);
                            intent.putExtra("phone",phone_number);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            otp_code=s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code=phoneAuthCredential.getSmsCode();
            if(code != null)
            {
                edt_otp.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    };

    private void getOTP(String phone_number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone_number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

}