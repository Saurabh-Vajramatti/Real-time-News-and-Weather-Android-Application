package com.example.newsapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Weather_fragment weather_fragment;
    private News_fragment news_fragment;

    private ArrayList<String> suggestions=new ArrayList<>();

    SearchView searchView;

    String myCity;
    String myState;
    private int bottom_tab_selected=0;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


//    @Override
//    public void onResume() {
//
//        super.onResume();
//        if(bottom_tab_selected==3)
//        {
//            bottom_tab_selected=3;
//            Bookmarked_fragment bookmarkedFragment=new Bookmarked_fragment();
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container,bookmarkedFragment)
//                    .commit();
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myCity="Los Angeles";
        myState="California";

//        newsLoadingBar=findViewById(R.id.news_loading_bar);
//        newsLoadingText=findViewById(R.id.news_loading_text);



//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowHomeEnabled(true);

        ArrayList<BookmarkedItem> bookmarkedList;

        SharedPreferences sharedPreferences= getSharedPreferences("bookmarkData",Context.MODE_PRIVATE);
//        sharedPreferences.edit().clear().commit();
        Gson gson= new Gson();
        String json= sharedPreferences.getString("bookmarkedList",null);
        Type type= new TypeToken<ArrayList<BookmarkedItem>>() {}.getType();
        bookmarkedList= gson.fromJson(json,type);

        if(bookmarkedList==null){
            bookmarkedList= new ArrayList<>();
            SharedPreferences.Editor editor=sharedPreferences.edit();

            String updatedJson=gson.toJson(bookmarkedList);
            editor.putString("bookmarkedList",updatedJson);
            editor.apply();
        }


//        Toast.makeText(getApplicationContext(),String.valueOf(bookmarkedList.size()),Toast.LENGTH_SHORT).show();


        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(getApplicationContext(),"Hello permission",Toast.LENGTH_SHORT).show();

            // Permission is not granted
            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
//            }
        }
//        else {
            // Permission has already been granted

            try{
                LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                double myLongitude = location.getLongitude();
                double myLatitude = location.getLatitude();

                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(myLatitude, myLongitude, 1);
                myCity = addresses.get(0).getLocality();
                myState = addresses.get(0).getAdminArea();
            }
            catch (Exception e){
//                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();

                Log.i("message",e.toString());
            }

//            String countryName = addresses.get(0).getAddressLine(2);
//        }


        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        String api_key="f0252e2b791e56d60b1fa2eb83d2dd72";
        String weather_url="https://api.openweathermap.org/data/2.5/weather?q="+myCity+"&units=metric&appid="+api_key;

        Log.i("WeatherURL",weather_url);
        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, weather_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try
                {
                    Log.i("MyTemp", "kkkjkjjj");
                    News_fragment newsFragment=new News_fragment();

                    JSONObject response_main_object=response.getJSONObject("main");
                    JSONArray response_weather_array= response.getJSONArray("weather");

                    String temperature=String.valueOf(Math.round(response_main_object.getDouble("temp")));
                    String summary=response_weather_array.getJSONObject(0).getString("main");

                    Bundle bundle=new Bundle();

                    bundle.putString("temperature",temperature);
                    bundle.putString("summary",summary);
                    bundle.putString("city",myCity);
                    bundle.putString("state",myState);
                    newsFragment.setArguments(bundle);

                    Log.i("MyTemp", bundle.getString("temperature"));

//                    Toast.makeText(getApplicationContext(),temperature,Toast.LENGTH_SHORT).show();

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container,newsFragment)
                            .commit();
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
        RequestQueue queue= Volley.newRequestQueue(MainActivity.this);
        queue.add(jsonObjectRequest);

//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                new News_fragment()).commit();


