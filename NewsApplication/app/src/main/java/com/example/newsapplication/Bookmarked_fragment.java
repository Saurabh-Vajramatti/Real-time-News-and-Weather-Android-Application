package com.example.newsapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Bookmarked_fragment extends Fragment {

//    private ArrayList<String> allNewsTexts=new ArrayList<>();
//    private ArrayList<String> allNewsImages=new ArrayList<>();
//    private ArrayList<String> allNewsTimes=new ArrayList<>();
//    private ArrayList<String> allNewsArticleIDs=new ArrayList<>();
//    private ArrayList<String> allNewsDate=new ArrayList<>();
//    private JSONArray home_news_card_array=new JSONArray();

    private ArrayList<BookmarkedItem> allBookmarkedNewsItems=new ArrayList<>();
    public static final String sharedPreferencesName="bookmarkData";

    RecyclerView newsRecyclerViewContainer;
    BookmarkedNewsRecyclerViewAdapter newsRecyclerViewAdapter;

    TextView noContent;
    //RecyclerView bookmarkRecycle;





    @Override
    public void onResume() {

        super.onResume();
        if(newsRecyclerViewAdapter!=null)
        {
            //allBookmarkedNewsItems.removeAll(allBookmarkedNewsItems);

            allBookmarkedNewsItems.clear();
            SharedPreferences sharedPreferences= getActivity().getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
            Gson gson= new Gson();
            String json= sharedPreferences.getString("bookmarkedList",null);
            Type type= new TypeToken<ArrayList<BookmarkedItem>>() {}.getType();
            allBookmarkedNewsItems= gson.fromJson(json,type);

//            ArrayList<BookmarkedItem> list = new ArrayList<BookmarkedItem>();
//            for(BookmarkedItem bk : allBookmarkedNewsItems){
//                list.add(bk);
//            }
//            allBookmarkedNewsItems = list;
//            allBookmarkedNewsItems.remove(allBookmarkedNewsItems.size()-1);
           // allBookmarkedNewsItems = new ArrayList<>();
            //allBookmarkedNewsItems = gson.fromJson(json,type);
            Log.i("sv3", String.valueOf(allBookmarkedNewsItems));

            if(allBookmarkedNewsItems.isEmpty()){
                noContent.setVisibility(View.VISIBLE);
                newsRecyclerViewContainer.setVisibility(View.GONE);
//                allBookmarkedNewsItems= new ArrayList<>();
//                SharedPreferences.Editor editor=sharedPreferences.edit();
//
//                String updatedJson=gson.toJson(allBookmarkedNewsItems);
//                editor.putString("bookmarkedList",updatedJson);
//                editor.apply();
            }
            else if(allBookmarkedNewsItems.size()==0)
            {
                noContent.setVisibility(View.VISIBLE);
                newsRecyclerViewContainer.setVisibility(View.GONE);
            }
            else
            {
                noContent.setVisibility(View.GONE);
                newsRecyclerViewContainer.setVisibility(View.VISIBLE);
            }

            Log.i("vaj8", String.valueOf(allBookmarkedNewsItems.size()));


            newsRecyclerViewAdapter= new BookmarkedNewsRecyclerViewAdapter(allBookmarkedNewsItems,noContent,newsRecyclerViewContainer,getActivity());
            newsRecyclerViewContainer.setAdapter(newsRecyclerViewAdapter);
            newsRecyclerViewAdapter.notifyDataSetChanged();
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        View view=inflater.inflate(R.layout.bookmarked_fragment,container,false);
        //bookmarkRecycle=view.findViewById(R.id.bookmarkedNewsRecyclerViewContainer);
        noContent=view.findViewById(R.id.no_bookmarks_text);

        SharedPreferences sharedPreferences= getActivity().getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        Gson gson= new Gson();
        String json= sharedPreferences.getString("bookmarkedList",null);
        Type type= new TypeToken<ArrayList<BookmarkedItem>>() {}.getType();
        allBookmarkedNewsItems= gson.fromJson(json,type);

        if(allBookmarkedNewsItems==null){
            view.findViewById(R.id.no_bookmarks_text).setVisibility(View.VISIBLE);
            view.findViewById(R.id.bookmarkedNewsRecyclerViewContainer).setVisibility(View.GONE);
            allBookmarkedNewsItems= new ArrayList<>();
            SharedPreferences.Editor editor=sharedPreferences.edit();

            String updatedJson=gson.toJson(allBookmarkedNewsItems);
            editor.putString("bookmarkedList",updatedJson);
            editor.commit();
        }
        else if(allBookmarkedNewsItems.size()==0)
        {
            view.findViewById(R.id.no_bookmarks_text).setVisibility(View.VISIBLE);
            view.findViewById(R.id.bookmarkedNewsRecyclerViewContainer).setVisibility(View.GONE);
        }
        else
        {
            view.findViewById(R.id.no_bookmarks_text).setVisibility(View.GONE);
            view.findViewById(R.id.bookmarkedNewsRecyclerViewContainer).setVisibility(View.VISIBLE);
        }


        Log.i("vaj82", String.valueOf(allBookmarkedNewsItems.size()));
        newsRecyclerViewContainer=view.findViewById(R.id.bookmarkedNewsRecyclerViewContainer);
        newsRecyclerViewAdapter= new BookmarkedNewsRecyclerViewAdapter(allBookmarkedNewsItems,(TextView) view.findViewById(R.id.no_bookmarks_text),(RecyclerView) view.findViewById(R.id.bookmarkedNewsRecyclerViewContainer),getActivity());

        newsRecyclerViewContainer.setAdapter(newsRecyclerViewAdapter);
        newsRecyclerViewContainer.setLayoutManager(new GridLayoutManager(getActivity(),2));
        return view;
    }
}
