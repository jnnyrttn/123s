package dev.apteki;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.net.UnknownHostException;
import com.mongodb.*;
import com.mongodb.util.*;
import com.mongodb.MongoClientURI;


public class WebserviceActivity extends AsyncTask<String, Void, JSONArray>{

    private Activity mActivity;
    private ProgressDialog dialog;

    private AsyncTaskListener callback;

    /*
     *  Web service constructor
     *  @param Activity - main activity;
     */
    public WebserviceActivity(Activity activity)
    {
        this.mActivity = activity;
        this.callback = (AsyncTaskListener)activity;
    }

    /*
     *   Send get request to server and fetch the results
     *   @param Array
     *   @return null
     */
    @Override
    protected JSONArray doInBackground(String... urls)
    {

        String qstring = urls[1];
        String lat = urls[2];
        String lng = urls[3];
        String resCount = urls[4];

        JSONArray result = new JSONArray();
        /*
        if(!qstring.isEmpty() || ( !lat.isEmpty() && !lng.isEmpty()) ){
            try{
                if(!lat.isEmpty() && !lng.isEmpty()){
                    String[] loc = {lat,lng};
                    result = this.MongoSearch(qstring,urls[0],resCount,loc);
                }
                else result = this.MongoSearch(qstring,urls[0],resCount,"");
            } catch (UnknownHostException e){

            }
        }
        */
        try{
            if(!lat.isEmpty() && !lng.isEmpty()){
                String[] loc = {lat,lng};
                result = this.MongoSearch(qstring,urls[0],resCount,loc);
            }
            else result = this.MongoSearch(qstring,urls[0],resCount,"");
        } catch (UnknownHostException e){

        }
        return result;
    }
    @Override
    protected void onPreExecute()
    {
        this.dialog = ProgressDialog.show(this.mActivity, "", "Wyszukuję...");
    }

    @Override
    public void onPostExecute(JSONArray result) {
        super.onPostExecute(result);
        this.dialog.dismiss();
        callback.onTaskComplete(result);
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    /*
        Query mongo service for objects containing street string in their address or description.
        @param String street
        @param String mongoUri
        @param String[] loc Latitude and longtitude.
     */

    public static JSONArray MongoSearch(String street, String mongoUri,String resCount, String... loc ) throws UnknownHostException{
        String result = "";

        MongoClientURI uri  = new MongoClientURI(mongoUri);
        MongoClient client = new MongoClient(uri);
        DB db = client.getDB(uri.getDatabase());

        DBCollection apteki = db.getCollection("apteki");
        DBObject query;

        if(loc.length > 1) {
            String lat = loc[1];
            String lon = loc[0];
            if(street.isEmpty())
            query = (DBObject) JSON.parse("{'loc': {'$near':["+ lat + "," + lon + "]}}");
            else query = (DBObject) JSON.parse("{'adress':{'$regex':'" + street + "','$options':'i'},'loc': {'$near':["+ lat + "," + lon + "]}}");
        } else {
            query = (DBObject) JSON.parse("{'adress':{'$regex':'" + street + "','$options':'i'}}");
        }
        DBCursor pharmResults = apteki.find(query).limit(Integer.decode(resCount));
        JSONArray returnArray = new JSONArray();


        while(pharmResults.hasNext()){
            DBObject apteka = pharmResults.next();
            JSONObject returnObject = new JSONObject();
            Object location = null;
            DBObject latlng = null;
            try{
                returnObject.put("name",apteka.get("name"));
                returnObject.put("adress",apteka.get("adress"));
                location = apteka.get("loc");
                latlng = (DBObject) JSON.parse(location.toString());
                returnObject.put("lat",latlng.get("lat"));
                returnObject.put("lon",latlng.get("lon"));

            } catch(JSONException e){

            }
            returnArray.put(returnObject);
        }
        return returnArray;
    }
}
