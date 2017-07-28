package com.example.android.newsapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

//数据库操作类
public class DatabaseOptUtil {
    private static DBHelper helper;
    private static SQLiteDatabase db;//数据库操作对象

    //instance of DBHelper
    public DatabaseOptUtil(Context context) {
        // TODO Auto-generated constructor stub
        helper=new DBHelper(context);
    }

    // insert news data into database
    public void insert(NewsItem newsItem){
        db=helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Contract.TABLE_NEWS.COLUMN_NAME_AUTHOR, newsItem.getAuthor());
        cv.put(Contract.TABLE_NEWS.COLUMN_NAME_TITLE, newsItem.getTitle());
        cv.put(Contract.TABLE_NEWS.COLUMN_NAME_DESCRIPTION, newsItem.getDescription());
        cv.put(Contract.TABLE_NEWS.COLUMN_NAME_URL, newsItem.getUrl());
        cv.put(Contract.TABLE_NEWS.COLUMN_NAME_URL_TO_IMAGE, newsItem.getUrlToImage());
        cv.put(Contract.TABLE_NEWS.COLUMN_NAME_PUBLISH_AT, newsItem.getPublishedAt());
        db.insert(Contract.TABLE_NEWS.TABLE_NAME, null, cv);
        db.close();
    }

    //read all the datas from the database
    public List<NewsItem> getAll(){
        List<NewsItem> newsItems =  new ArrayList<NewsItem>();
        db=helper.getWritableDatabase();
        Cursor cursor = db.query(
                Contract.TABLE_NEWS.TABLE_NAME, null, null, null, null, null,
                null);
        while(cursor.moveToNext()){
            String author = cursor.getString(cursor.getColumnIndex(Contract.TABLE_NEWS.COLUMN_NAME_AUTHOR));
            String title = cursor.getString(cursor.getColumnIndex(Contract.TABLE_NEWS.COLUMN_NAME_TITLE));
            String description = cursor.getString(cursor.getColumnIndex(Contract.TABLE_NEWS.COLUMN_NAME_DESCRIPTION));
            String url = cursor.getString(cursor.getColumnIndex(Contract.TABLE_NEWS.COLUMN_NAME_URL));
            String urlToImage = cursor.getString(cursor.getColumnIndex(Contract.TABLE_NEWS.COLUMN_NAME_URL_TO_IMAGE));
            String publishedAt = cursor.getString(cursor.getColumnIndex(Contract.TABLE_NEWS.COLUMN_NAME_PUBLISH_AT));
            NewsItem item = new NewsItem(author, title, description, url, urlToImage, publishedAt);
            newsItems.add(item);
        }
        cursor.close();
        db.close();
        return newsItems;
    }

    //delete database, clear all records
    public static void deleteAll() {
        db=helper.getWritableDatabase();
        //method for delete database
        db.delete(Contract.TABLE_NEWS.TABLE_NAME, null, null);
        db.close();
    }

}

