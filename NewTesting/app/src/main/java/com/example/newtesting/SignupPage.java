package com.example.newtesting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupPage extends AppCompatActivity {
    EditText e,p,mName,mMobno;
    Button Signup;
    private ProgressDialog progressDialog;
    private RadioGroup radioGroup;
    private RadioButton selectUser;
    private FirebaseDatabase firebaseDatabase;
    private Task<Void> databaseReference;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);
        setTitle("SignUP");
        e=(EditText)findViewById(R.id.semail);
        p=(EditText)findViewById(R.id.editText4);
       mMobno=(EditText)findViewById(R.id.mobno);
       mName=(EditText)findViewById(R.id.name);
       progressDialog=new ProgressDialog(this);
        Signup=(Button)findViewById(R.id.ssignbtn);
        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);
        FirebaseApp.initializeApp(this.getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email=e.getText().toString();
                final String password=p.getText().toString();
                final String uName=mName.getText().toString();
                final String uMobno=mMobno.getText().toString();
                if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password)||TextUtils.isEmpty(uMobno)||TextUtils.isEmpty(uName))
                {
                    Toast.makeText(getApplicationContext(),"Enter Details",Toast.LENGTH_LONG).show();
                }
                else {
                    sign(email, password,uName,uMobno);
                }

            }
        });


    }
    public void sign(String email, String password, final String name, final String mobno)
    {
        progressDialog.setTitle("Registration");
        progressDialog.setMessage("Please Wait..");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                            int selectedid=radioGroup.getCheckedRadioButtonId();
                            selectUser=(RadioButton)findViewById(selectedid);
                            if(selectUser.getText().equals("User"))
                            {
                                databaseReference=firebaseDatabase.getReference().child("User").child(userId).child("Name").setValue(name);
                                databaseReference=firebaseDatabase.getReference().child("User").child(userId).child("Mobile").setValue(mobno);
                                startActivity(new Intent(SignupPage.this,MainActivity.class));
                            }
                            else if (selectUser.getText().equals("Driver"))
                            {
                                databaseReference=firebaseDatabase.getReference().child("Driver").child(userId).child("Name").setValue(name);
                                databaseReference=firebaseDatabase.getReference().child("Driver").child(userId).child("Mobile").setValue(mobno);
                                startActivity(new Intent(SignupPage.this,DriverMainPage.class));
                            }


                        } else {

                            Toast.makeText(SignupPage.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                             progressDialog.dismiss();
                        }

                        // ...
                    }
                });


    }


}
