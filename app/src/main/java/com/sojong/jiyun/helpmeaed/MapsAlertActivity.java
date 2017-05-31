package com.sojong.jiyun.helpmeaed;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import static com.sojong.jiyun.helpmeaed.MapsActivity.REPEAT_DELAY;
import static com.sojong.jiyun.helpmeaed.R.drawable.target;
import static com.sojong.jiyun.helpmeaed.R.id.map;


public class MapsAlertActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String locationProvider = null;
    private Location lastKnownLocation = null;
    private ImageButton button_current_location;
    private TextView text_view_current_location;
    public String sendingMessage;

    public int userId;
    private double lat;
    private double lon;

    public final static int REPEAT_DELAY = 5000;
    public Handler handler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_alert);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
        button_current_location = (ImageButton) findViewById(R.id.button_current_location);
        text_view_current_location = (TextView) findViewById(R.id.text_view_current_location);

        button_current_location.setOnClickListener( buttonRefreshClickListener );

        lat = 37.5556352;
        lon = 126.93464719999997;

        button_current_location = (ImageButton) findViewById(R.id.button_current_location);


        handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                button_current_location.performClick();
                this.sendEmptyMessageDelayed(0, REPEAT_DELAY);        // REPEAT_DELAY 간격으로 계속해서 반복하게 만들어준다
            }
        };

        handler.sendEmptyMessage(0);

    }

    /**

     * 서버에 데이터를 보내는 메소드

     * @param msg

     * @return

     */

    private String SendByHttp(String msg) throws JSONException {

        // build jsonObject
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("user_id", userId);
        jsonObject.accumulate("lat", lat);
        jsonObject.accumulate("lon", lon);

        // convert JSONObject to JSON to String
        if(msg == null) {
            msg = jsonObject.toString();
        }



        // 서버를 설정
        String URL = "http://45.76.197.124:3000/userLocationTest";

        DefaultHttpClient client = new DefaultHttpClient();

        try {

			/* 서버로 전송 */

            // HttpPost post = new HttpPost(URL+"?msg="+msg);
            HttpPost post = new HttpPost(URL);


            StringEntity jsonparam = new StringEntity(jsonObject.toString());
            //jsonparam.setChunked(true);

            jsonparam.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            //post.addHeader("content-type", "application/json;charset=UTF-8");
            post.setEntity(jsonparam);




			/* 지연시간 최대 3초 */

            HttpParams params = client.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 3000);
            HttpConnectionParams.setSoTimeout(params, 3000);


			/* 데이터 보낸 뒤 서버에서 데이터를 받아오는 과정 */

            HttpResponse response = client.execute(post);


            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

            String line = null;
            String result = "";

            while ((line = bufreader.readLine()) != null) {
                result += line;
            }

            return result;

        } catch (Exception e) {

            e.printStackTrace();
            client.getConnectionManager().shutdown();	// 연결 지연 종료
            return "";
        }
    }



    /**

     * 받은 JSON 객체를 파싱하는 메소드

     * @param page

     * @return

     */

    public String[][] jsonParserList(String pRecvServerPage) {



        Log.i("서버에서 받은 전체 내용 : ", pRecvServerPage);



        try {

            JSONObject json = new JSONObject(pRecvServerPage);
            JSONArray jArr = json.getJSONArray("List");


            // 받아온 pRecvServerPage를 분석하는 부분

            String[] jsonName = {"msg1", "msg2", "msg3"};
            String[][] parseredData = new String[jArr.length()][jsonName.length];

            for (int i = 0; i < jArr.length(); i++) {

                json = jArr.getJSONObject(i);

                if(json != null) {

                    for(int j = 0; j < jsonName.length; j++) {

                        parseredData[i][j] = json.getString(jsonName[j]);

                    }

                }

            }





            // 분해 된 데이터를 확인하기 위한 부분

            for(int i=0; i<parseredData.length; i++){
                Log.i("JSON을 분석한 데이터 "+i+" : ", parseredData[i][0]);
                Log.i("JSON을 분석한 데이터 "+i+" : ", parseredData[i][1]);
                Log.i("JSON을 분석한 데이터 "+i+" : ", parseredData[i][2]);
            }



            return parseredData;

        } catch (JSONException e) {

            e.printStackTrace();

            return null;

        }

    }


    private void drawMarker() {

        //기존 마커 지우기
        mMap.clear();

        LatLng latLng = new LatLng(lat, lon);

        // Showing the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Map 을 zoom 합니다.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        // 마커 설정.
        MarkerOptions optFirst = new MarkerOptions();
        optFirst.alpha(0.7f);
        optFirst.anchor(0.5f, 0.5f);
        optFirst.position(latLng);// 위도 • 경도
        optFirst.icon(BitmapDescriptorFactory.fromResource(R.drawable.red_circle));
        //optFirst.icon(BitmapDescriptorFactory.fromResource(R.drawable.patient));
        mMap.addMarker(optFirst).showInfoWindow();
    }

    private View.OnClickListener buttonRefreshClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) throws SecurityException {

            // Update location to get.
            text_view_current_location.setText("finding your location...");
            LocationManager lm = (LocationManager) getSystemService(Context. LOCATION_SERVICE);
            lm.removeUpdates( locationListener );    // Stop the update if it is in progress.
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER , 0, 0, locationListener );

            //text_view_current_location.setText(SendByHttp(sendingMessage));
            try {
                Log.e("response data", SendByHttp(sendingMessage));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    };

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            LocationManager lm = (LocationManager)getSystemService(Context. LOCATION_SERVICE);

            // Get the last location, and update UI.
            lastKnownLocation = location;
            lat = lastKnownLocation.getLatitude();
            lon = lastKnownLocation.getLongitude();
            Log.e("coordinate", String.format("%f %f", lat, lon));
            text_view_current_location.setText(String.format("%f %f", lat, lon));
            drawMarker();

            // Stop the update to prevent changing the location.
            lm.removeUpdates( this );
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

    };


    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;

//        new Thread() {
//            public void run(GoogleMap map) {
//                //AEDParser parse = new AEDParser();
//
//                //System.out.println(parse.coord.get(0).first + "\t" + parse.coord.get(0).second);
//                map.addMarker(new MarkerOptions()
//                        .position(new LatLng(Integer.parseInt(parse.coord.get(0).first), Integer.parseInt(parse.coord.get(0).second)))
//                        .title("Hello world"));
//            }
//        }.start();




        LatLng latLng = new LatLng(lat, lon);

        // Showing the current location in Google Map
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Map 을 zoom 합니다.
        map.animateCamera(CameraUpdateFactory.zoomTo(17));


        // 마커 설정.
//        MarkerOptions optFirst = new MarkerOptions();
//        optFirst.alpha(1.0f);
//        optFirst.anchor(0.5f, 0.5f);
//        optFirst.position(latLng);// 위도 • 경도
//        //optFirst.icon(BitmapDescriptorFactory.fromResource(R.drawable.red_circle));
//        optFirst.icon(BitmapDescriptorFactory.fromResource(R.drawable.patient));
//        map.addMarker(optFirst).showInfoWindow();
//        map.addMarker(new MarkerOptions()
//                .position(new LatLng(37.556081435548286, 126.93584374615689))
//                .title("현대백화점 신촌점 1층 안전관리실"));

    }





}
