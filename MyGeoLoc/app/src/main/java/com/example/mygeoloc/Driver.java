package com.example.mygeoloc;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

public class Driver implements Parcelable {
    private String name;
    private String surname;
    private float totalDistanceDriverInMeter;
    private int totalTimeDriverInSec;
    private ArrayList<SavedMission> historic = new ArrayList<SavedMission>();
    private static final String TAG = "Driver";

    public Driver (String n, String s, int td, int tt, ArrayList<SavedMission> h){
        this.name=n;
        this.surname=s;
        this.totalDistanceDriverInMeter=td;
        this.totalTimeDriverInSec=tt;
        this.historic=h;
    }


    //set
    public void setName(String n){
        this.name = n;
    }
    public void setSurname(String s){
        this.surname=s;
    }
    public void setTotalDistanceDriverInMeter(float td){
        this.totalDistanceDriverInMeter=td;
    }
    public void setTotalTimeDriverInSec(int tt){
        this.totalTimeDriverInSec=tt;
    }
    public void addMission(SavedMission sv){
        this.historic.add(sv);
    }

    //get
    public String getName(){
        return this.name;
    }
    public String getSurName(){
        return this.surname;
    }
    public float getTotalDistanceDriverInMeter(){
        return this.totalDistanceDriverInMeter;
    }
    public int getTotalTimeDriverInSec(){
        return this.totalTimeDriverInSec;
    }
    public SavedMission getSavedMissionFromIndex(int index){
        if (index<0 || index>this.historic.size()){
            Log.e(TAG, "wrong index");
            return null;
        }
        return this.historic.get(index);
    }
    public void deleteSaveMissionFromIndex (int index){
        if (index<0 || index>this.historic.size()){
            Log.e(TAG, "wrong index");
        }
        this.historic.remove(index);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.surname);
        dest.writeFloat(this.totalDistanceDriverInMeter);
        dest.writeInt(this.totalTimeDriverInSec);
        dest.writeList(this.historic);
    }

    public void readFromParcel(Parcel source) {
        this.name = source.readString();
        this.surname = source.readString();
        this.totalDistanceDriverInMeter = source.readFloat();
        this.totalTimeDriverInSec = source.readInt();
        this.historic = new ArrayList<SavedMission>();
        source.readList(this.historic, SavedMission.class.getClassLoader());
    }

    protected Driver(Parcel in) {
        this.name = in.readString();
        this.surname = in.readString();
        this.totalDistanceDriverInMeter = in.readFloat();
        this.totalTimeDriverInSec = in.readInt();
        this.historic = new ArrayList<SavedMission>();
        in.readList(this.historic, SavedMission.class.getClassLoader());
    }

    public static final Parcelable.Creator<Driver> CREATOR = new Parcelable.Creator<Driver>() {
        @Override
        public Driver createFromParcel(Parcel source) {
            return new Driver(source);
        }

        @Override
        public Driver[] newArray(int size) {
            return new Driver[size];
        }
    };
}
