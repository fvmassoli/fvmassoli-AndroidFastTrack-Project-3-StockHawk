package com.udacity.stockhawk.utilities;


import java.util.concurrent.TimeUnit;

public class Constants {

    public static final String TAG = "logDebugInfo";

    public static final String DEFAULT_ARRAY_STOCKS      = "default_array_stocks";
    public static final String FIREBASE_JOB_TAG          = "stock_async_firebase_job";
    public static final String ACTION_DATA_ADDED         = "com.udacity.stockhawk.ACTION_DATA_ADDED";
    public static final String ACTION_DATA_NOT_FOUND     = "com.udacity.stockhawk.ACTION_DATA_NOT_FOUND";
    public static final String ACTION_DATA_UPDATED       = "com.udacity.stockhawk.ACTION_DATA_UPDATED";
    public static final String STOCK_SYMBOL_IS_EMPTY     = "com.udacity.stockhawk.STOCK_SYMBOL_IS_EMPTY";
    public static final String NETWORK_STATE_CHANGED     = "android.net.conn.CONNECTIVITY_CHANGE";

    public static final String STOCK_SYMBOL              = "stockSymbol";
    public static final String MAIN_FRAGMENT_TAG         = "MFT";
    public static final String DETAIL_FRAGMENT_TAG       = "DFT";
    public static final String STOCK_POSITION            = "stock";
    public static final String DIALOG_TYPE               = "dialog_type";

    public static final int STOCK_LOADER               = 1000;
    public static final int FAKE_POSITION              = -1;
    public static final int YEARS_OF_HISTORY           = 2;
    public static final int REMINDER_INTERVAL_MINUTES  = 15;
    public static final int REMINDER_INTERVAL_SECONDS  = (int) TimeUnit.MINUTES.toSeconds(REMINDER_INTERVAL_MINUTES);
    public static final int SYNC_FLEX_TIME_SECONDS     = REMINDER_INTERVAL_SECONDS;

}
