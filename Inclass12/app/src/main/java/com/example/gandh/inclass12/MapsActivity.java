package com.example.gandh.inclass12;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Marker start, end;
    boolean started = false;
    PolylineOptions rectOptions;
    Location semi_location;
    Polyline polyline;
    LatLngBounds.Builder builder;
    LatLngBounds bound;
    CameraUpdate cu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //setTitle("Tracking App");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(location!=semi_location) {
                    semi_location= location;
                    Log.d("Locationamar", location.getLatitude() + "," + location.getLongitude());
                    rectOptions.add(new LatLng(location.getLatitude(), location.getLongitude()));
                    builder.include(new LatLng(location.getLatitude(), location.getLongitude()));
                    bound= builder.build();
                    cu = CameraUpdateFactory.newLatLngBounds(bound, 25);
                    mMap.moveCamera(cu);
                    polyline = mMap.addPolyline(rectOptions);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

           SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!locationManager.isProviderEnabled(locationManager.GPS_PROVIDER))
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("GPS not enabled").setMessage("Would you like to enable GPS settings").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    finish();
                }
            });
            AlertDialog alertDialog=builder.create();
            alertDialog.show();
        }
        else
        {

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.setMyLocationEnabled(true);



        // Add a marker in Sydney and move the camera

       mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
           @Override
           public void onMapLongClick(LatLng latLng) {
            if(started) {
                started = false;
                rectOptions.add(latLng);
                builder.include(latLng);
                polyline = mMap.addPolyline(rectOptions);
                bound= builder.build();
                cu = CameraUpdateFactory.newLatLngBounds(bound, 25);
                mMap.moveCamera(cu);
                rectOptions = new PolylineOptions();
                mMap.addMarker(new MarkerOptions().position(latLng).title("End position"));

                if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.removeUpdates(locationListener);
                Toast.makeText(MapsActivity.this,"Tracking is now stopped",Toast.LENGTH_SHORT).show();
            }
               else {
                rectOptions = new PolylineOptions().add(latLng);
                started = true;
                mMap.clear();
                polyline = mMap.addPolyline(rectOptions);
                builder = new LatLngBounds.Builder();

                builder.include(latLng);

                mMap.addMarker(new MarkerOptions().position(latLng).title("start position"));
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 5000 , 2, locationListener);
                Toast.makeText(MapsActivity.this,"Tracking is now started",Toast.LENGTH_SHORT).show();
            }

           }
       });



    }


}
