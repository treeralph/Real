package com.example.real.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserProfile extends Data{

    private String RegisterTime;
    private String Rating="0";
    private String NickName;
    private String BO;
    private String SO;
    private String Description;
    private String UserLog;
    private String DeviceToken;
    private ArrayList<String> ChattingRoomID;

    public UserProfile(String rating, String nickName) {
        Rating = rating;
        NickName = nickName;
        LocalDateTime dateNow = LocalDateTime.now();
        RegisterTime = dateNow.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        BO = "";
        SO = "";
        Description = "";
        UserLog = "";
        DeviceToken = "";
        ChattingRoomID = new ArrayList<>();
    }


    public UserProfile(String rating, String nickName, String registerTime, String bo, String so, String description, String userLog, String deviceToken, ArrayList<String> chattingRoomID){
        RegisterTime = registerTime;
        Rating = rating;
        NickName = nickName;
        BO = bo;
        SO = so;
        Description = description;
        UserLog = userLog;
        DeviceToken = deviceToken;
        ChattingRoomID = chattingRoomID;
    }

    @Override
    public Map<String, Object> DataOut() {
        Map<String, Object> datum = new HashMap<>();
        datum.put("rating", Rating);
        datum.put("nickname", NickName);
        datum.put("registertime", RegisterTime);
        datum.put("BO", BO);
        datum.put("SO", SO);
        datum.put("Description", Description);
        datum.put("UserLog", UserLog);
        datum.put("DeviceToken", DeviceToken);
        datum.put("ChattingRoomID", ChattingRoomID);
        return datum;
    }

    public String getRegisterTime() {
        return RegisterTime;
    }

    public void setRegisterTime(String registerTime) {
        RegisterTime = registerTime;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getBO() {
        return BO;
    }

    public void setBO(String BO) {
        this.BO = BO;
    }

    public String getSO() {
        return SO;
    }

    public void setSO(String SO) {
        this.SO = SO;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUserLog() {
        return UserLog;
    }

    public void setUserLog(String userLog) {
        UserLog = userLog;
    }

    public String getDeviceToken() {
        return DeviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        DeviceToken = deviceToken;
    }

    public ArrayList<String> getChattingRoomID() {
        return ChattingRoomID;
    }

    public void setChattingRoomID(ArrayList<String> chattingRoomID) {
        ChattingRoomID = chattingRoomID;
    }
}
