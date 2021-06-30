package com.example.newsfresh;

import androidx.appcompat.app.AppCompatActivity;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.newsfresh.databinding.ActivityMainBinding;
import com.example.newsfresh.model.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NewsItemClicked  {
ActivityMainBinding binding;

    private  ArrayList<News> newsArrayList=new ArrayList<>();

    NewsListAdapter ca;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main);

        fetch_data(new NewsAsync() {
            @Override
            public void process_finished(ArrayList<News> newsArrayList1) {
                ca=new NewsListAdapter(newsArrayList,MainActivity.this::onItemClicked);
                binding.rv.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                binding.rv.setAdapter(ca);
            }
        });
        }



    public void fetch_data(NewsAsync callback) {
        String url="https://saurav.tech/NewsAPI/top-headlines/category/health/in.json";
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray=response.getJSONArray("articles");
                            for (int i = 0; i <jsonArray.length() ; i++) {
                                JSONObject jsonObject=jsonArray.getJSONObject(i);
                                String newsTitle=jsonObject.getString("title");
                                String author=jsonObject.getString("author");
                                String url=jsonObject.getString("url");
                                String url_Image=jsonObject.getString("urlToImage");
                                News  obj=new News(newsTitle,author,url_Image,url);
                                newsArrayList.add(obj);
                            }
                            if (callback!=null) callback.process_finished(newsArrayList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, error -> Log.d("error", "onErrorResponse: "));
        Log.d("fetch_data", "fetch_data: ");
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }
    @Override
    public void onItemClicked(News obj) {

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(obj.getUrl()));
    }
}