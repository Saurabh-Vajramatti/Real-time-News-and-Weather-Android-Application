package com.example.newsapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class BookmarkedNewsRecyclerViewAdapter extends RecyclerView.Adapter<BookmarkedNewsRecyclerViewAdapter.BookmarkedNewsViewHolder> {

    private ArrayList<BookmarkedItem> allBookmarkedNewsItems;
    private Context allNewsContext;
    public static final String sharedPreferencesName="bookmarkData";
    private TextView noBookmarkedTextview;
    private RecyclerView bookmarkRecyclerView;

    public BookmarkedNewsRecyclerViewAdapter(ArrayList<BookmarkedItem> allBookmarkedNewsItems,TextView noBookmarkedTextview, RecyclerView bookmarkRecyclerView,Context allNewsContext) {
        this.allBookmarkedNewsItems=allBookmarkedNewsItems;
        this.allNewsContext = allNewsContext;
        this.noBookmarkedTextview=noBookmarkedTextview;
        this.bookmarkRecyclerView=bookmarkRecyclerView;
    }


    @NonNull
    @Override
    public BookmarkedNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Responsible for inflating the view

        //Putting the views in the right positions
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmarked_news_item_layout, parent,false);
//        Toast.makeText(allNewsContext,bookmarkedList.size(),Toast.LENGTH_SHORT).show();
        BookmarkedNewsViewHolder newsViewHolder= new BookmarkedNewsViewHolder(view);


        return newsViewHolder;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final BookmarkedNewsViewHolder holder, final int position) {

//        Log.i("vaj83", String.valueOf(allBookmarkedNewsItems.size()));

        if(allBookmarkedNewsItems.get(position).imageUrl.equals(""))
        {
            holder.newsImage.setImageResource(R.drawable.guardian_fallback_logo);
        }
        else
        {
            Glide.with(allNewsContext)
                    .asBitmap()
                    .load(allBookmarkedNewsItems.get(position).imageUrl)
                    .into(holder.newsImage);
        }
        holder.newsText.setText(allBookmarkedNewsItems.get(position).title);

        ZonedDateTime zonedDateTimeNews= ZonedDateTime.parse(allBookmarkedNewsItems.get(position).date);
        ZoneId zoneIdLA = ZoneId.of( "America/Los_Angeles" );
        ZonedDateTime zonedDateTimeLAnews= zonedDateTimeNews.withZoneSameInstant(zoneIdLA);

        DateTimeFormatter detailedDateFormat = DateTimeFormatter.ofPattern("dd MMM");

        String newsDate=detailedDateFormat.format(zonedDateTimeLAnews);

        holder.newsDateSection.setText(newsDate+" | "+allBookmarkedNewsItems.get(position).section);


        holder.newsItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(allNewsContext,Main2Activity.class);
                intent.putExtra("detailed_page_articleID",allBookmarkedNewsItems.get(position).articleId);
                intent.putExtra("detailed_page_title",allBookmarkedNewsItems.get(position).title);
                intent.putExtra("detailed_page_section",allBookmarkedNewsItems.get(position).section);
                intent.putExtra("detailed_page_image",allBookmarkedNewsItems.get(position).imageUrl);
                intent.putExtra("detailed_page_date",allBookmarkedNewsItems.get(position).date);
                allNewsContext.startActivity(intent);
            }
        });

        holder.newsBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences= allNewsContext.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
                Gson gson= new Gson();
                String json= sharedPreferences.getString("bookmarkedList",null);
                Type type= new TypeToken<ArrayList<BookmarkedItem>>() {}.getType();

                ArrayList<BookmarkedItem> allNewBookmarkedNewsItems=new ArrayList<>();
                allNewBookmarkedNewsItems= gson.fromJson(json,type);

                String removed_news_title=allBookmarkedNewsItems.get(position).title;


