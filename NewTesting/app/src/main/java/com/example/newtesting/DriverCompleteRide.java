package com.example.newtesting;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverCompleteRide extends AppCompatActivity {

    private TextView name_driver,mobile_driver,price_driver;
    private Button complete_ride;
    private FirebaseDatabase firebaseDatabase;
    private  String cus_id;
    String TAG="DriverCompleteRide";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_complete_ride);

        name_driver=(TextView)findViewById(R.id.name_driver);
        mobile_driver=(TextView)findViewById(R.id.mobile_driver);
        price_driver=(TextView)findViewById(R.id.price_driver);
        complete_ride=(Button)findViewById(R.id.complete_ride);
        FirebaseDatabase.getInstance();
        FirebaseApp.initializeApp(DriverCompleteRide.this);

        FirebaseDatabase.getInstance().getReference().child("Driver").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        if (dataSnapshot.child("request information").exists()) {
                            cus_id = dataSnapshot.child("request information").child("Userid").getValue().toString();
                            Log.d("DriverCompleteRide", "Complete ride" + cus_id);
                            FirebaseDatabase.getInstance().getReference().child("User").child(cus_id)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                            Log.d("Myride", " my " + dataSnapshot1.child("Mobile").getValue());
                                            mobile_driver.setText(dataSnapshot1.child("Mobile").getValue().toString());
                                            name_driver.setText(dataSnapshot1.child("Name").getValue().toString());
                                            price_driver.setText(dataSnapshot1.child("price").getValue().toString());
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
        complete_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(DriverCompleteRide.this);
                builder1.setTitle("Cancel Ride");
                builder1.setMessage("     Are you sure..");
                builder1.setCancelable(false);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (!TextUtils.isEmpty(cus_id)) {
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User").child(cus_id);
                                   //May be you have to change single line
                                    FirebaseDatabase.getInstance().getReference().child("Driver").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Location").child("Destination").removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("Driver").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("request information").child("Userid").removeValue();
                                    ref.child("ride").removeValue();
                                }
                                startActivity(new Intent(DriverCompleteRide.this,DriverMainPage.class));
                                finish();
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



    }


}
