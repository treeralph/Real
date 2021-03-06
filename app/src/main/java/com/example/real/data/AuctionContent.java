package com.example.real.data;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.GeoPoint;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AuctionContent extends Content implements Cloneable{

    public final String AUCTION_STATE_ACQUIRED_CODE = "ACQUIRED";
    public final String AUCTION_STATE_INTERRUPTED_CODE = "INTERRUPTED";
    public final String AUCTION_STATE_EXPIRED_CODE = "EXPIRED";

    public final String ContentType = "AuctionContent";

    private String Title;
    private String Content;
    private String Time;
    private String Uid;

    private final double RateForPriceGap = 0.05;
    private final double ThresholdForPriceGap = 1000;

    private String Price;
    private String PriceGap;
    private ArrayList<String> AuctionUserList;
    private String AuctionDuration;
    private String AuctionStartTime;
    private String AuctionEndTime;
    private String AuctionState;
    private String category;
    private String location;
    private String latLng;
    private String adm_cd;
    private GeoPoint geoPoint;

    DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyyMMddHHmmss").appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();

    public AuctionContent(String title, String content, String uid, String price, String auctionDuration, String category, String location, String latLng, String adm_cd) {
        super(title, content, uid, category, location, price, latLng, adm_cd);

        Title = super.getTitle();
        Content = super.getContent();
        Time = super.getTime();
        Uid = super.getUid();
        Price = super.getPrice();
        this.category = super.getCategory();
        this.location = super.getLocation();
        this.latLng = super.getLatLng();
        this.adm_cd = super.getAdm_cd();
        PriceGapPolicy(price);

        AuctionUserList = new ArrayList<String>();
        AuctionUserList.add(uid);
        AuctionDuration = auctionDuration;
        AuctionStartTime = Time;

        LocalDateTime tempTime = LocalDateTime.parse(Time, formatter);
        LocalDateTime tempTime2 = tempTime.plusDays(Integer.parseInt(AuctionDuration));
        AuctionEndTime = tempTime2.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        AuctionState = AuctionContent.this.AUCTION_STATE_ACQUIRED_CODE;

        String[] latLng_split = this.latLng.split(",");
        double lat = Double.parseDouble(latLng_split[0]);
        double lng = Double.parseDouble(latLng_split[1]);

        geoPoint = new GeoPoint(lat, lng);
    }

    public AuctionContent(String title, String content, String uid, String price, String priceGap, String auctionDuration, String auctionState, ArrayList<String> auctionUserList, String time, String auctionEndTime, String category, String location, String latLng, String adm_cd) {
        super(title, content, uid, time, category, location, price, latLng, adm_cd);

        Title = super.getTitle();
        Content = super.getContent();
        Time = super.getTime();
        Uid = super.getUid();
        Price = super.getPrice();
        this.category = super.getCategory();
        this.location = super.getLocation();
        this.adm_cd = super.getAdm_cd();

        PriceGap = priceGap;
        AuctionUserList = auctionUserList;
        AuctionDuration = auctionDuration;
        AuctionStartTime = Time;
        AuctionEndTime = auctionEndTime;
        AuctionState = auctionState;

        String[] latLng_split = latLng.split(",");
        double lat = Double.parseDouble(latLng_split[0]);
        double lng = Double.parseDouble(latLng_split[1]);

        geoPoint = new GeoPoint(lat, lng);
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void PriceGapPolicy(String price){
        double temp = Double.parseDouble(price) * RateForPriceGap;
        if(temp > ThresholdForPriceGap){
            int tempPriceGap = (int)(((int)(temp / ThresholdForPriceGap) + 1) * ThresholdForPriceGap);
            PriceGap = String.valueOf(tempPriceGap);
        } else {
            PriceGap = String.valueOf((int) ThresholdForPriceGap);
        }
    }

    @Override
    public Map<String, Object> DataOut() {
        Map<String, Object> datum = new HashMap<>();
        datum.put("title", super.getTitle());
        datum.put("content", super.getContent());
        datum.put("uid", super.getUid());
        datum.put("time", super.getTime());
        datum.put("price", super.getPrice());
        datum.put("priceGap", PriceGap);
        datum.put("auctionUserList", AuctionUserList);
        datum.put("auctionDuration", AuctionDuration);
        datum.put("auctionStartTime", AuctionStartTime);
        datum.put("auctionEndTime", AuctionEndTime);
        datum.put("autionState", AuctionState);
        datum.put("contentType", ContentType);
        datum.put("category", category);
        datum.put("location", super.getLocation());
        datum.put("latLng", super.getLatLng());
        datum.put("adm_cd", super.getAdm_cd());
        datum.put("geoPoint", geoPoint);
        return datum;
    }

    public String getPrice() {
        return Price;
    }
    public String getPriceGap() {
        return PriceGap;
    }
    public ArrayList<String> getAuctionUserList() {
        return AuctionUserList;
    }
    public String getAuctionDruation() {
        return AuctionDuration;
    }
    public String getAuctionStartTime() {
        return AuctionStartTime;
    }
    public String getAuctionEndTime() {
        return AuctionEndTime;
    }
    public String getAuctionState() { return AuctionState; }
    @Override
    public String getContentType() { return ContentType; }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAuctionUserList(ArrayList<String> auctionUserList) { AuctionUserList = auctionUserList; }
    public void setPrice(String price) { Price = price; }
}