//                int flag=-1;
//                int removeIndex=-1;
//                for(int i=0;i<allNewBookmarkedNewsItems.size();i++)
//                {
//                    if(allNewBookmarkedNewsItems.get(i).articleId.equals(allBookmarkedNewsItems.get(position).articleId))
//                    {
//                        flag=1;
//                        removeIndex=i;
//                    }
//                }

//                if(flag==-1)
//                {
//                    BookmarkedItem newBookmarkedNewsItem=new BookmarkedItem();
//                    newBookmarkedNewsItem.articleId=allNewsArticleIDs.get(position);
//                    newBookmarkedNewsItem.title=allNewsTexts.get(position);
//                    newBookmarkedNewsItem.date=allNewsDate.get(position);
//                    newBookmarkedNewsItem.imageUrl=allNewsImages.get(position);
//                    newBookmarkedNewsItem.section=allNewsSection.get(position);
//                    bookmarkedList.add(newBookmarkedNewsItem);
//
//                    SharedPreferences sharedPreferences= allNewsContext.getSharedPreferences(sharedPreferencesName,Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor=sharedPreferences.edit();
//                    Gson gson= new Gson();
//                    String updatedJson=gson.toJson(bookmarkedList);
//                    editor.putString("bookmarkedList",updatedJson);
//                    editor.apply();
//                    holder.newsBookmark.setImageResource(R.drawable.ic_bookmark_black_24dp);
//                    Toast.makeText(allNewsContext,"BOOKMARKED",Toast.LENGTH_SHORT).show();
//                }
//

                allNewBookmarkedNewsItems.remove(position);

                String updatedJson=gson.toJson(allNewBookmarkedNewsItems);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("bookmarkedList",updatedJson);
                editor.commit();

                allBookmarkedNewsItems.remove(position);
                if(allBookmarkedNewsItems.isEmpty())
                {
                    bookmarkRecyclerView.setVisibility(View.GONE);
                    noBookmarkedTextview.setVisibility(View.VISIBLE);
                }
                else
                {
                    bookmarkRecyclerView.setVisibility(View.VISIBLE);
                    noBookmarkedTextview.setVisibility(View.GONE);
                }
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, allNewBookmarkedNewsItems.size());
                Toast.makeText(allNewsContext,removed_news_title+" was removed from Bookmarks",Toast.LENGTH_SHORT).show();

//                allBookmarkedNewsItems.remove(removeIndex);
//                RecyclerView.Adapter theAdapter= new BookmarkedNewsRecyclerViewAdapter(allBookmarkedNewsItems,allNewsContext);
//                theAdapter.notifyItemRemoved(removeIndex);




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

        holder.newsItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final BookmarkedItem longClickedItem= new BookmarkedItem();
                longClickedItem.title=allBookmarkedNewsItems.get(position).title;
                longClickedItem.imageUrl=allBookmarkedNewsItems.get(position).imageUrl;
                longClickedItem.articleId=allBookmarkedNewsItems.get(position).articleId;
                longClickedItem.section=allBookmarkedNewsItems.get(position).section;
                longClickedItem.date=allBookmarkedNewsItems.get(position).date;
                longClickedItem.articleUrl=allBookmarkedNewsItems.get(position).articleUrl;
//                longClickedItem.articleUrl=

                Log.i("vaj83", String.valueOf(allBookmarkedNewsItems.size()));
                // Create custom dialog object
                final Dialog dialog = new Dialog(allNewsContext);
                // Include dialog.xml file
                dialog.setContentView(R.layout.news_dialog_layout);
                // Set dialog title
                dialog.setTitle("Custom Dialog");

                // set values for custom dialog components - text, image and button
                TextView text = (TextView) dialog.findViewById(R.id.dialog_news_title);
                text.setText(longClickedItem.title);
                ImageView image = (ImageView) dialog.findViewById(R.id.dialog_image);

                ImageView bookmarkButton= dialog.findViewById(R.id.dialog_bookmark);
                ImageView shareButton=dialog.findViewById(R.id.dialog_twitter);

                if(longClickedItem.imageUrl.equals(""))
                {
                    image.setImageResource(R.drawable.guardian_fallback_logo);
                }
                else
                {
                    Glide.with(allNewsContext)
                            .asBitmap()
                            .load(longClickedItem.imageUrl)
                            .into(image);
                }

