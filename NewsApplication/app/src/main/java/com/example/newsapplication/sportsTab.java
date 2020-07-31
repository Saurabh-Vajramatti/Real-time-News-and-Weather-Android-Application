package com.example.newsapplication;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class sportsTab extends Fragment {

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
    private View view;

    public sportsTab() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {

        super.onResume();
        if(newsRecyclerViewAdapter!=null)
        {
            newsRecyclerViewContainer.setAdapter(newsRecyclerViewAdapter);
            newsRecyclerViewAdapter.notifyDataSetChanged();
        }
    }
    void populate() {
        String section_url="http://svandroidnewsappbackend.wl.r.appspot.com/getMobileSectionGuardianCardNews?section=sport";

        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, section_url, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                try
                {
                    newsLoadingBar.setVisibility(View.GONE);
                    newsLoadingText.setVisibility(View.GONE);
                    Log.i("section", "onNavigationItemSelected: section_url1"+response.toString());
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
                    Log.i("section", "onResponse: "+home_news_card_array.getJSONObject(0).getString("newsTitle"));

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

                    newsRecyclerViewContainer=view.findViewById(R.id.sectionNewsRecyclerViewContainer);
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
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.headlines_tab_recyclerview, container, false);


        newsLoadingBar=view.findViewById(R.id.headlines_news_loading_bar);
        newsLoadingText=view.findViewById(R.id.headlines_news_loading_text);

        mSwipeRefreshLayout = view.findViewById(R.id.headlines_swiperefresh_items);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populate();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        populate();



        return view;
    }

//    private void initializingNewsCardData(View view){
//
//        for(int i=0;i<home_news_card_array.length();i++)
//        {
//            try {
//                allNewsTexts.add(home_news_card_array.getJSONObject(i).getString("newsTitle"));
//            }
//            catch (Exception ex)
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
//    private void initializingNewsRecyclerView(View view){
//
//        RecyclerView newsRecyclerViewContainer=view.findViewById(R.id.sectionNewsRecyclerViewContainer);
//        NewsRecyclerViewAdapter newsRecyclerViewAdapter= new NewsRecyclerViewAdapter(allNewsTexts,allNewsImages,allNewsTimes,allNewsArticleIDs,allNewsDate,getActivity());
//        //Set the adapter to the recycler view
//        newsRecyclerViewContainer.setAdapter(newsRecyclerViewAdapter);
//        newsRecyclerViewContainer.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//    }
}
