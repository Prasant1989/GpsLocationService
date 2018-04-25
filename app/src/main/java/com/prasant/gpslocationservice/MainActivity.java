package com.prasant.gpslocationservice;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity {

    Context mContext;
    MapView mMapView;
    private GoogleMap googleMap;
   private double latitude,longitude;



    private static final String GET_LOCATION = "com.prasant.gpslocationservice";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       final TextView textView=(TextView)findViewById(R.id.latLongId);
        mContext=this;

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
      //  startService(intent);


        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                         latitude = intent.getDoubleExtra(GPSTrackerService.EXTRA_LATITUDE, 0);
                         longitude = intent.getDoubleExtra(GPSTrackerService.EXTRA_LONGITUDE, 0);
                        textView.setText("Lat: " + latitude + ", Lng: " + longitude);

                        Toast.makeText(context, "Lat: " + latitude + ", Lng: " + longitude, Toast.LENGTH_SHORT).show();
                    }
                }, new IntentFilter(GPSTrackerService.ACTION_LOCATION_BROADCAST)
        );



        mMapView = (MapView)findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(MainActivity.this.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                // For showing a move to my location button
                //googleMap.setMyLocationEnabled(true);
                // For dropping a marker at a point on the Map
                LatLng doctorLocation = new LatLng(latitude, longitude);
                System.out.println("......"+doctorLocation);
                googleMap.addMarker(new MarkerOptions().position(doctorLocation));
                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(doctorLocation).zoom(13f).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });






    }


    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, GPSTrackerService.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, GPSTrackerService.class));
    }

}
