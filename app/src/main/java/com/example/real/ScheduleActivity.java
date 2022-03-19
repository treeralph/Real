package com.example.real;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.real.adapter.ViewPagerAdapter;
import com.example.real.fragment.DatepickerFragment;
import com.example.real.fragment.TimepickerFragment;
import com.example.real.tool.MeasuredViewPager;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;
import com.r0adkll.slidr.model.SlidrPosition;

import java.time.LocalDateTime;
import java.util.Calendar;

public class ScheduleActivity extends AppCompatActivity {

    MeasuredViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    private SlidrInterface slidr;
    DatePicker datePicker;
    TimePicker timePicker;
    Button confirmbtn;
    String confirmeddate;
    Button successbtn;
    String confirmedtime;
    DatepickerFragment datepickerFragment;
    TimepickerFragment timepickerFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        SlidrConfig config = new SlidrConfig.Builder()
                .position(SlidrPosition.BOTTOM)
                .scrimStartAlpha(0f)
                .scrimEndAlpha(0f)
                .build();
        Slidr.attach(this, config);

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
                        Toast.makeText(ScheduleActivity.this, confirmeddate, Toast.LENGTH_SHORT).show();
                        viewPager.setCurrentItem(1,true);
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
                            setResult(69,intent);
                            finish(); }


                    }
                });
            }
        });
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addItem(datepickerFragment);
        viewPagerAdapter.addItem(timepickerFragment);
        viewPager.setAdapter(viewPagerAdapter);










        /*
        datePicker = findViewById(R.id.ScheduleActivitydataPicker);
        timePicker = findViewById(R.id.ScheduleActivityTimePicker);
        scheduletextview = findViewById(R.id.SchduleActivityScheduleTextView);
        timetextview = findViewById(R.id.SchduleActivityTimeTextView);
        confirmbtn = findViewById(R.id.ScheduleActivityConfirmBtn);

        year = LocalDateTime.now().getYear(); month = LocalDateTime.now().getMonthValue(); day = LocalDateTime.now().getDayOfMonth();

        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = year + "/" + monthOfYear + "/" + dayOfMonth;
                scheduletextview.setText(date);
                datePicker.setVisibility(View.GONE);
            }
        });
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                String time = hourOfDay + "/" + minute;
                timetextview.setText(time);
                timePicker.setVisibility(View.GONE);
            }
        });
        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("TEXTEDSCHEDULE",scheduletextview.getText() + "/" + timetextview.getText());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        */

    }

}