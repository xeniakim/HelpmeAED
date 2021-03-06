package com.sojong.jiyun.helpmeaed;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
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

import static android.R.attr.handle;
import static com.sojong.jiyun.helpmeaed.R.drawable.target;
import static com.sojong.jiyun.helpmeaed.R.id.map;
import static com.sojong.jiyun.helpmeaed.RegisterActivity.user_id;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String locationProvider = null;
    private Location lastKnownLocation = null;
    private ImageButton button_current_location;
    private TextView text_view_current_location;
    public String sendingMessage;


    //public int userId;   //public user_id register activity에 있음
    public static int user_id_patient;
    public static int user_id_tmp;
    private double lat;
    private double lon;

    public final static int USER_NORMAL = 0;
    public final static int USER_EMERGENCY = 1;
    public static int userState;


    public final static int REPEAT_DELAY = 5000;
    public Handler handler;

    public double lat1, lon1, lat2, lon2, lat3, lon3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

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

        lat1 = 0; lon1 = 0; lat2 = 0; lon2 = 0; lat3 = 0; lon3 = 0;

        button_current_location = (ImageButton) findViewById(R.id.button_current_location);


        user_id_patient = 2014;
        user_id_tmp = user_id;


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
        jsonObject.accumulate("user_id", user_id); //회원가입 시 생성된 아이디
        jsonObject.accumulate("lat", lat);
        jsonObject.accumulate("lon", lon);

        // convert JSONObject to JSON to String
        if(msg == null) {
            msg = jsonObject.toString();
        }

//TODO: 시계와 연결하면 위급상황시 userState 변경
        String URL = null;

        if (userState == USER_NORMAL) {
            URL = "http://45.76.197.124:3000/userRenew";
        } else if (userState == USER_EMERGENCY) {
            URL = "http://45.76.197.124:3000/userWarn";
        }

        // 서버를 설정
        //String URL = "http://45.76.197.124:3000/userRenew";
        //String URL = "http://45.76.197.124:3000/userWarn";


        DefaultHttpClient client = new DefaultHttpClient();

        try {

			/* 서버로 전송 */

           // HttpPost post = new HttpPost(URL+"?msg="+msg);
           HttpPost post = new HttpPost(URL);


            StringEntity jsonparam = new StringEntity(msg);
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

    public void jsonParser(String responseResult) {


//        {lon1 : rows[0].wgs84Lon,
//                lat1: rows[0].wgs84Lat,
//                lon2: rows[1].wgs84Lon,
//                lat2: rows[1].wgs84Lat,
//                lon3: rows[2].wgs84Lon,
//                lat3: rows[2].wgs84Lat}

        Log.e("서버에서 받은 전체 내용 : ", responseResult);



        try {

            JSONObject json = new JSONObject(responseResult);

            lat1 = json.getDouble("lat1");
            lon1 = json.getDouble("lon1");
            lat2 = json.getDouble("lat2");
            lon2 = json.getDouble("lon2");
            lat3 = json.getDouble("lat3");
            lon3 = json.getDouble("lon3");
            //userState = USER_NORMAL;

            Log.e("1", String.format("%f, %f", lat1, lon1));
            Log.e("2", String.format("%f, %f", lat2, lon2));
            Log.e("3", String.format("%f, %f", lat3, lon3));

            LatLng latLng1 = new LatLng(lat, lon);
            LatLng latLng2 = new LatLng(lat, lon);
            LatLng latLng3 = new LatLng(lat, lon);

            MarkerOptions opt1 = new MarkerOptions();
            opt1.position(latLng1);
            MarkerOptions opt2 = new MarkerOptions();
            opt1.position(latLng2);
            MarkerOptions opt3 = new MarkerOptions();
            opt1.position(latLng3);

            mMap.addMarker(opt1).showInfoWindow();
            mMap.addMarker(opt2).showInfoWindow();
            mMap.addMarker(opt3).showInfoWindow();

        } catch (JSONException e) {

            e.printStackTrace();


        }

    }


    private void drawMarker() {

        //기존 마커 지우기
        mMap.clear();

        LatLng latLng = new LatLng(lat, lon);

        // Showing the current location in Google Map
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//
//        // Map 을 zoom 합니다.
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        // 마커 설정.
        MarkerOptions optFirst = new MarkerOptions();
        optFirst.alpha(0.7f);
        optFirst.anchor(0.5f, 0.5f);
        optFirst.position(latLng);// 위도 • 경도
        optFirst.icon(BitmapDescriptorFactory.fromResource(R.drawable.red_circle));
        //optFirst.icon(BitmapDescriptorFactory.fromResource(R.drawable.patient));
        mMap.addMarker(optFirst).showInfoWindow();

        LatLng latLng1 = new LatLng(37.562388,126.935292);
        LatLng latLng2 = new LatLng(37.565784,126.93857200000002);
        LatLng latLng3 = new LatLng(37.5560736,126.93579769999997);

        MarkerOptions opt1 = new MarkerOptions();
        opt1.position(latLng1);
        MarkerOptions opt2 = new MarkerOptions();
        opt2.position(latLng2);
        MarkerOptions opt3 = new MarkerOptions();
        opt3.position(latLng3);

        mMap.addMarker(opt1).showInfoWindow();
        mMap.addMarker(opt2).showInfoWindow();
        mMap.addMarker(opt3).showInfoWindow();

        LatLng latLng_p = new LatLng(37.56309,126.93566820000001);
        MarkerOptions opt_p = new MarkerOptions();
        opt_p.position(latLng_p);
        opt_p.icon(BitmapDescriptorFactory.fromResource(R.drawable.patient));
        mMap.addMarker(opt_p).showInfoWindow();


        Log.e("state", Integer.toString(userState));
        user_id = user_id_tmp;
        userState = USER_NORMAL;
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

    public void changeUserState(View view) {
            user_id = user_id_patient;
//        if (userState == USER_NORMAL) {
            userState = USER_EMERGENCY;
//        } else if (userState == USER_EMERGENCY) {
//            userState = USER_NORMAL;
//        }
    }




}
