package dev.apteki.webservice;

import android.app.ProgressDialog;
import android.app.Activity;
import android.os.AsyncTask;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;


public class WebserviceActivity extends AsyncTask<String, Void, String>{



    private Activity mActivity;
    private ProgressDialog dialog;

    /*
     *  Web service constructor
     *  @param Activity - main activity;
     */
    public WebserviceActivity(Activity activity)
    {
        this.mActivity = activity;
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
            result = this.GET(serverURL);
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
        Log.d("Async", result);
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

    public static String GET(String url){
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

}
