package niners.ninernav;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private android.location.LocationListener locListen;
    private LocationManager locManage;
    private Double userLat;
    private Double userLong;
    private ArrayAdapter<String> places;

    private String[] buildingNames = new String[]{
            //Academic Buildings
            "Atkins",
            "Barnard",
            "Belk Gym",
            "Bioinformatics",
            "Burson",
            "Cameron Hall",
            "College of Education",
            "Colvard",
            "Denny",
            "Duke",
            "EPIC",
            "Fretwell",
            "Friday",
            "Garinger",
            "Grigg",
            "Health & Human Services",
            "Johnson Center",
            "Kulwicki",
            "Macy",
            "McEniry",
            "Memorial Hall",
            "Motorsports Research",
            "PORTAL",
            "Robinson",
            "Rowe",
            "Smith",
            "Storrs",
            "Winningham",
            "Woodward",
            //Other Service Buildings
            "Auxiliary Services",
            "Barnhardt (Student Activity Center/Halton Arena)",
            "Cafeteria Activities Building",
            "Cone",
            "Counseling Center",
            "Facilities Management",
            "Facilities Operations/Parking Services",
            "Hayes Stadium",
            "McMillan Greenhouse",
            "Miltimore-Wallis Center",
            "Campus Police",
            "Student Health Center",
            "Cato Hall",
            "Kennedy",
            "King",
            "Reese",
            "Belk Plaza",
            "Halton Wagner Complex",
            "Harris Alumni Center",
            "Hauser Alumni Pavilion",
            "Irwin Belk Track & Field",
            "Richardson Stadium",
            "Niner House",
            //Food Services
            "Prospector",
            "SoVi",
            "Student Union",
            //Residence Halls
            "Belk Hall",
            "Cedar Hall",
            "Elm Hall",
            "Greek Village",
            "Hawthorn Hall",
            "Hickory Hall",
            "Holshouser Hall",
            "Hunt Hall",
            "Laurel Hall",
            "Levine Hall",
            "Lynch Hall",
            "Maple Hall",
            "Martin Hall",
            "Moore Hall",
            "Miltimore Hall",
            "Oak Hall",
            "Pine Hall",
            "Sanford Hall",
            "Scott Hall",
            "Sycamore Hall",
            "Wallis Hall",
            "Witherspoon Hall",
            //Academic Building Abbreviations
            "ATKNS (Atkins)",
            "BIOIN (Bioinformatics)",
            "BRNRD (Barnard)",
            "BURSN (Burson)",
            "CARC (Cameron)",
            "CHHS (College of Health & Human Services)",
            "COED (College of Education)",
            "COLVD (Colvard)",
            "FRET (Fretwell)",
            "FRIDY (Friday)",
            "GRNGR (Garinger)",
            "GYMNS (Belk Gym)",
            "HEALC (Student Health Center)",
            "JBCB (Johnson Center)",
            "MCEN (McEniry)",
            "MCMIL (McMillan)",
            "MEMOR (Memorial Hall)",
            "ROBIN (Robinson)",
            "WINN (Winningham)",
            "WOODW (Woodward)"

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        AutoCompleteTextView textEntry = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        assert textEntry != null;

        places = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, buildingNames);

        textEntry.setAdapter(places);
        textEntry.setThreshold(1);

        locManage = (LocationManager) getSystemService(LOCATION_SERVICE);
        locListen = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                userLat = location.getLatitude();
                userLong = location.getLongitude();
                LatLng userLoc = new LatLng(userLat, userLong);
                //mMap.addMarker(new MarkerOptions().position(userLoc).title("You are here"));

            }

            public void onStatusChanged(String word, int num, Bundle bundle){

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
            }, 10);

            return;
        }
        locManage.requestLocationUpdates("network", 5000, 0, locListen);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng unccUnion = new LatLng(35.308406, -80.733683);
        float startingZoomLvl = 17;
        float minimumZoomLvl = 10;
        LatLng SW = new LatLng(35.300000, -80.744298);
        LatLng NE = new LatLng(35.313402, -80.721168);
        LatLngBounds mapBound = new LatLngBounds(SW, NE);
        mMap.addMarker(new MarkerOptions().position(unccUnion).title("Student Union"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(unccUnion, startingZoomLvl));
        mMap.setLatLngBoundsForCameraTarget(mapBound);
        mMap.setMinZoomPreference(minimumZoomLvl);
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
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int code, String[] permissions, int[] results){
        switch(code){
            case 10:


        }

    }
}
