package dev.apteki;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import android.content.Intent;
import android.app.AlertDialog;
import android.provider.Settings;
import android.content.DialogInterface;

public class LocationActivity  implements LocationListener{
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Location location;
    private ProgressDialog dialog;

    protected String latitude,longitude;

    private LocationTaskListener callback;
    private Activity mActivity;

    public LocationActivity(Activity activity){
        this.mActivity = activity;
        this.callback = (LocationTaskListener) activity;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.dialog.dismiss();
        callback.onLocationComplete( location );
        locationManager.removeUpdates(this);

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Async","disable");
        displayPromptForEnablingGPS(this.mActivity);
        this.dialog.dismiss();
    }

    public void displayPromptForEnablingGPS(
            final Activity activity)
    {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = this.mActivity.getString(R.string.enable_gps);

        builder.setMessage(message)
                .setPositiveButton("Tak",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Nie trzeba.",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Async","onProviderEnabled");
        this.dialog.dismiss();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Async","onStatusChanged");
    }
    public void getLocation() {
        Log.d("Async","getting location...");
        this.dialog = ProgressDialog.show(this.mActivity, "", "Wyszukuję...");
        locationManager = (LocationManager) ( (Activity) this.mActivity ).getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


        if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) )
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                    0, this);
        else
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
                    0, this);
    }
}