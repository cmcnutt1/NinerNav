package niners.ninernav;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.*;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.app.AlertDialog.Builder;
import android.graphics.Color;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.request.DirectionRequest;
import com.akexorcist.googledirection.GoogleDirection;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;
    private static android.location.LocationListener locListen;
    private static SupportMapFragment mapFragment;
    private static LocationManager locManage;
    private static Double userLat;
    private static Double userLong;
    private static LatLng userLoc;
    private static String navFromName;
    private static String navToName;
    private static LatLng navFrom;
    private static LatLng navTo;
    private static int parent_id;
    private static Intent parent_intent;
    private static boolean locationStatus = true;

    private static float userDir;
    private static boolean followUser = false;
    private static Marker currentMark;

    private static ArrayAdapter<String> places;
    private static GoogleApiClient mGAPIC;
    private static Places mPlaces;
    private static HashMap<String, LatLng> coordinates;
    private static ArrayList<Polygon> clickables;
    private static HashMap<Polygon, String> polyInfo;
    private static boolean inNavMode = false;
    private static Polyline navLine;
    private static Button goButton;
    private static Button cancelButton;

    private static Bundle extras;
    //privte String marker_image = Context.getApplicationInfo().dataDir;
    private static BitmapDescriptor pin;
    private static BitmapDescriptor overlay;

    private static String[] buildingNames = new String[]{
            //Academic Buildings
            "Atkins",
            "Barnard",
            "Belk Gym",
            "Bioinformatics",
            "Burson",
            "Cameron Hall", //5
            "College of Education",
            "Colvard",
            "Cone",
            "Denny",
            "Duke", //10
            "EPIC",
            "Fretwell",
            "Friday",
            "Garinger",
            "Grigg", //15
            "Health & Human Services",
            "Johnson Center", //17
            "Macy",
            "McEniry",
            "McMillan", //20
            "Memorial Hall",
            "Motorsports Research",
            "PORTAL",
            "Robinson",
            "Rowe", //25
            "Smith",
            "Storrs",
            "Student Health Center",
            "Winningham",
            "Woodward", //30
            //Other Service Buildings
            "Barnhardt (Student Activity Center/Halton Arena)",
            "Cato Hall",
            "Kennedy",
            "Reese",//51
            //Food Services
            "Prospector",
            "SoVi Dining Hall",
            "Student Union", //54
            //Residence Halls
            "Belk Hall",//38
            "Cedar Hall",
            "Elm Hall", //40
            "Greek Village",
            "Hawthorn Hall",//42
            "Hickory Hall",
            "Holshouser Hall",//44
            "Hunt Hall",
            "Laurel Hall",//46
            "Levine Hall",
            "Lynch Hall",//48
            "Maple Hall",
            "Martin Hall",//50
            "Moore Hall",
            "Miltimore Hall",//52
            "Oak Hall",
            "Pine Hall",//54
            "Sanford Hall",
            "Scott Hall",//56
            "Sycamore Hall",
            "Wallis Hall",//58
            "Witherspoon Hall", //59
            //Academic Building Abbreviations
            "ATKNS (Atkins)", //60
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
            "WOODW (Woodward)" //79


    };

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        coordinates = new HashMap<String, LatLng>();
        loadBuildingHash();


        final AutoCompleteTextView textEntry = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);

        //set onclick listener that clears text when clicked and onItemSelected listener for when user chooses search result
        assert textEntry != null;
        textEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textEntry.setText("");
            }

        });


        //To allow buildings to be referenced by auto complete text view
        places = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, buildingNames);

        textEntry.setAdapter(places);
        textEntry.setThreshold(1);


        final InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);

        pin  = BitmapDescriptorFactory.fromResource(R.drawable.pinpoint);


        //When a user selects a dropdown item from searchbar
        textEntry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userSelection = textEntry.getText().toString();
                LatLng buildingCoord = coordinates.get(userSelection);
                imm.hideSoftInputFromWindow(textEntry.getWindowToken(), 0);
                System.gc();
                removePins();
                System.gc();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(buildingCoord, 17));
                mMap.addMarker(new MarkerOptions()
                                     .position(buildingCoord)
                                     .title(userSelection)
                                     .icon(pin));


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

                if (followUser){
                    //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 18));
                }

            }

            public void onStatusChanged(String word, int num, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                final android.app.AlertDialog.Builder locationAlert = new android.app.AlertDialog.Builder(MapsActivity.this);

                locationAlert.setMessage("Please turn location services on");
                locationAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface v, int j) {
                        locationStatus = true;
                        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(i);
                    }
                });

                locationAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface v, int k) {
                        locationStatus = false;
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

        //Get updates from location manager every 3 seconds (3000 milliseconds) from "network" (wifi coordinates)
        locManage.requestLocationUpdates("network", 3000, 0, locListen);


        if((this.getIntent().getExtras().getInt("calledActivity") == 2) && !(this.getIntent().equals(null))){
            inNavMode = true;
            parent_intent = this.getIntent();
        }
        else if ((this.getIntent().getExtras().getInt("calledActivity") == 1)){
            inNavMode = false;
            if(mMap != null){
                mMap.clear();
            }
        }







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

        overlay = BitmapDescriptorFactory.fromResource(R.drawable.coloredmap);

        LatLng unccNE = new LatLng(35.313029, -80.726618);

        LatLng unccSW = new LatLng(35.303262, -80.743691);

        LatLngBounds mapBounds = new LatLngBounds(unccSW, unccNE);

        GroundOverlayOptions unccMap = new GroundOverlayOptions()
                .image(overlay)
                .positionFromBounds(mapBounds);
        mMap.addGroundOverlay(unccMap);

        createButtons();


        // Add a marker in Sydney and move the camera
        LatLng unccUnion = new LatLng(35.308406, -80.733683);
        float startingZoomLvl = 16;
        float minimumZoomLvl = 10;
        LatLng SW = new LatLng(35.302771, -80.744404);
        LatLng NE = new LatLng(35.313402, -80.721168);
        LatLngBounds mapBound = new LatLngBounds(SW, NE);
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
                if (mMap.getCameraPosition().zoom < 15) {
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                }
            }
        });

        if(inNavMode) {
            Location currLoc = locManage.getLastKnownLocation("network");
            userLoc = new LatLng(currLoc.getLatitude(), currLoc.getLongitude());
            getNavDirections(parent_intent);
            goButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
        }

        clickables = new ArrayList<Polygon>();
        polyInfo = new HashMap<Polygon, String>();
        setPolygons();



        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {

                String s = "";
                for (Polygon i : clickables){
                    if (polygon.equals(i)){
                        s = polyInfo.get(i);
                    }

                }

                LatLng pinLoc = coordinates.get(s);
                removePins();

                Marker currentMark = mMap.addMarker(new MarkerOptions().
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.pinpoint)).
                        position(pinLoc).title(s));
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String build = marker.getTitle();
                marker.setTitle("Navigate to " + build);
                marker.showInfoWindow();

                return true;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();
                if(locationStatus == true) {
                    getDirections(userLoc, marker.getPosition());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getMidPoint(userLoc, marker.getPosition()), 15));
                    goButton.setVisibility(View.VISIBLE);
                    cancelButton.setVisibility(View.VISIBLE);

                }
                else{
                    android.app.AlertDialog.Builder locationAlert = new android.app.AlertDialog.Builder(MapsActivity.this);

                    locationAlert.setMessage("Please turn location services on");
                    locationAlert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface v, int j) {
                            locationStatus = true;
                            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(i);
                        }
                    });

                    locationAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface v, int k) {
                        }
                    });

                    AlertDialog dialog = locationAlert.show();
                }

                marker.setTitle(marker.getTitle().substring(12));

            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goButton.setVisibility(View.GONE);
                //cancelButton.setVisibility(View.GONE);


                //Method to follow user
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 18));


            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
                followUser = false;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 16));
                removePins();
            }
        });






    }

    @Override
    public void onRequestPermissionsResult(int code, String[] permissions, int[] results) {
        switch (code) {
            case 10:


        }

    }

    public void loadBuildingHash() {

        //Atkins
        coordinates.put(buildingNames[0], new LatLng(35.305826, -80.732311));
        coordinates.put(buildingNames[60], new LatLng(35.305826, -80.732311));
        //Bioinformatics
        coordinates.put(buildingNames[3], new LatLng(35.312520, -80.741844));
        coordinates.put(buildingNames[61], new LatLng(35.312520, -80.741844));

        //Barnard
        coordinates.put(buildingNames[1], new LatLng(35.305887, -80.729845));
        coordinates.put(buildingNames[62], new LatLng(35.305887, -80.729845));

        //Burson
        coordinates.put(buildingNames[4], new LatLng(35.307703, -80.732476));
        coordinates.put(buildingNames[63], new LatLng(35.307703, -80.732476));

        //Cameron
        coordinates.put(buildingNames[5], new LatLng(35.307712, -80.731170));
        coordinates.put(buildingNames[64], new LatLng(35.307712, -80.731170));

        //CHHS
        coordinates.put(buildingNames[16], new LatLng(35.307555, -80.733402));
        coordinates.put(buildingNames[65], new LatLng(35.307555, -80.733402));

        //COED
        coordinates.put(buildingNames[6], new LatLng(35.307594, -80.734059));
        coordinates.put(buildingNames[66], new LatLng(35.307594, -80.734059));

        //Colvard
        coordinates.put(buildingNames[7], new LatLng(35.304860, -80.731748));
        coordinates.put(buildingNames[67], new LatLng(35.304860, -80.731748));

        //Fretwell
        coordinates.put(buildingNames[12], new LatLng(35.306014, -80.729420));
        coordinates.put(buildingNames[68], new LatLng(35.306014, -80.729420));

        //Friday
        coordinates.put(buildingNames[13], new LatLng(35.306329, -80.730243));
        coordinates.put(buildingNames[69], new LatLng(35.306329, -80.730243));

        //Garinger
        coordinates.put(buildingNames[14], new LatLng(35.305020, -80.729886));
        coordinates.put(buildingNames[70], new LatLng(35.305020, -80.729886));

        //Belk Gym
        coordinates.put(buildingNames[2], new LatLng(35.305383, -80.735206));
        coordinates.put(buildingNames[71], new LatLng(35.305383, -80.735206));

        //Student Health Center
        coordinates.put(buildingNames[28], new LatLng(35.310690, -80.729722));
        coordinates.put(buildingNames[72], new LatLng(35.310690, -80.729722));

        //Johnson Center
        coordinates.put(buildingNames[17], new LatLng(35.304151, -80.728858));
        coordinates.put(buildingNames[73], new LatLng(35.304151, -80.728858));
        //McEniry
        coordinates.put(buildingNames[19], new LatLng(35.307024, -80.730331));
        coordinates.put(buildingNames[74], new LatLng(35.307024, -80.730331));

        //McMillan
        coordinates.put(buildingNames[20], new LatLng(35.307802, -80.729754));
        coordinates.put(buildingNames[75], new LatLng(35.307802, -80.729754));

        //Memorial Hall
        coordinates.put(buildingNames[21], new LatLng(35.303823, -80.735707));
        coordinates.put(buildingNames[76], new LatLng(35.303823, -80.735707));

        //Robinson
        coordinates.put(buildingNames[24], new LatLng(35.304272, -80.729907));
        coordinates.put(buildingNames[77], new LatLng(35.304272, -80.729907));

        //Winningham
        coordinates.put(buildingNames[29], new LatLng(35.305168, -80.730375));
        coordinates.put(buildingNames[78], new LatLng(35.305168, -80.730375));

        //Woodward
        coordinates.put(buildingNames[30], new LatLng(35.307100, -80.735747));
        coordinates.put(buildingNames[79], new LatLng(35.307100, -80.735747));

        //Duke
        coordinates.put(buildingNames[10], new LatLng(35.311911, -80.741184));

        //MotorSports
        coordinates.put(buildingNames[22], new LatLng(35.312620, -80.740320));

        //Grigg
        coordinates.put(buildingNames[15], new LatLng(35.311298, -80.741924));

        //PORTAL
        coordinates.put(buildingNames[23], new LatLng(35.311666, -80.742927));

        //EPIC
        coordinates.put(buildingNames[11], new LatLng(35.309066, -80.741626));

        //Cone
        coordinates.put(buildingNames[8], new LatLng(35.305368, -80.733211));

        //Halton
        coordinates.put(buildingNames[31], new LatLng(35.306309, -80.734390));

        //Rowe
        coordinates.put(buildingNames[25], new LatLng(35.304512, -80.730743));

        //Storrs
        coordinates.put(buildingNames[27], new LatLng(35.304565, -80.729190));

        //Prospector
        coordinates.put(buildingNames[35], new LatLng(35.306824, -80.730893));

        //Macy
        coordinates.put(buildingNames[18], new LatLng(35.305683, -80.730404));

        //Kennedy
        coordinates.put(buildingNames[33], new LatLng(35.305982, -80.730925));

        //Smith
        coordinates.put(buildingNames[26], new LatLng(35.306968, -80.731422));

        //Student Union
        coordinates.put(buildingNames[37], new LatLng(35.308663, -80.733709));

        //Belk
        coordinates.put(buildingNames[38], new LatLng(35.310642, -80.734680));

        //Miltimore
        coordinates.put(buildingNames[52], new LatLng(35.310994, -80.734759));

        //Wallis
        coordinates.put(buildingNames[58], new LatLng(35.310903, -80.733905));

        //Lynch
        coordinates.put(buildingNames[48], new LatLng(35.310632, -80.734182));

        //Witherspoon
        coordinates.put(buildingNames[59], new LatLng(35.310902, -80.732298));

        //Pine
        coordinates.put(buildingNames[54], new LatLng(35.309300, -80.731398));

        //Maple
        coordinates.put(buildingNames[49], new LatLng(35.309040, -80.731314));

        //Elm
        coordinates.put(buildingNames[40], new LatLng(35.308775, -80.731500));

        //Oak
        coordinates.put(buildingNames[53], new LatLng(35.309046, -80.732252));

        //Cedar
        coordinates.put(buildingNames[39], new LatLng(35.309580, -80.728965));

        //Hickory
        coordinates.put(buildingNames[43], new LatLng(35.309197, -80.728966));

        //Sycamore
        coordinates.put(buildingNames[57], new LatLng(35.308845, -80.728965));

        //Martin
        coordinates.put(buildingNames[50], new LatLng(35.310036, -80.727464));

        //Hawthorn
        coordinates.put(buildingNames[42], new LatLng(35.311552, -80.727437));

        //SoVi
        coordinates.put(buildingNames[36], new LatLng(35.302797, -80.734882));

        //Laurel
        coordinates.put(buildingNames[46], new LatLng(35.302532, -80.736019));

        //Holshouser
        coordinates.put(buildingNames[44], new LatLng(35.302134, -80.736059));

        //Hunt
        coordinates.put(buildingNames[45], new LatLng(35.301385, -80.736174));

        //Moore
        coordinates.put(buildingNames[51], new LatLng(35.302620, -80.734144));

        //Sanford
        coordinates.put(buildingNames[55], new LatLng(35.303023, -80.733498));

        //Memorial
        coordinates.put(buildingNames[21], new LatLng(35.303760, -80.735832));

        //Scott
        coordinates.put(buildingNames[56], new LatLng(35.301752, -80.735346));

    }

    private void getDirections(LatLng from, LatLng to) {

        //mMap.clear();

        String key = "AIzaSyCM6P-fUqSR6lnxRHWQOUjAMc1B3SYMfg0";

        GoogleDirection.withServerKey(key)
                .from(from)
                .to(to)
                .transportMode("walking")
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        List<Route> r = direction.getRouteList();

                        PolylineOptions rectLine = new PolylineOptions().width(9).color(
                                Color.GREEN);
                        navLine = mMap.addPolyline(rectLine);


                        for(Route route : r){

                            navLine.setPoints(route.getOverviewPolyline().getPointList());

                        }


                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something here
                    }
                });

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getMidPoint(from, to), 15));




    }

    public LatLng getMidPoint(LatLng from, LatLng to){
        double lat = (from.latitude + to.latitude)/2;
        double lon = (from.longitude + to.longitude)/2;

        return new LatLng(lat, lon);
    }

    public void getNavDirections(Intent i) {

        extras = i.getExtras();

        navFromName = extras.getString("from");
        navToName =  extras.getString("to");
        inNavMode = extras.getBoolean("mode");

        //If user chooses My Location
        if (navFromName.equals("My Location") && !userLoc.equals(null)) {
            navFrom = userLoc;
        }
        else {
            navFrom = coordinates.get(navFromName);
        }

        navTo = coordinates.get(navToName);

        getDirections(navFrom, navTo);



    }

    public void setPolygons() {

       /* LatLng[] bursonBound = new LatLng[]{
                new LatLng(),
                new LatLng(),
                new LatLng(),
                new LatLng()
        };*/

        /*Polygon  = mMap.addPolygon(new PolygonOptions().
                add(new LatLng()).
                add(new LatLng()).
                add(new LatLng()).
                add(new LatLng()).visible(false));
                .setClickable(true);
                clickables.add();
                polyInfo.put(, "");*/

        Polygon atkins = mMap.addPolygon(new PolygonOptions().
                add(new LatLng(35.306170, -80.732939)).
                add(new LatLng(35.305481, -80.732746)).
                add(new LatLng(35.305488, -80.731303)).
                add(new LatLng(35.306138, -80.731432)).visible(false));

        atkins.setClickable(true);
        clickables.add(atkins);
        polyInfo.put(atkins, "ATKNS (Atkins)");

        Polygon burson = mMap.addPolygon(new PolygonOptions().
                add(new LatLng(35.307739, -80.733080)).
                add(new LatLng(35.307730, -80.731912)).
                add(new LatLng(35.307095, -80.731933)).
                add(new LatLng(35.307401, -80.733011)).visible(false));

        burson.setClickable(true);
        clickables.add(burson);
        polyInfo.put(burson, "BURSN (Burson)");

        Polygon chhs = mMap.addPolygon(new PolygonOptions().
                add(new LatLng(35.307943, -80.733577)).
                add(new LatLng(35.307945, -80.733129)).
                add(new LatLng(35.307229, -80.733119)).
                add(new LatLng(35.307067, -80.732931)).
                add(new LatLng(35.306866, -80.733140)).
                add(new LatLng(35.306899, -80.733585)).visible(false));

        chhs.setClickable(true);
        clickables.add(chhs);
        polyInfo.put(chhs, "CHHS (College of Health & Human Services)");

        Polygon coed = mMap.addPolygon(new PolygonOptions().
                add(new LatLng(35.307884, -80.734392)).
                add(new LatLng(35.307256, -80.734403)).
                add(new LatLng(35.307236, -80.733816)).
                add(new LatLng(35.307877, -80.733832)).visible(false));
        coed.setClickable(true);
        clickables.add(coed);
        polyInfo.put(coed, "COED (College of Education)");

        Polygon fret = mMap.addPolygon(new PolygonOptions().
                add(new LatLng(35.306484, -80.729419)).
                add(new LatLng(35.306092, -80.728469)).
                add(new LatLng(35.305862, -80.728466)).
                add(new LatLng(35.305853, -80.729429)).visible(false));
        fret.setClickable(true);
        clickables.add(fret);
        polyInfo.put(fret, "FRET (Fretwell)");

        Polygon friday = mMap.addPolygon(new PolygonOptions().
                add(new LatLng(35.306642, -80.730224)).
                add(new LatLng(35.306622, -80.729663)).
                add(new LatLng(35.306031, -80.729660)).
                add(new LatLng(35.306040, -80.730226)).visible(false));
        friday.setClickable(true);
        clickables.add(friday);
        polyInfo.put(friday, "FRIDY (Friday)");

        Polygon belkGym = mMap.addPolygon(new PolygonOptions().
                add(new LatLng(35.305742, -80.736208)).
                add(new LatLng(35.305764, -80.735194)).
                add(new LatLng(35.304994, -80.735199)).
                add(new LatLng(35.304968, -80.736213)).visible(false));
        belkGym.setClickable(true);
        clickables.add(belkGym);
        polyInfo.put(belkGym, "GYMNS (Belk Gym)");

        Polygon mcmil  = mMap.addPolygon(new PolygonOptions().
                add(new LatLng(35.308014, -80.729892)).
                add(new LatLng(35.308012, -80.729575)).
                add(new LatLng(35.307629, -80.729570)).
                add(new LatLng(35.307636, -80.729897)).visible(false));
        mcmil.setClickable(true);
        clickables.add(mcmil);
        polyInfo.put(mcmil, "MCMIL (McMillan)");



        Polygon wood = mMap.addPolygon(new PolygonOptions().
                add(new LatLng(35.307616, -80.735894)).
                add(new LatLng(35.306812, -80.735910)).
                add(new LatLng(35.306799, -80.735567)).
                add(new LatLng(35.307370, -80.735527)).
                add(new LatLng(35.307370, -80.734706)).
                add(new LatLng(35.307659, -80.734706)).visible(false));
        wood.setClickable(true);
        clickables.add(wood);
        polyInfo.put(wood, "WOODW (Woodward)");


        System.gc();
    }

    @Override
    protected void onStop() {
        //android.os.Process.killProcess(android.os.Process.myPid());

        super.onStop();
        if(mMap!=null)
        {

            mMap.clear();
            finish();


        }

    }

    @Override
    protected void onDestroy(){

        super.onDestroy();

        if(mMap != null){
            mMap.clear();
            navLine = null;
            mMap = null;
            finish();


        }
    }

    @Override
    public void onBackPressed(){

        super.onBackPressed();
    }

    public void removePins(){
        mMap.clear();

        LatLng unccNE = new LatLng(35.313029, -80.726618);

        LatLng unccSW = new LatLng(35.303262, -80.743691);

        LatLngBounds mapBounds = new LatLngBounds(unccSW, unccNE);



        GroundOverlayOptions unccMap = new GroundOverlayOptions()
                .image(overlay)
                .positionFromBounds(mapBounds);
        mMap.addGroundOverlay(unccMap);

        setPolygons();

    }

    public void createButtons(){
        goButton = (Button) findViewById(R.id.button7);
        cancelButton = (Button) findViewById(R.id.button5);

        goButton.setText("Go");
        cancelButton.setText("Cancel");

        goButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
    }

    public void followUser(){
        followUser = true;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 17));

    }

}
