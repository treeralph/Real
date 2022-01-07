package com.example.real.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Comment extends Data{

    public final String ContentType = "Comment";
    private String From;
    private String To;
    private String Time;
    private String Mention;
    private String Recomment_token;

    public Comment(String from, String to, String mention) {

        LocalDateTime dateNow = LocalDateTime.now();
        Time = dateNow.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        Recomment_token ="0";
        From = from;
        To = to;
        Mention = mention;
    }

    // Recomment token as header pointer
    public Comment(String from, String to, String mention, String Recomment_token) {

        LocalDateTime dateNow = LocalDateTime.now();
        Time = dateNow.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        this.Recomment_token = Recomment_token;
        From = from;
        To = to;
        Mention = mention;
    }

    public Comment(String from, String To, String Mention, String Time, String Recomment_token) {

        this.From = from;
        this.To = To;
        this.Mention = Mention;
        this.Time = Time;
        this.Recomment_token = Recomment_token;
    }



    @Override
    public Map<String, Object> DataOut() {

        Map<String, Object> datum = new HashMap<>();
        datum.put("From",From);
        datum.put("To",To);
        datum.put("Mention",Mention);
        datum.put("Time",Time);
        datum.put("Recomment_token",Recomment_token);
        return datum;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "ContentType='" + ContentType + '\'' +
                ", From='" + From + '\'' +
                ", To='" + To + '\'' +
                ", Time='" + Time + '\'' +
                ", Mention='" + Mention + '\'' +
                ", Recomment_token='" + Recomment_token + '\'' +
                '}';
    }

    public String getFrom() { return From; }
    public String getTo() { return To; }
    public String getTime() { return Time; }
    public String getMention() { return Mention; }

    public String getRecomment_token() { return Recomment_token; }
}
