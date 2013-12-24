package dev.apteki;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.TextView;
import android.util.Log;


public class LocationActivity  implements LocationListener{
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Location location;

    protected String latitude,longitude;

    private LocationTaskListener callback;
    private Activity mActivity;

    public LocationActivity(Activity activity){
        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        //Log.d("Async","Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude() +"");
        this.mActivity = activity;
        this.callback = (LocationTaskListener) activity;
    }

    @Override
    public void onLocationChanged(Location location) {

        callback.onLocationComplete( location );
        locationManager.removeUpdates(this);

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Async","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Async","onProviderEnabled");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Async","onStatusChanged");
    }
    public void getLocation() {
        Log.d("Async","getting location...");
        locationManager = (LocationManager) ( (Activity) this.mActivity ).getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        /*
        if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) )
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                    0, this);
        else
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
                    0, this);
        */
    }
}