package com.ali.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.TaskParams;

/**
 * Created by ali on 12/09/16.
 */
public class HistoryIntentService extends IntentService {

    public HistoryIntentService(){
        super(HistoryIntentService.class.getName());
    }

    public HistoryIntentService(String name) {
        super(name);
    }

    @Override protected void onHandleIntent(Intent intent) {
        Log.d(HistoryIntentService.class.getSimpleName(), "Histroy Intent Service");
        HistoryTaskService histroyTaskService = new HistoryTaskService(this);
        Bundle args = new Bundle();
//        if (intent.getStringExtra("tag").equals("add")){
//            args.putString("symbol", intent.getStringExtra("symbol"));
//        }
        // We can call OnRunTask from the intent service to force it to run immediately instead of
        // scheduling a task.
        args.putString("symbol", intent.getStringExtra("symbol"));
        histroyTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));
    }
}