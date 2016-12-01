package com.ali.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.database.Cursor;
import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.ali.android.stockhawk.data.HistoryColumns;
import com.ali.android.stockhawk.data.QuoteColumns;
import com.ali.android.stockhawk.data.QuoteProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by sam_chordas on 10/8/15.
 */
public class Utils {

  private static final String LOG_TAG = Utils.class.getSimpleName();

  public static boolean showPercent = true;
  public static String getDateBefore(String duration){
    Calendar cal = Calendar.getInstance();
    switch (duration) {
      case "week":
        cal.add(Calendar.WEEK_OF_MONTH, -1);
        break;
      case "month":
        cal.add(Calendar.MONTH, -1);
        break;
      case "sixMonths":
        cal.add(Calendar.MONTH, -6);
        break;
      case "year":
        cal.add(Calendar.YEAR, -1); // to get previous year add -1
        break;
    }
    Date dateNowMins=cal.getTime();
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    String endDate=format.format(dateNowMins);
    return endDate;
  }

  public static List<Entry> historyFromCursor(Cursor data){
    List<Entry> output=new ArrayList<>();
    data.moveToFirst();
    int i=0;
    int closeColumnIndex= data.getColumnIndex(HistoryColumns.CLOSE);
    while(data.moveToNext()) {
      output.add(new Entry((float)++i ,Float.valueOf(data.getString(closeColumnIndex))));
    }
    return output;
  }

  public static ArrayList quoteJsonHistoryToContentVals(String JSON){
    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
    JSONObject jsonObject = null;
    JSONArray resultsArray = null;
    try{
      jsonObject = new JSONObject(JSON);
      if (jsonObject != null && jsonObject.length() != 0){
        jsonObject = jsonObject.getJSONObject("query");
        int count = Integer.parseInt(jsonObject.getString("count"));
        if (count == 1){
          jsonObject = jsonObject.getJSONObject("results")
                  .getJSONObject("quote");
          batchOperations.add(buildBatchOperationHistory(jsonObject));
        } else{
          resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");

          if (resultsArray != null && resultsArray.length() != 0){
            for (int i = 0; i < resultsArray.length(); i++){
              jsonObject = resultsArray.getJSONObject(i);
              batchOperations.add(buildBatchOperationHistory(jsonObject));
            }
          }
        }
      }
    } catch (JSONException e){
      Log.e(LOG_TAG, "String to JSON failed: " + e);
    }
    return batchOperations;
  }
  public static boolean checkJsonNotNull(String JSON){
    boolean isNull=true;
    JSONObject jsonObject = null;
    try {
      jsonObject = new JSONObject(JSON);
      if (jsonObject != null && jsonObject.length() != 0) {
        jsonObject = jsonObject.getJSONObject("query");
        jsonObject = jsonObject.getJSONObject("results")
                .getJSONObject("quote");
      }
      String ask =jsonObject.getString("Ask");
      isNull= ask.equals("null");
    }catch(JSONException e){
        e.printStackTrace();
      }

      return isNull;
    }

  public static ArrayList quoteJsonToContentVals(String JSON){
    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
    JSONObject jsonObject = null;
    JSONArray resultsArray = null;
    try{
      jsonObject = new JSONObject(JSON);
      if (jsonObject != null && jsonObject.length() != 0){
        jsonObject = jsonObject.getJSONObject("query");
        int count = Integer.parseInt(jsonObject.getString("count"));
        if (count == 1){
          jsonObject = jsonObject.getJSONObject("results")
              .getJSONObject("quote");
          batchOperations.add(buildBatchOperation(jsonObject));
        } else{
          resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");

          if (resultsArray != null && resultsArray.length() != 0){
            for (int i = 0; i < resultsArray.length(); i++){
              jsonObject = resultsArray.getJSONObject(i);
              batchOperations.add(buildBatchOperation(jsonObject));
            }
          }
        }
      }
    } catch (JSONException e){
      Log.e(LOG_TAG, "String to JSON failed: " + e);
    }
    return batchOperations;
  }

  private static String truncateBidPrice(String bidPrice){

      bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));

    return bidPrice;
  }

  private static String truncateChange(String change, boolean isPercentChange){
    String weight = change.substring(0,1);
    String ampersand = "";
    if (isPercentChange){
      ampersand = change.substring(change.length() - 1, change.length());
      change = change.substring(0, change.length() - 1);
    }
    change = change.substring(1, change.length());
  //  Log.v("Tesy","Error !! "+change);
    double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
    //Log.v("Tesy","Error !! "+round);
    change = String.format("%.2f", round);
    StringBuilder changeBuffer = new StringBuilder(change);
    changeBuffer.insert(0, weight);
    changeBuffer.append(ampersand);
    change = changeBuffer.toString();
    return change;
  }
  private static ContentProviderOperation buildBatchOperationHistory(JSONObject jsonObject){
    ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
            QuoteProvider.History.CONTENT_URI);
    try {
      builder.withValue(HistoryColumns.SYMBOL, jsonObject.getString("Symbol"));
      builder.withValue(HistoryColumns.CLOSE, jsonObject.getString("Close"));
      builder.withValue(HistoryColumns.DATE, jsonObject.getString("Date"));

    } catch (JSONException e){
      e.printStackTrace();
    }
    return builder.build();
  }
  private static ContentProviderOperation buildBatchOperation(JSONObject jsonObject){
    ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
        QuoteProvider.Quotes.CONTENT_URI);
    try {
      String change = jsonObject.getString("Change");
      builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString("symbol"));
      builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(jsonObject.getString("Bid")));
      //Log.v("Tesy","Symbol"+jsonObject.getString("symbol"));
      builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
          jsonObject.getString("ChangeinPercent"), true));
      builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
      builder.withValue(QuoteColumns.ISCURRENT, 1);
      if (change.charAt(0) == '-'){
        builder.withValue(QuoteColumns.ISUP, 0);
      }else{
        builder.withValue(QuoteColumns.ISUP, 1);
      }

    } catch (JSONException e){
      e.printStackTrace();
    }
    return builder.build();
  }
}
