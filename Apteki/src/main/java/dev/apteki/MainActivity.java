package dev.apteki;


import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.util.Log;

import android.content.DialogInterface;

import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.view.View;
import android.view.ViewGroup;

import android.os.AsyncTask;

import android.widget.TextView;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import java.util.concurrent.ExecutionException;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;

import android.location.Location;

public class MainActivity extends ActionBarActivity implements AsyncTaskListener, LocationTaskListener, OnItemSelectedListener{

    public String lat;
    public String lng;
    public String result="6";


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
    protected void onStart(){
        super.onStart();
        setSpinner();
    }

    @Override
    public void onTaskComplete(JSONArray result) {

        GoogleMap map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();
        map.clear();
        try{
            JSONArray pharms = result;
            if(pharms.length()==0){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.setMessage(R.string.no_results)
                        .setTitle(R.string.search_results);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else{
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for(int i = 0; i < pharms.length(); i++){
                    JSONObject c = pharms.getJSONObject(i);
                    String name = c.getString("name");
                    String desc = c.getString("adress");

                    double  latitude = c.getDouble("lat");
                    double  longtitude = c.getDouble("lon");

                    LatLng local = new LatLng(latitude, longtitude);
                    Marker marker = map.addMarker(new MarkerOptions()
                            .title(name)
                            .snippet(desc)
                            .position(local));
                    builder.include(marker.getPosition());
                }
                double  lat = Double.parseDouble(this.lat);
                double  lon = Double.parseDouble(this.lng);
                LatLng position = new LatLng(lat, lon);
                String currPos = getResources().getString(R.string.position);
                Marker myPosition = map.addMarker(new MarkerOptions()
                        .title(currPos)
                        .position(position)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                        myPosition.showInfoWindow();
                builder.include(myPosition.getPosition());

                LatLngBounds bounds = builder.build();
                int padding = 80;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                map.moveCamera(cu);
                /*
                CameraPosition cp = new CameraPosition.Builder().target(position)
                        .zoom(10).build();

                map.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
                */
            }
        } catch (JSONException e) {
                // Oops
        }

    }

    public void onLocationComplete(Location location){
        lat = String.valueOf(location.getLatitude());
        lng = String.valueOf(location.getLongitude());

        EditText editField =  (EditText)findViewById(R.id.street_name);
        String street = editField.getText().toString();

        Log.d("Async","Latitude:" + lat + ", Longitude:" + lng);
        String url = "mongodb://guest:1234@ds061248.mongolab.com:61248/put_apteki";
        AsyncTask asyncTask = new WebserviceActivity(this).execute(url,street,lat,lng,result);
    }

    public void setSpinner(){
        Spinner spinner = (Spinner) findViewById(R.id.results_count);//(R.id.results_count);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.results_count_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        try{
            if(pos>0)
            result = parent.getItemAtPosition(pos).toString();
            else result="6";
        } catch(NullPointerException e){
            //Log.d("Async","Error : "+ e.getMessage().toString() );
        }
    }


    public void onNothingSelected(AdapterView<?> parent) {

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

        if(street.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            builder.setMessage(R.string.empty_phrase)
                    .setTitle(R.string.search_results);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(this.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        new LocationActivity(this).getLocation();
        }
    }

    public void searchStreetNear(View view) throws ExecutionException, InterruptedException {

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(this.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        new LocationActivity(this).getLocation();
    }
}
