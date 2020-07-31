package com.example.newsapplication;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class News_fragment extends Fragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    ProgressBar newsLoadingBar;
    TextView newsLoadingText;

    NewsRecyclerViewAdapter newsRecyclerViewAdapter;
    RecyclerView newsRecyclerViewContainer;

    private static final String TAG = "News_fragment";


    //Variables

    private ArrayList<String> allNewsTexts=new ArrayList<>();
    private ArrayList<String> allNewsImages=new ArrayList<>();
    private ArrayList<String> allNewsTimes=new ArrayList<>();
    private ArrayList<String> allNewsArticleIDs=new ArrayList<>();
    private ArrayList<String> allNewsDate=new ArrayList<>();
    private ArrayList<String> allNewsSection=new ArrayList<>();
    private ArrayList<String> allNewsUrls=new ArrayList<>();

    private JSONArray home_news_card_array=new JSONArray();
    private View view;

    @Override
    public void onResume() {

        super.onResume();
        if(newsRecyclerViewAdapter!=null)
        {
            Log.i("vajramat", "In");
            newsRecyclerViewContainer.setAdapter(newsRecyclerViewAdapter);
            newsRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        view=inflater.inflate(R.layout.news_fragment,container,false);

        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh_items);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                newsLoadingBar.setVisibility(View.VISIBLE);
//                newsLoadingText.setVisibility(View.VISIBLE);

                String home_url="http://svandroidnewsappbackend.wl.r.appspot.com/getMobileHomeGuardianCardNews";
                Log.i("home", "onNavigationItemSelected: home_url"+home_url);
                JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, home_url, null, new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            newsLoadingBar.setVisibility(View.GONE);
                            newsLoadingText.setVisibility(View.GONE);

                            Log.i("home", "onNavigationItemSelected: home_url1"+response.toString());
//                    News_fragment newsFragment = new News_fragment();

                            allNewsTexts.clear();
                            allNewsImages.clear();
                            allNewsArticleIDs.clear();
                            allNewsTimes.clear();
                            allNewsDate.clear();
                            allNewsSection.clear();
                            allNewsUrls.clear();
                            home_news_card_array=new JSONArray();

                            home_news_card_array= response.getJSONArray("newsCards");
                            Log.i("home", "onResponse: "+home_news_card_array.getJSONObject(0).getString("newsTitle"));

                            for(int i=0;i<home_news_card_array.length();i++)
                            {
                                try {

                                    String newsSection=home_news_card_array.getJSONObject(i).getString("newsSection");

                                    LocalDateTime ldt = LocalDateTime.now();

                                    String newsTime=" ago" +" | "+newsSection;
                                    String newsDateTimeString=home_news_card_array.getJSONObject(i).getString("newsDateTime");
                                    ZonedDateTime zonedDateTimeNews= ZonedDateTime.parse(newsDateTimeString);

                                    ZoneId zoneIdLA = ZoneId.of( "America/Los_Angeles" );
                                    ZonedDateTime zonedDateTimeLAnow  = ldt.atZone( zoneIdLA );

                                    ZonedDateTime zonedDateTimeLAnews= zonedDateTimeNews.withZoneSameInstant(zoneIdLA);

                                    Duration durationDifference = Duration.between( zonedDateTimeLAnews , zonedDateTimeLAnow );

                                    Log.i("AGO LA now", zonedDateTimeLAnow.toString());
                                    Log.i("AGO LA news", zonedDateTimeLAnews.toString());
                                    Log.i("AGO LA now-news", String.valueOf(durationDifference.getSeconds()));

                                    if(durationDifference.getSeconds()>=3600)
                                    {
                                        newsTime=String.valueOf(durationDifference.getSeconds()/3600)+"h"+newsTime;
                                    }
                                    else if(durationDifference.getSeconds()>=60)
                                    {
                                        newsTime=String.valueOf(durationDifference.getSeconds()/60)+"m"+newsTime;
                                    }
                                    else
                                    {
                                        newsTime=String.valueOf(durationDifference.getSeconds())+"s"+newsTime;
                                    }



                                    allNewsUrls.add(home_news_card_array.getJSONObject(i).getString("newsUrl"));
                                    allNewsSection.add(newsSection);
                                    allNewsDate.add(newsDateTimeString);
                                    allNewsTimes.add(newsTime);
                                    allNewsArticleIDs.add(home_news_card_array.getJSONObject(i).getString("newsArticleID"));

                                    allNewsImages.add(home_news_card_array.getJSONObject(i).getString("newsImage"));
                                    allNewsTexts.add(home_news_card_array.getJSONObject(i).getString("newsTitle"));
                                }
                                catch (Exception ex)
                                {
                                    Log.i("home","JSONException" +ex.getMessage());
                                }
                            }

                            newsRecyclerViewContainer=view.findViewById(R.id.newsRecyclerViewContainer);
                            newsRecyclerViewAdapter= new NewsRecyclerViewAdapter(allNewsTexts,allNewsImages,allNewsTimes,allNewsArticleIDs,allNewsDate,allNewsSection,allNewsUrls,getActivity());
                            //Set the adapter to the recycler view
                            newsRecyclerViewContainer.setAdapter(newsRecyclerViewAdapter);
                            newsRecyclerViewContainer.setLayoutManager(new LinearLayoutManager(getActivity()));
//                    Log.i("home", "onNavigationItemSelected: home_url2"+home_news_card_array.toString());
//                    Bundle bundle=new Bundle();
//
//                    bundle.putString("home_news_cards", home_news_card_array.toString());
//
//
//                    newsFragment.setArguments(bundle);

//                    Toast.makeText(getApplicationContext(),home_news_card_array.toString(),Toast.LENGTH_SHORT).show();

//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.fragment_container,newsFragment)
//                            .commit();
                        }
                        catch (JSONException e)
                        {
                            Log.i("kk","JSONException");
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
                );
                RequestQueue queue= Volley.newRequestQueue(getActivity());
                queue.add(jsonObjectRequest);

                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
        newsLoadingBar=view.findViewById(R.id.news_loading_bar);
        newsLoadingText=view.findViewById(R.id.news_loading_text);

        newsLoadingBar.setVisibility(View.VISIBLE);
        newsLoadingText.setVisibility(View.VISIBLE);
