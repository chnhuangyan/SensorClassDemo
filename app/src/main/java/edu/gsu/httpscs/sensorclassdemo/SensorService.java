package edu.gsu.httpscs.sensorclassdemo;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class SensorService extends Service {

    private final Timer timer;
    private LocationManager locationManager;

    public SensorService() {
        super.onCreate();
        timer = new Timer();

    }

    private void setTimerTask() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.v("Service","Saving...");
            }
        }, 1000, 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return super.onStartCommand(intent, flags, startId);
        }

        setTimerTask();

        //update every 2000ms or distance changed 8 meters
        locationManager.requestLocationUpdates
                (LocationManager.GPS_PROVIDER, 2000, 8, new LocationListener() {
                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }
                    @Override
                    public void onProviderEnabled(String provider) {
                    }
                    @Override
                    public void onProviderDisabled(String provider) {
                    }
                    @Override
                    public void onLocationChanged(Location location) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Live Location:\n");
                        sb.append("Longitude: ");
                        sb.append(location.getLongitude());
                        sb.append("\nLatitude: ");
                        sb.append(location.getLatitude());
                        sb.append("\nAltitude: ");
                        sb.append(location.getAltitude());
                        sb.append("\nSpeed：");
                        sb.append(location.getSpeed());
                        sb.append("\nBearing：");
                        sb.append(location.getBearing());
                        FileUtil.writeString
                                (getBaseContext().getExternalCacheDir().getAbsolutePath()+"/"+System.currentTimeMillis()+"data.txt",
                                       sb.toString());
                        Log.v("Service","location reading...");
                    }
                });

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
