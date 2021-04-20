package com.example.mygeoloc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.TestLooperManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Calendar;

public class DifficultiesActivities extends AppCompatActivity {

    protected CheckBox cb_parking;
    protected CheckBox cb_parkingSlot;
    protected CheckBox cb_pente;
    protected CheckBox cb_traffic;
    protected CheckBox cb_highway;
    protected CheckBox cb_city;
    protected CheckBox cb_national;
    protected Driver driver;
    protected static Mission currentMission;
    protected Button bt_save;
    protected TextView tv_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulties);

        cb_parking      = (CheckBox) findViewById(R.id.cb_parking);
        cb_parkingSlot  = (CheckBox) findViewById(R.id.cb_parkingSlot);
        cb_pente        = (CheckBox) findViewById(R.id.cb_pente);
        cb_traffic      = (CheckBox) findViewById(R.id.cb_traffic);
        cb_highway      = (CheckBox) findViewById(R.id.cb_highway);
        cb_city         = (CheckBox) findViewById(R.id.cb_city);
        cb_national     = (CheckBox) findViewById(R.id.cb_national);
        bt_save         = (Button) findViewById(R.id.bt_save);
        tv_title        = (TextView) findViewById(R.id.tv_titleDiff);

        //TODO click on Save button, write the data to Firebase
        //TODO Close the panel
        //What to do on Save button
        bt_save.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day = currentMission.today.get(Calendar.DAY_OF_MONTH);
                int month = currentMission.today.get(Calendar.MONTH)+1; //calendar.month starts at 0
                int year = currentMission.today.get(Calendar.YEAR);
                int hours= currentMission.currentDurationInSec/3600;
                int minutes = (currentMission.currentDurationInSec-hours*3600)/60;
                String text = "You are on the way to save a "+hours+"h"+minutes+"mn"+" long new mission of "+currentMission.currentDistanceInMeter/1000+"km performed on "+day+"/"+month+"/"+year+".\n";
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                Log.d("SAVE",text);
                driver.setTotalDistanceDriverInMeter(driver.getTotalDistanceDriverInMeter()+currentMission.currentDistanceInMeter);
                driver.setTotalTimeDriverInSec(driver.getTotalTimeDriverInSec()+currentMission.currentDurationInSec);
                Intent intent = new Intent(DifficultiesActivities.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //retrieve the mission and driver
        Intent i = getIntent();
        currentMission = (Mission)i.getParcelableExtra("CurrentMission");
        driver = (Driver)i.getParcelableExtra("Driver");
        tv_title.setText("Select difficulties of this mission of "+driver.getSurName()+" "+driver.getName());
    }

    public void onCheckboxClicked(View v) {
        boolean checked = ((CheckBox) v).isChecked();
        switch (v.getId()) {
            case R.id.cb_parking:
                if (checked)
                    currentMission.difficulties.parking = true;
                else
                    currentMission.difficulties.parking = false;
                break;
            case R.id.cb_parkingSlot:
                if (checked)
                    currentMission.difficulties.parkingSlot = true;
                else
                    currentMission.difficulties.parkingSlot = false;
                break;
            case R.id.cb_pente:
                if (checked)
                    currentMission.difficulties.pente = true;
                else
                    currentMission.difficulties.pente = false;
                break;
            case R.id.cb_traffic:
                if (checked)
                    currentMission.difficulties.traffic = true;
                else
                    currentMission.difficulties.traffic = false;
                break;
            case R.id.cb_highway:
                if (checked)
                    currentMission.difficulties.highway = true;
                else
                    currentMission.difficulties.highway = false;
                break;
            case R.id.cb_city:
                if (checked)
                    currentMission.difficulties.city = true;
                else
                    currentMission.difficulties.city = false;
                break;
            case R.id.cb_national:
                if (checked)
                    currentMission.difficulties.national = true;
                else
                    currentMission.difficulties.national = false;
                break;
        }

    }

}