package com.example.android.loginandsignup;

import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class TrackerActivity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    LatLng c2 = new LatLng(33.642299,72.988024);
    LatLng sada = new LatLng(33.646479,72.989204);
    LatLng nbs = new LatLng(33.645095, 72.991125);
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mRef;
    DatabaseReference dRef;
    FirebaseUser firebaseUser;
    LatLng driverLatLng;
    String uid;
    LatLng studentLatLng;
    Location driver = new Location("pointA");
    Location student = new Location("pointB");
    Marker driverMarker;
    Marker studentMarker;
    TextView text1;
    TextView text2;
    int time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button infoButton = (Button) findViewById(R.id.info);

        text1 = (TextView) findViewById(R.id.distance);
        text2 = (TextView) findViewById(R.id.eta);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), VanInfoActivity.class);
                startActivity(i);
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        String str1= "10:00:00";
        Date t1 = null;
        try {
            t1 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).parse(str1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c1 = Calendar.getInstance();
        c1.setTime(t1 );

        Date d1 = new Date();
        Calendar c3 = Calendar.getInstance();
        c3.setTime(d1);
        c3.add(Calendar.DATE, 1);

        Date x = c3.getTime();
        if (x.after(c1.getTime()))
        {
            time = 1;
        }else{
            time = 0;
        }

        mRef = firebaseDatabase.getReference("Students");
        dRef = firebaseDatabase.getReference("Drivers");
        mRef.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Driver").getValue() != null) {
                    uid = dataSnapshot.child("Driver").child("uid").getValue().toString();
                    populateMap();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_route, menu);
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
            firebaseAuth.signOut();
            Intent i2 = new Intent(getApplicationContext(), NotificationService.class);
            stopService(i2);
            Intent i = new Intent(getApplicationContext(), Manager.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(studentLatLng != null) {
            studentMarker = mMap.addMarker(new MarkerOptions().position(studentLatLng));
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    public void populateMap(){
                dRef.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("Location").getValue() != null) {
                            String lat = dataSnapshot.child("Location").child("latitude").getValue().toString();
                            String lng = dataSnapshot.child("Location").child("longitude").getValue().toString();
                            if(time == 1) {
                                String pickUpLocation = dataSnapshot.child("Timing").getValue().toString();
                                pickUpLocation = pickUpLocation.substring(5);
                                if (pickUpLocation.contains("NBS")) {
                                    studentLatLng = nbs;
                                }
                                else if (pickUpLocation.contains("SADA")) {
                                    studentLatLng = sada;
                                }
                                else if (pickUpLocation.contains("C2")) {
                                    studentLatLng = c2;
                                }
                                driverLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                                if (driverMarker != null) {
                                    driverMarker.setPosition(driverLatLng);
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(driverLatLng, 14));
                                    driver.setLatitude(driverLatLng.latitude);
                                    driver.setLongitude(driverLatLng.longitude);
                                    student.setLatitude(studentLatLng.latitude);
                                    student.setLongitude(studentLatLng.longitude);
                                    double distance = driver.distanceTo(student) / 1000;
                                    int eta = (int) Math.ceil((distance / 30) * 60);
                                    String eta2 = String.format(Locale.getDefault(), "Your driver is %d minutes away", eta);
                                    String distance2 = String.format(Locale.getDefault(), "Your driver is %.1f km away", distance);
                                    text1.setText(distance2);
                                    text2.setText(eta2);
                                }
                                else {
                                    driverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Van").draggable(true));
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(driverLatLng, 14));
                                    driver.setLatitude(driverLatLng.latitude);
                                    driver.setLongitude(driverLatLng.longitude);
                                    student.setLatitude(studentLatLng.latitude);
                                    student.setLongitude(studentLatLng.longitude);
                                    double distance = driver.distanceTo(student) / 1000;
                                    int eta = (int) Math.ceil((distance / 30) * 60);
                                    String eta2 = String.format(Locale.getDefault(), "Your driver is %d minutes away", eta);
                                    String distance2 = String.format(Locale.getDefault(), "Your driver is %.1f km away", distance);
                                    text1.setText(distance2);
                                    text2.setText(eta2);
                                }
                            }
                            else{
                                mRef.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.child("Address").getValue() != null) {
                                            Double lat = Double.parseDouble(dataSnapshot.child("Address").child("latitude").getValue().toString());
                                            Double lng = Double.parseDouble(dataSnapshot.child("Address").child("longitude").getValue().toString());
                                            studentLatLng = new LatLng(lat, lng);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
}
