//package com.sojong.jiyun.helpmeaed;
//
//import android.app.Fragment;
//import android.location.Location;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.MapView;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//import static com.sojong.jiyun.helpmeaed.R.id.button_current_location;
//
///**
// * Created by Jiyun on 2017-05-24.
// */
//
//public class MapsFragment extends Fragment implements OnMapReadyCallback {
//
//
//    private GoogleMap mMap;
//    private MapView mapView;
//    private boolean mapsSupported = true;
//    private String locationProvider = null;
//    private Location lastKnownLocation = null;
//    private ImageButton button_current_location;
//    private double lat;
//    private double lon;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        View rootView = inflater.inflate(R.layout.activity_loc, container, false);
//        button_current_location = (ImageButton) rootView.findViewById(R.id.button_current_location);
//        mapView = (MapView) rootView.findViewById(R.id.map);
//
//
//        return rootView;
//    }
//
//    @Override
//    public void onMapReady(GoogleMap map) {
//
////        new Thread() {
////            public void run(GoogleMap map) {
////                //AEDParser parse = new AEDParser();
////
////                //System.out.println(parse.coord.get(0).first + "\t" + parse.coord.get(0).second);
////                map.addMarker(new MarkerOptions()
////                        .position(new LatLng(Integer.parseInt(parse.coord.get(0).first), Integer.parseInt(parse.coord.get(0).second)))
////                        .title("Hello world"));
////            }
////        }.start();
//
//
//
//
//    }
//
//    private void initializeMap() {
//     if (mMap == null && mapsSupported) {
//        mapView = (MapView) getActivity().findViewById(R.id.map);
//        mMap = mapView.getMap();
//
//        GpsInfo gps = new GpsInfo(getActivity().getApplicationContext());
//        //gps.getLocation();
//        // GPS 사용유무 가져오기
//        if (gps.isGetLocation()) {
//            double latitude = gps.getLatitude();
//            double longitude = gps.getLongitude();
//
//            // Creating a LatLng object for the current location
//
//            //LatLng latLng = new LatLng(37.28,126.97243608534338);
//            LatLng latLng = new LatLng(latitude, longitude);
//            ex_point = latLng;
//
//            // Showing the current location in Google Map
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//
//            // Map 을 zoom 합니다.
//            googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
//
//            // 마커 설정.
//            MarkerOptions optFirst = new MarkerOptions();
//            optFirst.alpha(0.5f);
//            optFirst.anchor(0.5f, 0.5f);
//            optFirst.position(latLng);// 위도 • 경도
//            optFirst.title("현재 지점");// 제목 미리보기
//            optFirst.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
//            googleMap.addMarker(optFirst).showInfoWindow();
//
//            isInit = true;
//        }
//    }
//}
//
//}
