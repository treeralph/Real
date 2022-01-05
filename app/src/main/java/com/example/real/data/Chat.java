package com.example.real.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Chat extends Data{
    //field
    private String MakerUid;
    private String Time;
    private ArrayList<String> UserUids;

    //constructor
    public Chat(String makeruid){
        Date date_now = new Date(System.currentTimeMillis());
        SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMddHHmmssS");

        MakerUid = makeruid;
        Time = date_format.format(date_now);
        addUser(MakerUid);
    }


    @Override
    public Map<String, Object> DataOut() {

        Map<String, Object> datum = new HashMap<>();
        datum.put("MakerUid", MakerUid);
        datum.put("Time", Time);
        datum.put("UserUids", UserUids);

        return datum;
    }

    public void addUser(String uid){
        UserUids.add(uid);
    }

    public void deleteUser(String uid){
        UserUids.remove(uid);
    }


    public String getMakerUid() {
        return MakerUid;
    }

    public void setMakerUid(String makerUid) {
        MakerUid = makerUid;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public ArrayList<String> getUserUids() {
        return UserUids;
    }

    public void setUserUids(ArrayList<String> userUids) {
        UserUids = userUids;
    }
}
