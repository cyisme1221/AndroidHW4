/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.newsapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.android.newsapp.model.DatabaseOptUtil;
import com.example.android.newsapp.model.NewsItem;
import com.example.android.newsapp.utilities.Key;
import com.example.android.newsapp.utilities.NetworkUtils;
import org.json.JSONException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

//store data to sqlite database
public class QuestDataTasks {
    public static void getNews(Context context) {
        ArrayList<NewsItem> result = new ArrayList<NewsItem>();
        DatabaseOptUtil db = new DatabaseOptUtil(context);
        try {
            db.deleteAll();
            URL url = NetworkUtils.buildUrl(Key.apiKey);
            String json = NetworkUtils.getResponseFromHttpUrl(url);
            result.addAll(NetworkUtils.parseJSON(json));
            for(int i=0;i<result.size();i++){
                db.insert(result.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}