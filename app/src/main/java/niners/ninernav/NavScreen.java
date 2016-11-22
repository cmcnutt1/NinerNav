package niners.ninernav;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

public class NavScreen extends AppCompatActivity {

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
            "WOODW (Woodward)",
            "My Location"

    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_screen);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        final InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);


        final AutoCompleteTextView fromEntry = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView2);
        final AutoCompleteTextView toEntry = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView3);
        //To allow buildings to be referenced by auto complete text view
        places=new ArrayAdapter<String>(this,android.R.layout.select_dialog_item,buildingNames);

        fromEntry.setAdapter(places);
        fromEntry.setThreshold(1);

        fromEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromEntry.setText("");
            }
        });

        toEntry.setAdapter(places);
        toEntry.setThreshold(1);

        toEntry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toEntry.setText("");
            }
        });





        fromEntry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                imm.hideSoftInputFromWindow(fromEntry.getWindowToken(), 0);
            }
        });

        toEntry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                imm.hideSoftInputFromWindow(toEntry.getWindowToken(), 0);
            }
        });

        final Button go = (Button) findViewById(R.id.button6);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                i.putExtra("from", fromEntry.getText().toString());
                i.putExtra("to", toEntry.getText().toString());
                i.putExtra("mode", true);
                i.putExtra("calledActivity", 2);

                startActivity(i);
            }
        });
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("NavScreen Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());


    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();


    }


}
