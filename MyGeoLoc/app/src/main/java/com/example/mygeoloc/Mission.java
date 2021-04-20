package com.example.mygeoloc;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.CheckBox;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class Mission implements Parcelable {

    public static class Difficulties implements Parcelable {
        Boolean parking;
        Boolean parkingSlot;
        Boolean pente;
        Boolean traffic;
        Boolean highway;
        Boolean city;
        Boolean national;
        public Difficulties() {
            this.parking    = false;
            this.parkingSlot= false;
            this.pente      = false;
            this.traffic    = false;
            this.highway    = false;
            this.city       = false;
            this.national   = false;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(this.parking);
            dest.writeValue(this.parkingSlot);
            dest.writeValue(this.pente);
            dest.writeValue(this.traffic);
            dest.writeValue(this.highway);
            dest.writeValue(this.city);
            dest.writeValue(this.national);
        }

        public void readFromParcel(Parcel source) {
            this.parking = (Boolean) source.readValue(Boolean.class.getClassLoader());
            this.parkingSlot = (Boolean) source.readValue(Boolean.class.getClassLoader());
            this.pente = (Boolean) source.readValue(Boolean.class.getClassLoader());
            this.traffic = (Boolean) source.readValue(Boolean.class.getClassLoader());
            this.highway = (Boolean) source.readValue(Boolean.class.getClassLoader());
            this.city = (Boolean) source.readValue(Boolean.class.getClassLoader());
            this.national = (Boolean) source.readValue(Boolean.class.getClassLoader());
        }

        protected Difficulties(Parcel in) {
            this.parking = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.parkingSlot = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.pente = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.traffic = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.highway = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.city = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.national = (Boolean) in.readValue(Boolean.class.getClassLoader());
        }

        public static final Creator<Difficulties> CREATOR = new Creator<Difficulties>() {
            @Override
            public Difficulties createFromParcel(Parcel source) {
                return new Difficulties(source);
            }

            @Override
            public Difficulties[] newArray(int size) {
                return new Difficulties[size];
            }
        };
    }
    double lastLatitude;
    double lastLongitude;
    float currentDistanceInMeter; //distance of the current mission
    int currentDurationInSec; // duration of the current mission
    int gpsIsLocked;
    Calendar today;
    Difficulties difficulties;

    //constructor
    public Mission(double lastLat, double lastLong, int disTotal, int recordDist, int elapsedTim,
                   int gpsIsLocked) {
        this.lastLatitude = lastLat;
        this.lastLongitude = lastLong;
        this.currentDistanceInMeter = recordDist;
        this.currentDurationInSec=elapsedTim;
        this.gpsIsLocked=gpsIsLocked;
        this.difficulties = new Difficulties();
        this.today = Calendar.getInstance();
    }

    protected Mission (Parcel in){
        lastLatitude = in.readDouble();
        lastLongitude = in.readDouble();
        currentDistanceInMeter = in.readFloat();
        currentDurationInSec=in.readInt();
        gpsIsLocked=in.readInt();
        difficulties = in.readParcelable(Difficulties.class.getClassLoader());
        long milliseconds = in.readLong();
        String timezone_id = in.readString();
        today = new GregorianCalendar(TimeZone.getTimeZone(timezone_id));
        today.setTimeInMillis(milliseconds);
    }

    public static final Creator<Mission> CREATOR = new Creator() {
        @Override
        public Mission createFromParcel(Parcel in) {
            return new Mission(in);
        }

        @Override
        public Mission[] newArray(int size) {
            return new Mission[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lastLatitude);
        dest.writeDouble(lastLongitude);
        dest.writeFloat(currentDistanceInMeter);
        dest.writeInt(currentDurationInSec);
        dest.writeInt(gpsIsLocked);
        dest.writeParcelable(difficulties,flags);
        dest.writeLong(today.getTimeInMillis());
        dest.writeString(today.getTimeZone().getID());
    }
}
