package com.londonappbrewery.climapm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ChangeCityController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_city_layout);
        Log.d("Clima","Inside ChangeCity Controller activity !");
        final EditText newCity = findViewById(R.id.queryET);
        final ImageButton backButton = findViewById(R.id.backButton);

        newCity.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String intentCity = newCity.getText().toString();
                Intent myIntent =new Intent(ChangeCityController.this,WeatherController.class);
                myIntent.putExtra("City",intentCity);
                startActivity(myIntent);
                return false;
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Clima","2nd activity dropped from phone memory !");
                finish();
            }
        });
    }


}