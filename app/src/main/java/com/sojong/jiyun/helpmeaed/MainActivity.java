package com.sojong.jiyun.helpmeaed;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String userName = "jiyun";
    String welcomeMessage = " 님, 오늘도 건강한 하루 되세요!";
    private boolean mIsBound = false;
    private ProviderService mProviderService = null;
    private static TextView connectionState;
    //TextView textViewWelcomeMessage = (TextView) findViewById(R.id.text_view_welcome_message);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Drawable alpha = ((ImageView)findViewById(R.id.image_view_background)).getDrawable();
        alpha.setAlpha(0xDD);
        connectionState = (TextView) findViewById(R.id.text_view_connection_state);
        mIsBound = bindService(new Intent(MainActivity.this, ProviderService.class), mConnection, Context.BIND_AUTO_CREATE);

        //DBHelper dbHelper = new DBHelper();

        //textViewWelcomeMessage.setText(userName + welcomeMessage);
        /*new Thread() {
            public void run() {
                AEDParser parse = new AEDParser();
            }
        }.start();*/
    }

    public void callSOS(View view) {
        String mNum = "01030615975";
        String tel = "tel:" + mNum;
        startActivity(new Intent("android.intent.action.CALL", Uri.parse(tel)));

    }

    public void showMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        /*intent.putExtra("map_coord", new AEDParser().coord);
        intent.putExtra("map_place_name", new AEDParser().place_name);*/
        startActivity(intent);
    }

    public void causeAlert(View view) {
        Intent intent = new Intent(this, AlertActivity.class);
        startActivity(intent);
    }

    public void seeHowto (View view) {
        Intent intent = new Intent(this, HowtoActivity.class);
        startActivity(intent);
    }

    public void goRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mProviderService = ((ProviderService.LocalBinder) service).getService();
            mIsBound = true;
            updateTextView("Watch Connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mProviderService = null;
            mIsBound = false;
            updateTextView("Watch Disconnected");
        }
    };

    public static void updateTextView(final String str) {
        connectionState.setText(str);
    }
    public static void addMessage(String data) { connectionState.setText(data); }



//    private boolean isFragmentB = true ;
//
//    // ... 코드 계속
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        // ... 코드 계속 [STE]
//
//        Button button1 = (Button) findViewById(R.id.button1) ;
//        button1.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switchFragment() ;
//            }
//        });
//    }
//
//    public void switchFragment() {
//        Fragment fr;
//
//        if (isFragmentB) {
//            fr = new FragmentB() ;
//        } else {
//            fr = new FragmentC() ;
//        }
//
//        isFragmentB = (isFragmentB) ? false : true ;
//
//        FragmentManager fm = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fm.beginTransaction();
//        fragmentTransaction.replace(R.id.fragmentBorC, fr);
//        fragmentTransaction.commit();
//    }
}