//        String home_url="http://svandroidnewsappbackend.wl.r.appspot.com/getMobileHomeGuardianCardNews";
//        Log.i("home", "onNavigationItemSelected: home_url"+home_url);
//        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, home_url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try
//                {
//                    Log.i("home", "onNavigationItemSelected: home_url1");
//                    News_fragment newsFragment = new News_fragment();
//                    JSONArray home_news_card_array= response.getJSONArray("newsCards");
//
//
//                    Log.i("home", "onNavigationItemSelected: home_url2"+home_news_card_array.toString());
//                    Bundle bundle=new Bundle();
//
//                    bundle.putString("home_news_cards", home_news_card_array.toString());
//
//
//                    newsFragment.setArguments(bundle);
//
//                    Toast.makeText(getApplicationContext(),home_news_card_array.toString(),Toast.LENGTH_SHORT).show();
//
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.fragment_container,newsFragment)
//                            .commit();
//                }
//                catch (JSONException e)
//                {
//                    Log.i("kk","JSONException");
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        }
//        );
//        RequestQueue queue= Volley.newRequestQueue(MainActivity.this);
//        queue.add(jsonObjectRequest);

//        Toast.makeText(getApplicationContext(),"City:"+myCity+" State:"+myState,Toast.LENGTH_SHORT).show();
        //Instances of fragments


//        weather_fragment=new Weather_fragment();
//        news_fragment=new News_fragment();
//
//
//        String api_key="f0252e2b791e56d60b1fa2eb83d2dd72";
//        String weather_url="https://api.openweathermap.org/data/2.5/weather?q="+myCity+"&units=metric&appid="+api_key;
//
//        Log.i("WeatherURL",weather_url);
//        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, weather_url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try
//                {
//
//                    JSONObject response_main_object=response.getJSONObject("main");
//                    JSONArray response_weather_array= response.getJSONArray("weather");
//
//                    String temperature=String.valueOf(Math.round(response_main_object.getDouble("temp")));
//                    String summary=response_weather_array.getJSONObject(0).getString("main");
//
//                    Bundle bundle=new Bundle();
//
//                    bundle.putString("temperature",temperature);
//                    bundle.putString("summary",summary);
//                    bundle.putString("city",myCity);
//                    bundle.putString("state",myState);
//                    weather_fragment.setArguments(bundle);
//
//                    Toast.makeText(getApplicationContext(),temperature,Toast.LENGTH_SHORT).show();
//
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.weather_container,weather_fragment)
//                            .replace(R.id.news_container,news_fragment)
//                            .commit();
//                }
//                catch (JSONException e)
//                {
//                    Log.i("kk","JSONException");
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        }
//        );
//        RequestQueue queue= Volley.newRequestQueue(this);
//        queue.add(jsonObjectRequest);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }




    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId())
                    {
                        case R.id.home_tab:

                            bottom_tab_selected=0;
                            String api_key="f0252e2b791e56d60b1fa2eb83d2dd72";
                            String weather_url="https://api.openweathermap.org/data/2.5/weather?q="+myCity+"&units=metric&appid="+api_key;

                            Log.i("WeatherURL",weather_url);
                            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, weather_url, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try
                                    {
                                        Log.i("MyTemp", "kkkjkjjj");
                                        News_fragment newsFragment=new News_fragment();

                                        JSONObject response_main_object=response.getJSONObject("main");
                                        JSONArray response_weather_array= response.getJSONArray("weather");

                                        String temperature=String.valueOf(Math.round(response_main_object.getDouble("temp")));
                                        String summary=response_weather_array.getJSONObject(0).getString("main");

                                        Bundle bundle=new Bundle();

                                        bundle.putString("temperature",temperature);
                                        bundle.putString("summary",summary);
                                        bundle.putString("city",myCity);
                                        bundle.putString("state",myState);
                                        newsFragment.setArguments(bundle);

                                        Log.i("MyTemp", bundle.getString("temperature"));

//                                        Toast.makeText(getApplicationContext(),temperature,Toast.LENGTH_SHORT).show();

                                        getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.fragment_container,newsFragment)
                                                .commit();
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
                            RequestQueue queue= Volley.newRequestQueue(MainActivity.this);
                            queue.add(jsonObjectRequest);

//                            String home_url="http://svandroidnewsappbackend.wl.r.appspot.com/getMobileHomeGuardianCardNews";
//                            Log.i("home", "onNavigationItemSelected: home_url"+home_url);
//                            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, home_url, null, new Response.Listener<JSONObject>() {
//                                @Override
//                                public void onResponse(JSONObject response) {
//                                    try
//                                    {
//                                        Log.i("home", "onNavigationItemSelected: home_url1");
//                                        News_fragment newsFragment = new News_fragment();
//                                        JSONArray home_news_card_array= response.getJSONArray("newsCards");
//
//
//                                        Log.i("home", "onNavigationItemSelected: home_url2"+home_news_card_array.toString());
//                                        Bundle bundle=new Bundle();
//
//                                        bundle.putString("home_news_cards", home_news_card_array.toString());
//
//
//                                        newsFragment.setArguments(bundle);
//
//                                        Toast.makeText(getApplicationContext(),home_news_card_array.toString(),Toast.LENGTH_SHORT).show();
//
//                                        getSupportFragmentManager().beginTransaction()
//                                                .replace(R.id.fragment_container,newsFragment)
//                                                .commit();
//                                    }
//                                    catch (JSONException e)
//                                    {
//                                        Log.i("kk","JSONException");
//                                    }
//
//                                }
//                            }, new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//
//                                }
//                            }
//                            );
//                            RequestQueue queue= Volley.newRequestQueue(MainActivity.this);
//                            queue.add(jsonObjectRequest);

                            break;
                            
                        case R.id.headline_tab:
                            bottom_tab_selected=1;
                            Headline_fragment headlineFragment=new Headline_fragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container,headlineFragment)
                                    .commit();
                            break;
                            
                        case R.id.trending_tab:
                            bottom_tab_selected=2;
                            Trending_fragment trendingFragment=new Trending_fragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container,trendingFragment)
                                    .commit();
                            
                            break;
                            
                        case R.id.bookmark_tab:
                            bottom_tab_selected=3;
                            Bookmarked_fragment bookmarkedFragment=new Bookmarked_fragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container,bookmarkedFragment)
                                    .commit();
                            break;
                    }

                    return true;
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the search menu action bar.
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.omnipresent_search_menu,menu);

        // Get the search menu.
        MenuItem searchMenu= menu.findItem(R.id.news_search_button);

        // Get SearchView object.
        searchView=(SearchView)
                searchMenu.getActionView();

        // Get SearchView autocomplete object.


        // Create a new ArrayAdapter and add data to search auto complete object.

