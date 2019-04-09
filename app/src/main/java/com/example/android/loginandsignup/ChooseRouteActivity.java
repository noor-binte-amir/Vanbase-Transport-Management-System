package com.example.android.loginandsignup;

import android.content.Intent;
import android.net.*;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChooseRouteActivity extends AppCompatActivity{

    private static int Route;
    public static int routeNumber;
    private FirebaseAuth firebaseAuth;
    private static Button buttonChooseRoute;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private static ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_route);

        firebaseAuth = FirebaseAuth.getInstance();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
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


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        private FirebaseAuth firebaseAuth ;
        private FirebaseDatabase firebaseDatabase;
        private DatabaseReference databaseReference;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_choose_route, container, false);
            ImageView mapPhoto = (ImageView) rootView.findViewById(R.id.mapImage);
            mapPhoto.setImageResource(getRouteImage(getArguments().getInt(ARG_SECTION_NUMBER)));
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference();
            routeNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            buttonChooseRoute =  (Button) rootView.findViewById(R.id.buttonChooseRoute);
            buttonChooseRoute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveRouteInfo();
                    Toast.makeText(getActivity().getBaseContext(), "Route has been assigned", Toast.LENGTH_SHORT).show();
                }
            });
            TextView text = (TextView) rootView.findViewById(R.id.label);
            if (routeNumber == 1) {
                text.setText("NUST - Lalazar");
            }
            else if (routeNumber == 2) {
                text.setText("NUST - Sohan - Korang Town - PWD - NUST");
            }
            else {
                text.setText("NUST - Faizabad - Shamsabad - Chandni Chowk - Moti Mahal Plaza - NUST");
            }
            ImageButton mapImage = (ImageButton) rootView.findViewById(R.id.mapImage);
            mapImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (routeNumber == 1){
                        Uri uri = Uri.parse("https://goo.gl/maps/s7pxMX5xtW22");
                        Intent i = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(i);
                    }
                    else if (routeNumber == 2){
                        Uri uri = Uri.parse("https://goo.gl/maps/BCC2XSebN412");
                        Intent i = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(i);
                    }
                    else {
                        Uri uri = Uri.parse("https://goo.gl/maps/Eud76cefasz");
                        Intent i = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(i);
                    }
                }
            });
            return rootView;


        }
        public void saveRouteInfo(){
            Route = mViewPager.getCurrentItem()+1;
            UserRoute userRoute = new UserRoute(Route);
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            databaseReference.child("Students").child(firebaseUser.getUid()).child("Route").setValue(userRoute);
        }

        public int getRouteImage(int position) {
            switch (position) {
                case 1: {

                    return R.drawable.route1;
                }case 2: {

                    return R.drawable.route2;
                }case 3:
                {
                    return R.drawable.route3;
            }}
            return 0;
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "ROUTE 1";
                case 1:
                    return "ROUTE 2";
                case 2:
                    return "ROUTE 3";
            }
            return null;
        }
    }
}
