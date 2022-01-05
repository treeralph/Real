package com.example.real.data;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Content extends Data{

    public final String ContentType = "Content";
    private String Title;
    private String Content;
    private String Time;
    private String Uid;


    // make minSDKversion 21 -> 26 to use LocalDateTime class.
    public Content(String title, String content, String uid) {

        // If there exists error here, erase the under and use Remark.
        LocalDateTime dateNow = LocalDateTime.now();
        Time = dateNow.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        /*
        Date date_now = new Date(System.currentTimeMillis());
        SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMddHHmmssS");
        Time = date_format.format(date_now);
         */

        Title = title;
        Content = content;
        Uid = uid;

    }

    public Content(String title, String content, String uid, String time) {

        Title = title;
        Content = content;
        Uid = uid;
        Time = time;
    }

    @Override
    public Map<String, Object> DataOut() {
        Map<String, Object> datum = new HashMap<>();
        datum.put("title", Title);
        datum.put("content", Content);
        datum.put("uid", Uid);
        datum.put("time", Time);
        datum.put("contentType", ContentType);
        return datum;
    }

    public String getTitle() { return Title; }
    public String getContent() { return Content; }
    public String getUid() { return Uid; }
    public String getTime() { return Time; }
    public String getContentType() { return ContentType; }
}
