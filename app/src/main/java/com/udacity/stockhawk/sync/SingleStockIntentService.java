package com.udacity.stockhawk.sync;

import android.app.IntentService;
import android.content.Intent;
import timber.log.Timber;

import static com.udacity.stockhawk.utilities.Constants.STOCK_SYMBOL;


public class SingleStockIntentService extends IntentService {

    public SingleStockIntentService() {
        super(SingleStockIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("Intent handled");
        QuoteSyncJob.getQuote(this, intent.getStringExtra(STOCK_SYMBOL));
    }
}