package niners.ninernav;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.app.AlertDialog.Builder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzpu;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private android.location.LocationListener locListen;
    private LocationManager locManage;
    private Double userLat;
    private Double userLong;
    private LatLng userLoc;
    private ArrayAdapter<String> places;
    private GoogleApiClient mGAPIC;
    private Places mPlaces;
    private HashMap<String, LatLng> coordinates;


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
            "Kulwicki",  //17
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
            "Facilities Management", //34
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
            "Niner House", //51
            //Food Services
            "Prospector",
            "SoVi",
            "Student Union", //54
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
            "Witherspoon Hall", //76
            //Academic Building Abbreviations
            "ATKNS (Atkins)", //77
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
            "WOODW (Woodward)" //96

    };

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        coordinates = new HashMap<String, LatLng>();
        loadBuildingHash();

        final AutoCompleteTextView textEntry =(AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);

        //set onclick listener that clears text when clicked and onItemSelected listener for when user chooses search result
        assert textEntry != null;
        textEntry.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                textEntry.setText("");
            }

        });



        //To allow buildings to be referenced by auto complete text view
        places = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, buildingNames);

        textEntry.setAdapter(places);
        textEntry.setThreshold(1);

        final InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);

        textEntry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userSelection = textEntry.getText().toString();
                LatLng buildingCoord = coordinates.get(userSelection);
                imm.hideSoftInputFromWindow(textEntry.getWindowToken(), 0);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(buildingCoord, 18));
            }
        });
        


        //Set up location manager and listener
        locManage = (LocationManager) getSystemService(LOCATION_SERVICE);
        locListen = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                userLat = location.getLatitude();
                userLong = location.getLongitude();
                userLoc = new LatLng(userLat, userLong);

            }

            public void onStatusChanged(String word, int num, Bundle bundle){

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                final android.app.AlertDialog.Builder locationAlert = new android.app.AlertDialog.Builder(MapsActivity.this);

                locationAlert.setMessage("Please turn location services on");
                locationAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface v, int j){
                        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(i);
                    }
                });

                locationAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface v, int k){
                    }
                });

                AlertDialog dialog = locationAlert.show();


            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
            }, 10);

            return;
        }

        //Get updates from location manager every 5 seconds (5000 milliseconds) from "network" (wifi coordinates)
        locManage.requestLocationUpdates("network",5000,0,locListen);


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

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                if (mMap.getCameraPosition().zoom < 16){
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
                }
            }
        });




    }

    @Override
    public void onRequestPermissionsResult(int code, String[] permissions, int[] results){
        switch(code){
            case 10:


        }

    }

    public void loadBuildingHash(){

        //Atkins
        coordinates.put(buildingNames[77], new LatLng(35.305472, -80.732210));
        //Bioinformatics
        coordinates.put(buildingNames[78], new LatLng(35.312520, -80.741844));
        //Barnard
        coordinates.put(buildingNames[79], new LatLng(35.305887, -80.729845));
        //Burson
        coordinates.put(buildingNames[80], new LatLng(35.307703, -80.732476));
        //Cameron
        coordinates.put(buildingNames[81], new LatLng(35.307712, -80.731170));
        //CHHS
        coordinates.put(buildingNames[82], new LatLng(35.307555, -80.733402));
        //COED
        coordinates.put(buildingNames[83], new LatLng(35.307594, -80.734059));
        //Colvard
        coordinates.put(buildingNames[84], new LatLng(35.304860, -80.731748));
        //Fretwell
        coordinates.put(buildingNames[85], new LatLng(35.306014, -80.729420));
        //Friday
        coordinates.put(buildingNames[86], new LatLng(35.306329, -80.730243));
        //Garinger
        coordinates.put(buildingNames[87], new LatLng(35.305020, -80.729886));
        //Belk Gym
        coordinates.put(buildingNames[88], new LatLng(35.305383, -80.735206));
        //Student Health Center
        coordinates.put(buildingNames[89], new LatLng(35.310690, -80.729722));
        //Johnson Center
        coordinates.put(buildingNames[90], new LatLng(35.304151, -80.728858));
        //McEniry
        coordinates.put(buildingNames[91], new LatLng(35.307024, -80.730331));
        //McMillan
        coordinates.put(buildingNames[92], new LatLng(35.307802, -80.729754));
        //Memorial Hall
        coordinates.put(buildingNames[93], new LatLng(35.303823, -80.735707));
        //Robinson
        coordinates.put(buildingNames[94], new LatLng(35.304272, -80.729907));
        //Winningham
        coordinates.put(buildingNames[95], new LatLng(35.305168, -80.730375));
        //Woodward
        coordinates.put(buildingNames[96], new LatLng(35.307589, -80.735593));


    }
}
