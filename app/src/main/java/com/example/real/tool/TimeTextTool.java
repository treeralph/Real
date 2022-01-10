package com.example.real.tool;

import android.util.Log;
import android.widget.Switch;

import com.google.type.DateTime;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class TimeTextTool {

    private final int UNDERMINUTE = 0;
    private final int UNDERHOUR = 1;
    private final int UNDERDAY = 2;
    private final int DATE = 3;
    private int flag ;

    String Time;
    LocalDateTime Time2Local;
    int period;

    public TimeTextTool(String time) {

        Time = time;
        Log.d("TIME_CHECK", Time);
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyyMMddHHmmss").appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();

        Time2Local = LocalDateTime.parse(Time, formatter);

        LocalDateTime now = LocalDateTime.now();

        int duration = (int) (Duration.between(Time2Local, now).getSeconds());
        int duration_abs = Math.abs(duration);
        period = duration_abs;
        if(duration_abs<60){
            flag = UNDERMINUTE;
        }else if(duration_abs<3600){
            flag = UNDERHOUR;
        }else if(duration_abs<86400){
            flag = UNDERDAY;
        }else{
            flag = DATE;
        }
    }

    public String Time2Text(){
        switch(flag){
            case UNDERMINUTE:
                return String.valueOf(period)+"초전";
            case UNDERHOUR:
                int temp_minute = (int) (period/60);
                return String.valueOf(temp_minute) + "분전";
            case UNDERDAY:
                int temp_hour = (int) (period/3600);
                return String.valueOf(temp_hour) + "시간전";
            case DATE:
                return Time2Local.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) ;
            default:
                return "";
        }
    }

}
