package com.example.newsapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class NewsDialog extends AppCompatDialogFragment {

    private ImageView bookmarkButton;
    private ImageView shareButton;
    private TextView dialogTitle;
    private ImageView dialogImage;

    private BookmarkedItem longClickedItem;
    private View newsItemView;
    private Context allNewsContext;

    private ArrayList<BookmarkedItem> bookmarkedList;
    public static final String sharedPreferencesName="bookmarkData";

    public NewsDialog(BookmarkedItem longClickedItem, View newsItemView,Context allNewsContext){
        this.longClickedItem=longClickedItem;
        this.newsItemView=newsItemView;
        this.allNewsContext=allNewsContext;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        LayoutInflater inflater= getActivity().getLayoutInflater();
        View view= inflater.inflate(R.layout.news_dialog_layout,null);

        dialogTitle=view.findViewById(R.id.dialog_news_title);
        dialogImage=view.findViewById(R.id.dialog_image);
        bookmarkButton= view.findViewById(R.id.dialog_bookmark);
        shareButton=view.findViewById(R.id.dialog_twitter);

        dialogTitle.setText(longClickedItem.title);

        if(longClickedItem.imageUrl.equals(""))
        {
            dialogImage.setImageResource(R.drawable.guardian_fallback_logo);
        }
        else
        {
            Glide.with(getActivity())
                    .asBitmap()
                    .load(longClickedItem.imageUrl)
                    .into(dialogImage);
        }

        SharedPreferences sharedPreferences= allNewsContext.getSharedPreferences(sharedPreferencesName,Context.MODE_PRIVATE);
        Gson gson= new Gson();
        String json= sharedPreferences.getString("bookmarkedList",null);
        Type type= new TypeToken<ArrayList<BookmarkedItem>>() {}.getType();
        bookmarkedList= gson.fromJson(json,type);
        int flag=-1;
        for(int i=0;i<bookmarkedList.size();i++)
        {
            if(bookmarkedList.get(i).articleId.equals(longClickedItem.articleId))
            {
                bookmarkButton.setImageResource(R.drawable.bookmarked_red_24dp);
                flag=1;
                break;
            }
        }

        if(flag==-1)
        {
            bookmarkButton.setImageResource(R.drawable.outline_bookmark_border_24_detailed);
        }

//        shareButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Toast.makeText(this,"TWITTER",Toast.LENGTH_SHORT).show();
//                String detailed_page_tweet="https://twitter.com/intent/tweet?text="+ Uri.encode("Check out this Link:"+newsArticleUrl+"#CSCI571NewsSearch");
//                Uri uri = Uri.parse(detailed_page_tweet);
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                allNewsContext.startActivity(intent);
//            }
//        });


        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences= allNewsContext.getSharedPreferences(sharedPreferencesName,Context.MODE_PRIVATE);
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
                int removeIndex=-1;
                for(int i=0;i<bookmarkedList.size();i++)
                {
                    if(bookmarkedList.get(i).articleId.equals(longClickedItem.articleId))
                    {
                        flag=1;
                        removeIndex=i;
                    }
                }

                if(flag==-1)
                {
                    BookmarkedItem newBookmarkedNewsItem=new BookmarkedItem();
                    newBookmarkedNewsItem.articleId=longClickedItem.articleId;
                    newBookmarkedNewsItem.title=longClickedItem.title;
                    newBookmarkedNewsItem.date=longClickedItem.date;
                    newBookmarkedNewsItem.imageUrl=longClickedItem.imageUrl;
                    newBookmarkedNewsItem.section=longClickedItem.section;

                    String added_news_title=longClickedItem.title;
                    bookmarkedList.add(newBookmarkedNewsItem);

                    SharedPreferences.Editor editor=sharedPreferences.edit();

                    String updatedJson=gson.toJson(bookmarkedList);
                    editor.putString("bookmarkedList",updatedJson);
                    editor.apply();

                    ImageView bookmarkImage=newsItemView.findViewById(R.id.news_bookmark);
                    bookmarkImage.setImageResource(R.drawable.ic_bookmark_black_24dp);
                    bookmarkButton.setImageResource(R.drawable.bookmarked_red_24dp);
//                    Toast.makeText(allNewsContext,"BOOKMARKED",Toast.LENGTH_SHORT).show();

                    Toast.makeText(allNewsContext,added_news_title+" was added to Bookmarks",Toast.LENGTH_SHORT).show();
                }

                else
                {
                    String removed_news_title=longClickedItem.title;
                    bookmarkedList.remove(removeIndex);
//                    SharedPreferences sharedPreferences= allNewsContext.getSharedPreferences(sharedPreferencesName,Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
//                    Gson gson= new Gson();
                    String updatedJson=gson.toJson(bookmarkedList);
                    editor.putString("bookmarkedList",updatedJson);
                    editor.apply();

                    ImageView bookmarkImage=newsItemView.findViewById(R.id.news_bookmark);
                    bookmarkImage.setImageResource(R.drawable.outline_bookmark_border_24);
                    bookmarkButton.setImageResource(R.drawable.outline_bookmark_border_24_detailed);
                    Toast.makeText(allNewsContext,removed_news_title+" was removed from Bookmarks",Toast.LENGTH_SHORT).show();
//                    for(int i=0;i<bookmarkedList.size();i++)
//                    {
//                        Toast.makeText(allNewsContext,bookmarkedList.get(i).articleId,Toast.LENGTH_SHORT).show();
//                    }
                }

            }

        });
        builder.setView(view);



        return builder.create();
    }

}
