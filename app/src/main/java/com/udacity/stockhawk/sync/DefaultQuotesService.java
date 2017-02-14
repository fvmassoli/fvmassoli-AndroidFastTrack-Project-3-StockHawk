package com.udacity.stockhawk.sync;


import android.app.IntentService;
import android.content.Intent;

import static com.udacity.stockhawk.utilities.Constants.DEFAULT_ARRAY_STOCKS;

public class DefaultQuotesService extends IntentService {

    public DefaultQuotesService() {
        super(DefaultQuotesService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        QuoteSyncJob.getDefaultQuotes(this, intent.getStringArrayExtra(DEFAULT_ARRAY_STOCKS));
    }
}
