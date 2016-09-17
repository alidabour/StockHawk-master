package com.sam_chordas.android.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * WidgetDataProvider acts as the adapter for the collection view widget,
 * providing RemoteViews to the widget in the getViewAt method.
 */@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetDataProvider extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;
            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                Uri uri = QuoteProvider.Quotes.CONTENT_URI;
                data = getContentResolver().query(uri,
                        new String[]{QuoteColumns._ID,QuoteColumns.SYMBOL,QuoteColumns.PERCENT_CHANGE,
                                QuoteColumns.CHANGE,QuoteColumns.BIDPRICE,
                        QuoteColumns.CREATED,QuoteColumns.ISUP,QuoteColumns.ISCURRENT},
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews view = new RemoteViews(getPackageName(),
                R.layout.list_item_quote);
                view.setTextViewText(R.id.stock_symbol,data.getString(data.getColumnIndex("symbol")));
                view.setTextViewText(R.id.bid_price, data.getString(data.getColumnIndex("bid_price")));
                view.setTextViewText(R.id.change, data.getString(data.getColumnIndex("change")));
                if(data.getInt(data.getColumnIndex("is_up"))==1) {
                    view.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                }
                else {
                    view.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);

                }
                final Intent fillInIntent = new Intent();
                Uri uri = QuoteProvider.Quotes.withSymbol(data.getString(data.getColumnIndex("symbol")));
                fillInIntent.setData(uri);
                fillInIntent.putExtra("symbol",data.getString(data.getColumnIndex("symbol")));
                view.setOnClickFillInIntent(R.id.list_item_quote, fillInIntent);
                return view;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.list_item_quote);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position)){
                    return Long.parseLong(data.getString(data.getColumnIndex(QuoteColumns._ID)));
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }


//    private final List<Stock> mCollection = new ArrayList<>();
//    private Context mContext = null;
//
//    public WidgetDataProvider(Context context, Intent intent) {
//        mContext = context;
//    }
//
//    @Override
//    public void onCreate() {
//        initData();
//    }
//
//    @Override
//    public void onDataSetChanged() {
//        initData();
//    }
//
//    @Override
//    public void onDestroy() {
//
//    }
//
//    @Override
//    public int getCount() {
//        return mCollection.size();
//    }
//
//    @Override
//    public RemoteViews getViewAt(int position) {
//        RemoteViews view = new RemoteViews(mContext.getPackageName(),
//                R.layout.list_item_quote);
//        Intent updateIntent = new Intent();
//        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//       // updateIntent.putExtra(WidgetDataProvider.WIDGET_IDS_KEY, ids);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                mContext, 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        view.setOnClickPendingIntent(R.id.widget_list, pendingIntent);
//
//        view.setTextViewText(R.id.stock_symbol, mCollection.get(position).getSymbol());
//        view.setTextViewText(R.id.bid_price, mCollection.get(position).getBidPrice());
//        view.setTextViewText(R.id.change, mCollection.get(position).getChange());
//        if(mCollection.get(position).isUp()==1) {
//            view.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
//        }
//        else {
//            view.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
//
//        }
//        final Intent fillInIntent = new Intent();
//        Uri quoteUri = QuoteProvider.Quotes.withSymbol(mCollection.get(position).getSymbol());
//        fillInIntent.setData(quoteUri);
//        fillInIntent.putExtra("symbol",mCollection.get(position).getSymbol());
//        view.setOnClickFillInIntent(R.id.list_item_quote, fillInIntent);
//        return view;
//    }
//
//    @Override
//    public RemoteViews getLoadingView() {
//        return null;
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return 1;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public boolean hasStableIds() {
//        return true;
//    }
//
//    private void initData() {
//        Cursor data=null;
//        Uri quoteUri = QuoteProvider.Quotes.CONTENT_URI;
//        final long token = Binder.clearCallingIdentity();
//        try {
//            data=mContext.getContentResolver().query(quoteUri,
//                    new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
//                            QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
//                    QuoteColumns.ISCURRENT + " = ?",
//                    new String[]{"1"},
//                    null);
//            data.moveToFirst();
//            mCollection.clear();
//       do{
//           Stock s=new Stock(data.getString(data.getColumnIndex("symbol")),
//                   data.getString(data.getColumnIndex("bid_price")),
//                   data.getString(data.getColumnIndex("change")),
//                   data.getInt(data.getColumnIndex("is_up")));
//            mCollection.add(s);
//        }
//       while (data.moveToNext());
//        } finally {
//            Binder.restoreCallingIdentity(token);
//            if(data !=null)
//            data.close();
//        }
//
//    }

}
