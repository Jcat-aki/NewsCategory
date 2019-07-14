package com.example.anakano.newscategory;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by anakano on 17/03/13.
 */

public class NewsItemAdapter extends ArrayAdapter<NewsItem> {

    private List<NewsItem> mNewsItems;
    private LayoutInflater mInflater;


    public NewsItemAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mNewsItems = new ArrayList<>();
    }

    @Override
    public int getCount(){
        return mNewsItems.size();
    }

    @Override
    public NewsItem getItem(int position){
        return mNewsItems.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View view = convertView;
        ViewHolder viewHolder;

        if( view == null ){
            view = mInflater.inflate(R.layout.news_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.titleView = (TextView)view.findViewById(R.id.title_item);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.titleView.setText(mNewsItems.get(position).japaneseTitle);

        return view;
    }

    public void setData(List<NewsItem> newsItems){
        mNewsItems = newsItems;
    }

    private static class ViewHolder {
        TextView titleView;
    }
}
