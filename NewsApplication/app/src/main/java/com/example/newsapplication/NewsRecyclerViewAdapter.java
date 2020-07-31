package com.example.newsapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.NewsViewHolder>{
    private static final String TAG = "NewsRecyclerViewAdapter";

    public static final String sharedPreferencesName="bookmarkData";


    private ArrayList<BookmarkedItem> bookmarkedList;
    private ArrayList<String> allNewsTexts=new ArrayList<>();
    private ArrayList<String> allNewsImages=new ArrayList<>();
    private ArrayList<String> allNewsTimes=new ArrayList<>();
    private ArrayList<String> allNewsArticleIDs=new ArrayList<>();
    private ArrayList<String> allNewsDate=new ArrayList<>();
    private ArrayList<String> allNewsSection=new ArrayList<>();
    private ArrayList<String> allNewsUrls=new ArrayList<>();
    private Context allNewsContext;
    private View newsView;

    public NewsRecyclerViewAdapter(ArrayList<String> allNewsTexts, ArrayList<String> allNewsImages,ArrayList<String> allNewsTimes,ArrayList<String> allNewsArticleIDs,ArrayList<String> allNewsDate,ArrayList<String> allNewsSection,ArrayList<String> allNewsUrls, Context allNewsContext) {
        this.allNewsTexts = allNewsTexts;
        this.allNewsImages = allNewsImages;
        this.allNewsContext = allNewsContext;
        this.allNewsTimes=allNewsTimes;
        this.allNewsArticleIDs=allNewsArticleIDs;
        this.allNewsDate=allNewsDate;
        this.allNewsSection=allNewsSection;
        this.allNewsUrls=allNewsUrls;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Responsible for inflating the view

        //Putting the views in the right positions
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_layout, parent,false);

        newsView=view;
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


//        Toast.makeText(allNewsContext,bookmarkedList.size(),Toast.LENGTH_SHORT).show();
        NewsViewHolder newsViewHolder= new NewsViewHolder(view);


        return newsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final NewsViewHolder holder, final int position) {
        Log.d(TAG,"onBindViewHolder:called");

        if(allNewsImages.get(position).equals(""))
        {
            holder.newsImage.setImageResource(R.drawable.guardian_fallback_logo);
        }
        else
        {
            Glide.with(allNewsContext)
                    .asBitmap()
                    .load(allNewsImages.get(position))
                    .into(holder.newsImage);
        }
        holder.newsText.setText(allNewsTexts.get(position));
        holder.newsTime.setText(allNewsTimes.get(position));
//        holder.newsImage.setImageResource(R.drawable.sunny_weather);

        int flag=-1;
        for(int i=0;i<bookmarkedList.size();i++)
        {
            if(bookmarkedList.get(i).articleId.equals(allNewsArticleIDs.get(position)))
            {
                holder.newsBookmark.setImageResource(R.drawable.ic_bookmark_black_24dp);
                flag=1;
                break;
            }
        }

        if(flag==-1)
        {
            holder.newsBookmark.setImageResource(R.drawable.outline_bookmark_border_24);
        }



        holder.newsItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick: I just clicked on"+ allNewsTexts.get(position));

//                Toast.makeText(allNewsContext,allNewsTexts.get(position),Toast.LENGTH_SHORT).show();

                Intent intent=new Intent(allNewsContext,Main2Activity.class);
                intent.putExtra("detailed_page_articleID",allNewsArticleIDs.get(position));
                intent.putExtra("detailed_page_image",allNewsImages.get(position));
                intent.putExtra("detailed_page_title",allNewsTexts.get(position));
                intent.putExtra("detailed_page_date",allNewsDate.get(position));
                intent.putExtra("detailed_page_section",allNewsSection.get(position));

                allNewsContext.startActivity(intent);
            }
        });

        holder.newsItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                BookmarkedItem longClickedItem= new BookmarkedItem();
                longClickedItem.title=allNewsTexts.get(position);
                longClickedItem.imageUrl=allNewsImages.get(position);
                longClickedItem.articleId=allNewsArticleIDs.get(position);
                longClickedItem.section=allNewsSection.get(position);
                longClickedItem.date=allNewsDate.get(position);