//        String dataArr[] = {"Apple" , "Amazon" , "Amd", "Microsoft", "Microwave", "MicroNews", "Intel", "Intelligence"};
//        ArrayAdapter<String> newsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, dataArr);
//        searchAutoComplete.setAdapter(newsAdapter);





        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                Toast.makeText(getApplicationContext(),"currentWord",Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(),query,Toast.LENGTH_SHORT).show();
//                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
//                alertDialog.setMessage("Search keyword is " + query);
//                alertDialog.show();

                Intent intent=new Intent(MainActivity.this,NewsSearchResults.class);
                intent.putExtra("currentNewsSearchKeyword",query);
//                intent.putExtra("detailed_page_image",allNewsImages.get(position));
//                intent.putExtra("detailed_page_title",allNewsTexts.get(position));
//                intent.putExtra("detailed_page_date",allNewsDate.get(position));
//                intent.putExtra("detailed_page_section",allNewsSection.get(position));

                MainActivity.this.startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

//                Toast.makeText(getApplicationContext(),"currentWord",Toast.LENGTH_SHORT).show();
                if(newText!=null && newText!="" && newText.length()>=2)
                {
                    String contentUrl="https://saurabh-vajramatti.cognitiveservices.azure.com/bing/v7.0/suggestions?q="+newText;

                    JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, contentUrl, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
//                            Toast.makeText(getApplicationContext(),String.valueOf(response),Toast.LENGTH_SHORT).show();
                            try
                            {
                                final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(androidx.appcompat.R.id.search_src_text);

                                ArrayAdapter<String> newsAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, suggestions);
                                searchAutoComplete.setAdapter(newsAdapter);
                                searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                                        String queryString=(String)adapterView.getItemAtPosition(itemIndex);
                                        searchAutoComplete.setText("" + queryString);
//                                        Toast.makeText(MainActivity.this, "you clicked " + queryString, Toast.LENGTH_LONG).show();
                                    }
                                });

