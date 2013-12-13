package dev.apteki;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.text.Html;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.os.AsyncTask;

import android.widget.TextView;
import java.util.concurrent.ExecutionException;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;

public class MainActivity extends ActionBarActivity implements AsyncTaskListener{

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
        //GoogleMap map = ((MapFragment) getFragmentManager()
        //        .findFragmentById(R.id.map)).getMap();

    }

    @Override
    public void onTaskComplete(String result) {
        this.jsonParsed = (TextView) findViewById(R.id.json);
        try{
            JSONObject jObject = new JSONObject(result);
            JSONArray phrams = null;
            phrams = jObject.getJSONArray("apteki");
            this.jsonParsed.setText("\n");
            for(int i = 0; i < phrams.length(); i++){
                JSONObject c = phrams.getJSONObject(i);
                String name = c.getString("name");
                String street = c.getString("street");
                this.jsonParsed.append(Html.fromHtml("<h3>"+ name + "</h3>" + "<p>" + street + "</p>"));
            }

            Log.d("Async", result);
        } catch (JSONException e) {
                // Oops
        }

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

        String url = "http://stroner.ayz.pl/apteki/?json=";
        AsyncTask asyncTask = new WebserviceActivity(this).execute(url,street);
    }

}
