package com.example.proiectcm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity  implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_LOCATION = 0;
    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout=findViewById(R.id.layout);

        Button button=(Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRestaurants();
            }
        });
    }

    public void showRestaurants(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            //VERIFICAM  sa vedem daca avem permisiune,daca o avem lista cu restaurate este afisata.
            Snackbar.make(mLayout,
                    "Locatia este pornita. Lista cu restaurante este urmatoarea:", Snackbar.LENGTH_SHORT).show();
            startRestaurants();
        } else {
             //Permisiunea nu a fost oferita si trebuie acceptata.
            requestLocationPermission();
        }
    }

    private void  requestLocationPermission(){
        //Permisiunea nu a fost acordata si trebuie solicitat
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)){
            //trebuie sa ii dam utilizatorului o justificare suplimentara daca permisiunea nu e acordata
            //afisam un snackBar pentru a solicita permisiunea care lipseste
            Snackbar.make(mLayout," Accesul la locatie este necesar pentru a afisa restaurantele din zona ta.",
            Snackbar.LENGTH_INDEFINITE).setAction("OK",new
                    View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Cere permisiunea
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_REQUEST_LOCATION);
                }
            }).show();

        } else {
            Snackbar.make(mLayout,
                    "Permisiunea nu a fost acordata.Permiteti locatia.",
                    Snackbar.LENGTH_SHORT).show();
            //Cerem permisiunea iar rezultatul va fi primit in metoda onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            // Solicitam permisiunea locației
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permisiunea a fost acordata. Se poate vizualiza activitatea.
                Snackbar.make(mLayout, "Cererea locatiei a fost acceptata. Puteti vizualiza restaurantele.",
                        Snackbar.LENGTH_SHORT)
                        .show();
                startRestaurants();
            } else {
                // in caz ca permisiunea pentru aflarea locatiei a fost respinsa, vom afisa un mesaj de instiintare
                Snackbar.make(mLayout, "Permisiunea pentru locatie a fost refuzata",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }
    @SuppressLint("MissingPermission")
    public void startRestaurants() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            Intent intent = new Intent(this, DisplayRestaurants.class);
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Double longitude = location.getLongitude();
           Double latitude = location.getLatitude();
           String longit = Double.toString(longitude);
           String lat = Double.toString(latitude);
            intent.putExtra("long", longit);
            intent.putExtra("lat", lat);
            startActivity(intent);
        }
    }
}
