package com.example.real.data;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.GeoPoint;

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
    private String category;
    private String location;
    private String latLng;
    private String price;
    private String adm_cd;
    private GeoPoint geoPoint;

    // make minSDKversion 21 -> 26 to use LocalDateTime class.
    public Content(String title, String content, String uid, String category, String location, String price, String latLng, String adm_cd) {

        // If there exists error here, erase the under and use Remark.
        LocalDateTime dateNow = LocalDateTime.now();
        Time = dateNow.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        Title = title;
        Content = content;
        Uid = uid;
        this.category = category;
        this.location = location;
        this.price = price;
        this.latLng = latLng;
        this.adm_cd = adm_cd;

        String[] latLng_split = this.latLng.split(",");
        double lat = Double.parseDouble(latLng_split[0]);
        double lng = Double.parseDouble(latLng_split[1]);

        geoPoint = new GeoPoint(lat, lng);
    }

    public Content(String title, String content, String uid, String time, String category, String location, String price, String latLng, String adm_cd) {

        Title = title;
        Content = content;
        Uid = uid;
        Time = time;
        this.category = category;
        this.location = location;
        this.price = price;
        this.latLng = latLng;
        this.adm_cd = adm_cd;

        String[] latLng_split = this.latLng.split(",");
        double lat = Double.parseDouble(latLng_split[0]);
        double lng = Double.parseDouble(latLng_split[1]);

        geoPoint = new GeoPoint(lat, lng);
    }


    @Override
    public Map<String, Object> DataOut() {
        Map<String, Object> datum = new HashMap<>();
        datum.put("title", Title);
        datum.put("content", Content);
        datum.put("uid", Uid);
        datum.put("time", Time);
        datum.put("contentType", ContentType);
        datum.put("category", category);
        datum.put("location", location);
        datum.put("latLng", latLng);
        datum.put("price", price);
        datum.put("adm_cd", adm_cd);
        datum.put("geoPoint", geoPoint);
        return datum;
    }


    public String getCategory() {
        return category;
    }
    public String getTitle() { return Title; }
    public String getContent() { return Content; }
    public String getUid() { return Uid; }
    public String getTime() { return Time; }
    public String getContentType() { return ContentType; }
    public String getLocation() {
        return location;
    }
    public String getPrice() {
        return price;
    }
    public String getLatLng() { return latLng; }
    public String getAdm_cd() { return adm_cd; }
    public GeoPoint getGeoPoint() { return geoPoint; }
}
