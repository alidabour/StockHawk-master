package com.ali.android.stockhawk.service;

import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.ali.android.stockhawk.data.QuoteProvider;
import com.ali.android.stockhawk.rest.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ali on 12/09/16.
 */
public class HistoryTaskService extends GcmTaskService {
    private static final String TESY = "Tesy";
    private Context mContext;
    private final OkHttpClient client = new OkHttpClient();
    private final String LOG_TAG = HistoryTaskService.class.getSimpleName();

    public HistoryTaskService() {
    }

    HistoryTaskService(Context context) {
        this.mContext = context;
    }

    private String fetchData(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        //Cursor initQueryCursor;
        if (mContext == null) {
            mContext = this;
        }
        StringBuilder urlStringBuilder = new StringBuilder();
        String symbol =taskParams.getExtras().getString("symbol");
        Calendar cal = Calendar.getInstance();
        Date dateNow = cal.getTime();
        cal.add(Calendar.YEAR, -1); // to get previous year add -1
        Date dateNowMinsYear=cal.getTime();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        String startDate=format.format(dateNow);
        String endDate=format.format(dateNowMinsYear);



        try {
            // Base URL for the Yahoo query
            urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
            urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.historicaldata where symbol=", "UTF-8"));
            urlStringBuilder.append(URLEncoder.encode("\""+symbol+"\"","UTF-8"));
            urlStringBuilder.append(URLEncoder.encode("and startDate =","UTF-8"));
            urlStringBuilder.append(URLEncoder.encode("\""+endDate+"\"","UTF-8"));
            urlStringBuilder.append(URLEncoder.encode("and endDate =","UTF-8"));
            urlStringBuilder.append(URLEncoder.encode("\""+startDate+"\"","UTF-8"));

            urlStringBuilder.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
                    + "org%2Falltableswithkeys&callback=");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String urlString;
        String getResponse;
        int result = GcmNetworkManager.RESULT_FAILURE;
        if (urlStringBuilder != null) {
            urlString = urlStringBuilder.toString();
            try {
                getResponse = fetchData(urlString);
                Log.v(TESY, "HistoryTaskService "+urlString);
                result = GcmNetworkManager.RESULT_SUCCESS;
                try {
                    mContext.getContentResolver().delete(QuoteProvider.History.withSymbol(symbol),null,null);
                    mContext.getContentResolver().applyBatch(QuoteProvider.AUTHORITY,
                            Utils.quoteJsonHistoryToContentVals(getResponse));
                } catch (RemoteException | OperationApplicationException e) {
                    Log.e(LOG_TAG, "Error applying batch insert", e);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        return result;
    }
}
