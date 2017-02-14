package com.udacity.stockhawk.data;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.sync.SingleStockIntentService;
import com.udacity.stockhawk.utilities.Constants;
import com.udacity.stockhawk.utilities.Utilities;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.udacity.stockhawk.utilities.Constants.STOCK_LOADER;
import static com.udacity.stockhawk.utilities.Constants.STOCK_SYMBOL;

public class ModelPresenter implements IModelContract {

    private static ModelPresenter INSTANCE = null;

    private IModelContract.DataDownloadedCallback mDataDownloadedCallback;
    private IModelContract.IDownloadData mRemoteDataSource;
    private IModelContract.ILocalStorage mLocalDataSource;
    private Context       mContext;
    private LoaderManager mLoaderManager;

    private ModelPresenter(@NonNull IModelContract.IDownloadData tasksRemoteDataSource,
                            @NonNull IModelContract.ILocalStorage tasksLocalDataSource) {
        mRemoteDataSource = checkNotNull(tasksRemoteDataSource);
        mLocalDataSource = checkNotNull(tasksLocalDataSource);
    }

    public static ModelPresenter getInstance(IDownloadData remoteDataSource,
                                             LocalDataSource localDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new ModelPresenter(remoteDataSource, localDataSource);
        }
        return INSTANCE;
    }

    @Override
    public void setMainPresenter(DataDownloadedCallback dataDownloadedCallback, LoaderManager loaderManager,
                                 Context context) {
        this.mContext = context;
        this.mLoaderManager = checkNotNull(loaderManager);
        this.mDataDownloadedCallback = checkNotNull(dataDownloadedCallback);
    }

    @Override
    public void syncronizeDataImmediately() {
        mRemoteDataSource.downloadImmediately(mContext);
    }

    @Override
    public void scheduleFirebaseJob(final IModelContract.DataDownloadedCallback dataDownloadedCallback, Context context) {
        checkNotNull(dataDownloadedCallback);
        mRemoteDataSource.scheduleDownloads(context);
    }

    @Override
    public void downloadStock(List<String> list, String stockSymbol) {
        if(list != null) {
            for (String s : list) {
                if (s.equals(stockSymbol)) {
                    mDataDownloadedCallback.onStockLareadyPresent(s);
                    return;
                }
            }
        }
        mRemoteDataSource.addStock(mContext, stockSymbol);
    }

    @Override
    public void downloadDefaultStocks(String[] values){
        mRemoteDataSource.addDefaultStocks(mContext, values);
    }

    @Override
    public void loadLocalData() {
        mLocalDataSource.loadDataFromLocalDb(mLoaderManager, mContext, mDataDownloadedCallback);
    }

    @Override
    public void removeStockFromLocalStorage(String symbol) {
        mLocalDataSource.removeDataFromLocalDb(mContext, symbol);
    }

}
