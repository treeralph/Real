package com.example.real.data;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Message extends Data{

    private String fromUid;
    private String toUid;
    private String message;
    private String time;
    private String fromToken;
    private String toToken;

    public Message(){

    }

    public Message(String fromUid, String toUid, String message, String fromToken, String toToken) {
        Date date_now = new Date(System.currentTimeMillis());
        SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMddHHmmssSSS");

        this.fromUid = fromUid;
        this.toUid = toUid;
        this.message = message;
        this.time = date_format.format(date_now);
        this.fromToken = fromToken;
        this.toToken = toToken;
    }

    public Message(String fromUid, String toUid, String message, String time, String fromToken, String toToken) {
        this.fromUid = fromUid;
        this.toUid = toUid;
        this.message = message;
        this.time = time;
        this.fromToken = fromToken;
        this.toToken = toToken;
    }

    @NonNull
    @Override
    public String toString() {
        return fromUid + "/" + toUid + "/" + message + "/" + time + "/" + fromToken + "/" + toToken;
    }

    @Override
    public Map<String, Object> DataOut() {
        Map<String, Object> datum = new HashMap<>();
        return datum;
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
}
