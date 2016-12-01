package com.ali.android.stockhawk.ui;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.ali.android.stockhawk.R;
import com.ali.android.stockhawk.data.HistoryColumns;
import com.ali.android.stockhawk.data.QuoteProvider;
import com.ali.android.stockhawk.rest.Utils;
import com.ali.android.stockhawk.service.HistoryIntentService;

import java.util.List;

public class DetailActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String SYMBOL = "symbol";
    private static final String DURATION = "duration";
    private static final String WEEK = "week";
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String SIX_MONTHS = "sixMonths";
    private List<Entry> histories;
    private LineChart chart;
    private LineDataSet dataSet;
    private Intent mServiceIntent;
    private static final int CURSOR_LOADER_ID = 1;

    private final Bundle arg = new Bundle();
    private String symbol;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        symbol = intent.getStringExtra(SYMBOL);
        mServiceIntent = new Intent(this, HistoryIntentService.class);
        arg.putString(DURATION, WEEK);
        if (savedInstanceState == null) {
            // Run the initialize task service so that some stocks appear upon an empty database
            mServiceIntent.putExtra(SYMBOL, symbol);
            startService(mServiceIntent);
        }

        getLoaderManager().initLoader(CURSOR_LOADER_ID, arg, this);
        chart = (LineChart) findViewById(R.id.chart);

    }


    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(this, QuoteProvider.History.withSymbol(symbol),
                new String[]{HistoryColumns.SYMBOL, HistoryColumns.DATE, HistoryColumns.CLOSE},
                HistoryColumns.DATE + " >=? ",
                new String[]{Utils.getDateBefore(WEEK)}
                , HistoryColumns.DATE + " ASC");


        if (args != null) {
            Log.v("Tesy", "args not null");
            if (args.getString(DURATION).equals(YEAR)) {
                cursorLoader = new CursorLoader(this, QuoteProvider.History.withSymbol(symbol),
                        new String[]{HistoryColumns.SYMBOL, HistoryColumns.DATE, HistoryColumns.CLOSE},
                        HistoryColumns.DATE + " >=?",
                        new String[]{Utils.getDateBefore(YEAR)}
                        , HistoryColumns.DATE + " ASC");
            } else if (args.getString(DURATION).equals(MONTH)) {
                cursorLoader = new CursorLoader(this, QuoteProvider.History.withSymbol(symbol),
                        new String[]{HistoryColumns.SYMBOL, HistoryColumns.DATE, HistoryColumns.CLOSE},
                        HistoryColumns.DATE + " >=? ",
                        new String[]{Utils.getDateBefore(MONTH)}
                        , HistoryColumns.DATE + " ASC");
            } else if (args.getString(DURATION).equals(SIX_MONTHS)) {
                cursorLoader = new CursorLoader(this, QuoteProvider.History.withSymbol(symbol),
                        new String[]{HistoryColumns.SYMBOL, HistoryColumns.DATE, HistoryColumns.CLOSE},
                        HistoryColumns.DATE + " >=? ",
                        new String[]{Utils.getDateBefore(SIX_MONTHS)}
                        , HistoryColumns.DATE + " ASC");
            }
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        histories = Utils.historyFromCursor(data);
        dataSet = new LineDataSet(histories, "Label");
        dataSet.setColors(new int[]{R.color.material_blue_500}, getApplicationContext());
        dataSet.setDrawCircles(false);
        LineData lineData = new LineData(dataSet);
        lineData.setValueTextColor(R.color.material_green_700);
        lineData.setValueTextSize(20);

        chart.setData(lineData);
        chart.setDescription(getString(R.string.chart_description));
        chart.setDescriptionColor(R.color.material_red_700);
        chart.setDescriptionTextSize(35);
        chart.setDrawGridBackground(false);
        chart.setDrawBorders(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled(false);
//        leftAxis.setDrawGridLines(false);
//        leftAxis.setDrawAxisLine(false);
        YAxis right = chart.getAxisRight();
        right.setEnabled(false);
//        right.setDrawGridLines(false);
//        right.setDrawAxisLine(false);
        XAxis xAxis = chart.getXAxis();
//        xAxis.setDrawGridLines(false);
//        xAxis.setDrawAxisLine(false);
//        xAxis.setTextSize(20);
//        xAxis.setTextColor(R.color.md_divider_white);
        xAxis.setEnabled(false);
        chart.invalidate(); // refresh
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader.reset();
    }

    public void getHistory(View view) {
        int days = view.getId();
        switch (days) {
            case R.id.week:
                arg.putString(DURATION, WEEK);
                getLoaderManager().restartLoader(CURSOR_LOADER_ID, arg, this);
                break;
            case R.id.month:
                arg.putString(DURATION, MONTH);
                getLoaderManager().restartLoader(CURSOR_LOADER_ID, arg, this);
                break;
            case R.id.sixMonths:
                arg.putString(DURATION, SIX_MONTHS);
                getLoaderManager().restartLoader(CURSOR_LOADER_ID, arg, this);
                break;
            case R.id.year:
                arg.putString(DURATION, YEAR);
                getLoaderManager().restartLoader(CURSOR_LOADER_ID, arg, this);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }

    @Override
    public void onStop() {
        super.onStop();


    }
}
