package com.example.mygeoloc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.location.Location.distanceBetween;


// https://javapapers.com/android/get-current-location-in-android/
public class MainActivity extends Activity implements LocationListener, View.OnClickListener {

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Button btStart;
    protected Button btStop;
    protected Button btManual;
    protected ImageView mImageCar;
    protected ImageView mImageGps;
    protected ImageView mImageManual;
    TextView tvDistance,tvTime,tvDriver,tvDriverTotalDistance,tvDriverTotalTime;
    Switch swManualGpsSwitch;
    String latitude, longitude;
    String provider;
    Mission currentMission = new Mission(0,0,0,0,0,0);
    ArrayList<SavedMission> h = new ArrayList<SavedMission>();
    Driver driver = new Driver("Meunier","Toinou",10000,1000, h);
    private Timer timer;
    private Integer almostLocked=0;
    private String uiState="IDLE";
    private int gpsLockedForDebug=0;
    private static DecimalFormat df = new DecimalFormat("0.00");
    private static final String TAG = "MyActivity";
    private PowerManager.WakeLock wL;
    private DatabaseReference mDatabase;
    private ChildEventListener mChildDBEventListener;


    //TODO retrieve driver info from firebase

    //1s tick for the duration in GPS mode
    class TickTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    currentMission.currentDurationInSec++;
                    Integer hours = currentMission.currentDurationInSec / 3600;
                    Integer minutes = (currentMission.currentDurationInSec % 3600) / 60;
                    Integer seconds = currentMission.currentDurationInSec % 60;
                    if (hours == 0 && minutes == 0)
                        tvTime.setText("Elapsed time : " + seconds + "s");
                    else if (hours == 0 && minutes > 0)
                        tvTime.setText("Elapsed time : " + minutes + "mn" + seconds + "s");
                    else
                        tvTime.setText("Elapsed time : " + hours + "h" + minutes + "mn" + seconds + "s");


