package com.example.real;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.example.real.adapter.ViewPagerAdapter;
import com.example.real.fragment.DatepickerFragment;
import com.example.real.fragment.MapFragment;
import com.example.real.fragment.TimepickerFragment;
import com.example.real.tool.MeasuredViewPager;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.geometry.Tm128;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;
import com.r0adkll.slidr.model.SlidrPosition;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;

public class ScheduleActivity extends AppCompatActivity {

    private final String TAG = "ScheduledActivity";

    MeasuredViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    private SlidrInterface slidr;

    DatePicker datePicker;
    TimePicker timePicker;

    CardView confirmbtn;
    String confirmeddate;
    CardView successbtn;
    String confirmedtime;
    String location;
    DatepickerFragment datepickerFragment;
    TimepickerFragment timepickerFragment;
    MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        SlidrConfig config = new SlidrConfig.Builder()
                .position(SlidrPosition.BOTTOM)
                .scrimStartAlpha(0f)
                .scrimEndAlpha(0f)
                .build();
        slidr =  Slidr.attach(this, config);

        viewPager = findViewById(R.id.SchduleActivityViewPager);

        datepickerFragment = new DatepickerFragment(new Callback() {
            @Override
            public void OnCallback(Object object) {
                View v = (View) object;
                datePicker = v.findViewById(R.id.FragmentDatePicker);
                confirmbtn = v.findViewById(R.id.FragmentConfirmBtn);
                confirmbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmeddate = datePicker.getYear() + "/" + String.valueOf(datePicker.getMonth()+1) + "/" + datePicker.getDayOfMonth();
                        viewPager.setCurrentItem(2,true);
                    }
                });
            }
        });
        timepickerFragment = new TimepickerFragment(new Callback() {
            @Override
            public void OnCallback(Object object) {
                View v = (View) object;
                timePicker = v.findViewById(R.id.FragmentTimePicker);
                successbtn = v.findViewById(R.id.FragmentSuccessBtn);
                successbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmedtime = timePicker.getHour() + "/" + timePicker.getMinute();
                        Intent intent = new Intent();
                        if(timePicker.getHour()<12){
                            String X = "오전 ";
                            String refined = String.valueOf(datePicker.getMonth()+1)+ "월 " + datePicker.getDayOfMonth() + "일, " +
                                    X + timePicker.getHour() + "시 " + timePicker.getMinute()+"분";
                            intent.putExtra("CONFIRMED",refined);
                            setResult(69,intent);
                            finish();}
                        else{
                            String X = "오후 ";
                            String refined = String.valueOf(datePicker.getMonth()+1)+ "월 " + datePicker.getDayOfMonth() + "일, " +
                                    X + timePicker.getHour() + "시 " + timePicker.getMinute()+"분";
                            intent.putExtra("CONFIRMED",refined);
                            intent.putExtra("CONFIRMED_LOCATION",location);
                            setResult(69,intent);
                            finish(); }


                    }
                });
            }
        });

        mapFragment = new MapFragment(this, new Callback() {
            @Override
            public void OnCallback(Object object) {

                MotionEvent event = (MotionEvent) object;
                Log.w(TAG, String.valueOf(event.getAction()));
                if (isInside(mapFragment.getMapView(), event)) {
                    Log.w(TAG, "Is inside");
                    slidr.lock();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.w(TAG, "Is outside");
                    slidr.unlock();
                }
            }
        }, new Callback() {
            @Override
            public void OnCallback(Object object) {
                View view = (View) object;
                CardView cardView = view.findViewById(R.id.mapFragmentCheckButton);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        location = mapFragment.getFocusAddress();
                        viewPager.setCurrentItem(1,true);
                    }
                });

            }
        });


        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addItem(mapFragment);
        viewPagerAdapter.addItem(datepickerFragment);
        viewPagerAdapter.addItem(timepickerFragment);
        viewPager.setAdapter(viewPagerAdapter);
    }

    private boolean isInside(View v, MotionEvent e) {
        return !(e.getX() < 0 || e.getY() < 0
                || e.getX() > v.getMeasuredWidth()
                || e.getY() > v.getMeasuredHeight());
    }
}