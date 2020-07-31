package com.example.newsapplication;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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

public class NewsSearchResults extends AppCompatActivity {
    private SwipeRefreshLayout mSwipeRefreshLayout;

    ProgressBar newsLoadingBar;
    TextView newsLoadingText;
    NewsRecyclerViewAdapter newsRecyclerViewAdapter;
    RecyclerView newsRecyclerViewContainer;

    private ArrayList<String> allNewsTexts=new ArrayList<>();
    private ArrayList<String> allNewsImages=new ArrayList<>();
    private ArrayList<String> allNewsTimes=new ArrayList<>();
    private ArrayList<String> allNewsArticleIDs=new ArrayList<>();
    private ArrayList<String> allNewsDate=new ArrayList<>();
    private ArrayList<String> allNewsSection=new ArrayList<>();
    private ArrayList<String> allNewsUrls=new ArrayList<>();
    private JSONArray home_news_card_array=new JSONArray();


    @Override
    public void onResume() {

        super.onResume();
        if(newsRecyclerViewAdapter!=null)
        {
//            populate();
//            allNewsTexts.clear();
//            allNewsImages.clear();
//            allNewsArticleIDs.clear();
//            allNewsTimes.clear();
//            allNewsDate.clear();
//            allNewsSection.clear();
//            allNewsUrls.clear();
//            populate();
            newsRecyclerViewContainer.setAdapter(newsRecyclerViewAdapter);
            newsRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    //    private View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.headlines_tab_recyclerview);

        newsLoadingBar=findViewById(R.id.headlines_news_loading_bar);
        newsLoadingText=findViewById(R.id.headlines_news_loading_text);

        mSwipeRefreshLayout = findViewById(R.id.headlines_swiperefresh_items);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Bundle bundle=getIntent().getExtras();

                if(bundle!=null)
                {
                    if(bundle.getString("currentNewsSearchKeyword")!=null){
                        String newsSearchKeyword=bundle.getString("currentNewsSearchKeyword");
                        final String searchUrl="http://svandroidnewsappbackend.wl.r.appspot.com/getMyMobileSearchResults?q="+newsSearchKeyword;

                        getSupportActionBar().setTitle("Search Results for "+newsSearchKeyword);
                        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, searchUrl, null, new Response.Listener<JSONObject>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onResponse(JSONObject response) {
                                try
                                {
                                    Log.i("home", "onNavigationItemSelected: home_url1"+response.toString());
//                    News_fragment newsFragment = new News_fragment();

                                    newsLoadingBar.setVisibility(View.GONE);
                                    newsLoadingText.setVisibility(View.GONE);
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

                                            if(durationDifference.getSeconds()>=86400)
                                            {
                                                newsTime=String.valueOf(durationDifference.getSeconds()/86400)+"d"+newsTime;
                                            }
                                            else if(durationDifference.getSeconds()>=3600)
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

                                    newsRecyclerViewContainer=findViewById(R.id.sectionNewsRecyclerViewContainer);
                                    Log.d("svaj", String.valueOf(response));
                                    newsRecyclerViewAdapter= new NewsRecyclerViewAdapter(allNewsTexts,allNewsImages,allNewsTimes,allNewsArticleIDs,allNewsDate,allNewsSection,allNewsUrls,NewsSearchResults.this);
                                    //Set the adapter to the recycler view
                                    newsRecyclerViewContainer.setAdapter(newsRecyclerViewAdapter);
                                    newsRecyclerViewContainer.setLayoutManager(new LinearLayoutManager(NewsSearchResults.this));
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
                                    Log.i("vajs","ex");
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }
                        );
                        RequestQueue queue= Volley.newRequestQueue(NewsSearchResults.this);
                        queue.add(jsonObjectRequest);

                    }
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });


        Bundle bundle=getIntent().getExtras();

        if(bundle!=null)
        {
            if(bundle.getString("currentNewsSearchKeyword")!=null){
                String newsSearchKeyword=bundle.getString("currentNewsSearchKeyword");
                final String searchUrl="http://svandroidnewsappbackend.wl.r.appspot.com/getMyMobileSearchResults?q="+newsSearchKeyword;

                getSupportActionBar().setTitle("Search Results for "+newsSearchKeyword);
                JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, searchUrl, null, new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            Log.i("home", "onNavigationItemSelected: home_url1"+response.toString());
//                    News_fragment newsFragment = new News_fragment();

                            newsLoadingBar.setVisibility(View.GONE);
                            newsLoadingText.setVisibility(View.GONE);
                            allNewsTexts.clear();
                            allNewsImages.clear();
                            allNewsArticleIDs.clear();
                            allNewsTimes.clear();
                            allNewsDate.clear();
                            allNewsSection.clear();
                            allNewsUrls.clear();
                            home_news_card_array=new JSONArray();

                            home_news_card_array= response.getJSONArray("newsCards");
                            Log.i("sv1", String.valueOf(response));
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

                                    if(durationDifference.getSeconds()>=86400)
                                    {
                                        newsTime=String.valueOf(durationDifference.getSeconds()/86400)+"d"+newsTime;
                                    }
                                    else if(durationDifference.getSeconds()>=3600)
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

                            newsRecyclerViewContainer=findViewById(R.id.sectionNewsRecyclerViewContainer);

                            Log.i("sv1", String.valueOf(response));
                            newsRecyclerViewAdapter= new NewsRecyclerViewAdapter(allNewsTexts,allNewsImages,allNewsTimes,allNewsArticleIDs,allNewsDate,allNewsSection,allNewsUrls,NewsSearchResults.this);
                            //Set the adapter to the recycler view
                            newsRecyclerViewContainer.setAdapter(newsRecyclerViewAdapter);
                            newsRecyclerViewContainer.setLayoutManager(new LinearLayoutManager(NewsSearchResults.this));
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
                RequestQueue queue= Volley.newRequestQueue(NewsSearchResults.this);
                queue.add(jsonObjectRequest);

            }
        }


    }
}
