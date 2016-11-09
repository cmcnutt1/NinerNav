package niners.ninernav;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);


        //Button Listener for Navigation Button
        ImageButton navButton = (ImageButton)findViewById(R.id.mapButton);
        assert navButton != null;
        navButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(i);
            }
        });

        //Button Listener for Settings Button
        ImageButton settingsButton = (ImageButton)findViewById(R.id.navButton);
        assert settingsButton != null;
        settingsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent j = new Intent(getApplicationContext(), NavScreen.class);
                startActivity(j);
            }
        });
    }


}
