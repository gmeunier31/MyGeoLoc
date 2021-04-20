package com.example.mygeoloc;

import java.util.Date;

public class SavedMission {
    private Date date; //dd/mm/yy
    private int distance; //in km
    private int duration; //in s
    private String nameMission;


    public SavedMission(Date d, int di, int du, String dep, String arr){
        this.date = d;
        this.distance=di;
        this.duration=du;
    }

    //set
    public void setDate(Date d){
        this.date=d;
    }
    public void setDistance (int di){
        this.distance=di;
    }
    public void setDuration (int du){
        this.duration=du;
    }

    //get
    public Date getDate(){
        return this.date;
    }
    public int getDistance(){
        return this.distance;
    }
    public int getDuration(){
        return this.duration;
    }

}
