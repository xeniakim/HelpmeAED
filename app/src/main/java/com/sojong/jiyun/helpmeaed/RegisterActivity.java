package com.sojong.jiyun.helpmeaed;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import org.apache.http.HttpResponse;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static android.media.CamcorderProfile.get;
import static com.sojong.jiyun.helpmeaed.MapsActivity.userState;
import static com.sojong.jiyun.helpmeaed.MapsActivity.USER_NORMAL;

/**
 * Created by Jiyun on 2017-05-30.
 */

public class RegisterActivity extends Activity {

    //private EditText edit_text_user_id = (EditText) findViewById(R.id.edit_text_user_id);
    private EditText edit_text_password;
    private RadioGroup radio_group;
    private Button button_register_send;

    private String sendingPassword;
    private String sendingGender;

    public static int user_id;
    public static String password;
    public static String gender;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        edit_text_password = (EditText) findViewById(R.id.edit_text_password);
        radio_group = (RadioGroup) findViewById(R.id.radio_group);
        button_register_send = (Button) findViewById(R.id.button_register_send);
    }

    private String sendByHttp() throws JSONException {

        String msg = null;

        sendingPassword = edit_text_password.getText().toString();
        if (radio_group.getCheckedRadioButtonId() == R.id.radio_male) {
            sendingGender = "m";
        } else if (radio_group.getCheckedRadioButtonId() == R.id.radio_female) {
            sendingGender = "f";
        }


        // build jsonObject
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("password", sendingPassword);
        jsonObject.accumulate("gender", sendingGender);

        // convert JSONObject to JSON to String
        if(msg == null) {
            msg = jsonObject.toString();
        }



        // 서버를 설정
        String URL = "http://45.76.197.124:3000/userRegister";

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

        Log.e("서버에서 받은 전체 내용 : ", responseResult);

        try {

            JSONObject json = new JSONObject(responseResult);

            user_id = json.getInt("user_id");
            password = json.getString("password");
            gender = json.getString("gender");
            userState = USER_NORMAL;

            Log.e("user_id", Integer.toString(user_id));
            Log.e("password", password);
            Log.e("gender", gender);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void sendUserInfo(View view) throws JSONException {

        jsonParser(sendByHttp());
        finish();

    }


}
