package com.example.newsapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    ProgressBar newsLoadingBar;
    TextView newsLoadingText;

    String newsArticleUrl;
    String newsArticleID;

    String newsCardImageUrl;
    String newsCardTitle;
    String newsCardDate;
    String newsCardSection;


    String detailedNewsTitle;
    String detailedNewsImageUrl;
    String detailedNewsDateTime;
    String detailedNewsSection;
    String detailedNewsDescription;
    String detailedNewsArticleUrl;


    public static final String sharedPreferencesName="bookmarkData";
    private ArrayList<BookmarkedItem> bookmarkedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        newsLoadingBar=findViewById(R.id.headlines_news_loading_bar);
        newsLoadingText=findViewById(R.id.headlines_news_loading_text);

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null)
        {
            if(bundle.getString("detailed_page_articleID")!=null){


                newsCardTitle=bundle.getString("detailed_page_title");
                newsCardDate=bundle.getString("detailed_page_date");
                newsCardImageUrl=bundle.getString("detailed_page_image");
                newsCardSection=bundle.getString("detailed_page_section");
//                Toast.makeText(getApplicationContext(),bundle.getString("detailed_page_Image"),Toast.LENGTH_SHORT).show();
                newsArticleID=bundle.getString("detailed_page_articleID");
                final String detailed_article_url="http://svandroidnewsappbackend.wl.r.appspot.com/getMobileGuardianDetailedPage?articleid="+bundle.getString("detailed_page_articleID");
                JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, detailed_article_url, null, new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            newsLoadingBar.setVisibility(View.GONE);
                            newsLoadingText.setVisibility(View.GONE);
                            detailedNewsTitle=response.getString("newsTitle");
                            detailedNewsImageUrl=response.getString("newsImage");
                            detailedNewsDateTime=response.getString("newsDateTime");
                            detailedNewsSection=response.getString("newsSection");
                            detailedNewsDescription=response.getString("newsDescription");
                            detailedNewsArticleUrl=response.getString("newsArticleUrl");

                            newsArticleUrl=response.getString("newsArticleUrl");

                            getSupportActionBar().setTitle(detailedNewsTitle);


                            ZonedDateTime zonedDateTimeNews= ZonedDateTime.parse(detailedNewsDateTime);
                            ZoneId zoneIdLA = ZoneId.of( "America/Los_Angeles" );
                            ZonedDateTime zonedDateTimeLAnews= zonedDateTimeNews.withZoneSameInstant(zoneIdLA);

                            DateTimeFormatter detailedDateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy");

//                            Toast.makeText(getApplicationContext(),detailedDateFormat.format(zonedDateTimeLAnews),Toast.LENGTH_SHORT).show();


                            if(detailedNewsImageUrl.equals(""))
                            {
                                ((ImageView) findViewById(R.id.detailed_news_image)).setImageResource(R.drawable.guardian_fallback_logo);
                            }
                            else
                            {
                                Glide.with(Main2Activity.this)
                                        .asBitmap()
                                        .load(detailedNewsImageUrl)
                                        .into((ImageView) findViewById(R.id.detailed_news_image));
                            }

                            ((TextView)findViewById(R.id.detailed_news_title)).setText(detailedNewsTitle);
                            ((TextView)findViewById(R.id.detailed_news_section)).setText(detailedNewsSection);
                            ((TextView)findViewById(R.id.detailed_news_time)).setText(detailedDateFormat.format(zonedDateTimeLAnews));
                            ((TextView)findViewById(R.id.detailed_news_description)).setText(Html.fromHtml(detailedNewsDescription, HtmlCompat.FROM_HTML_MODE_LEGACY));

                            String newsLink = "<a style=\"color:blue;\" href=\""+detailedNewsArticleUrl+"\">View Full Article</a>";


                            ((TextView)findViewById(R.id.detailed_news_link)).setText(Html.fromHtml(newsLink, HtmlCompat.FROM_HTML_MODE_LEGACY));
                            ((TextView)findViewById(R.id.detailed_news_link)).setMovementMethod(LinkMovementMethod.getInstance());

//                            Toast.makeText(getApplicationContext(),detailedNewsTitle,Toast.LENGTH_SHORT).show();
                            Log.i("detailed page", detailedNewsTitle);
                        }
                        catch (Exception e)
                        {
                            Log.i("Detailed page data error",e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
                );
                RequestQueue queue= Volley.newRequestQueue(Main2Activity.this);
                queue.add(jsonObjectRequest);
            }
        }
    }

    public boolean onSupportNavigateUp(){
        finish();
        return  true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailed_news_menu,menu);

        SharedPreferences sharedPreferences= Main2Activity.this.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
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
        int flag=-1;
        for(int i=0;i<bookmarkedList.size();i++)
        {
            if(bookmarkedList.get(i).articleId.equals(newsArticleID))
            {
                menu.findItem(R.id.detailed_bookmark).setIcon(R.drawable.bookmarked_red_24dp);
                flag=1;
                break;
            }
        }

        if(flag==-1)
        {
            menu.findItem(R.id.detailed_bookmark).setIcon(R.drawable.outline_bookmark_border_24_detailed);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();

        if(id==R.id.detailed_twitter)
        {
//            Toast.makeText(this,"TWITTER",Toast.LENGTH_SHORT).show();
            String detailed_page_tweet="https://twitter.com/intent/tweet?text="+ Uri.encode("Check out this Link:\n"+detailedNewsArticleUrl+"\n#CSCI571NewsSearch");
            Uri uri = Uri.parse(detailed_page_tweet);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            this.startActivity(intent);
        }
        else if(id==R.id.detailed_bookmark)
        {
//            Toast.makeText(this,"BOOKMARK clicked",Toast.LENGTH_SHORT).show();

            SharedPreferences sharedPreferences= Main2Activity.this.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
            Gson gson= new Gson();
            String json= sharedPreferences.getString("bookmarkedList",null);
            Type type= new TypeToken<ArrayList<BookmarkedItem>>() {}.getType();
            bookmarkedList= gson.fromJson(json,type);

            int flag=-1;
            int removeIndex=-1;
            for(int i=0;i<bookmarkedList.size();i++)
            {
                if(bookmarkedList.get(i).articleId.equals(newsArticleID))
                {
                    flag=1;
                    removeIndex=i;
                }
            }

            if(flag==-1)
            {
                BookmarkedItem newBookmarkedNewsItem=new BookmarkedItem();
                newBookmarkedNewsItem.articleId=newsArticleID;
                newBookmarkedNewsItem.title=newsCardTitle;
                newBookmarkedNewsItem.date=newsCardDate;
                newBookmarkedNewsItem.imageUrl=newsCardImageUrl;
                newBookmarkedNewsItem.section=newsCardSection;

                String added_news_title=newsCardTitle;
                bookmarkedList.add(newBookmarkedNewsItem);

//                SharedPreferences sharedPreferences= allNewsContext.getSharedPreferences(sharedPreferencesName,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
//                Gson gson= new Gson();
                String updatedJson=gson.toJson(bookmarkedList);
                editor.putString("bookmarkedList",updatedJson);
                editor.apply();
                item.setIcon(R.drawable.bookmarked_red_24dp);
//                    Toast.makeText(allNewsContext,"BOOKMARKED",Toast.LENGTH_SHORT).show();

                Toast.makeText(Main2Activity.this,added_news_title+" was added to Bookmarks",Toast.LENGTH_SHORT).show();
            }

            else
            {
                String removed_news_title=detailedNewsTitle;
                bookmarkedList.remove(removeIndex);
//                SharedPreferences sharedPreferences= allNewsContext.getSharedPreferences(sharedPreferencesName,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
//                Gson gson= new Gson();
                String updatedJson=gson.toJson(bookmarkedList);
                editor.putString("bookmarkedList",updatedJson);
                editor.apply();

                item.setIcon(R.drawable.outline_bookmark_border_24_detailed);
                Toast.makeText(Main2Activity.this,removed_news_title+" was removed from Bookmarks",Toast.LENGTH_SHORT).show();
//                    for(int i=0;i<bookmarkedList.size();i++)
//                    {
//                        Toast.makeText(allNewsContext,bookmarkedList.get(i).articleId,Toast.LENGTH_SHORT).show();
//                    }
            }
        }
        return super.onOptionsItemSelected(item);
    }
}