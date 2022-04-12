package com.example.real.data;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Message extends Data{

    public static final String normalMessageFlag = "0";
    public static final String imageMessageFlag = "1";
    public static final String appointmentMessageFlag = "2";
    public static final String chickenWinnerMessageFlag = "3";

    private String flag;
    private String fromUid;
    private String toUid;
    private String message;
    private String time;
    private String fromToken;
    private String toToken;
    private String imageUri;

    private String ReservedTime;
    private String location;
    private Boolean isconfirmed;

    private String chickenBidUserUid;
    private String bidPrice;

    public Message(){

    }

    /**
     * reservation message의 경우 message에 시간,장소 등의 정보를 담아서 전달한다. {reservedTime/Location/isConfirmed}
     * chicken message의 경우 message에 낙찰자의 uid와 낙찰 가격등의 정보를 담아서 전달한다. {uid/price}
     * */
    public Message(String flag, String fromUid, String toUid, String message, String fromToken, String toToken, String imageUri) {
        Date date_now = new Date(System.currentTimeMillis());
        SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMddHHmmssSSS");

        this.flag = flag;
        this.fromUid = fromUid;
        this.toUid = toUid;
        this.message = message;
        this.time = date_format.format(date_now);
        this.fromToken = fromToken;
        this.toToken = toToken;
        this.imageUri = imageUri;
    }

    public Message(String flag, String fromUid, String toUid, String message, String time, String fromToken, String toToken, String imageUri) {

        this.flag = flag;
        this.fromUid = fromUid;
        this.toUid = toUid;
        this.message = message;
        this.time = time;
        this.fromToken = fromToken;
        this.toToken = toToken;
        this.imageUri = imageUri;
    }

    @NonNull
    @Override
    public String toString() {
        return fromUid + "/" + toUid + "/" + message + "/" + time + "/" + fromToken + "/" + toToken + "/" + imageUri;
    }

    @Override
    public Map<String, Object> DataOut() {
        Map<String, Object> datum = new HashMap<>();
        return datum;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getFromUid() {
        return fromUid;
    }

    public void setFromUid(String fromUid) {
        this.fromUid = fromUid;
    }

    public String getToUid() {
        return toUid;
    }

    public void setToUid(String toUid) {
        this.toUid = toUid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFromToken() {
        return fromToken;
    }

    public void setFromToken(String fromToken) {
        this.fromToken = fromToken;
    }

    public String getToToken() {
        return toToken;
    }

    public void setToToken(String toToken) {
        this.toToken = toToken;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

}
