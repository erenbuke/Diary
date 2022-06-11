package com.example.diary;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Entry {

    private String name;
    private String password = null;
    private String date;
    private String mood;
    private String maintext;
    private String imagePath = null;

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    public Entry(String name, String date, String mood, String maintext, String password){

        this.name = name;
        this.date = date;
        this.mood = mood;
        this.maintext = maintext;

        if(password == null || password.equals("")){
            this.password = " ";
        }
        else{
            this.password = password;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getMaintext() {
        return maintext;
    }

    public void setMaintext(String maintext) {
        this.maintext = maintext;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public static String changeString(List<Entry> Entries){

        String output = "";

        for(Entry entry: Entries){
            output += entry.name + "#"
                    + entry.password + "#"
                    + entry.date + "#"
                    + entry.mood + "#"
                    + entry.maintext + "#"
                    + entry.imagePath + "\n";
        }

        return output;
    }
}
