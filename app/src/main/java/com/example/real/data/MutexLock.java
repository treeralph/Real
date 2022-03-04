package com.example.real.data;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MutexLock {

    private String flag;
    private String time;

    public MutexLock() {

    }

    public MutexLock(String flag) {
        this.flag = flag;
        this.time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }


    public String getFlag() {
        return flag;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    @NonNull
    @Override
    public String toString() {
        return "MutexLock: " + flag ;
    }
}
