package com.example.newtesting;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DriverMainPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {
    private GoogleMap mMap;
    private String TAG="DriverMainPage";
    private EditText destinationLocation;
    SupportMapFragment mapFragment;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private Task<Void> databaseReference;
    Button u_comfirm_btn;
    final int flag=0;
    private String Uid;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        destinationLocation=(EditText)findViewById(R.id.driverDestinationLocation);
        setSupportActionBar(toolbar);
        FirebaseApp.initializeApp(this.getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        Uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        getLocationPermission();
        init();
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Driver").child(Uid);

        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if (dataSnapshot.child("request information").exists()){
                final String userinfo = dataSnapshot.child("request information").child("Userid").getValue().toString();
                if (!userinfo.equals(null)) {
                    final Dialog dialog = new Dialog(DriverMainPage.this);
                    dialog.setContentView(R.layout.userrequest);
                    dialog.setCancelable(false);
                    final TextView u_name = (TextView) dialog.findViewById(R.id.u_name);
                    final TextView u_mobile = (TextView) dialog.findViewById(R.id.u_mobile);
                    final TextView u_address=(TextView)dialog.findViewById(R.id.u_address);
                    final TextView u_price=(TextView)dialog.findViewById(R.id.u_price);
                    final Button u_cancel=(Button)dialog.findViewById(R.id.u_cancel);
                    u_comfirm_btn = (Button) dialog.findViewById(R.id.confirm_request);

                    final DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("User").child(userinfo);
                    u_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                FirebaseDatabase.getInstance().getReference().child("User").child(userinfo).child("flag").setValue(1);
                            FirebaseDatabase.getInstance().getReference().child("Driver").child(Uid).child("request information").removeValue();
                            dialog.dismiss();

                        }
                    });
                    databaseReference2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {


                            destinationLocation.setText("");
                            u_name.setText(dataSnapshot.child("Name").getValue().toString());
                            u_mobile.setText(dataSnapshot.child("Mobile").getValue().toString());
                            u_price.setText(dataSnapshot.child("price").getValue().toString());
                            u_address.setText(dataSnapshot.child("Location").child("Current Location").getValue().toString());

                            u_comfirm_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    databaseReference2.child("ride").setValue(Uid);
                                    double latitude,longitude;
                                    u_address.setText(dataSnapshot.child("Location").child("Current Location").getValue().toString());
                                    u_price.setText(dataSnapshot.child("price").getValue().toString());

                                    latitude=(Double) dataSnapshot.child("Location").child("Latitude").getValue();
                                    longitude=(Double) dataSnapshot.child("Location").child("Logitude").getValue();

                                    mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Your Partner").icon(BitmapDescriptorFactory.fromResource(R.drawable.sharedlocation)));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), (float) 10));

                                    dialog.dismiss();

                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    dialog.show();
                }
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    void init()
    {
        destinationLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int ActionId, KeyEvent keyEvent) {
                if(ActionId== EditorInfo.IME_ACTION_SEARCH
                        ||ActionId==EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction()==KeyEvent.ACTION_DOWN
                        ||keyEvent.getAction()==KeyEvent.KEYCODE_ENTER )
                {
                    geoLocation();
                }
                return false;
            }
        });
    }
    public void moveCameraToDestination(LatLng latLng,Float zoom)
    {

        mMap.addMarker(new MarkerOptions().position(latLng).title("You Destination").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        destinationLocation.setText("");
    }
    public void geoLocation()
    {
        String searchAddress=destinationLocation.getText().toString()+" Pune";
        Geocoder geocoder=new Geocoder(DriverMainPage.this);
        List<Address> address=new ArrayList<>();
        try
        {
            address=geocoder.getFromLocationName(searchAddress,1);
        }catch (Exception i)
        {
            Log.d(TAG,"GeoLocation Exception"+i.getMessage());
        }
        if(address.size()>0)
        {
            mMap.clear();
            Address address1=address.get(0);
            moveCameraToDestination(
                    new LatLng(address1.getLatitude(),address1.getLongitude()),
                    (float)14);


        }
        databaseReference=firebaseDatabase.getReference().child("Driver")
                .child(Uid).child("Location")
                .child("Destination")
                .setValue(searchAddress);
    }
    public void getLocationPermission()
    {
        Log.d(TAG,"Getting location permission");

        String permissions[]={Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_DENIED)
        {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_DENIED)
            {
                mLocationPermissionGranted=true;
                intiMap();
            }
            else
            {
                ActivityCompat.requestPermissions(this,permissions,1234);
            }
        }
        else
        {
            ActivityCompat.requestPermissions(this,permissions,1234);
        }


    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode)
        {
            case 1234:

                if(grantResults.length>0)
                {
                    for(int i=0;i<grantResults.length;i++)
                    {
                        if(grantResults[i]==PackageManager.PERMISSION_DENIED)
                        {
                            mLocationPermissionGranted=false;
                            return;
                        }
                    }

                    mLocationPermissionGranted=true;
                    intiMap();
                }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(getApplicationContext(), "Map is ready", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onMapReady:Map is ready");
        mMap = googleMap;
        getDeviceLocation();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

    }
    void intiMap() {
        Log.d(TAG, "initMap:Mp is initlizing");
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    public void moveCamera(LatLng latLng,Float zoom)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }
    public void getDeviceLocation() {
        Log.d(TAG, "Getting current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {
                Task location;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    String permissions[]={Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};
                    ActivityCompat.requestPermissions(this,permissions,1234);
                    return;
                }
                location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful())
                        {
                            Location currentLocation=(Location)task.getResult();
                            Geocoder geocoder = new Geocoder(DriverMainPage.this);
                            List<Address> addresses = null;
                            try {
                                addresses = geocoder.getFromLocation(currentLocation.getLatitude(),currentLocation.getLongitude(), 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Log.d(TAG, "Location Found");

                            String userAddress=addresses.get(0).getAddressLine(0);
                            Log.d(TAG,userAddress);

                            databaseReference=firebaseDatabase.getReference().child("Driver")
                                    .child(Uid).child("Location")
                                    .child("Current Location")
                                    .setValue(userAddress);
                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),
                                    (float) 14);
                        }else
                        {
                            Log.d(TAG, "Location not Found");
                        }
                    }
                });
            }
        }catch (Exception e)
        {
            Log.d(TAG,"Sequrity Exception"+e.getMessage());
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.driver_main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

     if (id == R.id.nav_send) {
            mAuth.signOut();
            startActivity(new Intent(DriverMainPage.this,MainActivity.class));
            finish();
        }else  if (id == R.id.ride_completed) {

         startActivity(new Intent(DriverMainPage.this,DriverCompleteRide.class));

     }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
