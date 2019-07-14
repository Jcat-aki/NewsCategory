package com.example.anakano.newscategory;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class TranslationTasks extends AsyncTask<String, Integer, String> {

    private ProgressDialog mProgressDialog;
    private OnCallback<String> mCallBack;

    /**
     * コンストラクタ
     *
     * @param progressDialog 進捗状況を表示するダイアログを表示
     * @param callback
     */
    public TranslationTasks(ProgressDialog progressDialog, OnCallback<String> callback) {
        super();
        mProgressDialog = progressDialog;
        mCallBack = callback;
    }

    /**
     * UIスレッド上で動くが、doInBackgroundが呼ばれる前に動くため、初期化に最適
     */
    @Override
    protected void onPreExecute() {

        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected String doInBackground(String... title) {

        String japaneseTitle = null;

        try {
            final URL url = new URL("https://script.google.com/macros/s/AKfycbwO1BOtnBqs1S6ZRfNIGnRhDWAGs5PK-Aj5pRkt3Uow5gG6-T5b/exec?text=" +
                    title + "&source=en&target=ja");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");

            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String readLine = "";
                StringBuffer sb = new StringBuffer();
                while ((readLine = br.readLine()) != null) {
                    sb.append(readLine);
                }
                String jsonResult = sb.toString();

                JSONObject jsonObject = new JSONObject(jsonResult);
                japaneseTitle = jsonObject.getString("translateed");
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return japaneseTitle;
    }

    /**
     * doInBackground中に呼ばれて進捗状況を逐次報告してくれる最高なメソッド<br>
     * こいつは、UIメソッド上で動くよ
     *
     * @param progress 進捗状況の数値
     */
    @Override
    protected void onProgressUpdate(Integer... progress) {
        mProgressDialog.incrementProgressBy(progress[0]);
    }

    /**
     * doInBackgroundが完了したらよばれるやつ。UIスレッド上で動く。
     *
     * @param title 日本語訳されたタイトル
     */
    @Override
    protected void onPostExecute(String title) {

        if (mCallBack != null) {
            mCallBack.onSuccess(title);
        } else {
            mCallBack.onFailure(null);
        }

        mProgressDialog.dismiss();
    }
}