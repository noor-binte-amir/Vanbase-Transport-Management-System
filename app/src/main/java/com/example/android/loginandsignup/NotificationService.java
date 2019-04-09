package com.example.android.loginandsignup;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationService extends Service {

    LatLng c2 = new LatLng(33.642299,72.988024);
    LatLng sada = new LatLng(33.646479,72.989204);
    LatLng nbs = new LatLng(33.645095, 72.991125);
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    LatLng studentLatLng;
    String uid;
    int nCount = 0;
    Location driverLocation = new Location("driver");
    Location studentLocation = new Location("student");
    FirebaseUser firebaseUser;

    NotificationCompat.Builder mBuilder;
    NotificationManager mNotificationManager;


    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.van2)
                        .setContentTitle("Your Van is about to arrive!")
                        .setContentText("Your van is arriving in a few minutes");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, TrackerActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(TrackerActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("Students").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Driver").getValue() != null && dataSnapshot.child("DefaultTimings").child("selectedTime").getValue() != null){
                    uid = dataSnapshot.child("Driver").child("uid").getValue().toString();
                    String pickUpLocation = dataSnapshot.child("DefaultTimings").child("selectedTime").getValue().toString();
                    pickUpLocation = pickUpLocation.substring(5);
                    if (pickUpLocation.contains("NBS")) {
                        studentLatLng = nbs;
                        studentLocation.setLatitude(studentLatLng.latitude);
                        studentLocation.setLongitude(studentLatLng.longitude);
                        nCount = 0;
                    } else if (pickUpLocation.contains("SADA")) {
                        studentLatLng = sada;
                        studentLocation.setLatitude(studentLatLng.latitude);
                        studentLocation.setLongitude(studentLatLng.longitude);
                        nCount = 0;
                    } else if (pickUpLocation.contains("C2")) {
                        studentLatLng = c2;
                        studentLocation.setLatitude(studentLatLng.latitude);
                        studentLocation.setLongitude(studentLatLng.longitude);
                        nCount = 0;
                    }
                    firebaseDatabase.getReference("Drivers").child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("Location").getValue() != null) {
                                String lat = dataSnapshot.child("Location").child("latitude").getValue().toString();
                                String lng = dataSnapshot.child("Location").child("longitude").getValue().toString();
                                driverLocation.setLatitude(Double.parseDouble(lat));
                                driverLocation.setLongitude(Double.parseDouble(lng));
                                if (driverLocation.distanceTo(studentLocation) <= 500 && nCount == 0) {
                                    mNotificationManager.notify(101, mBuilder.build());
                                    nCount++;
                                }
                            }
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
