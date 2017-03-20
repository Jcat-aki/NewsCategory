package com.example.anakano.newscategory;

import android.app.ProgressDialog;
import android.content.pm.LabeledIntent;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anakano on 17/03/12.
 */

public class GetArticleTasks extends AsyncTask<List<NewsItem>, Integer, List<NewsItem>> {

    private ProgressDialog mProgressDialog;
    private TextView mTextView;
    private List<NewsItem> mNewsItems;
    private OnCallback<List<NewsItem>> mCallBack;

    /**
     * コンストラクタ
     * @param progressDialog 進捗状況を表示するダイアログを表示
     * @param items
     * @param callback
     */
    public GetArticleTasks(ProgressDialog progressDialog, List<NewsItem> items, OnCallback<List<NewsItem>> callback){
        super();
        mProgressDialog = progressDialog;
        mNewsItems = items;
        mCallBack = callback;
    }

    /**
     * UIスレッド上で動くが、doInBackgroundが呼ばれる前に動くため、初期化に最適
     */
    @Override
    protected void onPreExecute() {
        // とりあえず、よんでおくだけ
        System.out.print("hogehoge");

        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected List<NewsItem> doInBackground(List<NewsItem>... newsItems) {

        HttpURLConnection httpURLConnection = null;
        final List<NewsItem> results = new ArrayList<>();
        int roop = 0;

        try {
            // 一旦この場所に記述し、必要に応じてメソッドに分割する
            for (NewsItem item : newsItems[0]) {
                final URL url = new URL("https://hacker-news.firebaseio.com/v0/item/" + item.id + ".json?print=pretty");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");

                httpURLConnection.connect();

                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String readLine = "";
                    StringBuffer sb = new StringBuffer();
                    String jsonResult = new String();
                    while ((readLine = br.readLine()) != null) {
                        sb.append(readLine);
                    }
                    jsonResult = sb.toString();

                    JSONObject jsonObject = new JSONObject(jsonResult);
                    final NewsItem newsItem = new NewsItem();
                    newsItem.id = String.valueOf(jsonObject.getInt("id"));
                    newsItem.by = jsonObject.getString("by");
                    newsItem.type = jsonObject.getString("type");
                    newsItem.title = jsonObject.getString("title");
                    newsItem.score = jsonObject.getInt("score");
                    //newsItem.kids = (ArrayList<String>)jsonObject.get("kids");

                    results.add(newsItem);
                }
                publishProgress(10);
            }
        }catch(ProtocolException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
       return results;
    }

    /**
     * doInBackground中に呼ばれて進捗状況を逐次報告してくれる最高なメソッド<br>
     * こいつは、UIメソッド上で動くよ
     * @param progress 進捗状況の数値
     */
    @Override
    protected void onProgressUpdate( Integer... progress){
        mProgressDialog.incrementProgressBy(progress[0]);
    }

    /**
     * doInBackgroundが完了したらよばれるやつ。UIスレッド上で動く。
     * @param results 結果
     */
    @Override
    protected void onPostExecute( List<NewsItem> results){

       if( mCallBack != null ){
           mCallBack.onSuccess(results);
       }else{
           mCallBack.onFailure(null);
       }

        mProgressDialog.dismiss();
    }
}
