package com.sojong.jiyun.helpmeaed;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Jiyun on 2017-05-25.
 */

public class AlertActivity extends Activity {

    String userName = "user";
    String welcomeMessage = " 님이 위급상황입니다.\n당신의 도움이 필요합니다!";
    TextView textViewAlertMessage;

    Vibrator vibrator;
    long pattern[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        //DBHelper dbHelper = new DBHelper();

        textViewAlertMessage = (TextView) findViewById(R.id.text_view_alert_message);
        textViewAlertMessage.setText(userName + welcomeMessage);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);  // <- or Context.VIBRATE_SERVICE)
        //long[] pattern = { 2000, 1000 };
        //vibrator.vibrate(pattern,10);
        vibrator.vibrate(30000);
        sendSMS("01030615975", "서울특별시 마포구 노고산동 49-2\t지오다노 신촌점\t에서 심정지 환자가 발생했습니다. " +
                "신속한 출동 부탁드립니다.");
    }

    public void showMap(View view) {
        vibrator.cancel();
        Intent intent = new Intent(this, MapsAlertActivity.class);
        /*intent.putExtra("map_coord", new AEDParser().coord);
        intent.putExtra("map_place_name", new AEDParser().place_name);*/
        startActivity(intent);
    }

    public void stopAlert(View view) {
        vibrator.cancel();
        finish();
    }

    private void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "알림 문자 메시지가 전송되었습니다.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }


}