//                image.setImageResource(R.drawable.image0);

//                SharedPreferences sharedPreferences= allNewsContext.getSharedPreferences(sharedPreferencesName,Context.MODE_PRIVATE);
//                Gson gson= new Gson();
//                String json= sharedPreferences.getString("bookmarkedList",null);
//                Type type= new TypeToken<ArrayList<BookmarkedItem>>() {}.getType();
//
//                ArrayList<BookmarkedItem> allNewBookmarkedNewsItems=new ArrayList<>();
//                allNewBookmarkedNewsItems= gson.fromJson(json,type);

                bookmarkButton.setImageResource(R.drawable.bookmarked_red_24dp);
//                int flag=-1;
//                for(int i=0;i<allNewBookmarkedNewsItems.size();i++)
//                {
//                    if(allNewBookmarkedNewsItems.get(i).articleId.equals(allBookmarkedNewsItems.get(position).articleId))
//                    {
//                        bookmarkButton.setImageResource(R.drawable.bookmarked_red_24dp);
//                        flag=1;
//                        break;
//                    }
//                }
//
//                if(flag==-1)
//                {
//                    bookmarkButton.setImageResource(R.drawable.outline_bookmark_border_24_detailed);
//                }

                bookmarkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                        Log.i("vaj85", String.valueOf(allBookmarkedNewsItems.size()));

                        SharedPreferences sharedPreferences= allNewsContext.getSharedPreferences(sharedPreferencesName,Context.MODE_PRIVATE);
                        Gson gson= new Gson();
                        String json= sharedPreferences.getString("bookmarkedList",null);
                        Type type= new TypeToken<ArrayList<BookmarkedItem>>() {}.getType();

                        ArrayList<BookmarkedItem> allNewBookmarkedNewsItems=new ArrayList<>();
                        allNewBookmarkedNewsItems= gson.fromJson(json,type);

                        if(allNewBookmarkedNewsItems==null){
                            allNewBookmarkedNewsItems= new ArrayList<>();
                            SharedPreferences.Editor editor=sharedPreferences.edit();

                            String updatedJson=gson.toJson(allNewBookmarkedNewsItems);
                            editor.putString("bookmarkedList",updatedJson);
                            editor.commit();
                        }

                        if(allBookmarkedNewsItems.size()==0)
                        {
                            allBookmarkedNewsItems=gson.fromJson(json,type);
                        }
                        Log.i("vaj99", String.valueOf(allBookmarkedNewsItems.size()));

                        int flag=-1;
                        int removeIndex=-1;
                        for(int i=0;i<allNewBookmarkedNewsItems.size();i++)
                        {
                            if(allNewBookmarkedNewsItems.get(i).articleId.equals(longClickedItem.articleId))
                            {
                                flag=1;
                                removeIndex=i;
                            }
                        }

