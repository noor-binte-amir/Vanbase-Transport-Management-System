package com.example.android.loginandsignup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.android.loginandsignup.R.id.license;
import static com.example.android.loginandsignup.R.id.timing;

public class VanInfoActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth ;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mRef;
    private DatabaseReference dRef;
    private FirebaseUser firebaseUser;
    private String uid;
    private String driverName;
    private String driverContact;
    private String vanLicense;
    private String vanColor;
    private String timing1;
    ProgressDialog progressDialog;
    TextView name;
    TextView contact;
    TextView license;
    TextView color;
    TextView timing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_van_info);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mRef = firebaseDatabase.getReference("Students");
        dRef = firebaseDatabase.getReference("Drivers");
        progressDialog = new ProgressDialog(this);

        name = (TextView) findViewById(R.id.name);
        contact = (TextView) findViewById(R.id.contact);
        license = (TextView) findViewById(R.id.license);
        color = (TextView) findViewById(R.id.color);
        timing = (TextView) findViewById(R.id.timing);


        mRef.child(firebaseUser.getUid()).child("Driver").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uid = dataSnapshot.child("uid").getValue().toString();
                Log.i("uid",uid);
                getDriverData();
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


    private void getDriverData() {
        progressDialog.show();
        dRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                driverName = dataSnapshot.child("Name").getValue().toString();
                driverContact = dataSnapshot.child("Contact").getValue().toString();
                vanLicense = dataSnapshot.child("Van Number").getValue().toString();
                vanColor = dataSnapshot.child("Color").getValue().toString();
                timing1 = dataSnapshot.child("Timing").getValue().toString();
                setTexts();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setTexts(){
        name.setText(driverName);
        contact.setText(driverContact);
        license.setText(vanLicense);
        color.setText(vanColor);
        timing.setText(timing1);
        progressDialog.cancel();
    }
}
