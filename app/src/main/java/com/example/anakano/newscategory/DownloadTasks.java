package com.example.anakano.newscategory;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by anakano on 17/03/05.
 */

public class DownloadTasks extends AsyncTask<URL, Integer, List<String>> {

    private ProgressDialog mProgressDialog;
    private List<NewsItem> mNewsItems;
    private OnCallback<List<String>> mCallBack;
    private Exception mException;

    /**
     * コンストラクタ
     * @param progressDialog 進捗状況を表示するダイアログを表示
     * @param callback
     */
    public DownloadTasks(ProgressDialog progressDialog, OnCallback<List<String>> callback){
        super();
        mProgressDialog = progressDialog;
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

    /**
     * このメソッドは、UIスレッド以外で処理ができるようにするために、使われます。<br>
     * そのためダウンロード中もユーザが画面を触って動作できるようになる利点があります。
     * @param urls
     * @return ダウンロードした結果を返す
     */
    @Override
    protected List<String> doInBackground(URL... urls) {
        String result = "";
        HttpURLConnection httpURLConnection = null;
        List<String> ids = new ArrayList<>();
        mNewsItems = new ArrayList<>();


        try{
            httpURLConnection = (HttpURLConnection) urls[0].openConnection();
            httpURLConnection.setReadTimeout(1000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            int resp = httpURLConnection.getResponseCode();

            StringBuffer sb = new StringBuffer();
            String readLine = "";
            if( resp == HttpURLConnection.HTTP_OK) { // リクエスト失敗時
                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                while ((readLine = br.readLine()) != null) {
                    sb.append(readLine);
                }
            }
            // 一旦接続を解除する
            httpURLConnection.disconnect();
            publishProgress(0);
            result = sb.toString();

            result = result.replaceAll("\\[","");
            result = result.replaceAll("]","");
            result = result.replaceAll(" ","");
            ids = Arrays.asList(result.split(","));

        } catch (IOException e) {
            e.printStackTrace();
            mException = e;
        } finally {
            httpURLConnection.disconnect();
        }
        publishProgress(100);
        return ids;
    }

    /**
     * doInBackground中に呼ばれて進捗状況を逐次報告してくれる最高なメソッド<br>
     * こいつは、UIメソッド上で動くよ
     * @param progress 進捗状況の数値
     */
    @Override
    protected void onProgressUpdate( Integer... progress){
        mProgressDialog.setProgress(progress[0]);
    }

    /**
     * doInBackgroundが完了したらよばれるやつ。UIスレッド上で動く。
     * @param results 結果
     */
    @Override
    protected void onPostExecute(List<String> results){
        mProgressDialog.dismiss();
        if( mCallBack != null){
            if( mException == null) {
                mCallBack.onSuccess(results);
            } else{
                mCallBack.onFailure(mException);
            }
        }

    }

}