//                                Toast.makeText(getApplicationContext(),"TRY",Toast.LENGTH_SHORT).show();
//                                News_fragment newsFragment=new News_fragment();

//                                JSONObject response_main_object=response.getJSONObject("main");
                                JSONArray response_suggestionGroups_array= response.getJSONArray("suggestionGroups");

                                Log.i("SEARCHsv1", String.valueOf(response_suggestionGroups_array.length()));
//                                String temperature=String.valueOf(Math.round(response_main_object.getDouble("temp")));
                                JSONObject response_suggestionGroups_array_obj0=response_suggestionGroups_array.getJSONObject(0);
                                Log.i("SEARCHsv2", String.valueOf(response_suggestionGroups_array_obj0));
                                JSONArray searchSuggestions_array=response_suggestionGroups_array_obj0.getJSONArray("searchSuggestions");
                                Log.i("SEARCHsv3", String.valueOf(searchSuggestions_array));
//                                Toast.makeText(getApplicationContext(),String.valueOf(searchSuggestions_array.length()),Toast.LENGTH_SHORT).show();
                                int maxLength=5;
//                                Toast.makeText(getApplicationContext(),String.valueOf(searchSuggestions_array.length()),Toast.LENGTH_SHORT).show();
//                                Log.i("SEARCHsv", String.valueOf(searchSuggestions_array.length()));
                                if(searchSuggestions_array.length()<maxLength)
                                {
                                    maxLength=searchSuggestions_array.length();
                                }
                                suggestions.clear();
//                                Toast.makeText(getApplicationContext(),String.valueOf(maxLength),Toast.LENGTH_SHORT).show();
                                for(int i=0;i<maxLength;i++)
                                {
                                    String currentWord=searchSuggestions_array.getJSONObject(i).getString("displayText");
                                    suggestions.add(currentWord);
//                                    Toast.makeText(getApplicationContext(),currentWord,Toast.LENGTH_SHORT).show();
                                }



//                                Bundle bundle=new Bundle();
//
//                                bundle.putString("temperature",temperature);
//                                bundle.putString("summary",summary);
//                                bundle.putString("city",myCity);
//                                bundle.putString("state",myState);
//                                newsFragment.setArguments(bundle);
//
//                                Log.i("MyTemp", bundle.getString("temperature"));
//
////                                        Toast.makeText(getApplicationContext(),temperature,Toast.LENGTH_SHORT).show();
//
//                                getSupportFragmentManager().beginTransaction()
//                                        .replace(R.id.fragment_container,newsFragment)
//                                        .commit();
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
                    ){    //this is the part, that adds the header to the request
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Ocp-Apim-Subscription-Key", "21c8a2f1dd4745e3952ca7b3616fb908");
                            return params;
                        }
                    };
                    RequestQueue queue= Volley.newRequestQueue(MainActivity.this);
                    queue.add(jsonObjectRequest);
                }

                return false;
            }
        });



                // Listen to search view item on click event.
//        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
//                String queryString=(String)adapterView.getItemAtPosition(itemIndex);
//                searchAutoComplete.setText("" + queryString);
//                Toast.makeText(MainActivity.this, "you clicked " + queryString, Toast.LENGTH_LONG).show();
//            }
//        });
//
//        // Below event is triggered when submit search query.
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
//                alertDialog.setMessage("Search keyword is " + query);
//                alertDialog.show();
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });





//        // Get SearchView object.
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenu);
//
//        // Get SearchView autocomplete object.
//        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

        return super.onCreateOptionsMenu(menu);
    }

}