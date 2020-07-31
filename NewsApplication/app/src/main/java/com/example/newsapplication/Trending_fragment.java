package com.example.newsapplication;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Entity;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Map;

public class Trending_fragment extends Fragment{


    LineChart trendLineChart;
    JSONArray trends_array;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        view=inflater.inflate(R.layout.trending_fragment,container,false);

        generateChart("CoronaVirus");


        final EditText trendsEditText=view.findViewById(R.id.trends_edit_text);
        trendsEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode==event.KEYCODE_ENTER || event.getAction()==event.ACTION_DOWN){
                    generateChart(trendsEditText.getText().toString());
                }
                return false;
            }
        });







        return view;
    }


    void generateChart(final String keyword)
    {
        trendLineChart=(LineChart) view.findViewById(R.id.trending_line_chart);

        String trends_url="http://svandroidnewsappbackend.wl.r.appspot.com/getGoogleTrends?trendWord="+keyword;

        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, trends_url, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                try
                {
                    trends_array = response.getJSONArray("vals");
                    LineDataSet lineDataSet=new LineDataSet(trendsData(),"Data Set");
                    lineDataSet.setCircleColor(getResources().getColor(R.color.colorTrends));
                    lineDataSet.setCircleHoleColor(getResources().getColor(R.color.colorTrends));
                    lineDataSet.setColor(getResources().getColor(R.color.colorTrends));
                    ArrayList<ILineDataSet> dataSets= new ArrayList<>();
                    dataSets.add(lineDataSet);


                    LineData lineData= new LineData((dataSets));
                    trendLineChart.setData(lineData);
                    trendLineChart.getXAxis().setDrawGridLines(false);
                    trendLineChart.getAxisLeft().setDrawAxisLine(false);
                    trendLineChart.getAxisLeft().setDrawGridLines(false);
                    trendLineChart.getAxisRight().setDrawGridLines(false);

                    Legend legend=trendLineChart.getLegend();

                    legend.setEnabled(true);

                    LegendEntry[] legendEntries= new LegendEntry[1];
                    LegendEntry entry= new LegendEntry();
                    entry.formColor=getResources().getColor(R.color.colorTrends);
                    entry.formSize=15;
                    entry.label="Trending Chart for "+keyword;


                    legendEntries[0]=entry;
                    legend.setCustom(legendEntries);



                    trendLineChart.invalidate();
                }
                catch (JSONException e) {
                    Log.i("kk", "JSONException");
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

    private ArrayList<Entry> trendsData()
    {

        ArrayList<Entry> trendList= new ArrayList<Entry>();


        for(int i=0;i<trends_array.length();i++)
        {
            try{
                trendList.add(new Entry(i,trends_array.getInt(i)));
            }

            catch (JSONException e) {
            Log.i("trending", e.getMessage());
        }
        }
//        trendList.add(new Entry(0,20));
//        trendList.add(new Entry(1,24));
//        trendList.add(new Entry(2,2));
//        trendList.add(new Entry(3,3));
//        trendList.add(new Entry(4,10));
//        trendList.add(new Entry(5,28));

        return trendList;
    }
}