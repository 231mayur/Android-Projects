package com.example.newtesting;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListOfDriver extends AppCompatActivity {

  ArrayList<String> mobile;
    ArrayList<String> names;
    ArrayList<String> driverIds;
    ArrayList<String> driverLocation;
   private ListView listView;
   String userDesti;
    private FirebaseAuth mAuth;
    private String TAG="ListOfDriver";
    private FirebaseDatabase firebaseDatabase;

    private int flag=0;
    private ProgressDialog progressDialog;

    private ValueEventListener databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_driver);
        mobile=new ArrayList<String>();
        names=new ArrayList<String>();
        driverIds=new ArrayList<String>();
        driverLocation=new ArrayList<String>();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        this.setTitle("List of Drivers");
        progressDialog.setMessage("Getting Drivers for you");

           listView=(ListView)findViewById(R.id.listView);
            userDesti=getIntent().getStringExtra("desLocation");
        firebaseDatabase = FirebaseDatabase.getInstance();
        progressDialog.show();
        databaseReference=firebaseDatabase.getReference().child("Driver").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int i=0;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    Log.d("ListOfDriver","IDs "+postSnapshot.getKey());
                  if(userDesti.equals( postSnapshot.child("Location").child("Destination").getValue())) {
                        Log.d("ListOfDriver","Matched location");
                        driverIds.add(postSnapshot.getKey());
                        mobile.add(postSnapshot.child("Mobile").getValue().toString());
                      driverLocation.add(postSnapshot.child("Location").child("Current Location").getValue().toString());
                        names.add(postSnapshot.child("Name").getValue().toString());
                        Log.d(TAG, "Firebase DAta   " + mobile.get(i));
                        i++;

                   }

                }

                CustomAdapter customAdapter=new CustomAdapter();
                listView.setAdapter(customAdapter);
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Dialog dialog = new Dialog(ListOfDriver.this);
                dialog.setContentView(R.layout.activity_cutom__alert);
                TextView c_mobile=(TextView)dialog.findViewById(R.id.custom_mobile);
                TextView c_name=(TextView)dialog.findViewById(R.id.custom_name);
                TextView d_c_location=(TextView)dialog.findViewById(R.id.drivercurrentlocation);
                Button sent_reduest=(Button)dialog.findViewById(R.id.cutom_button);
                c_mobile.setText(mobile.get(i));
                c_name.setText(names.get(i));
                d_c_location.setText(driverLocation.get(i));
                dialog.show();
                final String id_of_user=driverIds.get(i);
                sent_reduest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        FirebaseDatabase.getInstance().getReference().child("Driver")
                                .child(id_of_user).child("request information").child("Userid")
                                .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        startActivity(new Intent(ListOfDriver.this,Myride.class));
                        finish();

                    }
                });


            }
        });



    }
    class CustomAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return mobile.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view=getLayoutInflater().inflate(R.layout.activity_list_view,null);
            TextView textView=(TextView)view.findViewById(R.id.Label);
            TextView textView1=(TextView)view.findViewById(R.id.Label2);
            textView1.setText(names.get(i));
            textView.setText(mobile.get(i));
            Log.d(TAG,"Data"+mobile.get(i));

            return view;
        }
    }
}
