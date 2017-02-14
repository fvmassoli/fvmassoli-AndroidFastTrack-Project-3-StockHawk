package com.udacity.stockhawk.data;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.udacity.stockhawk.utilities.Constants.STOCK_LOADER;

public class LocalDataSource implements IModelContract.ILocalStorage, LoaderManager.LoaderCallbacks<Cursor> {

    private static LocalDataSource INSTANCE;
    private Context mContext;
    private IModelContract.DataDownloadedCallback mDataDownloadedCallback;

    private LocalDataSource(@NonNull ContentResolver contentResolver) {
        checkNotNull(contentResolver);
    }

    public static LocalDataSource getInstance(@NonNull ContentResolver contentResolver) {
        if (INSTANCE == null) {
            INSTANCE = new LocalDataSource(contentResolver);
        }
        return INSTANCE;
    }

    @Override
    public void loadDataFromLocalDb(LoaderManager loaderManager, Context context,
                                    IModelContract.DataDownloadedCallback dataDownloadedCallback) {
        this.mContext = context;
        this.mDataDownloadedCallback = dataDownloadedCallback;
        if(loaderManager.getLoader(STOCK_LOADER) != null)
            loaderManager.restartLoader(STOCK_LOADER, null, this);
        else
            loaderManager.initLoader(STOCK_LOADER, null, this);
    }

    @Override
    public void removeDataFromLocalDb(Context context, String symbol) {
        context.getContentResolver().delete(Contract.Quote.makeUriForStock(symbol), null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                mContext,
                Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null,
                null,
                Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mDataDownloadedCallback.onUpdateWidget();
        if(data.getCount() != 0)
            mDataDownloadedCallback.onLocalDataLoaded(data);
        else
            mDataDownloadedCallback.onLocalDataNotAvailable(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDataDownloadedCallback.onLocalDataLoaderReset();
    }
}
