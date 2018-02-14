package edu.gsu.httpscs.sensorclassdemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SensorActivity extends AppCompatActivity {

    private final int LOCATION_CODE=1111;
    private final int FILE_CODE=2222;
    private final int SERVICE_CODE=3333;

    @BindView(R.id.sensor_tv_data)
    TextView data;
    private Intent intent;


    @OnClick(R.id.sensor_bt_read)
    public void read(Button button) {
        startReading();
    }

    @OnClick(R.id.sensor_bt_save)
    public void save(Button button) {

        if (checkFilePermission()){
            saveFile();
        }else{
            requestPermissions(new String[]
                    {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,},FILE_CODE);
        }
    }

    @OnClick(R.id.sensor_bt_service)
    public void startService(Button button) {
        if (checkFilePermission()&&checkLocationPermission()){
            startService();
        }else{
            requestPermissions(new String[]
                    {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},SERVICE_CODE);
        }
    }

    private void startService(){
        intent = new Intent(this, SensorService.class);
        startService(intent);
    }

    private void saveFile(){
        if (FileUtil.writeString
                (getBaseContext().getExternalCacheDir().getAbsolutePath()+"/data.txt",
                        data.getText().toString())){
            Toast.makeText(this,"Save Successed",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"Save Failed",Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkFilePermission() {
        return ActivityCompat.checkSelfPermission
                (this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkLocationPermission(){
        return ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String stringMsg = intent.getStringExtra("stringKey");
        int intMsg = intent.getIntExtra("numberkey", 0);
        Log.v("sensor", stringMsg);
        Log.e("sensor", intMsg + "");

    }

    private void startReading() {
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Check if user has permission
        if (!checkLocationPermission()) {
            //Only for API:23 and above
            requestPermissions(new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,},LOCATION_CODE);
            return;
        }
        Location location = locationManager.getLastKnownLocation
                (LocationManager.GPS_PROVIDER);

        //update every 2000ms or distance changed 8 meters
        locationManager.requestLocationUpdates
                (LocationManager.GPS_PROVIDER, 2000, 8, new LocationListener() {
                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        Log.v("Activity","status reading...");
                    }
                    @Override
                    public void onProviderEnabled(String provider) {
                    }
                    @Override
                    public void onProviderDisabled(String provider) {
                    }
                    @Override
                    public void onLocationChanged(Location location) {
                        update(location);
                        Log.v("Activity","location reading...");
                    }
                });

        update(location);
    }

    private void update(Location location) {
        if (location != null) {
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
            data.setText(sb.toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,grantResults);
        switch(requestCode) {
            case LOCATION_CODE:
                if (checkLocationPermission()) {
                    startReading();
                }else{
                    Toast.makeText(this,"You don't have permission to access Location",Toast.LENGTH_LONG).show();
                }
                break;
            case FILE_CODE:
                if (checkFilePermission()){
                    saveFile();
                }else{
                    Toast.makeText(this,"You don't have permission to access storage",Toast.LENGTH_LONG).show();
                }
                break;
            case SERVICE_CODE:
                if (checkFilePermission()&&checkLocationPermission()){
                    startService();
                }else{
                    Toast.makeText(this,"Open permission before start service!",Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intent);
    }
}
