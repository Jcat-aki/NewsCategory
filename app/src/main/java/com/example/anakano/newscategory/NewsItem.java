package com.example.anakano.newscategory;

import java.util.List;

/**
 * 記事の内容を格納するためのクラス
 */

public class NewsItem {

    /** 記事のid( ユニーク ) */
    public String id;
    /** 記事のタイトル */
    public String title;
    /** 記事の種類: job, story, comment .. */
    public String type;
    /** story の点数 */
    public int score;
    /** author の名前 */
    public String by;
    /** 記事のコメント */
    public List<String> kids;
    /** 日本語タイトル */
    public String japaneseTitle;

    public NewsItem(){
        // empty
    }

}
