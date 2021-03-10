package com.google.carparkingforkabul;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code=99;
    private double latitude,longitud;
    private int proximityRadius=10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
        checkUserLocationPermision();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    public void onClick(View v)
    {
        String Hospital="Hospital",Restaurant="Restaurant",Parking="Parking";
        Object transferData[]=new Object[2];
        GetNearbyPlaces getNearbyPlaces=new GetNearbyPlaces();

        switch (v.getId())
        {
            case R.id.normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.hybridMap:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.search_address:
                EditText addressField=(EditText)findViewById(R.id.location_search);
                String address=addressField.getText().toString();
                List<Address> addressList=null;
                MarkerOptions userMarkerOpions=new MarkerOptions();
                if (!TextUtils.isEmpty(address))
                {
                    Geocoder geocoder=new Geocoder(this);
                    try {
                        addressList=geocoder.getFromLocationName(address,6);
                        if (addressList!=null)
                        {
                            for (int i=0;i<addressList.size();i++)
                            {
                                Address userAddress=addressList.get(i);
                                LatLng latLng=new LatLng(userAddress.getLatitude(),userAddress.getLongitude());
                                userMarkerOpions.position(latLng);
                                userMarkerOpions.title(address);
                                userMarkerOpions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                mMap.addMarker(userMarkerOpions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                            }
                        }else
                            {
                                Toast.makeText(this,"موقعیت پیدا نشد",Toast.LENGTH_SHORT).show();
                            }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else
                {
                    Toast.makeText(this,"لطفا اسم یکی از موقعیت ها را بنویسید",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.hospital_nearby:

                    mMap.clear();
                    String url = getUrl(latitude, longitud, Hospital);
                    transferData[0] = mMap;
                    transferData[1] = url;
                    getNearbyPlaces.execute(transferData);
                    Toast.makeText(this, "تلاش کردن برای شفاخانه های نزدیک...", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "نشان دادن شفاخانه های نزدیک...", Toast.LENGTH_SHORT).show();


               //Toast.makeText(this, "Please Check your GPS or Network", Toast.LENGTH_SHORT).show();

                break;

            case R.id.university_nearby:
                mMap.clear();
                url=getUrl(latitude,longitud,Restaurant);
                transferData[0]=mMap;
                transferData[1]=url;
                getNearbyPlaces.execute(transferData);
                Toast.makeText(this, "تلاش کردن برای پوهنتون های نزدیک...", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "نشان دادن پوهنتون های نزدیک...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.parking_nearby:

                    mMap.clear();
                    url = getUrl(latitude, longitud, Parking);
                    transferData[0] = mMap;
                    transferData[1] = url;
                    getNearbyPlaces.execute(transferData);
                Toast.makeText(this, "تلاش کردن براپارکنگ های نزدیک...", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "نشان دادن پارکنگ های نزدیک...", Toast.LENGTH_SHORT).show();
                //Toast.makeText(this, "هیچ موقعیت نزدیک شما پیدا نشد", Toast.LENGTH_SHORT).show();
                break;
            case R.id.RoadMap:
                Intent intent=new Intent(MapsActivity.this,RoutMap.class);
                startActivity(intent);
            break;
            case R.id.register:
                //If a Device does not have map installed, then redirect it to Play store
                try
                {
                    //If map installed
                    // Initialized URI
                    Uri uri=Uri.parse("https://www.google.co.in/maps/");
                    intent=new Intent(Intent.ACTION_VIEW,uri);
                    //Set Package
                    intent.setPackage("com.google.android.apps.maps");
                    intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                    // Start Activity
                    startActivity(intent);

                }catch (ActivityNotFoundException e)
                {
                    // if Google map is not installed
                    // initialize URi
                    Uri uri=Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
                    // Initialize Intent with action view
                    intent=new Intent(Intent.ACTION_VIEW,uri);
                    // set flags
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
                break;

        }
    }
    private String getUrl(double latitude ,double longitud,String nearbyPlace)
    {
            StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            googleURL.append("location=" + latitude + "," + longitud);
            googleURL.append("&radius=" + proximityRadius);
            googleURL.append("&type=" + nearbyPlace);
            googleURL.append("&sensor=true");
            googleURL.append("&key=" + "AIzaSyA2gl5nO-7-XL7hQyc5-HYWdIvmZkcsp_A");
            Log.d("GoogleMapsActivity", "url=" + googleURL.toString());
            return googleURL.toString();


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
        //this is not get an error without if statement
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);

        }
    }
    public boolean checkUserLocationPermision()
    {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);

            }return false;

        }else
        {return true;}
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case Request_User_Location_Code:
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if (googleApiClient==null)
                        {
                            buildGoogleApiClient();
                        }
                           mMap.setMyLocationEnabled(true);
                        }
                }else
                {
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
    protected synchronized void buildGoogleApiClient()
    {
        googleApiClient=new GoogleApiClient.Builder(this).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).
                addApi(LocationServices.API).
                build();
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude=location.getLatitude();
        longitud=location.getLongitude();
        lastLocation=location;
        if (currentUserLocationMarker!=null)
        {
            currentUserLocationMarker.remove();
        }
        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("User Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentUserLocationMarker=mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(12));
        if (googleApiClient!=null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest=new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        // this is not get an error without if
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