//
//        newsLoadingBar.setVisibility(View.VISIBLE);
//        newsLoadingText.setVisibility(View.VISIBLE);

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




        String home_url="http://svandroidnewsappbackend.wl.r.appspot.com/getMobileHomeGuardianCardNews";
        Log.i("home", "onNavigationItemSelected: home_url"+home_url);
        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, home_url, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                try
                {
                    newsLoadingBar.setVisibility(View.GONE);
                    newsLoadingText.setVisibility(View.GONE);
                    Log.i("home", "onNavigationItemSelected: home_url1"+response.toString());
//                    News_fragment newsFragment = new News_fragment();

                    allNewsTexts.clear();
                    allNewsImages.clear();
                    allNewsArticleIDs.clear();
                    allNewsTimes.clear();
                    allNewsDate.clear();
                    allNewsSection.clear();
                    allNewsUrls.clear();
                    home_news_card_array=new JSONArray();

                    home_news_card_array= response.getJSONArray("newsCards");
                    Log.i("home", "onResponse: "+home_news_card_array.getJSONObject(0).getString("newsTitle"));

                    for(int i=0;i<home_news_card_array.length();i++)
                    {
                        try {

                            String newsSection=home_news_card_array.getJSONObject(i).getString("newsSection");

                            LocalDateTime ldt = LocalDateTime.now();

                            String newsTime=" ago" +" | "+newsSection;
                            String newsDateTimeString=home_news_card_array.getJSONObject(i).getString("newsDateTime");
                            ZonedDateTime zonedDateTimeNews= ZonedDateTime.parse(newsDateTimeString);

                            ZoneId zoneIdLA = ZoneId.of( "America/Los_Angeles" );
                            ZonedDateTime zonedDateTimeLAnow  = ldt.atZone( zoneIdLA );

                            ZonedDateTime zonedDateTimeLAnews= zonedDateTimeNews.withZoneSameInstant(zoneIdLA);

                            Duration durationDifference = Duration.between( zonedDateTimeLAnews , zonedDateTimeLAnow );

                            Log.i("AGO LA now", zonedDateTimeLAnow.toString());
                            Log.i("AGO LA news", zonedDateTimeLAnews.toString());
                            Log.i("AGO LA now-news", String.valueOf(durationDifference.getSeconds()));

                            if(durationDifference.getSeconds()>=3600)
                            {
                                newsTime=String.valueOf(durationDifference.getSeconds()/3600)+"h"+newsTime;
                            }
                            else if(durationDifference.getSeconds()>=60)
                            {
                                newsTime=String.valueOf(durationDifference.getSeconds()/60)+"m"+newsTime;
                            }
                            else
                            {
                                newsTime=String.valueOf(durationDifference.getSeconds())+"s"+newsTime;
                            }



                            allNewsUrls.add(home_news_card_array.getJSONObject(i).getString("newsUrl"));
                            allNewsSection.add(newsSection);
                            allNewsDate.add(newsDateTimeString);
                            allNewsTimes.add(newsTime);
                            allNewsArticleIDs.add(home_news_card_array.getJSONObject(i).getString("newsArticleID"));

                            allNewsImages.add(home_news_card_array.getJSONObject(i).getString("newsImage"));
                            allNewsTexts.add(home_news_card_array.getJSONObject(i).getString("newsTitle"));
                        }
                        catch (Exception ex)
                        {
                            Log.i("home","JSONException" +ex.getMessage());
                        }
                    }

                    newsRecyclerViewContainer=view.findViewById(R.id.newsRecyclerViewContainer);
                    newsRecyclerViewAdapter= new NewsRecyclerViewAdapter(allNewsTexts,allNewsImages,allNewsTimes,allNewsArticleIDs,allNewsDate,allNewsSection,allNewsUrls,getActivity());
                    //Set the adapter to the recycler view
                    newsRecyclerViewContainer.setAdapter(newsRecyclerViewAdapter);
                    newsRecyclerViewContainer.setLayoutManager(new LinearLayoutManager(getActivity()));
//                    Log.i("home", "onNavigationItemSelected: home_url2"+home_news_card_array.toString());
//                    Bundle bundle=new Bundle();
//
//                    bundle.putString("home_news_cards", home_news_card_array.toString());
//
//
//                    newsFragment.setArguments(bundle);

//                    Toast.makeText(getApplicationContext(),home_news_card_array.toString(),Toast.LENGTH_SHORT).show();

//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.fragment_container,newsFragment)
//                            .commit();
                }
                catch (JSONException e)
                {
                    Log.i("kk","JSONException");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        RequestQueue queue= Volley.newRequestQueue(getActivity());
        queue.add(jsonObjectRequest);






//        String home_news_card_array_string=getArguments().getString("home_news_cards");
//        Log.i(TAG, home_news_card_array_string);
        Log.d(TAG, "onCreateView: has started");

//        initializingNewsCardData(view);

        return view;
    }

