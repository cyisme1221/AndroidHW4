

package com.example.android.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
// Use Firebase’s JobDispatcher, modify app, so can upload new news every minute.
public class ScheduleUtilities {
    private static boolean sInitialized;
    private static final int PERIODICITY = 60;
    private static final int TOLERANCE_INTERVAL = 60;

    synchronized public static void scheduleRefresh(@NonNull final Context context){
        if(sInitialized) return;

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        //create a job that works only when
        Job constraintRefreshJob = dispatcher.newJobBuilder()
                .setService(NewsJob.class)
                .setTag("news_job_tag")
                .setConstraints(Constraint.ON_ANY_NETWORK)//only run on an unmetered network
                .setLifetime(Lifetime.FOREVER)//persisted on device reboot
                .setRecurring(true)//periodically schedule
                .setTrigger(Trigger.executionWindow(PERIODICITY,
                        PERIODICITY + TOLERANCE_INTERVAL))//schedule every 60 seconds, start between 60 - 120 seconds
                .setReplaceCurrent(true)//overwrite an existing job with the same tag
                .build();
        dispatcher.schedule(constraintRefreshJob);
        sInitialized = true;
    }
}
