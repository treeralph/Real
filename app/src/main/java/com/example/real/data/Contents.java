package com.example.real.data;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class Contents extends Data{

    private String ContentId;
    private String ContentType;

    public Contents(String contentId, String contentType){
        ContentId = contentId;
        ContentType = contentType;
    }

    @Override
    public Map<String, Object> DataOut() {
        Map<String, Object> datum = new HashMap<>();
        datum.put("ContentId", ContentId);
        datum.put("ContentType", ContentType);
        return datum;
    }

    public String getContentId() {
        return ContentId;
    }
    public String getContentType() { return ContentType; }

    public void setContentId(String contentId) {
        ContentId = contentId;
    }
}
