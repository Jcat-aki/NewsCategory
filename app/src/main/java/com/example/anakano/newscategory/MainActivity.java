package com.example.anakano.newscategory;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<NewsItem> items;
    private NewsItemAdapter mAdapter;
    private Toast mToast;
    private Context mContext;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        items = new ArrayList<>();
        mAdapter = new NewsItemAdapter(this,R.layout.news_item);
        final ListView listView = (ListView) findViewById(R.id.main_api_response_listview);
        listView.setAdapter(mAdapter);

        mToast = new Toast(this);
        mContext = this;

        try {
            final URL url = new URL("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
            final ProgressDialog progressDialog = new ProgressDialog(this);
            DownloadTasks downloadTasks = (DownloadTasks) new DownloadTasks(progressDialog, new OnCallback<List<String>>() {
                @Override
                public void onSuccess(final List<String> ids) {
                    for( int i = 0; i < 10; i++ ){
                        NewsItem item = new NewsItem();
                        item.id = ids.get(items.size());
                        items.add(item);
                    }
                    GetArticleTasks getArticleTasks = (GetArticleTasks) new GetArticleTasks(progressDialog, items, new OnCallback<List<NewsItem>>() {
                        @Override
                        public void onSuccess(List<NewsItem> object) {
                            mAdapter.setData(object);
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            mAdapter.setData(items);
                        }
                    }).execute(items);
                }

                @Override
                public void onFailure(Exception e) {
                    mToast.setText(e.toString());
                    mToast.show();
                }
            }).execute(url);
        } catch (MalformedURLException e) {
            mToast.setText(e.toString());
            mToast.show();
            e.printStackTrace();
        }
    }



}
