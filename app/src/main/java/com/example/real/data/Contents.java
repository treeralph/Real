package com.example.real.data;

import android.content.Context;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Contents extends Data{

    private String ContentId;
    private String ContentType;
    private String ContentTitle;
    private String Category;
    private ArrayList<String> wordCase;
    private String latLng;
    private String time;
    private String adm_cd;
    private GeoPoint geoPoint;


    public Contents(String contentId, String contentType, String contentTitle, String category, ArrayList<String> wordCase, String latLng, String time, String adm_cd){
        ContentId = contentId;
        ContentType = contentType;
        ContentTitle = contentTitle;
        Category = category;
        this.wordCase = wordCase;
        this.latLng = latLng;
        this.time = time;
        this.adm_cd = adm_cd;

        String[] latLng_split = this.latLng.split(",");
        double lat = Double.parseDouble(latLng_split[0]);
        double lng = Double.parseDouble(latLng_split[1]);

        geoPoint = new GeoPoint(lat, lng);
    }

    @Override
    public Map<String, Object> DataOut() {
        Map<String, Object> datum = new HashMap<>();
        datum.put("ContentId", ContentId);
        datum.put("ContentType", ContentType);
        datum.put("ContentTitle", ContentTitle);
        datum.put("Category", Category);
        datum.put("WordCase", wordCase);
        datum.put("latLng", latLng);
        datum.put("time", time);
        datum.put("adm_cd", adm_cd);
        datum.put("geoPoint", geoPoint);
        return datum;
    }

    public ArrayList<String> getWordCase() {
        return wordCase;
    }

    public void setWordCase(ArrayList<String> wordCase) {
        this.wordCase = wordCase;
    }

    public String getContentId() {
        return ContentId;
    }

    public void setContentId(String contentId) {
        ContentId = contentId;
    }

    public String getContentType() {
        return ContentType;
    }

    public void setContentType(String contentType) {
        ContentType = contentType;
    }

    public String getContentTitle() {
        return ContentTitle;
    }

    public void setContentTitle(String contentTitle) {
        ContentTitle = contentTitle;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getLatLng() {
        return latLng;
    }

    public String getAdm_cd() { return adm_cd; }

    public String getTime() {
        return time;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }
}

