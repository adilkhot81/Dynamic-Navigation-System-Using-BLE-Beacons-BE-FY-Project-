package com.example.gloria.dataset_recorder;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static java.lang.Double.valueOf;
import static java.lang.Math.pow;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {

    private static final String TAG = "MainActivity";
    private SensorManager sensorManager;
    Button getLocationBtn;
    TextView locationText;
    private Sensor mMagno;
    TextView xValue, yValue, zValue,latitude,longitude;
    private Button b;
    private TextView t;
    private LocationManager locationManager;
    private LocationListener listener;
   // private View.OnClickListener listener;
    private int i=0;
    double lat,lng;
    double lastLat=0,lastLng=0;

    ArrayList<Double> lastLatList=new ArrayList<Double>();
    ArrayList<Double> lastLngList=new ArrayList<Double>();
    ArrayList<Double> magXList=new ArrayList<Double>();
    ArrayList<Double> magYList=new ArrayList<Double>();
    ArrayList<Double> magZList=new ArrayList<Double>();
    ArrayList<Double> LatList=new ArrayList<Double>();
    ArrayList<Double> LngList=new ArrayList<Double>();
   // int lines=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InputStream is=getResources().openRawResource(R.raw.latlongdata);
        BufferedReader reader =new BufferedReader(new InputStreamReader(is,Charset.forName("UTF-8")));
        String line="";
        try {
            reader.readLine(); //first line is wasted

            while ((line = reader.readLine()) != null) {

                String[] tokens=line.split(",");
                lastLatList.add(valueOf(tokens[0]));
                lastLngList.add(valueOf(tokens[1]));
                magXList.add(valueOf(tokens[2]));
                magYList.add(valueOf(tokens[3]));
                magZList.add(valueOf(tokens[4]));
                LatList.add(valueOf(tokens[5]));
                LngList.add(valueOf(tokens[6]));
            }
        }catch (IOException e) {
            e.printStackTrace();
        }


        xValue = (TextView) findViewById(R.id.xValue);
        yValue = (TextView) findViewById(R.id.yValue);
        zValue = (TextView) findViewById(R.id.zValue);
        latitude=(TextView) findViewById(R.id.latitude);
        longitude=(TextView) findViewById(R.id.longitude);


        Log.d(TAG, "onCreate: Initializing Sensor Services");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mMagno = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (mMagno != null) {
            sensorManager.registerListener(MainActivity.this, mMagno, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered Magnetometer Listener");
        } else {
            xValue.setText("Magno Not Supported");
            yValue.setText("Magno Not Supported");
            zValue.setText("Magno Not Supported");
        }
        t = (TextView) findViewById(R.id.textView);
        b = (Button) findViewById(R.id.button);



        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                lat=location.getLatitude();
                lng=location.getLongitude();

                t.setText("\n " +lat+ " " + lng);
                //Log.d("buttonActivity", location.getLongitude()+" "+location.getLatitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        configure_button();

    }

    double knn(ArrayList<Double> lastList,ArrayList<Double> magXList,ArrayList<Double> magYList,ArrayList<Double> magZList,ArrayList<Double> ExpectedOut,double user_last,double userMagx,double userMagy,double userMagz)
    {
        double min_d=Double.MAX_VALUE;
        double min_out=0,d;
        int l=lastList.size();

        for(int i=0;i<l;i++)
        {
           d=cd(lastList.get(i).doubleValue(),magXList.get(i).doubleValue(),magYList.get(i).doubleValue(),magZList.get(i).doubleValue(),user_last,userMagx,userMagy,userMagz);
            if(d<min_d)
            {
                min_d = d;
                min_out = ExpectedOut.get(i).doubleValue();
            }


        }
        return min_d + min_out;
    }

    double cd(double lastList,double magXList,double magYList,double magZList, double user_last ,double userMagx,double userMagy,double userMagz){
        double d=pow((pow((lastList-user_last),4)+pow((magXList-userMagx),4)+pow((magYList-userMagy),4)+pow((magZList-userMagz),4)),(1/4));
        Log.d("d :", String.valueOf(d));
        return d;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor sensor = sensorEvent.sensor;
        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            //Log.d(TAG, "onSensorChanged: X: " + sensorEvent.values[0] + " Y: " + sensorEvent.values[1] + " Z: " + sensorEvent.values[2]);

            xValue.setText(Double.toString(sensorEvent.values[0]));
            yValue.setText(Double.toString(sensorEvent.values[1]));
            zValue.setText(Double.toString(sensorEvent.values[2]));
        }
    }
    void configure_button(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }

            return;
        }

        locationManager.requestLocationUpdates("gps", 0, 0, listener);

        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        b.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                String latt,lngg;

                //latt=Integer.toString((int)(lat*pow(10,9)));
                //lngg=Integer.toString((int)(lng*pow(10,9)));
                if(lastLat==0 && lastLng==0)
                {
                    lastLat=lat;
                    lastLng=lng;
                }
                String Label=Integer.toString(i);
                String x="",y="",z="";
                x=xValue.getText().toString();
                y=yValue.getText().toString();
                z=zValue.getText().toString();

                double predlat,predlng;
                predlat=knn(lastLatList, magXList, magYList, magZList, LatList, lastLat, Double.parseDouble(x),Double.parseDouble(y),Double.parseDouble(z));
                predlng=knn(lastLngList, magXList, magYList, magZList, LngList, lastLng, Double.parseDouble(x),Double.parseDouble(y),Double.parseDouble(z));
                //Log.d("LAT-LNG",Double.toString(predlat)+" "+Double.toString(predlng));
                predlat=predlat-1;
                predlng=predlng-1;
                latitude.setText(Double.toString(predlat));
                longitude.setText(Double.toString(predlng));

                lastLat=lat;
                lastLng=lng;


            }
        });
    }

    @Override
    public void onClick(View v) {


    }


}