                    //Log.d(TAG, "Tick Tick..." + currentMission.currentDurationInSec);
                }
            });
        }
    }

    //to display the picture without issue with the stack otherwise it takes too many ram and crashes
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("onCreate","IN");

        mDatabase =FirebaseDatabase.getInstance().getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btStart = (Button) findViewById(R.id.bt_start);
        btStart.setOnClickListener(this);
        btStop = (Button) findViewById(R.id.bt_stop);;
        btStop.setOnClickListener(this);
        btManual = (Button) findViewById(R.id.bt_manual);
        btManual.setOnClickListener(this);
        tvDriver = (TextView) findViewById(R.id.tv_driver);
        tvDriverTotalDistance = (TextView) findViewById(R.id.tv_totalDistance) ;
        tvDriverTotalTime = (TextView) findViewById(R.id.tv_totalTime);

        tvTime = (TextView) findViewById(R.id.tv_time);
        tvDistance = (TextView) findViewById(R.id.tv_distance);
        mImageCar = (ImageView) findViewById(R.id.imageView2);
        mImageCar.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.car, 500, 400));
        mImageGps = (ImageView) findViewById(R.id.imageGPS);
        mImageManual = (ImageView) findViewById(R.id.imageWriting);

        initGraphicalObject();
        tvDriver.setText(driver.getSurName()+" "+driver.getName());
        tvDriverTotalDistance.setText(String.valueOf(driver.getTotalDistanceDriverInMeter()/1000)+" km");
        int hours = driver.getTotalTimeDriverInSec()/3600;
        int minutes = (driver.getTotalTimeDriverInSec()-hours*3600)/60;
        tvDriverTotalTime.setText(hours+"h"+minutes+"mn");

        //Firebase mgt
        mChildDBEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        //TODO https://developer.android.com/training/monitoring-device-state/doze-standby.html
        PowerManager pM = (PowerManager)getSystemService(Context.POWER_SERVICE);
        wL=pM.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"whatever");
        wL.acquire();

        //switch ManualGPS
        swManualGpsSwitch = (Switch) findViewById(R.id.sw_gpsOrManual);
        swManualGpsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("Switch State=", ""+isChecked);
                //GPS mode is selected
                Animation animationGPS = new AlphaAnimation(1, 0); //to change visibility from visible to invisible
                animationGPS.setDuration(1000); //1 second duration for each animation cycle
                animationGPS.setInterpolator(new LinearInterpolator());
                animationGPS.setRepeatCount(Animation.INFINITE); //repeating indefinitely
                animationGPS.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.

                Animation animationFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
                Animation animationFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
                animationFadeIn.setFillAfter(true);
                animationFadeOut.setFillAfter(true);

                if (isChecked){
                    //start timer for blinking the GPS icon
                    mImageGps.startAnimation(animationGPS); //to start animation
                    almostLocked=0;
                    gpsLockedForDebug=0;
                    currentMission.gpsIsLocked=0;
                    if (currentMission.gpsIsLocked == 0) {
                        Toast.makeText(getApplicationContext(), "Stay steady as long as GPS is not locked", Toast.LENGTH_LONG).show();
                    }

                    //Fade out manual icon
                    mImageManual.startAnimation(animationFadeOut); //to start animation

                    //disable manual enter button
                    btManual.setEnabled(false);
                    btManual.setBackgroundColor(getResources().getColor(R.color.greyMonaco));
                    btManual.setTextColor(getResources().getColor(R.color.white));
                }
                // Manual enter is selected
                else {
                    //disable GPS icon and associated buttons and clear text distance and time
                    uiState="IDLE";
                    updateButton();

                    tvDistance.setText("Distance: "+ driver.getTotalDistanceDriverInMeter());
                    tvTime.setText("Elapsed time :"+currentMission.currentDurationInSec + " s");
                    mImageGps.clearAnimation(); //to stop animation
                    mImageGps.startAnimation(animationFadeOut);

                    //Fade in Manual writing icon
                    mImageManual.startAnimation(animationFadeIn); //to start animation

                    //enable manual enter button
                    btManual.setEnabled(true);
                    btManual.setBackgroundColor(getResources().getColor(R.color.redMonaco));
                    btManual.setTextColor(getResources().getColor(R.color.whiteMonaco));
                }
            }
        });

        Log.v("GPS is enabled", "");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, this);
    }




    // Compute distance and time when GPS location changes
    @Override
    public void onLocationChanged(@NonNull Location location) {
        //Log.d("onLocationChanged","IN");
        //tvCurrentLocation = (TextView) findViewById(R.id.tv_currentLocation);
        float[] distance = new float[3];
        double currentLatitude =location.getLatitude();
        double currentLongitude = location.getLongitude();
        distanceBetween(currentMission.lastLatitude,currentMission.lastLongitude,currentLatitude,currentLongitude,distance);
        currentMission.lastLatitude=currentLatitude;
        currentMission.lastLongitude=currentLongitude;
        if (currentMission.gpsIsLocked==0 && swManualGpsSwitch.isChecked()){
            Toast.makeText(getApplicationContext(),"Stay steady as long as GPS is not locked",Toast.LENGTH_SHORT).show();
            //Locked when n measures are bellow a threshold
           if(distance[0]<=getResources().getInteger(R.integer.GPS_LOCK_THRESHOLD_DISTANCE) || gpsLockedForDebug<=4){
            //if(distance[0]<=getResources().getInteger(R.integer.GPS_LOCK_THRESHOLD_DISTANCE)){
                almostLocked++;
                gpsLockedForDebug++;
                if (almostLocked==getResources().getInteger(R.integer.GPS_LOCK_SAMPLES)) {
                    currentMission.gpsIsLocked = 1;
                    uiState="GPS_LOCKED";
                    updateButton();
                    mImageGps.clearAnimation(); //to stop animation
                }
            }
            else {
                almostLocked = 0;
                currentMission.gpsIsLocked=0;
                btStart.setEnabled(false);
                uiState="IDLE";
                updateButton();
            }
        }

        //increment distance of the current mission when GPS is locked and we have pressed the start button
        if (distance[0]>=getResources().getInteger(R.integer.GPS_LOCK_THRESHOLD_DISTANCE) && (uiState=="MISSION_RESUME") &&  currentMission.gpsIsLocked==1){
            currentMission.currentDistanceInMeter+=distance[0];
        }

        String speed = df.format(location.getSpeed()*3.6);
        /*tvCurrentLocation.setText(
                "Almost Locked:" + (getResources().getInteger(R.integer.GPS_LOCK_SAMPLES)-almostLocked) + "\n" +
                        "Latitude:" + currentLatitude + ", " + "\n" +
                        "Longitude" + currentLongitude + "\n" +
                        "distance: " + distance[0] + "\n" +
                        "speed: " + speed + "km/h");*/
        tvDistance.setText("Distance: "+ currentMission.currentDistanceInMeter/1000 + "Km");
    }
    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentMission = savedInstanceState.getParcelable("parcelable");
        tvTime.setText("Elapsed time :"+currentMission.currentDurationInSec + " s");
        Log.d("OnrestoreInstance","IN");
        //TODO replace code here
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("parcelable", currentMission);
        Log.d("onSaveInstanceState","IN");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("onStart","IN");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("onPause","IN");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("onRestart","IN");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume","IN");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy","IN");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("onStop","IN");
    }

    @Override
    //What to do on START, STOP and Manual button
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.bt_start: {
                if (uiState=="GPS_LOCKED"){
                    //we pressed START
                    currentMission.currentDistanceInMeter=0;
                    currentMission.currentDurationInSec=0;
                    timer = new Timer();
                    timer.schedule(new TickTask(), 0,1000);
                    wL.acquire();
                    uiState="MISSION_RESUME";
                } else if (uiState=="MISSION_PAUSE"){
                    //we pressed CONTINUE
                    timer.cancel();
                    timer = new Timer();
                    timer.schedule(new TickTask(), 0,1000);
                    wL.acquire();
                    uiState="MISSION_RESUME";
                }
                updateButton();
                break;
            }

            case R.id.bt_stop: {
                if (uiState=="MISSION_PAUSE"){
                    wL.release();
                    uiState="MISSION_END";
                    Intent intent = new Intent(this,DifficultiesActivities.class);
                    intent.putExtra("key",currentMission);
                    intent.putExtra("Driver",driver);
                    startActivity(intent);
                } else if (uiState=="MISSION_RESUME"){
                    //we pressed the PAUSE button
                    timer.cancel();
                    wL.release();
                    uiState="MISSION_PAUSE";
                }
                updateButton();
                break;
            }

            case R.id.bt_manual:{
                Intent intent = new Intent(this, ManualMissionActivity.class);
                intent.putExtra("CurrentMission",currentMission);
                intent.putExtra("Driver",driver);
                startActivity(intent);
            }
            //.... etc
        }
    }

    public void initGraphicalObject(){
        btStop.setEnabled(false);
        btStop.setBackgroundColor(getResources().getColor(R.color.greyMonaco));
        btStop.setTextColor(getResources().getColor(R.color.whiteMonaco));
        btStart.setEnabled(false);
        btStart.setBackgroundColor(getResources().getColor(R.color.greyMonaco));
        btStart.setTextColor(getResources().getColor(R.color.whiteMonaco));
        btManual.setEnabled(true);
        btManual.setBackgroundColor(getResources().getColor(R.color.redMonaco));
        btManual.setTextColor(getResources().getColor(R.color.whiteMonaco));
        //TODO Color the buttons and manual panel
    }

    // update color of button and text according to ui state
    public void updateButton(){
        Log.d(TAG, "State: "+uiState);
        switch (uiState){
            case "IDLE":{
                //disable all button and set to default color
                btStop.setEnabled(false);
                btStop.setBackgroundColor(getResources().getColor(R.color.greyMonaco));
                btStop.setTextColor(getResources().getColor(R.color.whiteMonaco));
                btStart.setEnabled(false);
                btStart.setBackgroundColor(getResources().getColor(R.color.greyMonaco));
                btStart.setTextColor(getResources().getColor(R.color.whiteMonaco));
                currentMission.currentDistanceInMeter =0;
                currentMission.currentDurationInSec=0;
                break;
            }
            case "GPS_LOCKED":
            case "MISSION_END": {
                btStop.setText("STOP");
                btStop.setEnabled(false);
                btStop.setBackgroundColor(getResources().getColor(R.color.greyMonaco));
                btStop.setTextColor(getResources().getColor(R.color.white));

                btStart.setText("START");
                btStart.setEnabled(true);
                btStart.setBackgroundColor(getResources().getColor(R.color.redMonaco));
                btStart.setTextColor(getResources().getColor(R.color.whiteMonaco));
                break;
            }
            case "MISSION_RESUME":{
                btStop.setText("PAUSE");
                btStop.setEnabled(true);
                btStop.setBackgroundColor(getResources().getColor(R.color.redMonaco));
                btStop.setTextColor(getResources().getColor(R.color.whiteMonaco));

                btStart.setEnabled(false);
                btStart.setBackgroundColor(getResources().getColor(R.color.greyMonaco));
                btStart.setTextColor(getResources().getColor(R.color.white));

                break;
            }
            case "MISSION_PAUSE":{
                btStop.setText("STOP");
                btStop.setEnabled(true);
                btStop.setBackgroundColor(getResources().getColor(R.color.redMonaco));
                btStop.setTextColor(getResources().getColor(R.color.whiteMonaco));

                btStart.setText("CONTINUE");
                btStart.setEnabled(true);
                btStart.setBackgroundColor(getResources().getColor(R.color.redMonaco));
                btStart.setTextColor(getResources().getColor(R.color.whiteMonaco));
                break;
            }
        }
    }

}