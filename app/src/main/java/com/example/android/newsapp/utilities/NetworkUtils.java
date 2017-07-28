package com.example.android.newsapp.utilities;

/**
 * Created by cy on 6/23/17.
 */

import android.net.Uri;
import android.util.Log;

import com.example.android.newsapp.model.NewsItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import static android.content.ContentValues.TAG;

//进行网络操作 及其JSON数据解析
public class NetworkUtils {

    final static String BASE_URL = "https://newsapi.org/v1/articles";

    final static String SOURCE = "the-next-web";
    final static String SORT = "latest";

    final static String SOURCE_PARAM = "source";
    final static String SORT_PARAM = "sortBy";
    final static String KEY_PARAM = "apiKey";

//    public static URL buildUrl() {
//        // COMPLETED (1) Fill in this method to build the proper Github query URL
//        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
//                .appendQueryParameter(SOURCE_PARAM, SOURCE)
//                .appendQueryParameter(SORT_PARAM, SORT)
//                .build();
//
//        URL url = null;
//        try {
//            url = new URL(builtUri.toString());
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//
//        return url;
//    }

    public static URL buildUrl(String key) {
        // COMPLETED (1) Fill in this method to build the proper Github query URL
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(SOURCE_PARAM, SOURCE)
                .appendQueryParameter(SORT_PARAM, SORT)
                .appendQueryParameter(KEY_PARAM, key)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static ArrayList<NewsItem> parseJSON(String json) throws JSONException {
        ArrayList<NewsItem> result = new ArrayList<NewsItem>();
        JSONObject main = new JSONObject(json);
        JSONArray items = main.getJSONArray("articles");

        String author;
        String title;
        String description;
        String url;
        String urlToImage;
        String publishedAt;

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            author = item.getString("author");
            title = item.getString("title");
            description = item.getString("description");
            url= item.getString("url");
            urlToImage = item.getString("urlToImage");
            publishedAt = item.getString("publishedAt");
            NewsItem newsItem = new NewsItem(author,title,description,url,urlToImage,publishedAt);
            result.add(newsItem);
        }
        return result;
    }

}
