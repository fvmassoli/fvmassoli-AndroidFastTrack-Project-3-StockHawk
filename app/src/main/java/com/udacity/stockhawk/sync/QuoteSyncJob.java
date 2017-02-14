package com.udacity.stockhawk.sync;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.IModelContract;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuote;

import static com.udacity.stockhawk.utilities.Constants.ACTION_DATA_ADDED;
import static com.udacity.stockhawk.utilities.Constants.ACTION_DATA_NOT_FOUND;
import static com.udacity.stockhawk.utilities.Constants.ACTION_DATA_UPDATED;
import static com.udacity.stockhawk.utilities.Constants.FIREBASE_JOB_TAG;
import static com.udacity.stockhawk.utilities.Constants.REMINDER_INTERVAL_SECONDS;
import static com.udacity.stockhawk.utilities.Constants.STOCK_SYMBOL_IS_EMPTY;
import static com.udacity.stockhawk.utilities.Constants.SYNC_FLEX_TIME_SECONDS;
import static com.udacity.stockhawk.utilities.Constants.YEARS_OF_HISTORY;
import static com.udacity.stockhawk.utilities.Constants.TAG;

public final class QuoteSyncJob {

   public static boolean sInitialize;

    private QuoteSyncJob() {
    }


    public static void getQuotes(Context context) {

        Timber.d("Running sync job");

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, -YEARS_OF_HISTORY);

        Cursor cursor = context.getContentResolver().query(
                Contract.Quote.URI,
                Contract.Quote.QUOTE_STOCKS_SYMOBL.toArray(new String[]{}),
                null,
                null,
                Contract.Quote._ID);
        String[] stocks = new String[cursor.getCount()];
        for(int i=0; i<cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            stocks[i] = cursor.getString(Contract.Quote.POSITION_SYMBOL);
        }

        if(stocks.length != 0) {
            try {

                Map<String, Stock> quotes = YahooFinance.get(stocks);

                Timber.d(quotes.toString());

                ArrayList<ContentValues> quoteCVs = new ArrayList<>();

                for(int i=0; i<stocks.length; i++) {
                    String symbol = stocks[i];

                    Stock stock = quotes.get(symbol);
                    StockQuote quote = stock.getQuote();

                    float price = quote.getPrice().floatValue();
                    float change = quote.getChange().floatValue();
                    float percentChange = quote.getChangeInPercent().floatValue();

                    List<HistoricalQuote> history = stock.getHistory(from, to, Interval.WEEKLY);

                    StringBuilder historyValueBuilder = new StringBuilder();
                    StringBuilder historyDateBuilder = new StringBuilder();

                    for (HistoricalQuote it : history) {
                        historyDateBuilder.append(it.getDate().getTimeInMillis());
                        historyDateBuilder.append(" ");
                        historyValueBuilder.append(it.getClose());
                        historyValueBuilder.append(" ");
                    }

                    ContentValues quoteCV = new ContentValues();

                    quoteCV.put(Contract.Quote.COLUMN_SYMBOL, symbol);
                    quoteCV.put(Contract.Quote.COLUMN_PRICE, price);
                    quoteCV.put(Contract.Quote.COLUMN_PERCENTAGE_CHANGE, percentChange);
                    quoteCV.put(Contract.Quote.COLUMN_ABSOLUTE_CHANGE, change);
                    quoteCV.put(Contract.Quote.COLUMN_HISTORY_VALUE, historyValueBuilder.toString());
                    quoteCV.put(Contract.Quote.COLUMN_HISTORY_DATE, historyDateBuilder.toString());

                    quoteCVs.add(quoteCV);
                }

                context.getContentResolver()
                        .bulkInsert(
                                Contract.Quote.URI,
                                quoteCVs.toArray(new ContentValues[quoteCVs.size()]));

                Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
                context.sendBroadcast(dataUpdatedIntent);

            } catch (UnknownHostException e){
                e.printStackTrace();
            } catch (NoRouteToHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Intent dataUpdatedIntent = new Intent(STOCK_SYMBOL_IS_EMPTY);
            context.sendBroadcast(dataUpdatedIntent);
        }
    }

    public static void getDefaultQuotes(Context context, String[] stocks) {

        Timber.d("Running sync job");

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, -YEARS_OF_HISTORY);

        StringBuilder historyValueBuilder;
        StringBuilder historyDateBuilder;