//                longClickedItem.articleUrl=


                // Create custom dialog object
                final Dialog dialog = new Dialog(allNewsContext);
                // Include dialog.xml file
                dialog.setContentView(R.layout.news_dialog_layout);
                // Set dialog title
                dialog.setTitle("Custom Dialog");

                // set values for custom dialog components - text, image and button
                TextView text = (TextView) dialog.findViewById(R.id.dialog_news_title);
                text.setText(allNewsTexts.get(position));
                ImageView image = (ImageView) dialog.findViewById(R.id.dialog_image);

                ImageView bookmarkButton= dialog.findViewById(R.id.dialog_bookmark);
                ImageView shareButton=dialog.findViewById(R.id.dialog_twitter);

                if(allNewsImages.get(position).equals(""))
                {
                    image.setImageResource(R.drawable.guardian_fallback_logo);
                }
                else
                {
                    Glide.with(allNewsContext)
                            .load(allNewsImages.get(position))
                            .into(image);
                }

//                image.setImageResource(R.drawable.image0);

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
                            if(bookmarkedList.get(i).articleId.equals(allNewsArticleIDs.get(position)))
                            {
                                flag=1;
                                removeIndex=i;
                            }
                        }

                        if(flag==-1)
                        {
                            BookmarkedItem newBookmarkedNewsItem=new BookmarkedItem();
                            newBookmarkedNewsItem.articleId=allNewsArticleIDs.get(position);
                            newBookmarkedNewsItem.title=allNewsTexts.get(position);
                            newBookmarkedNewsItem.date=allNewsDate.get(position);
                            newBookmarkedNewsItem.imageUrl=allNewsImages.get(position);
                            newBookmarkedNewsItem.section=allNewsSection.get(position);
                            newBookmarkedNewsItem.articleUrl=allNewsUrls.get(position);

                            String added_news_title=allNewsTexts.get(position);
                            bookmarkedList.add(newBookmarkedNewsItem);

                            SharedPreferences.Editor editor=sharedPreferences.edit();

                            String updatedJson=gson.toJson(bookmarkedList);
                            editor.putString("bookmarkedList",updatedJson);
                            editor.apply();

//                            ImageView bookmarkImage=newsItemView.findViewById(R.id.news_bookmark);

//                            holder.newsBookmark.setImageResource(R.drawable.ic_bookmark_black_24dp);
                            notifyDataSetChanged();
                            ((ImageView)dialog.findViewById(R.id.dialog_bookmark)).setImageResource(R.drawable.bookmarked_red_24dp);
//                    Toast.makeText(allNewsContext,"BOOKMARKED",Toast.LENGTH_SHORT).show();

                            Toast.makeText(allNewsContext,added_news_title+" was added to Bookmarks",Toast.LENGTH_SHORT).show();
                        }

                        else
                        {
                            String removed_news_title=allNewsTexts.get(position);
                            bookmarkedList.remove(removeIndex);
//                    SharedPreferences sharedPreferences= allNewsContext.getSharedPreferences(sharedPreferencesName,Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
//                    Gson gson= new Gson();
                            String updatedJson=gson.toJson(bookmarkedList);
                            editor.putString("bookmarkedList",updatedJson);
                            editor.apply();

//                            ImageView bookmarkImage=newsItemView.findViewById(R.id.news_bookmark);
                            notifyDataSetChanged();
//                            holder.newsBookmark.setImageResource(R.drawable.outline_bookmark_border_24);
                            ((ImageView)dialog.findViewById(R.id.dialog_bookmark)).setImageResource(R.drawable.outline_bookmark_border_24_detailed);
                            Toast.makeText(allNewsContext,removed_news_title+" was removed from Bookmarks",Toast.LENGTH_SHORT).show();
//                    for(int i=0;i<bookmarkedList.size();i++)
//                    {
//                        Toast.makeText(allNewsContext,bookmarkedList.get(i).articleId,Toast.LENGTH_SHORT).show();
//                    }
                        }

                    }

                });

                shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(allNewsContext,"TWITTER",Toast.LENGTH_SHORT).show();
                        String detailed_page_tweet="https://twitter.com/intent/tweet?text="+ Uri.encode("Check out this Link:\n"+allNewsUrls.get(position)+"\n#CSCI571NewsSearch");
                        Uri uri = Uri.parse(detailed_page_tweet);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        allNewsContext.startActivity(intent);
                    }
                });

                dialog.show();

