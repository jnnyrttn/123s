package dev.apteki;

import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.text.Html;
import android.view.inputmethod.InputMethodManager;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.os.AsyncTask;

import android.widget.TextView;
import java.util.concurrent.ExecutionException;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;

import android.location.Location;

public class MainActivity extends ActionBarActivity implements AsyncTaskListener, LocationTaskListener{

    public TextView jsonParsed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }

    @Override
    public void onTaskComplete(JSONArray result) {

        GoogleMap map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();
        map.clear();
        try{
            JSONArray phrams = result;

            for(int i = 0; i < phrams.length(); i++){
                JSONObject c = phrams.getJSONObject(i);
                String name = c.getString("name");
                String desc = c.getString("adress");

                double  latitude = c.getDouble("lat");
                double  longtitude = c.getDouble("lon");

                LatLng local = new LatLng(latitude, longtitude);
                map.addMarker(new MarkerOptions()
                        .title(name)
                        .snippet(desc)
                        .position(local));
            }

        } catch (JSONException e) {
                // Oops
        }

    }

    public void onLocationComplete(Location location){
        Log.d("Async","getting location");
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    /*
        Called when user click on search button. Ivokes dev.apteki.webservice methods
     */

    public void searchStreet(View view) throws ExecutionException, InterruptedException {
        EditText editField =  (EditText)findViewById(R.id.street_name);
        String street = editField.getText().toString();

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(this.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        String url = "mongodb://guest:1234@ds061248.mongolab.com:61248/put_apteki";
        //String url = "mongodb://guest:1234@192.168.2.31/apteki";
        //String url = "http://stroner.ayz.pl/apteki/?json=1";
        AsyncTask asyncTask = new WebserviceActivity(this).execute(url,street,"","");
    }

    public void searchStreetNear(View view) throws ExecutionException, InterruptedException {
        String url = "mongodb://guest:1234@ds061248.mongolab.com:61248/put_apteki";
        //String url = "mongodb://guest:1234@192.168.2.31/apteki";

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(this.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        new LocationActivity(this).getLocation();
        //AsyncTask asyncTask = new WebserviceActivity(this).execute(url,"","16.5191403","52.3574853");
    }
}
