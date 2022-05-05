package com.example.real.databasemanager;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.example.real.Callback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

public class AssetDatabaseManager {

    private String TAG = "AssetDatabaseManager";

    public static final String KEY_Length_1 = "length_1";
    public static final String KEY_Length_2 = "length_2";
    public static final String KEY_Length_3 = "length_3";

    Context context;
    AssetManager assetManager;

    public AssetDatabaseManager(Context context) {

        this.context = context;
        this.assetManager = context.getResources().getAssets();
    }

    public void read(String adm_cd, String option, Callback callback){

        InputStream in;

        try{
            in = assetManager.open("areas/" + adm_cd + ".json");
            int size = in.available();
            byte buffer[] = new byte[size];

            in.read(buffer);
            String result = new String(buffer, "UTF-8");
            in.close();

            Log.d(TAG, "adm_cd => " + result);

            JSONObject jsonObject = new JSONObject(result);
            JSONObject adm_cd_pathJson = jsonObject.getJSONObject(adm_cd);
            JSONArray needs = (JSONArray) adm_cd_pathJson.get(option);

            callback.OnCallback(needs);

        }catch(Exception e){
            Log.e(TAG, e.getMessage());
        }
    }
}
