package com.example.anakano.newscategory;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AbsListView.OnScrollListener {

    final private static int LIMIT_SIZE = 20;

    private List<NewsItem> mItems;
    private List<String> mIds;
    private NewsItemAdapter mAdapter;
    private Toast mToast;
    private GetArticleTasks mGetArticleTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mItems = new ArrayList<>();
        mAdapter = new NewsItemAdapter(this,R.layout.news_item);
        final ListView listView = (ListView) findViewById(R.id.main_api_response_listview);
        listView.setAdapter(mAdapter);
        listView.setOnScrollListener(this);

        mToast = new Toast(this);

        try {
            final URL url = new URL("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
            final ProgressDialog progressDialog = new ProgressDialog(this);
            DownloadTasks downloadTasks = (DownloadTasks) new DownloadTasks(progressDialog, new OnCallback<List<String>>() {
                @Override
                public void onSuccess(final List<String> ids) {
                    mIds = ids;
                    for( int i = 0; i < LIMIT_SIZE; i++ ){
                        NewsItem item = new NewsItem();
                        item.id = ids.get(mItems.size());
                        mItems.add(item);
                    }
                    mGetArticleTasks = (GetArticleTasks) new GetArticleTasks(progressDialog, new OnCallback<List<NewsItem>>() {
                        @Override
                        public void onSuccess(List<NewsItem> object) {
                            for( int i = 0; i < LIMIT_SIZE; i++){
                                mItems.set(i, object.get(i));
                            }
                            mAdapter.setData(object);
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            mAdapter.setData(mItems);
                        }
                    }).execute(mItems);
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


    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if( absListView.getLastVisiblePosition() == (mAdapter.getCount() - 1) && absListView.getLastVisiblePosition() < mIds.size() - 1){ // 最後の列の読み込み
            final List<NewsItem> items = new ArrayList<>();
            for ( int x = 0 ; x < LIMIT_SIZE ; x++){
                NewsItem item = new NewsItem();
                item.id = mIds.get(mItems.size() + x);
                items.add(item);
            }
            final ProgressDialog progressDialog = new ProgressDialog(this);
            mGetArticleTasks = (GetArticleTasks) new GetArticleTasks(progressDialog, new OnCallback<List<NewsItem>>() {
                @Override
                public void onSuccess(List<NewsItem> object) {
                    mItems.addAll(object);
                    mAdapter.setData(mItems);
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Exception e) {
                    mAdapter.setData(mItems);
                }
            }).execute(items);
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

}
