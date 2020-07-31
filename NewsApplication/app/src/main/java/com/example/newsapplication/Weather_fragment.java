package com.example.newsapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

public class Weather_fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.weather_fragment,container,false);

        String temperature_string=getArguments().getString("temperature");
        String summary_string=getArguments().getString("summary");
        String city_string=getArguments().getString("city");
        String state_string=getArguments().getString("state");


        CardView weatherCard=view.findViewById(R.id.weatherCard);
        ImageView weatherImage=view.findViewById(R.id.weatherImage);
        TextView weatherTemperature=view.findViewById(R.id.weatherTemperature);
        weatherTemperature.setText(temperature_string+" "+"\u2103");

        TextView weatherSummary=view.findViewById(R.id.weatherSummary);
        weatherSummary.setText(summary_string);

        TextView weatherCity=view.findViewById(R.id.weatherCity);
        weatherCity.setText(city_string);

        TextView weatherState=view.findViewById(R.id.weatherState);
        weatherState.setText(state_string);

        if(summary_string.toLowerCase().equals("clouds"))
        {
            Log.i("cloud","cloud");
            weatherImage.setImageResource(R.drawable.cloudy_weather);
        }
        else if(summary_string.toLowerCase().equals("clear"))
        {
            Log.i("clear","clear");
            weatherImage.setImageResource(R.drawable.clear_weather);
        }
        else if(summary_string.toLowerCase().equals("snow"))
        {
            Log.i("snow","snow");
            weatherImage.setImageResource(R.drawable.snowy_weather);
        }
        else if(summary_string.toLowerCase().equals("rain") || summary_string.toLowerCase().equals("drizzle"))
        {
            Log.i("rain/drizzle","rain/drizzle");
            weatherImage.setImageResource(R.drawable.rainy_weather);
        }
        else if(summary_string.toLowerCase().equals("thunderstorm"))
        {
            Log.i("thunderstorm","thunderstorm");
            weatherImage.setImageResource(R.drawable.thunder_weather);
        }
        else
        {
            Log.i("defaultOthers","defaultOthers");
            weatherImage.setImageResource(R.drawable.sunny_weather);
        }


        return view;
    }
}