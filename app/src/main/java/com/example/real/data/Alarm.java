package com.example.real.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Alarm extends Data{

    public static final String databasePath = "/ScheduledTask";

    String description;
    String deviceToken;
    String time; // time String format yyMMddHH
    String timeComplement;

    public Alarm() {
    }

    public Alarm(String description, String deviceToken){

        Date date_now = new Date(System.currentTimeMillis());
        SimpleDateFormat date_format = new SimpleDateFormat("yyMMddHH");

        this.description = description;
        this.deviceToken = deviceToken;
        this.time = date_format.format(date_now);
        this.timeComplement = getTimeComplement(this.time);
    }

    public Alarm(String description, String deviceToken, String time){

        this.description = description;
        this.deviceToken = deviceToken;
        this.time = time;
        this.timeComplement = getTimeComplement(this.time);
    }

    public String getTimeComplement(String time){
        int threshold = 100000000;
        int intTime = Integer.parseInt(time);
        int intComplement = threshold - intTime;
        return String.valueOf(intComplement);
    }

    @Override
    public Map<String, Object> DataOut() {

        Map<String, Object> data = new HashMap<>();
        data.put("description", description);
        data.put("deviceToken", deviceToken);
        data.put("time", time);
        return data;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "description='" + description + '\'' +
                ", deviceToken='" + deviceToken + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public String getTimeComplement() {
        return timeComplement;
    }

    public void setTimeComplement(String timeComplement) {
        this.timeComplement = timeComplement;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
