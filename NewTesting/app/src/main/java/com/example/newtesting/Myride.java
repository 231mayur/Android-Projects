package com.example.newtesting;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Myride extends AppCompatActivity {
    private ProgressDialog progressDialog1;
    private TextView name_driver,mobile_driver;
    private Button cancel_ride;
    private  String driver_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myride);
        name_driver=(TextView)findViewById(R.id.name_driver);
        mobile_driver=(TextView)findViewById(R.id.mobile_driver);

        cancel_ride=(Button) findViewById(R.id.cancel_ride);



        progressDialog1=new ProgressDialog(Myride.this);
        progressDialog1.setTitle("Confirming driver");
        progressDialog1.setMessage("Please wait...");
        progressDialog1.setCancelable(false);
        progressDialog1.show();
        cancel_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Myride.this);
                builder1.setTitle("Cancel Ride");
                builder1.setMessage("     Are you sure..");
                builder1.setCancelable(false);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Driver").child(driver_id);
                                FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("ride").removeValue();
                                ref.child("request information").removeValue();
                                cancel_ride.setVisibility(View.GONE);
                                startActivity(new Intent(Myride.this,AfterLogin.class));
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });
        FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("flag").exists())
                        {
                            Toast.makeText(getApplicationContext(),"Please select another Driver",Toast.LENGTH_LONG).show();
                            FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("flag").removeValue();
                            startActivity(new Intent(Myride.this,AfterLogin.class));
                        }
                        if( dataSnapshot.child("ride").exists())
                        {
                            progressDialog1.dismiss();

                            driver_id =dataSnapshot.child("ride").getValue().toString();
                            Log.d("Myride"," my "+driver_id);
                            FirebaseDatabase.getInstance().getReference().child("Driver").child(driver_id)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                          Log.d("Myride"," my "+ dataSnapshot1.child("Mobile").getValue());

                                          mobile_driver.setText(dataSnapshot1.child("Mobile").getValue().toString());
                                          name_driver.setText(dataSnapshot1.child("Name").getValue().toString());
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
}