        if(stocks.length != 0) {
            try {

                Map<String, Stock> quotes = YahooFinance.get(stocks);

                Timber.d(quotes.toString());

                ArrayList<ContentValues> quoteCVs = new ArrayList<>();

                for(int i=0; i<stocks.length; i++) {

                    String symbol = stocks[i];

                    Stock stock = quotes.get(symbol);
                    StockQuote quote = stock.getQuote();

                    float price = quote.getPrice().floatValue();
                    float change = quote.getChange().floatValue();
                    float percentChange = quote.getChangeInPercent().floatValue();

                    List<HistoricalQuote> history = stock.getHistory(from, to, Interval.WEEKLY);

                    historyValueBuilder = new StringBuilder();
                    historyDateBuilder  = new StringBuilder();

                    for (HistoricalQuote it : history) {
                        historyDateBuilder.append(it.getDate().getTimeInMillis());
                        historyDateBuilder.append(" ");
                        historyValueBuilder.append(it.getClose());
                        historyValueBuilder.append(" ");
                    }

                    ContentValues quoteCV = new ContentValues();

                    quoteCV.put(Contract.Quote.COLUMN_SYMBOL, symbol);
                    quoteCV.put(Contract.Quote.COLUMN_PRICE, price);
                    quoteCV.put(Contract.Quote.COLUMN_PERCENTAGE_CHANGE, percentChange);
                    quoteCV.put(Contract.Quote.COLUMN_ABSOLUTE_CHANGE, change);
                    quoteCV.put(Contract.Quote.COLUMN_HISTORY_VALUE, historyValueBuilder.toString());
                    quoteCV.put(Contract.Quote.COLUMN_HISTORY_DATE, historyDateBuilder.toString());

                    quoteCVs.add(quoteCV);
                }

                context.getContentResolver()
                        .bulkInsert(
                                Contract.Quote.URI,
                                quoteCVs.toArray(new ContentValues[quoteCVs.size()]));

                Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
                context.sendBroadcast(dataUpdatedIntent);

            } catch (UnknownHostException e){
                e.printStackTrace();
            } catch (NoRouteToHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Intent dataUpdatedIntent = new Intent(STOCK_SYMBOL_IS_EMPTY);
            context.sendBroadcast(dataUpdatedIntent);
        }
    }

    public synchronized static void scheduleFirebaseJob(@NonNull Context context) {

        if(sInitialize) return;
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher firebaseJobDispatcher = new FirebaseJobDispatcher(driver);
        Job scheduledJob = firebaseJobDispatcher.newJobBuilder()
                .setService(QuoteFirebaseService.class)
                .setTag(FIREBASE_JOB_TAG)
                .setConstraints(Constraint.DEVICE_CHARGING, Constraint.ON_UNMETERED_NETWORK)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(REMINDER_INTERVAL_SECONDS, REMINDER_INTERVAL_SECONDS + SYNC_FLEX_TIME_SECONDS))
                .setReplaceCurrent(true)
                .build();
        firebaseJobDispatcher.schedule(scheduledJob);
        sInitialize = true;
    }

    public static synchronized void syncImmediatelyFirebaseJob(Context context) {
        Intent nowIntent = new Intent(context, QuoteIntentService.class);
        context.startService(nowIntent);
    }

    static void getQuote(Context context, String stockSymbol) {

        Timber.d("Running sync job");

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, -YEARS_OF_HISTORY);

        String[] stockArray = new String[]{stockSymbol};

        try {

            if (stockArray.length == 0) return;

            Stock stock = YahooFinance.get(stockSymbol);

            ContentValues quoteCV = new ContentValues();

            StockQuote quote = stock.getQuote();

            if (!quote.toString().contains("null")) {

                float price = quote.getPrice().floatValue();
                float change = quote.getChange().floatValue();
                float percentChange = quote.getChangeInPercent().floatValue();

                List<HistoricalQuote> history = stock.getHistory(from, to, Interval.WEEKLY);

                StringBuilder historyValueBuilder = new StringBuilder();
                StringBuilder historyDateBuilder = new StringBuilder();

                for (HistoricalQuote it : history) {
                    historyDateBuilder.append(it.getDate().getTimeInMillis());
                    historyDateBuilder.append(" ");
                    historyValueBuilder.append(it.getClose());
                    historyValueBuilder.append(" ");
                }

                quoteCV.put(Contract.Quote.COLUMN_SYMBOL, stockSymbol);
                quoteCV.put(Contract.Quote.COLUMN_PRICE, price);
                quoteCV.put(Contract.Quote.COLUMN_PERCENTAGE_CHANGE, percentChange);
                quoteCV.put(Contract.Quote.COLUMN_ABSOLUTE_CHANGE, change);
                quoteCV.put(Contract.Quote.COLUMN_HISTORY_VALUE, historyValueBuilder.toString());
                quoteCV.put(Contract.Quote.COLUMN_HISTORY_DATE, historyDateBuilder.toString());

                context.getContentResolver().insert(Contract.Quote.URI, quoteCV);

                Intent dataUpdatedIntent = new Intent(ACTION_DATA_ADDED);
                context.sendBroadcast(dataUpdatedIntent);
            } else {
                Intent dataUpdatedIntent = new Intent(ACTION_DATA_NOT_FOUND);
                context.sendBroadcast(dataUpdatedIntent);
            }

        } catch (IOException exception) {
            Log.d(TAG, exception.toString());
        } catch (Error error) {
            throw new Error(error.toString());
        } catch (StringIndexOutOfBoundsException exception) {
            throw new StringIndexOutOfBoundsException("StringIndexOutOfBoundsException  " + exception.toString());
        }
    }

}
