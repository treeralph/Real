package com.example.real.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.real.Callback;
import com.example.real.R;
import com.example.real.tool.OnSwipeTouchListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.geometry.Tm128;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.EventListener;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private final String TAG = "MapTest";

    NaverMap myNaverMap;
    EditText editText;
    Button button;

    Handler handler;
    Handler handlerT;

    Marker marker;

    Context context;
    Callback callback;
    Callback callback1;
    MapView mapView;

    String focusAddress = "";

    public MapFragment(Context context, Callback callback, Callback callback1) {
        this.context = context;
        this.callback = callback;
        this.callback1 = callback1;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        callback1.OnCallback(view);

        editText = view.findViewById(R.id.mapFragmentEditText);
        button = view.findViewById(R.id.mapFragmentButton);

        FragmentManager fm = this.getChildFragmentManager();
        com.naver.maps.map.MapFragment mapFragment = (com.naver.maps.map.MapFragment) fm.findFragmentById(R.id.mapFragmentNaverMap);
        mapView = mapFragment.getMapView();

        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                container.onInterceptTouchEvent(motionEvent);
                callback.OnCallback(motionEvent);
                return false;
            }
        });
        mapFragment.getMapAsync(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tempString = editText.getText().toString();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        requestGeocode(tempString, new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                MyCoordinate result = (MyCoordinate) object;
                                Log.w(TAG, "result: " + result.toString());
                                Message message = handler.obtainMessage();
                                message.obj = result;
                                handler.sendMessage(message);
                            }
                        });
                    }
                });
                thread.start();
            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                MyCoordinate result = (MyCoordinate) message.obj;
                Tm128 tm128 = new Tm128(result.x, result.y);
                LatLng latLng = tm128.toLatLng();

                CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(latLng, 18);
                myNaverMap.moveCamera(cameraUpdate);

                Log.w(TAG, "tempString: " + result.toString());

                marker = new Marker();
                marker.setPosition(latLng);
                marker.setMap(myNaverMap);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        requestReverseGeocode(latLng, new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                String tempValue = (String) object;
                                Message message = handlerT.obtainMessage();
                                message.obj = tempValue;
                                handlerT.sendMessage(message);
                            }
                        });
                    }
                });
                thread.start();
                return false;
            }
        });

        handlerT = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {

                String textViewText = (String) message.obj;
                InfoWindow infoWindow = new InfoWindow();
                infoWindow.setAdapter(new InfoWindow.DefaultViewAdapter(context) {
                    @NonNull
                    @Override
                    protected View getContentView(@NonNull InfoWindow infoWindow) {
                        View view = View.inflate(context, R.layout.map_info, null);
                        TextView txtV = view.findViewById(R.id.textViewDesign);
                        txtV.setText(textViewText);
                        focusAddress = textViewText;
                        return view;
                    }
                });
                infoWindow.open(marker);
                return false;
            }
        });
        return view;
    }

    public String getFocusAddress() {
        return focusAddress;
    }

    public View getMapView() {
        return mapView;
    }

    public void requestGeocode(String address, Callback callback) {
        try {
            BufferedReader bufferedReader;
            StringBuilder stringBuilder = new StringBuilder();

            String query = "https://openapi.naver.com/v1/search/local.json?query=" + URLEncoder.encode(address, "UTF-8");

            URL url = new URL(query);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if (conn != null) {

                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                conn.setRequestMethod("GET");

                conn.setRequestProperty("X-Naver-Client-Id", "0WrITPF1lWl1GkDNVSsK");
                conn.setRequestProperty("X-Naver-Client-Secret", "rVkV33A6Fc");

                conn.setDoInput(true);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    Log.w(TAG, line + "\n");
                    stringBuilder.append(line + "\n");
                }

                JSONObject jsonObject = new JSONObject(stringBuilder.toString());

                Log.w(TAG, "JSON_Test: " + jsonObject.toString());

                String tempString = jsonObject.get("items").toString();
                JSONObject jsonAddress = new JSONObject(tempString.substring(1, tempString.length() - 1));

                int intX = Integer.parseInt(jsonAddress.get("mapx").toString());
                int intY = Integer.parseInt(jsonAddress.get("mapy").toString());
                MyCoordinate myCoordinate = new MyCoordinate(intX, intY);

                bufferedReader.close();
                conn.disconnect();

                callback.OnCallback(myCoordinate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestReverseGeocode(LatLng latLng, Callback callback) {
        try {
            BufferedReader bufferedReader;
            StringBuilder stringBuilder = new StringBuilder();

            String coordsQuery = "coords=" + latLng.longitude + "," + latLng.latitude;
            String sourcecrsQuery = "sourcecrs=epsg:4326";
            String orderQuery = "orders=legalcode,admcode,addr,roadaddr";
            String outputQuery = "output=json";

            String query = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?request=coordsToaddr&" + coordsQuery + "&" + sourcecrsQuery + "&" + orderQuery + "&" + outputQuery;
            Log.w(TAG, "Query: " + query);

            URL url = new URL(query);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if (conn != null) {

                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                conn.setRequestMethod("GET");

                conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "465zd9bim0");
                conn.setRequestProperty("X-NCP-APIGW-API-KEY", "L9UFA2GUsydoxsTKT2fXjALBWlzV2swkWF1uw4Ii");
                conn.setDoInput(true);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    Log.w(TAG, line + "\n");
                    stringBuilder.append(line + "\n");
                }

                JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                Log.w(TAG, "JSON_Test: " + jsonObject.toString());
                JSONArray jsonResultList = jsonObject.getJSONArray("results");

                JSONObject jsonResult = (JSONObject) jsonResultList.get(jsonResultList.length() - 1);

                String returnValue = "";
                switch ((String) jsonResult.get("name")) {
                    case "roadaddr":

                        JSONObject regionJson = jsonResult.getJSONObject("region");
                        JSONObject landJson = jsonResult.getJSONObject("land");

                        JSONObject area1Json = regionJson.getJSONObject("area1");
                        JSONObject area2Json = regionJson.getJSONObject("area2");
                        JSONObject area3Json = regionJson.getJSONObject("area3");
                        JSONObject addition0Json = landJson.getJSONObject("addition0");

                        ArrayList<String> addressList = new ArrayList<>();

                        addressList.add(area1Json.getString("name"));
                        addressList.add(area2Json.getString("name"));
                        addressList.add(area3Json.getString("name"));
                        addressList.add(landJson.getString("name"));
                        addressList.add(landJson.getString("number1"));
                        addressList.add(landJson.getString("number2"));
                        addressList.add(addition0Json.getString("value"));

                        returnValue = String.join(" ", addressList);
                        Log.w(TAG, "ReturnValue: " + returnValue);

                        break;

                    case "addr":

                        JSONObject regionJsonA = jsonResult.getJSONObject("region");
                        JSONObject landJsonA = jsonResult.getJSONObject("land");

                        JSONObject area1JsonA = regionJsonA.getJSONObject("area1");
                        JSONObject area2JsonA = regionJsonA.getJSONObject("area2");
                        JSONObject area3JsonA = regionJsonA.getJSONObject("area3");

                        ArrayList<String> addressListA = new ArrayList<>();

                        addressListA.add(area1JsonA.getString("name"));
                        addressListA.add(area2JsonA.getString("name"));
                        addressListA.add(area3JsonA.getString("name"));
                        if (landJsonA.getString("number2").equals("")) {
                            addressListA.add(landJsonA.getString("number1"));
                        } else {
                            addressListA.add(landJsonA.getString("number1") + "-" + landJsonA.getString("number2"));
                        }
                        returnValue = String.join(" ", addressListA);
                        Log.w(TAG, "ReturnValue: " + returnValue);

                        break;
                    case "admcode":

                        break;
                    case "legalcode":

                        break;
                    default:

                }

                bufferedReader.close();
                conn.disconnect();

                callback.OnCallback(returnValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        myNaverMap = naverMap;
        myNaverMap.setExtent(new LatLngBounds(new LatLng(31.43, 122.37), new LatLng(44.35, 132)));
        myNaverMap.setMinZoom(5.0);
        myNaverMap.setMaxZoom(18.0);

        UiSettings uiSettings = myNaverMap.getUiSettings();
        uiSettings.setZoomControlEnabled(false);

        marker = new Marker();

        myNaverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {

                marker.setPosition(new LatLng(latLng.latitude, latLng.longitude));
                marker.setMap(myNaverMap);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        requestReverseGeocode(latLng, new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                String tempValue = (String) object;
                                Message message = handlerT.obtainMessage();
                                message.obj = tempValue;
                                handlerT.sendMessage(message);
                            }
                        });
                    }
                });
                thread.start();

            }
        });

        myNaverMap.setOnSymbolClickListener(symbol -> {
            LatLng latLng =  symbol.getPosition();

            marker.setPosition(new LatLng(latLng.latitude, latLng.longitude));
            marker.setMap(myNaverMap);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    requestReverseGeocode(latLng, new Callback() {
                        @Override
                        public void OnCallback(Object object) {
                            String tempValue = (String) object;
                            Message message = handlerT.obtainMessage();
                            message.obj = tempValue;
                            handlerT.sendMessage(message);
                        }
                    });
                }
            });
            thread.start();

            return true;
        });
    }


    public static class MyCoordinate{
        private int x;
        private int y;
        public MyCoordinate(int x, int y){
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        @NonNull
        @Override
        public String toString() {
            return String.valueOf(getX()) + ", " + String.valueOf(getY());
        }
    }
}