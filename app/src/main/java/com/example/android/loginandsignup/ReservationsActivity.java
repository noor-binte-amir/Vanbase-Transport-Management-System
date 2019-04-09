package com.example.android.loginandsignup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.android.loginandsignup.R.id.radioGroup;
import static com.example.android.loginandsignup.R.id.timing;

public class ReservationsActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth ;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;

    private RadioGroup radioGroup;
    private RadioButton selectedRadioButton;
    private Button buttonSave;

    private int buttonID;
    private String SelectedTime;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        progressDialog = new ProgressDialog(this);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        buttonSave= (Button) findViewById(R.id.saveButton);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton r1 = (RadioButton) findViewById(R.id.time1);
                RadioButton r2 = (RadioButton) findViewById(R.id.time2);
                RadioButton r3 = (RadioButton) findViewById(R.id.time3);
                RadioButton r4 = (RadioButton) findViewById(R.id.time4);
                buttonID = radioGroup.getCheckedRadioButtonId();
                selectedRadioButton = (RadioButton) findViewById(buttonID);
                SelectedTime = selectedRadioButton.getText().toString();
            }
        });

        buttonSave.setOnClickListener(this);

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
    public void onClick(View v) {
        if (v==buttonSave){
            progressDialog.setMessage("Reserving...");
            progressDialog.show();
            saveTimingInfo();
        }
    }

    private void saveTimingInfo(){
        UserTimings userTimings = new UserTimings(buttonID,SelectedTime);
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference.child("Students").child(firebaseUser.getUid()).child("Reservations").setValue(userTimings);
        Toast.makeText(ReservationsActivity.this,"Reserved",Toast.LENGTH_SHORT).show();
        progressDialog.cancel();
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(i);
    }
}
