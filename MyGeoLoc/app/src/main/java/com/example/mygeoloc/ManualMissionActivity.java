package com.example.mygeoloc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.nfc.tech.NfcB;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Locale;

public class ManualMissionActivity extends AppCompatActivity implements View.OnClickListener {
    //Driver and currentMission are objects that come from Main activity and that will be used here.
    protected Driver driver;
    protected Mission currentMission;
    //title of the screen
    protected TextView tv_user;
    //Distance
    protected TextView tv_distanceManual;
    protected Button bt_0Distance, bt_1Distance, bt_2Distance, bt_3Distance, bt_4Distance, bt_5Distance;
    protected Button bt_6Distance, bt_7Distance, bt_8Distance, bt_9Distance, bt_SeparatorDistance;
    protected Button bt_ClearDistance;
    private float manualDistanceInKm;
    Boolean distanceIsDecimal;
    private int nbDecimalDigits;
    //Duration
    Boolean hoursIsSelected;
    Integer nbOfDigitsInHour, nbOfDigitsInMinutes;
    String durationHours ="00",durationMinutes ="00";
    Integer hours =0, minutes = 0;
    protected TextView tv_durationHrManual, tv_durationMnManual;
    protected Button bt_0Duration, bt_1Duration, bt_2Duration, bt_3Duration, bt_4Duration, bt_5Duration;
    protected Button bt_6Duration, bt_7Duration, bt_8Duration, bt_9Duration, bt_HoursMinutes;
    protected Button bt_ClearDuration;
    //save
    protected Button bt_save;
    //date
    TextView et_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_mission);
        // title of the activity
        tv_user = (TextView) findViewById(R.id.tv_titleManual);
        //Distance
        manualDistanceInKm = (float) 0;
        distanceIsDecimal = false;
        nbDecimalDigits = 0;
        tv_distanceManual = (TextView) findViewById(R.id.tv_distanceManual);
        bt_0Distance = (Button) findViewById(R.id.bt_zeroManualDistance);
        bt_1Distance = (Button) findViewById(R.id.bt_oneManualDistance);
        bt_2Distance = (Button) findViewById(R.id.bt_twoManualDistance);
        bt_3Distance = (Button) findViewById(R.id.bt_threeManualDistance);
        bt_4Distance = (Button) findViewById(R.id.bt_fourManualDistance);
        bt_5Distance = (Button) findViewById(R.id.bt_fiveManualDistance);
        bt_6Distance = (Button) findViewById(R.id.bt_sixManualDistance);
        bt_7Distance = (Button) findViewById(R.id.bt_sevenManualDistance);
        bt_8Distance = (Button) findViewById(R.id.bt_heightManualDistance);
        bt_9Distance = (Button) findViewById(R.id.bt_nineManualDistance);
        bt_SeparatorDistance = (Button) findViewById(R.id.bt_separatorManualDistance);
        bt_ClearDistance = (Button) findViewById(R.id.bt_clearManualDistance);
        bt_0Distance.setOnClickListener(this);
        bt_1Distance.setOnClickListener(this);
        bt_2Distance.setOnClickListener(this);
        bt_3Distance.setOnClickListener(this);
        bt_4Distance.setOnClickListener(this);
        bt_5Distance.setOnClickListener(this);
        bt_6Distance.setOnClickListener(this);
        bt_7Distance.setOnClickListener(this);
        bt_8Distance.setOnClickListener(this);
        bt_9Distance.setOnClickListener(this);
        bt_SeparatorDistance.setOnClickListener(this);
        bt_ClearDistance.setOnClickListener(this);
        //Duration
        hoursIsSelected = true;
        nbOfDigitsInMinutes = nbOfDigitsInHour = 0;
        tv_durationHrManual = (TextView) findViewById(R.id.tv_durationHrManual);
        tv_durationMnManual = (TextView) findViewById(R.id.tv_durationMnManual);
        bt_0Duration = (Button) findViewById(R.id.bt_zeroManualTime);
        bt_1Duration = (Button) findViewById(R.id.bt_oneManualTime);
        bt_2Duration = (Button) findViewById(R.id.bt_twoManualTime);
        bt_3Duration = (Button) findViewById(R.id.bt_threeManualTime);
        bt_4Duration = (Button) findViewById(R.id.bt_fourManualTime);
        bt_5Duration = (Button) findViewById(R.id.bt_fiveManualTime);
        bt_6Duration = (Button) findViewById(R.id.bt_sixManualTime);
        bt_7Duration = (Button) findViewById(R.id.bt_sevenManualTime);
        bt_8Duration = (Button) findViewById(R.id.bt_heightManualTime);
        bt_9Duration = (Button) findViewById(R.id.bt_nineManualTime);
        bt_HoursMinutes = (Button) findViewById(R.id.bt_HourMinutesManualTime);
        bt_ClearDuration = (Button) findViewById(R.id.bt_clearManualTime);
        bt_0Duration.setOnClickListener(mTimeListener);
        bt_1Duration.setOnClickListener(mTimeListener);
        bt_2Duration.setOnClickListener(mTimeListener);
        bt_3Duration.setOnClickListener(mTimeListener);
        bt_4Duration.setOnClickListener(mTimeListener);
        bt_5Duration.setOnClickListener(mTimeListener);
        bt_6Duration.setOnClickListener(mTimeListener);
        bt_7Duration.setOnClickListener(mTimeListener);
        bt_8Duration.setOnClickListener(mTimeListener);
        bt_9Duration.setOnClickListener(mTimeListener);
        bt_HoursMinutes.setOnClickListener(mTimeListener);
        bt_ClearDuration.setOnClickListener(mTimeListener);
        //underline the hours
        Spannable content=new SpannableString(tv_durationHrManual.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tv_durationHrManual.setText(content);
        //save
        bt_save = (Button) findViewById(R.id.bt_save);
        bt_save.setOnClickListener(this);
        //date
        et_date = (TextView) findViewById(R.id.ed_date);

        DatePickerDialog.OnDateSetListener date= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                currentMission.today.set(Calendar.YEAR,year);
                currentMission.today.set(Calendar.MONTH, month);
                currentMission.today.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ManualMissionActivity.this, date, currentMission.today
                        .get(Calendar.YEAR), currentMission.today.get(Calendar.MONTH),
                        currentMission.today.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //retrieve the mission
        Intent i = getIntent();
        currentMission = (Mission) i.getParcelableExtra("CurrentMission");
        driver = (Driver) i.getParcelableExtra("Driver");
        tv_user.setText("Enter new mission for "+driver.getSurName()+" "+driver.getName());

    }


    //date
    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);

        et_date.setText(sdf.format(currentMission.today.getTime()));
    }

    @Override
    public void onClick(View v) {
        int keyVal = 0;
        if (nbDecimalDigits < 3) {
            switch (v.getId()) {
                case R.id.bt_zeroManualDistance:
                    keyVal = 0;
                    break;
                case R.id.bt_oneManualDistance:
                    keyVal = 1;
                    if (distanceIsDecimal)
                        nbDecimalDigits++;
                    break;
                case R.id.bt_twoManualDistance:
                    keyVal = 2;
                    if (distanceIsDecimal)
                        nbDecimalDigits++;
                    break;
                case R.id.bt_threeManualDistance:
                    keyVal = 3;
                    if (distanceIsDecimal)
                        nbDecimalDigits++;
                    break;
                case R.id.bt_fourManualDistance:
                    keyVal = 4;
                    if (distanceIsDecimal)
                        nbDecimalDigits++;
                    break;
                case R.id.bt_fiveManualDistance:
                    keyVal = 5;
                    if (distanceIsDecimal)
                        nbDecimalDigits++;
                    break;
                case R.id.bt_sixManualDistance:
                    keyVal = 6;
                    if (distanceIsDecimal)
                        nbDecimalDigits++;
                    break;
                case R.id.bt_sevenManualDistance:
                    keyVal = 7;
                    if (distanceIsDecimal)
                        nbDecimalDigits++;
                    break;
                case R.id.bt_heightManualDistance:
                    keyVal = 8;
                    if (distanceIsDecimal)
                        nbDecimalDigits++;
                    break;
                case R.id.bt_nineManualDistance:
                    keyVal = 9;
                    if (distanceIsDecimal)
                        nbDecimalDigits++;
                    break;
                case R.id.bt_separatorManualDistance:
                    keyVal = 10;
                    distanceIsDecimal = true;
                    break;
            }
        }
        if (v.getId() == R.id.bt_clearManualDistance) {
            manualDistanceInKm = (float) 0.0;
            distanceIsDecimal = false;
            nbDecimalDigits = 0;
        }
        //save mgt (ugly I know!)
        if (v.getId()==R.id.bt_save) {
            currentMission.currentDistanceInMeter=manualDistanceInKm*1000;
            currentMission.currentDurationInSec=Integer.parseInt(durationHours)*3600+Integer.parseInt(durationMinutes)*60;
            Intent intent = new Intent(this, DifficultiesActivities.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("CurrentMission",currentMission);
            intent.putExtra("Driver",driver);
            startActivity(intent);
        }
        else {
            if (manualDistanceInKm == 0 && keyVal != 10 && !distanceIsDecimal)
                manualDistanceInKm = keyVal;//to convert in meter
            else if (manualDistanceInKm != 0 && !distanceIsDecimal && keyVal != 10)
                manualDistanceInKm = manualDistanceInKm * 10 + keyVal;
            else if (distanceIsDecimal && keyVal != 10 && nbDecimalDigits <= 3)
                manualDistanceInKm = (float) (manualDistanceInKm + keyVal * Math.pow(10, -nbDecimalDigits));

            BigDecimal bd = new BigDecimal(manualDistanceInKm);
            bd = bd.setScale(nbDecimalDigits, BigDecimal.ROUND_HALF_UP);
            tv_distanceManual.setText("" + bd);
        }
    }



    // Create an anonymous implementation of OnClickListener
    private View.OnClickListener mTimeListener = new View.OnClickListener() {
        public void onClick(View v) {
            String keyString = "";
            //Duration
            if ((nbOfDigitsInHour < 3 && hoursIsSelected) ||
                    (nbOfDigitsInMinutes < 3 && !hoursIsSelected)) {
                switch (v.getId()) {
                    case R.id.bt_zeroManualTime:
                        keyString = "0";
                        break;
                    case R.id.bt_oneManualTime:
                        keyString = "1";
                        break;
                    case R.id.bt_twoManualTime:
                        keyString = "2";
                        break;
                    case R.id.bt_threeManualTime:
                        keyString = "3";
                        break;
                    case R.id.bt_fourManualTime:
                        keyString = "4";
                        break;
                    case R.id.bt_fiveManualTime:
                        keyString = "5";
                        break;
                    case R.id.bt_sixManualTime:
                        keyString = "6";
                        break;
                    case R.id.bt_sevenManualTime:
                        keyString = "7";
                        break;
                    case R.id.bt_heightManualTime:
                        keyString = "8";
                        break;
                    case R.id.bt_nineManualTime:
                        keyString = "9";
                        break;
                    case R.id.bt_HourMinutesManualTime:
                        keyString = "10";
                        if (hoursIsSelected) {
                            //underline minutes
                            tv_durationMnManual.setPaintFlags(tv_durationMnManual.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                            //change button text, now we will enter minutes
                            bt_HoursMinutes.setText("Minutes");
                            //remove underline under hours
                            tv_durationHrManual.setPaintFlags(tv_durationHrManual.getPaintFlags() & ~Paint.UNDERLINE_TEXT_FLAG);
                        }
                        else{
                            // underline Hours
                            tv_durationHrManual.setPaintFlags(tv_durationHrManual.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                            //change button text, now we will enter hours
                            bt_HoursMinutes.setText("Hours");
                            //remove underline under minutes
                            tv_durationMnManual.setPaintFlags(tv_durationMnManual.getPaintFlags() & ~Paint.UNDERLINE_TEXT_FLAG);
                        }

                        hoursIsSelected = !hoursIsSelected;
                        break;
                }
            }
            if (hoursIsSelected) {
                if (keyString != "10") {
                    if (nbOfDigitsInHour == 0)
                        durationHours = "0" + keyString;
                    else {
                        durationHours = durationHours.substring(durationHours.length()-1) + keyString;
                    }
                    nbOfDigitsInHour++;
                    //underline Hours
                    tv_durationHrManual.setPaintFlags(tv_durationHrManual.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    tv_durationHrManual.setText(durationHours);
                    //remove underline under minutes
                    tv_durationMnManual.setPaintFlags(tv_durationMnManual.getPaintFlags() & ~Paint.UNDERLINE_TEXT_FLAG);

                    try {
                        hours = Integer.parseInt(durationHours);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Could not parse " + nfe);
                    }
                }
            } else {
                if (keyString != "10") {
                    if (nbOfDigitsInMinutes == 0) {
                        durationMinutes = "0" + keyString;
                    } else if (minutes <= 5) {
                        durationMinutes = durationMinutes.substring(durationHours.length()-1) + keyString;
                    }
                    nbOfDigitsInMinutes++;
                    // underline Minutes
                    tv_durationMnManual.setPaintFlags(tv_durationMnManual.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    tv_durationMnManual.setText(durationMinutes);
                    //remove underline under minutes
                    tv_durationHrManual.setPaintFlags(tv_durationHrManual.getPaintFlags() & ~Paint.UNDERLINE_TEXT_FLAG);

                    try {
                        minutes = Integer.parseInt(durationMinutes);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Could not parse " + nfe);
                    }
                }
            }
            if (v.getId() == R.id.bt_clearManualTime) {
                durationMinutes = durationHours = "00";
                tv_durationHrManual.setText(durationHours);
                tv_durationMnManual.setText(durationMinutes);
                nbOfDigitsInMinutes=nbOfDigitsInHour=0;
            }
            //TODO when hours is selected , video inverse of hours section in the textView

        }
    };
}