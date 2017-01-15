package com.evostest.animallocation;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.evostest.animallocation.model.Animal;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public final static int REQUEST_LOCATION_PERMISSION = 1000;

    private GoogleMap mMap;
    private Marker marker;
    LatLng position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle(getResources().getString(R.string.title_activity_maps));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (savedInstanceState != null && savedInstanceState.containsKey(API.KEY_MARKER_POSITION)) {
            position = savedInstanceState.getParcelable(API.KEY_MARKER_POSITION);
        } else if (getIntent().hasExtra(API.KEY_MARKER_POSITION)) {
            //previous choice
            position = getIntent().getParcelableExtra(API.KEY_MARKER_POSITION);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (getIntent().hasExtra(API.KEY_LIST_ANIMALS)) {
            ArrayList<Animal> animals = getIntent().getParcelableArrayListExtra(API.KEY_LIST_ANIMALS);
            createMarkers(animals);
        } else {
            checkPermissions();
            if (position != null) {
                marker = mMap.addMarker(new MarkerOptions().position(position)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 12));
            }
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng point) {
                    if (marker != null) {
                        marker.remove();
                    }
                    marker = mMap.addMarker(new MarkerOptions().position(point)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

                }
            });
        }
    }

    private void setCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            mMap.setMyLocationEnabled(true);
         //   mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mMap.getUiSettings().setMapToolbarEnabled(false);

            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            Location location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, false));
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkPermissions() {

        int permissionCheck1 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck1 == PackageManager.PERMISSION_GRANTED
                && permissionCheck2 == PackageManager.PERMISSION_GRANTED) {
            setCurrentLocation();
        } else {
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if ( ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

                    ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                            REQUEST_LOCATION_PERMISSION );
                }
            } else {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    setCurrentLocation();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    void createMarkers(ArrayList<Animal> animals) {
        if (animals.size() == 0) {
            return;
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        Marker curMarker = null;
        for (Animal animal : animals) {
            LatLng location = animal.getLocation();
            String name = animal.getName();
            String type = "";
            switch (animal.getType()) {
                case BIRD:
                    type = getResources().getString(R.string.type_bird);
                    break;
                case MAMMAL:
                    type = getResources().getString(R.string.type_mammal);
                    break;
            }
            String markerTitle = String.format(Locale.US, "%s - %s", type, name);
            curMarker = mMap.addMarker(new MarkerOptions().position(location).title(markerTitle)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
            builder.include(curMarker.getPosition());
        }
        final LatLngBounds bounds = builder.build();
        int padding = 100; // offset from edges of the map in pixels
        CameraUpdate cu;
        if (animals.size() == 1) {
            if (curMarker != null) {
                cu = CameraUpdateFactory.newLatLngZoom(curMarker.getPosition(), 12F);
            } else {
                cu = CameraUpdateFactory.newLatLngZoom(animals.get(0).getLocation(), 12F);
            }
        } else {
            cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        }
        setMarkers(cu);
    }

    void setMarkers(CameraUpdate cu) {
        try {
            mMap.moveCamera(cu);
        } catch (IllegalStateException e) {
            e.printStackTrace();

            try {
                final View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();
                final CameraUpdate cuFinal = cu;

                if (mapView != null && mapView.getViewTreeObserver().isAlive()) {
                    mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                        @Override
                        public void onGlobalLayout() {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                mapView.getViewTreeObserver()
                                        .removeGlobalOnLayoutListener(this);
                            } else {
                                mapView.getViewTreeObserver()
                                        .removeOnGlobalLayoutListener(this);
                            }
                            mMap.moveCamera(cuFinal);
                        }
                    });
                }
                ;
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        };
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (marker != null) {
            outState.putParcelable(API.KEY_MARKER_POSITION, marker.getPosition());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu_done:
                if (!getIntent().hasExtra(API.KEY_LIST_ANIMALS)) {
                    if (marker == null) {
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.toast_choose_location), Toast.LENGTH_LONG).show();
                        break;
                    }
                    Intent intent = new Intent();
                    intent.putExtra(API.KEY_LOCATION, marker.getPosition());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!getIntent().hasExtra(API.KEY_LIST_ANIMALS)) {
            getMenuInflater().inflate(R.menu.menu_done, menu);
        }
        return true;
    }
}
