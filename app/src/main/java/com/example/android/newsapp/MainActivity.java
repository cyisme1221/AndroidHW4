package com.example.android.newsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.newsapp.model.DatabaseOptUtil;
import com.example.android.newsapp.model.NewsItem;
import com.example.android.newsapp.utilities.NetworkUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Void>, NewsAdapter.ItemClickListener{
//    private ProgressDialog dialog;
    private NewsAdapter newsAdapter; //adapter which will show data
    private RecyclerView news_recycler_view;
    private ArrayList<NewsItem> mData = new ArrayList<NewsItem>();
    LoaderManager loaderManager;
    private DatabaseOptUtil db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        progress = new ProgressBar(this);
//        progress.setVisibility(View.GONE);
        news_recycler_view = (RecyclerView) findViewById(R.id.news_recycler_view);
        news_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        db = new DatabaseOptUtil(this);//get instance of DatabaseOptUtil
        newsAdapter = new NewsAdapter(this, mData, this);//instance of adapter
        news_recycler_view.setAdapter(newsAdapter);//set adapter into RecyclerView

        loaderManager = getSupportLoaderManager();//get LoaderManager object
        /*
         * Initialize the loader
         */
        loaderManager.initLoader(1, null, this);//initialize loader

        //check App installed before or not
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirst = prefs.getBoolean("isFirstTime", true);
        if (isFirst) {//if haven't install yet then loadData
            loadData();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isFirstTime", false);
            editor.commit();
        }else{//if not the first time to use, then read data from local sqlite database
            mData.clear();
            mData.addAll(db.getAll());
            newsAdapter.setData(mData);
        }
        //create job schedule
        ScheduleUtilities.scheduleRefresh(this);// Firebase’s JobDispatcher refresh
    }

    //loading data
    public void loadData() {
        loaderManager.restartLoader(1, null, this).forceLoad();
    }


    //menu operations
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_refresh) {
            loadData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //use AsyncTaskLoader to load news data to local sqlite database
    @Override
    public Loader<Void> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Void>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
            }

            @Override
            public Void loadInBackground() {
                QuestDataTasks.getNews(MainActivity.this);
                return null;
            }

        };
    }

    //load data from local database
    @Override
    public void onLoadFinished(Loader<Void> loader, Void data) {

        mData.clear();
        mData.addAll(db.getAll());
        newsAdapter.setData(mData);
    }

    @Override
    public void onLoaderReset(Loader<Void> loader){

    }

    //open news website when clicked
    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(mData.get(clickedItemIndex).getUrl()));
        startActivity(intent);
    }
}
