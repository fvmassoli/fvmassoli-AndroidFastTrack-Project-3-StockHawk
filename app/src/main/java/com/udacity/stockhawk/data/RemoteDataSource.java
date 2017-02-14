package com.udacity.stockhawk.data;


import android.content.Context;
import android.content.Intent;

import com.udacity.stockhawk.sync.DefaultQuotesService;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.sync.SingleStockIntentService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.udacity.stockhawk.utilities.Constants.DEFAULT_ARRAY_STOCKS;
import static com.udacity.stockhawk.utilities.Constants.STOCK_SYMBOL;

public class RemoteDataSource implements IModelContract.IDownloadData {

    private static RemoteDataSource INSTANCE;

    private RemoteDataSource() {
    }

    public static RemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RemoteDataSource();
        }
        return INSTANCE;
    }

    @Override
    public void downloadImmediately(Context context) {
        QuoteSyncJob.syncImmediatelyFirebaseJob(context);
    }

    @Override
    public void scheduleDownloads(Context context) {
        QuoteSyncJob.scheduleFirebaseJob(context);
    }

    @Override
    public void addStock(Context context, String stockSymbol) {
        context.startService(new Intent(context, SingleStockIntentService.class)
                .putExtra(STOCK_SYMBOL, stockSymbol));
    }

    @Override
    public void addDefaultStocks(Context context, String[] stocks) {
        context.startService(new Intent(context, DefaultQuotesService.class)
                    .putExtra(DEFAULT_ARRAY_STOCKS, stocks));
    }
}
