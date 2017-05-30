package com.sojong.jiyun.helpmeaed;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Jiyun on 2017-05-17.
 */

public class DBHelper extends SQLiteOpenHelper {

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        /* USER 테이블. 자동으로 값이 증가하는 기본키 컬럼과
        이메일, 패스워드, 별명, 실명, 생년월일, 성별 컬럼 */
        db.execSQL("CREATE TABLE USER (user_no INTEGER PRIMARY KEY AUTOINCREMENT, user_email TEXT UNIQUE NOT NULL, user_password TEXT NOT NULL, " +
                "user_nickname TEXT NOT NULL, user_name TEXT, user_birth TEXT, user_age INTEGER, user_sex TEXT);");
        /* AED 테이블. rnum 기본키 컬럼과
        경도, 위도, 설치기관, 설치장소, 주소(시도, 구군, 상세주소) 컬럼 */
        db.execSQL("CREATE TABLE AED (aed_id INTEGER PRIMARY KEY AUTOINCREMENT, aed_lat REAL NOT NULL, aed_lon REAL NOT NULL, " +
                "org TEXT NOT NULL, build_place TEXT NOT NULL, sido TEXT NOT NULL, gugun TEXT NOT NULL, detailed_address TEXT NOT NULL);");

    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertUser(String _email, String _password, String _nickname, String _name, String _birth, String _sex) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");
        String getTime = sdf.format(date);
        int age;
        if (Integer.parseInt(getTime.substring(4, 8)) - Integer.parseInt(_birth.substring(4, 8)) >= 0) {
            age = Integer.parseInt(getTime.substring(0, 4)) - Integer.parseInt(_birth.substring(0, 4));
        } else {
            age = Integer.parseInt(getTime.substring(0, 4)) - Integer.parseInt(_birth.substring(0, 4)) - 1;
        }

        db.execSQL("INSERT INTO USER VALUES(null, '" + _email + "', '" + _password + "', '" + _nickname + "', '" + _name
                + "', '"+ _birth + "', "+ age + ", '" + _sex +"');");
        db.close();
    }

    /*AED (aed_id INTEGER PRIMARY KEY, aed_lon REAL NOT NULL, aed_lat REAL NOT NULL,
            org TEXT NOT NULL, bulid_place TEXT NOT NULL, sido TEXT NOT NULL, gugun TEXT NOT NULL, detailed_address TEXT NOT NULL*/
    public void insertAED() throws IOException, ParserConfigurationException, SAXException {
        StringBuilder urlBuilder;
        StringBuilder sb = new StringBuilder();
        int pageNo = 0;

        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();

        while (true) {


            urlBuilder = new StringBuilder("http://apis.data.go.kr/B552657/AEDInfoInqireService/getEgytAedManageInfoInqire"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=QNUmjn16vY7tGSDfXBWh1seVyizuNDskzsx736v24kMgDvyp91Cljmc%2FoYu32I%2B4Z%2FMugV933PTH0mpzkqv6lg%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("Q0", "UTF-8") + "=" + URLEncoder.encode("서울특별시", "UTF-8")); /**/
            urlBuilder.append("&" + URLEncoder.encode("Q1", "UTF-8") + "=" + URLEncoder.encode("서대문구", "UTF-8")); /**/
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(Integer.toString(pageNo), "UTF-8")); /**/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /**/
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            System.out.println("Response code: " + conn.getResponseCode());
            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                break;
            }

            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
        }



        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(new InputSource(new StringReader(sb.toString())));

        NodeList nodelist = document.getElementsByTagName("item");

        double lon, lat;
        String org, build_place, sido, gugun, detailed_address;

        for(int i = 0; i< nodelist.getLength(); i++)
        {
            Node itemNode = nodelist.item(i);
            Element itemElement = (Element)itemNode;
            lat = Double.parseDouble(itemElement.getElementsByTagName("wgs84Lat").item(0).getTextContent());
            lon = Double.parseDouble(itemElement.getElementsByTagName("wgs84Lon").item(0).getTextContent());
            org = itemElement.getElementsByTagName("org").item(0).getTextContent();
            build_place = itemElement.getElementsByTagName("buildPlace").item(0).getTextContent();
            sido = itemElement.getElementsByTagName("sido").item(0).getTextContent();
            gugun = itemElement.getElementsByTagName("gugun").item(0).getTextContent();
            detailed_address = itemElement.getElementsByTagName("buildAddress").item(0).getTextContent();

            // DB에 행 추가

            /*AED (aed_id INTEGER PRIMARY KEY, aed_lon REAL NOT NULL, aed_lat REAL NOT NULL,
            org TEXT NOT NULL, bulid_place TEXT NOT NULL, sido TEXT NOT NULL, gugun TEXT NOT NULL, detailed_address TEXT NOT NULL*/
            db.execSQL("INSERT INTO AED VALUES(null, " + lat + ", " + lon + ", '" + org + "', '" + build_place
                    + "', '"+ sido + "', "+ gugun + ", '" + detailed_address +"');");

        }

        db.close();
    }

//    public void update(String item, int price) {
//        SQLiteDatabase db = getWritableDatabase();
//        // 입력한 항목과 일치하는 행의 가격 정보 수정
//        db.execSQL("UPDATE MONEYBOOK SET price=" + price + " WHERE item='" + item + "';");
//        db.close();
//    }

    public void deleteUser(String email) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM USER WHERE user_email='" + email + "';");
        db.close();
    }

    public String getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM USER", null);
        while (cursor.moveToNext()) {
            result += cursor.getString(0)
                    + " : "
                    + cursor.getString(1)
                    + " | "
                    + cursor.getInt(2)
                    + "원 "
                    + cursor.getString(3)
                    + "\n";
        }

        return result;
    }

}
