package com.example.real.data;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class Contents extends Data{

    private String ContentId;
    private String ContentType;
    private String ContentTitle;
    private String Category;

    public Contents(String contentId, String contentType, String contentTitle, String category){
        ContentId = contentId;
        ContentType = contentType;
        ContentTitle = contentTitle;
        Category = category;
    }

    @Override
    public Map<String, Object> DataOut() {
        Map<String, Object> datum = new HashMap<>();
        datum.put("ContentId", ContentId);
        datum.put("ContentType", ContentType);
        datum.put("ContentTitle", ContentTitle);
        datum.put("Category", Category);
        return datum;
    }

    public String getContentId() {
        return ContentId;
    }

    public void setContentId(String contentId) {
        ContentId = contentId;
    }

    public String getContentType() {
        return ContentType;
    }

    public void setContentType(String contentType) {
        ContentType = contentType;
    }

    public String getContentTitle() {
        return ContentTitle;
    }

    public void setContentTitle(String contentTitle) {
        ContentTitle = contentTitle;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }
}
