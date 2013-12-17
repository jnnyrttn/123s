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


public class WebserviceActivity extends AsyncTask<String, Void, String>{

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
    protected String doInBackground(String... urls)
    {

        String qstring = urls[1];
        String result = "";
        if(!qstring.isEmpty()){
            String serverURL = urls[0]+qstring;
            //result = this.GET(serverURL,"","","");
            try{
                result = this.MongoSearch(qstring);
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
    public void onPostExecute(String result) {
        super.onPostExecute(result);
        this.dialog.dismiss();
        callback.onTaskComplete(result);

        //jsonParsed.setText(result);

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

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Error occured!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }
    public static String POST(String url, String address, String latitude, String longtitude) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        InputStream inputStream = null;
        String result = "";

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            //nameValuePairs.add(new BasicNameValuePair("json", "1"));
            //nameValuePairs.add(new BasicNameValuePair("lat", latitude));
            //nameValuePairs.add(new BasicNameValuePair("long", longtitude));
            //nameValuePairs.add(new BasicNameValuePair("address", address));

            nameValuePairs.add(new BasicNameValuePair("query", "{}"));
            httppost.setHeader("Content-Type",
                    "application/json;charset=utf-8");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            inputStream = response.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Error occured!";

        } catch (ClientProtocolException e) {

        } catch (IOException e) {

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        Log.d("Async",result);
        return result;
    }
    public static String MongoSearch(String street) throws UnknownHostException{
        String result = "";
        String mongoUri = "mongodb://put_aptuser:fgkgw23x8@ds061248.mongolab.com:61248/put_apteki";
        MongoClientURI uri  = new MongoClientURI(mongoUri);
        MongoClient client = new MongoClient(uri);
        DB db = client.getDB(uri.getDatabase());

        DBCollection apteki = db.getCollection("apteki");
        BasicDBObject findQuery = new BasicDBObject("street", new BasicDBObject("$eq","Naramowicka"));

        DBCursor docs = apteki.find(findQuery);
        Log.d("Async","Foo");
        while(docs.hasNext()){
            DBObject doc = docs.next();
            Log.d("Async", " " + doc.get("name") + " ");
        }

        return result;
    }
}
