package com.example.real.databasemanager;

import android.content.Context;
import android.util.Log;

import com.example.real.Callback;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class InternalDatabaseManager {

    private String TAG = "InternalDatabaseManager";

    Context context;

    public InternalDatabaseManager(Context context) {

        this.context = context;
    }

    public void read(String path, Callback callback_success, Callback callback_failure){

        Log.d(TAG, "read internal database path: " + path);

        FileInputStream in;

        try{
            in = context.openFileInput(path);
            int size = in.available();
            byte buffer[] = new byte[size];
            in.read(buffer);

            String result = new String(buffer, "UTF-8");
            in.close();

            callback_success.OnCallback(result);

        }catch(Exception e){
            Log.e(TAG, e.getMessage());

            callback_failure.OnCallback(null);
        }
    }

    public void write(String path, String data, Callback callback){

        Log.d(TAG, "write internal database path: " + path + " => " + data);

        FileOutputStream out;

        try {
            out = context.openFileOutput(path, Context.MODE_PRIVATE);
            out.write(data.getBytes());
            out.close();

            callback.OnCallback(null);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
