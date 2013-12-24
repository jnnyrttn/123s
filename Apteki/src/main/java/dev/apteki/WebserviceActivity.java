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
        JSONArray result = new JSONArray();

        if(!qstring.isEmpty() || ( !lat.isEmpty() && !lng.isEmpty()) ){
            try{
                if(!lat.isEmpty() && !lng.isEmpty()){
                    String[] loc = {lat,lng};
                    result = this.MongoSearch(qstring,urls[0],loc);
                }
                else result = this.MongoSearch(qstring,urls[0]);
            } catch (UnknownHostException e){

            }
        }
        return result;
    }
    @Override
    protected void onPreExecute()
    {
        this.dialog = ProgressDialog.show(this.mActivity, "", "Loading...");
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

    public static String GET(String url, String address, String latitude, String longtitude){
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Error occured!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    public static JSONArray MongoSearch(String street, String mongoUri, String... loc ) throws UnknownHostException{
        String result = "";

        MongoClientURI uri  = new MongoClientURI(mongoUri);
        MongoClient client = new MongoClient(uri);
        DB db = client.getDB(uri.getDatabase());

        DBCollection apteki = db.getCollection("apteki");
        DBObject query;

        if(loc.length > 1) {
            String lat = loc[0];
            String lon = loc[1];
            query = (DBObject) JSON.parse("{'loc': {'$near':["+ lat + "," + lon + "]}}");
            //Log.d("Async","{loc: {$near:["+ lat + ","+ lon +"]}}" );
        } else {
            query = (DBObject) JSON.parse("{'adress':{'$regex':'" + street + "','$options':'i'}}");
        }

        DBCursor pharmResults = apteki.find(query).limit(6);
        JSONArray returnArray = new JSONArray();

        if(pharmResults.count() == 0){
            Log.d("Async","No results found !");
        }


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
            //Log.d("Async","Results count :" + pharmResults.count());
            //Log.d("Async"," name: " + apteka.get("name") + " address : " + apteka.get("adress") + " lat : " + latlng.get("lat") + " lon : " + latlng.get("lon"));
            //System.out.println(location);
        }
        //return result;
        return returnArray;
    }
}