//    private void initializingNewsCardData(View view){
//        Log.d(TAG, "initializingNewsCardData: preparing");
//
//        for(int i=0;i<home_news_card_array.length();i++)
//        {
//            try {
//                allNewsTexts.add(home_news_card_array.getJSONObject(i).getString("newsTitle"));
//            }
//             catch (Exception ex)
//            {
//                Log.i("home","JSONException" +ex.getMessage());
//            }
//        }
//
////        allNewsTexts.add("News1");
////
////        allNewsTexts.add("News2");
////
////        allNewsTexts.add("News3");
////
////        allNewsTexts.add("News4");
////
////        allNewsTexts.add("News5");
////
////        allNewsTexts.add("News6");
////
////        allNewsTexts.add("News7");
////
////        allNewsTexts.add("News8");
////
////        allNewsTexts.add("News9");
////
////        allNewsTexts.add("News10");
//
//        initializingNewsRecyclerView(view);
//    }
//
//
//    private void initializingNewsRecyclerView(View view){
//        Log.d(TAG, "initializingNewsRecyclerView: initial rec view");
//
//        RecyclerView newsRecyclerViewContainer=view.findViewById(R.id.newsRecyclerViewContainer);
//        NewsRecyclerViewAdapter newsRecyclerViewAdapter= new NewsRecyclerViewAdapter(allNewsTexts,allNewsImages,allNewsTimes,allNewsArticleIDs,allNewsDate,getActivity());
//        //Set the adapter to the recycler view
//        newsRecyclerViewContainer.setAdapter(newsRecyclerViewAdapter);
//        newsRecyclerViewContainer.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//    }
}
