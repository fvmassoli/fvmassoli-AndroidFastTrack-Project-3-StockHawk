package com.udacity.stockhawk.widget;


import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.Stock;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import static com.udacity.stockhawk.widget.StockWidgetProvider.NOTIFY_WIDGET;
import static com.udacity.stockhawk.widget.StockWidgetProvider.UPDATE;

public class FetchDataService extends IntentService {

    private int appWidgetId;
    public static ArrayList<Stock> stockList;
    private DecimalFormat dollarFormat;
    private Intent        mIntent;

    public FetchDataService() {
        super(FetchDataService.class.getSimpleName());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID))
            appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

        Log.d("asdasda", "onStartCommand");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        stockList = new ArrayList<>();
        Cursor cursor = this.getContentResolver().query(
                Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null,
                null,
                Contract.Quote.COLUMN_SYMBOL);

        while (cursor.moveToNext()){
            Stock stock = new Stock();
            stock.setSymbol(cursor.getString(Contract.Quote.POSITION_SYMBOL));
            stock.setAbsoluteChange(cursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE));
            dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
            stock.setPrice(dollarFormat.format(cursor.getFloat(Contract.Quote.POSITION_PRICE)));
            stockList.add(stock);
        }

        mIntent = new Intent(this, StockWidgetProvider.class);
        mIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        if(intent.hasExtra(NOTIFY_WIDGET))
            mIntent.setAction(NOTIFY_WIDGET);
        else
            mIntent.setAction(UPDATE);

        sendBroadcast(mIntent);
    }
}
