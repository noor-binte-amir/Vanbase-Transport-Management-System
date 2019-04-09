package com.example.android.loginandsignup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class PreferencesActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth ;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mRef;
    private DatabaseReference dRef;
    FirebaseUser firebaseUser;
    Object route;
    Object timing;
    int found = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mRef = firebaseDatabase.getReference();
        dRef = firebaseDatabase.getReference("Drivers");
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

    public void chooseRoute(View view){
        Intent i = new Intent(getApplicationContext(), ChooseRouteActivity.class);
        startActivity(i);
    }

    public void defaultTimings(View view){
        Intent i = new Intent(getApplicationContext(), DefaultTimingsActivity.class);
        startActivity(i);
    }

    public void requestRoute(View view){
        Intent i = new Intent(getApplicationContext(), RequestRouteActivity.class);
        startActivity(i);
    }

    public void setAddress(View view){
        Intent i = new Intent(getApplicationContext(), SetAddressActivity.class);
        startActivity(i);
    }

    public void save(View view){
        mRef.child("Students").child(firebaseUser.getUid()).child("DefaultTimings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                timing = dataSnapshot.child("selectedTime").getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mRef.child("Students").child(firebaseUser.getUid()).child("Route").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                route = dataSnapshot.child("Route").getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for(DataSnapshot child: children){
                    DatabaseReference driver = child.getRef();
                    String driverName = child.child("Name").getValue().toString();
                    Object value = child.child("Route").getValue();
                    Object timing1 = child.child("Timing").getValue();
                    if (value.toString().equals(route.toString()) && timing1.toString().equals(timing.toString())){
                        driver.child("Students").push().setValue(new Person(firebaseUser.getUid()));
                        mRef.child("Students").child(firebaseUser.getUid()).child("Driver").setValue(new Person(child.getKey()));
                        Toast.makeText(PreferencesActivity.this, "You have been assigned "+driverName, Toast.LENGTH_LONG).show();
                        found = 1;
                        break;
                    }
                }
                if (found == 0) {
                    Toast.makeText(PreferencesActivity.this, "No driver found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
