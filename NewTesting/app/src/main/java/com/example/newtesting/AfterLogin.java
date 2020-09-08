package com.example.newtesting;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class AfterLogin extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private static final String TAG = "AfterLogin";
    private static final int ERROR_LOG = 9001;
    private GoogleMap mMap;
    private ImageButton drivers;
    private float ride_price=0;
    private FirebaseAuth mAuth;
    SupportMapFragment mapFragment;

    private String Uid;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private FirebaseDatabase firebaseDatabase;
    private Task<Void> databaseReference;
    private String searchAddress;
    private double currentlat1,currentlog1,currentlat2,currentlog2;
    private EditText destinationLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseApp.initializeApp(this.getApplicationContext());


        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        Uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        destinationLocation=(EditText)findViewById(R.id.destinationLocation);

        drivers=(ImageButton)findViewById(R.id.driver);
        drivers.setVisibility(View.GONE);
        this.setTitle("Welcome");
        getLocationPermission();
        init();


        drivers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AfterLogin.this,ListOfDriver.class).putExtra("desLocation",searchAddress));
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
   public void geoLocation()
   {
      searchAddress=destinationLocation.getText().toString()+" Pune";
       Geocoder geocoder=new Geocoder(AfterLogin.this);
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
           //location 2
           currentlat2=address1.getLatitude();
           currentlog2=address1.getLongitude();
           float[] results = new float[1];
           Location.distanceBetween(currentlat1,currentlog1,
                   currentlat2,currentlog2, results);
           float distance_in_meter=results[0];
           float distance_in_km=distance_in_meter/1000;
           ride_price=distance_in_km*5;
           databaseReference=firebaseDatabase.getReference().child("User").child(Uid)
                   .child("price").setValue(ride_price);
           moveCameraToDestination(


                   new LatLng(address1.getLatitude(),address1.getLongitude()),
                   (float)14);






       }
       databaseReference=firebaseDatabase.getReference().child("User")
               .child(Uid).child("Location")
               .child("Destination")
               .setValue(searchAddress);
       drivers.setVisibility(View.VISIBLE);
   }
    void intiMap() {
        Log.d(TAG, "initMap:Mp is initlizing");
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void onMapReady(GoogleMap googleMap) {

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
                   // Toast.makeText(getApplicationContext(),"Location Permission fail",Toast.LENGTH_LONG).show();

                }
                location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful())
                        {
                            Log.d(TAG, "Location Found");
                            Location currentLocation=(Location)task.getResult();
                            // Location 1
                            currentlat1=currentLocation.getLatitude();
                            currentlog1=currentLocation.getLongitude();
                           databaseReference=firebaseDatabase.getReference().child("User")
                                    .child(Uid).child("Location")
                                    .child("Latitude")
                                    .setValue(currentlat1);
                            databaseReference=firebaseDatabase.getReference().child("User")
                                    .child(Uid).child("Location")
                                    .child("Logitude")
                                    .setValue(currentlog1);

                            Geocoder geocoder = new Geocoder(AfterLogin.this);
                            try
                            {
                                List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(),currentLocation.getLongitude(), 1);
                                String userAddress=addresses.get(0).getAddressLine(0);
                                Log.d(TAG,userAddress);

                                databaseReference=firebaseDatabase.getReference().child("User")
                                        .child(Uid).child("Location")
                                        .child("Current Location")
                                        .setValue(userAddress);
                            }catch (Exception e)
                            {
                                Log.d(TAG,e.getMessage());
                            }
                              moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),
                                    (float) 14);
                        }else
                        {
                            Log.d(TAG, "Location not Found");
                           // Toast.makeText(getApplicationContext(),"Location not found",Toast.LENGTH_LONG).show();
                        }
                    }
                });
        }
       }catch (Exception e)
       {
           Log.d(TAG,"Sequrity Exception"+e.getMessage());
       }

   }
     public void moveCamera(LatLng latLng,Float zoom)
     {
      mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
     }
    public void moveCameraToDestination(LatLng latLng,Float zoom)
    {

        mMap.addMarker(new MarkerOptions().position(latLng).title("You Destination").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
       destinationLocation.setText("");
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
        getMenuInflater().inflate(R.menu.after_login, menu);
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
            startActivity(new Intent(AfterLogin.this,MainActivity.class));
        }else if(id==R.id.profile)
       {
        startActivity(new Intent(AfterLogin.this,UserProfile.class));
       }else if (id==R.id.myride)
       {
           startActivity(new Intent(AfterLogin.this,Myride.class));
       }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    @Override
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
}
