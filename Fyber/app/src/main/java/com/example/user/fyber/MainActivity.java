package com.example.user.fyber;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.google.android.gms.ads.identifier.AdvertisingIdClient.getAdvertisingIdInfo;


public class MainActivity extends AppCompatActivity implements  AsyncResponse{

    private TextView mTextView;
    private ProgressBar progress;
    private Button mButton;
    private ListView list;
    private AdapterOffers adapter;
    private MyAsyncTask asyncTask;


    final protected static char[] hexArray = "0123456789abcdef".toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String SHA1(String text) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(text.getBytes(), 0, text.length());
            byte[] sha1hash = md.digest();
            return bytesToHex(sha1hash);

        } catch (Exception e) {

        }

        return text;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mTextView = (TextView)findViewById(R.id.response);
        mButton = (Button)findViewById(R.id.btn);

            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new MyAsyncTask(MainActivity.this).execute();

                }
            });

        list = (ListView)findViewById(R.id.list);
        progress = (ProgressBar)findViewById(R.id.progress);


    }

    @Override
    public void processFinish(ArrayList<Offer> offers, boolean anyResults) {

        if(anyResults) {
            adapter = new AdapterOffers(MainActivity.this, offers);
            list.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        else
          mTextView.setText("No data!");

        progress.setVisibility(View.INVISIBLE);
        mButton.setEnabled(true);

    }

    class MyAsyncTask extends AsyncTask<String, String, Void> {


        InputStream inputStream = null;
        String result = "";
        public AsyncResponse delegate = null;

        public MyAsyncTask( AsyncResponse delegate) {
            super();
            this.delegate = delegate;
        }

        protected void onPreExecute() {

            progress.setVisibility(View.VISIBLE);
            mButton.setEnabled(false);
            mTextView.setText("");
        }

        @Override
        protected Void doInBackground(String... params) {

            try {

            Long tsLong = System.currentTimeMillis()/1000;
            String ts = tsLong.toString();

                WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
                String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

            String url = "http://api.fyber.com/feed/v1/offers.json?";
            String parameters ="appid=2070&device_id="+ getAdvertisingIdInfo(MainActivity.this).getId()+"&google_ad_id="+ getAdvertisingIdInfo(MainActivity.this).getId()+
                    "&google_ad_id_limited_tracking_enabled=false&ip="+ip+"&locale=de&offer_types=112&os_version="+android.os.Build.VERSION.RELEASE+"&timestamp="+ ts +
                    "&uid=spiderman";
            String hashkey = SHA1(parameters+"&1c915e3b5d42d05136185030892fbb846c278927");
            parameters+="&hashkey="+hashkey;

            Log.d("Fyber",url+parameters);

            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();


                // Set up HTTP post

                // HttpClient is more then less deprecated. Need to change to URLConnection
                HttpClient httpClient = new DefaultHttpClient();

                HttpGet httpPost = new HttpGet(url+parameters);
                //httpGet.setEntity(new UrlEncodedFormEntity(param));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();

                // Read content & Log
                inputStream = httpEntity.getContent();
            } catch (UnsupportedEncodingException e1) {
                Log.d("UnsupportedEncoding", e1.toString());
                e1.printStackTrace();
            } catch (ClientProtocolException e2) {
                Log.d("ClientProtocolException", e2.toString());
                e2.printStackTrace();
            } catch (IllegalStateException e3) {
                Log.d("IllegalStateException", e3.toString());
                e3.printStackTrace();
            } catch (IOException e4) {
                Log.d("IOException", e4.toString());
                e4.printStackTrace();
            }
             catch (Exception e5) {
                Log.d("Exception", e5.toString());
                e5.printStackTrace();
        }
            // Convert response to string using String Builder
            try {
                BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                StringBuilder sBuilder = new StringBuilder();

                String line = null;
                while ((line = bReader.readLine()) != null) {
                    sBuilder.append(line + "\n");
                }

                inputStream.close();
                result = sBuilder.toString();

            } catch (Exception e) {
               Log.d("Fyber", "Error converting result " + e.toString());
            }


            return null;
        } // protected Void doInBackground(String... params)

        protected void onPostExecute(Void v) {
            //parse JSON data

            boolean anyResults = true;

            ArrayList<Offer> results = new ArrayList<Offer>();
            try {

                    JSONObject jObject = new JSONObject(result);
                    JSONArray offers = jObject.getJSONArray("offers");

               String code = jObject.get("code").toString();
                Log.d("Fyber", code+"");

                if( code.equalsIgnoreCase("NO_CONTENT"))
                    anyResults = false;


                for( int i= 0 ; i < offers.length(); i++) {
                    String title = offers.getJSONObject(i).get("title").toString();
                    String teaser = offers.getJSONObject(i).get("teaser").toString();
                    String payout = offers.getJSONObject(i).get("payout").toString();
                    JSONObject thumbnails = offers.getJSONObject(i).getJSONObject("thumbnail");
                    String hires = thumbnails.getString("hires");

                    results.add(new Offer(title,teaser,payout,hires));

                    Log.d("Fyber", title + " " + teaser + " " + payout + " " + hires);
                }


            } catch (JSONException e) {
                Log.d("Fyber", "JSONException " + e.toString());
            } // catch (JSONException e)


           delegate.processFinish(results,anyResults);


        } // protected void onPostExecute(Void v)
    } //class MyAsyncTask extends AsyncTask<String, String, Void>


}