//                        if(flag==-1)
//                        {
//                            BookmarkedItem newBookmarkedNewsItem=new BookmarkedItem();
//                            newBookmarkedNewsItem.articleId=longClickedItem.articleId;
//                            newBookmarkedNewsItem.title=longClickedItem.title;
//                            newBookmarkedNewsItem.date=longClickedItem.date;
//                            newBookmarkedNewsItem.imageUrl=longClickedItem.imageUrl;
//                            newBookmarkedNewsItem.section=longClickedItem.section;
//                            newBookmarkedNewsItem.articleUrl=longClickedItem.articleUrl;
//
//                            String added_news_title=longClickedItem.title;
//                            allNewBookmarkedNewsItems.add(newBookmarkedNewsItem);
//
//                            SharedPreferences.Editor editor=sharedPreferences.edit();
//
//                            String updatedJson=gson.toJson(allNewBookmarkedNewsItems);
//                            editor.putString("bookmarkedList",updatedJson);
//                            editor.apply();
//
////                            ImageView bookmarkImage=newsItemView.findViewById(R.id.news_bookmark);
////                            holder.newsBookmark.setImageResource(R.drawable.ic_bookmark_black_24dp);
//                            allBookmarkedNewsItems.add(position,newBookmarkedNewsItem);
//                            notifyItemInserted(position);
//                            notifyItemRangeChanged(position, allNewBookmarkedNewsItems.size());
//                            ((ImageView)dialog.findViewById(R.id.dialog_bookmark)).setImageResource(R.drawable.bookmarked_red_24dp);
////                    Toast.makeText(allNewsContext,"BOOKMARKED",Toast.LENGTH_SHORT).show();
//
//                            Toast.makeText(allNewsContext,added_news_title+" was added to Bookmarks",Toast.LENGTH_SHORT).show();
//                        }

                        if(flag==1)
                        {
                            String removed_news_title=longClickedItem.title;
                            allNewBookmarkedNewsItems.remove(removeIndex);
//                    SharedPreferences sharedPreferences= allNewsContext.getSharedPreferences(sharedPreferencesName,Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
//                    Gson gson= new Gson();
                            String updatedJson=gson.toJson(allNewBookmarkedNewsItems);
                            editor.putString("bookmarkedList",updatedJson);
                            editor.commit();

//                            ImageView bookmarkImage=newsItemView.findViewById(R.id.news_bookmark);
//                            holder.newsBookmark.setImageResource(R.drawable.outline_bookmark_border_24);
//                            ((ImageView)dialog.findViewById(R.id.dialog_bookmark)).setImageResource(R.drawable.outline_bookmark_border_24_detailed);
                            Log.i("sv340", String.valueOf(allBookmarkedNewsItems.size()));
                            Log.i("sv347", String.valueOf(allNewBookmarkedNewsItems.size()));
                            if(allBookmarkedNewsItems.size()!=0)
                            {
                                allBookmarkedNewsItems.remove(position);

                                Log.i("sv345", String.valueOf(allNewBookmarkedNewsItems.size()));
                            }



                            if(allBookmarkedNewsItems.isEmpty())
                            {
                                bookmarkRecyclerView.setVisibility(View.GONE);
                                noBookmarkedTextview.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                bookmarkRecyclerView.setVisibility(View.VISIBLE);
                                noBookmarkedTextview.setVisibility(View.GONE);
                            }

                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, allNewBookmarkedNewsItems.size());
                            notifyDataSetChanged();

                            Toast.makeText(allNewsContext,removed_news_title+" was removed from Bookmarks",Toast.LENGTH_SHORT).show();
//                    for(int i=0;i<bookmarkedList.size();i++)
//                    {
//                        Toast.makeText(allNewsContext,bookmarkedList.get(i).articleId,Toast.LENGTH_SHORT).show();
//                    }
                            dialog.dismiss();
                        }


                    }

                });

                shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(allNewsContext,"TWITTER",Toast.LENGTH_SHORT).show();
                        String detailed_page_tweet="https://twitter.com/intent/tweet?text="+ Uri.encode("Check out this Link:\n"+allBookmarkedNewsItems.get(position).articleUrl+"\n#CSCI571NewsSearch");
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
    }
    @Override
    public int getItemCount() {
        return allBookmarkedNewsItems.size();
    }

    public class BookmarkedNewsViewHolder extends RecyclerView.ViewHolder{

        ImageView newsImage;
        ImageView newsBookmark;
        TextView newsText;
        TextView newsDateSection;
        RelativeLayout newsItemLayout;


        public BookmarkedNewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsImage=itemView.findViewById(R.id.bookmarked_news_image);
            newsText=itemView.findViewById(R.id.bookmarked_news_title);
            newsDateSection=itemView.findViewById(R.id.bookmarked_news_date_section);
            newsItemLayout=itemView.findViewById(R.id.bookmarked_news_item_layout);
            newsBookmark=itemView.findViewById(R.id.bookmarked_news_bookmark);
        }
    }
}