//                NewsDialog newsDialog=new NewsDialog(longClickedItem,newsView,allNewsContext);
////                newsDialog.getDialog().getWindow().setLayout(100,100);
//
//                newsDialog.show(((FragmentActivity)allNewsContext).getSupportFragmentManager(),"News Dialog");

                return false;
            }
        });

        holder.newsBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int flag=-1;
                int removeIndex=-1;
                for(int i=0;i<bookmarkedList.size();i++)
                {
                    if(bookmarkedList.get(i).articleId.equals(allNewsArticleIDs.get(position)))
                    {
                        flag=1;
                        removeIndex=i;
                    }
                }

                if(flag==-1)
                {
                    BookmarkedItem newBookmarkedNewsItem=new BookmarkedItem();
                    newBookmarkedNewsItem.articleId=allNewsArticleIDs.get(position);
                    newBookmarkedNewsItem.title=allNewsTexts.get(position);
                    newBookmarkedNewsItem.date=allNewsDate.get(position);
                    newBookmarkedNewsItem.imageUrl=allNewsImages.get(position);
                    newBookmarkedNewsItem.section=allNewsSection.get(position);
                    newBookmarkedNewsItem.articleUrl=allNewsUrls.get(position);

                    String added_news_title=allNewsTexts.get(position);
                    bookmarkedList.add(newBookmarkedNewsItem);

                    SharedPreferences sharedPreferences= allNewsContext.getSharedPreferences(sharedPreferencesName,Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    Gson gson= new Gson();
                    String updatedJson=gson.toJson(bookmarkedList);
                    editor.putString("bookmarkedList",updatedJson);
                    editor.apply();
                    holder.newsBookmark.setImageResource(R.drawable.ic_bookmark_black_24dp);
//                    Toast.makeText(allNewsContext,"BOOKMARKED",Toast.LENGTH_SHORT).show();

                    Toast.makeText(allNewsContext,added_news_title+" was added to Bookmarks",Toast.LENGTH_SHORT).show();
                }

                else
                {
                    String removed_news_title=allNewsTexts.get(position);
                    bookmarkedList.remove(removeIndex);
                    SharedPreferences sharedPreferences= allNewsContext.getSharedPreferences(sharedPreferencesName,Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    Gson gson= new Gson();
                    String updatedJson=gson.toJson(bookmarkedList);
                    editor.putString("bookmarkedList",updatedJson);
                    editor.apply();

                    holder.newsBookmark.setImageResource(R.drawable.outline_bookmark_border_24);
                    Toast.makeText(allNewsContext,removed_news_title+" was removed from Bookmarks",Toast.LENGTH_SHORT).show();
//                    for(int i=0;i<bookmarkedList.size();i++)
//                    {
//                        Toast.makeText(allNewsContext,bookmarkedList.get(i).articleId,Toast.LENGTH_SHORT).show();
//                    }
                }


//                if(holder.newsBookmark.getDrawable().equals(""))
//                {
//
//                }


//                SharedPreferences sharedPreferences= allNewsContext.getSharedPreferences(sharedPreferencesName,Context.MODE_PRIVATE);
//                Gson gson= new Gson();
//                String json= sharedPreferences.getString("bookmarkedList",null);
//                Type type= new TypeToken<ArrayList<BookmarkedItem>>() {}.getType();
//                bookmarkedList= gson.fromJson(json,type);

            }
        });
    }

    @Override
    public int getItemCount() {
        return allNewsTexts.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder{

        ImageView newsImage;
        ImageView newsBookmark;
        TextView newsText;
        TextView newsTime;
        RelativeLayout newsItemLayout;


        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsImage=itemView.findViewById(R.id.news_image);
            newsText=itemView.findViewById(R.id.news_text);
            newsTime=itemView.findViewById(R.id.news_time);
            newsItemLayout=itemView.findViewById(R.id.news_item_layout);
            newsBookmark=itemView.findViewById(R.id.news_bookmark);
        }
    }
}
