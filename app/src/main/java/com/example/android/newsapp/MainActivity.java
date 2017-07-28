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
    private NewsAdapter newsAdapter; //显示数据的适配器
    private RecyclerView news_recycler_view;//数据显示的列表
    private ArrayList<NewsItem> mData = new ArrayList<NewsItem>();//缓存显示用的数据
    LoaderManager loaderManager;//LoaderManager对象
    private DatabaseOptUtil db;//数据库操作对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        progress = new ProgressBar(this);
//        progress.setVisibility(View.GONE);
        news_recycler_view = (RecyclerView) findViewById(R.id.news_recycler_view);
        news_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        db = new DatabaseOptUtil(this);//实例化数据库操作对象
        newsAdapter = new NewsAdapter(this, mData, this);//实例化adapter
        news_recycler_view.setAdapter(newsAdapter);//把adapter设置到RecyclerView现实

        loaderManager = getSupportLoaderManager();//获取LoaderManager对象
        /*
         * Initialize the loader
         */
        loaderManager.initLoader(1, null, this);//初始化loader

        //判断之前是否安装过APP
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirst = prefs.getBoolean("isFirstTime", true);
        if (isFirst) {//如果还没有安装过
            loadData();//加载网络数据
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isFirstTime", false);
            editor.commit();
        }else{//不是第一次安装使用 则从本地sqlite数据库当中读取数据
            mData.clear();
            mData.addAll(db.getAll());
            newsAdapter.setData(mData);//更新适配器显示
        }
        //create job schedule
        ScheduleUtilities.scheduleRefresh(this);//执行 Firebase’s JobDispatcher 定时刷新
    }

    //加载数据
    public void loadData() {
        loaderManager.restartLoader(1, null, this).forceLoad();
    }


    //菜单操作
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

    //使用AsyncTaskLoader加载新闻数据到本地sqlite数据库
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

    //从本地数据库加载数据显示出来
    @Override
    public void onLoadFinished(Loader<Void> loader, Void data) {

        mData.clear();
        mData.addAll(db.getAll());
        newsAdapter.setData(mData);
    }

    @Override
    public void onLoaderReset(Loader<Void> loader){

    }

    //绑定点击事件跳转打开
    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(mData.get(clickedItemIndex).getUrl()));
        startActivity(intent);
    }
}
