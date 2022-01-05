package com.example.real.tool;

import android.os.AsyncTask;
import android.widget.TextView;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class


AsyncTaskForRealTimeTextView extends AsyncTask<Void, Void, Void> {

    TextView textView;
    LocalDateTime endTime;

    public AsyncTaskForRealTimeTextView(TextView textView, LocalDateTime endTime) {
        this.textView = textView;
        this.endTime = endTime;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
    } //1296000

    @Override
    protected void onProgressUpdate(Void... values) {
        LocalDateTime currentTime = LocalDateTime.now();
        Duration duration = Duration.between(endTime, currentTime);

        long durationSeconds = Math.abs(duration.getSeconds());
        int hour = 0;
        int minute = 0;
        int second = 0;
        if(durationSeconds>3600){
            hour = (int)(durationSeconds / 3600);
            durationSeconds -= 3600 * hour;
        } else if(durationSeconds>60){
            minute = (int)(durationSeconds / 60);
            durationSeconds -= 60 * minute;
        } else{
            second = (int)durationSeconds;
        }
        textView.setText(hour + ":" + minute + ":" + second);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(Void unused) {
        super.onCancelled(unused);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while(true) {
            publishProgress();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
