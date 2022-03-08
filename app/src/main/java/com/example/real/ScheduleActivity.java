package com.example.real;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;
import com.r0adkll.slidr.model.SlidrPosition;

import java.time.LocalDateTime;
import java.util.Calendar;

public class ScheduleActivity extends AppCompatActivity {

    private SlidrInterface slidr;
    DatePicker datePicker;
    TimePicker timePicker;
    TextView scheduletextview;
    TextView timetextview;
    Button confirmbtn;
    int year ; int month ; int day;

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


    }
}