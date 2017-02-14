package com.udacity.stockhawk.data;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;

import java.util.List;

public interface IModelContract {

    void setMainPresenter(IModelContract.DataDownloadedCallback dataDownloadedCallback, LoaderManager loaderManager,
                          Context context);

    void scheduleFirebaseJob(IModelContract.DataDownloadedCallback dataDownloadedCallback, Context context);

    void downloadStock(List<String> list, String stockSymbol);

    void downloadDefaultStocks(String[] values);

    void syncronizeDataImmediately();

    void loadLocalData();

    void removeStockFromLocalStorage(String symbol);

    interface DataDownloadedCallback{

        void onUpdateWidget();

        void onLocalDataLoaded(Cursor data);

        void onLocalDataLoaderReset();

        void onLocalDataNotAvailable(Cursor data);

        void onStockLareadyPresent(String stockSymbol);
    }

    interface IDownloadData {

        void downloadImmediately(Context context);

        void scheduleDownloads(Context context);

        void addStock(Context context, String stockSymbol);

        void addDefaultStocks(Context context, String[] stocks);

    }

    interface ILocalStorage {

        void loadDataFromLocalDb(LoaderManager loaderManager, Context context,
                                 IModelContract.DataDownloadedCallback dataDownloadedCallback);

        void removeDataFromLocalDb(Context context, String symbol);

    }

}
