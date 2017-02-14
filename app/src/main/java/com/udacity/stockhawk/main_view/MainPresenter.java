package com.udacity.stockhawk.main_view;


import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.util.Log;

import com.udacity.stockhawk.data.IModelContract;
import com.udacity.stockhawk.utilities.Utilities;
import java.util.List;

public class MainPresenter implements IViewPresenterContract.Presenter, IModelContract.DataDownloadedCallback {

    private IViewPresenterContract.View mMainView;
    private Context mContext;
    private LoaderManager  mLoaderManager;
    private IModelContract mModelPresenter;

    public MainPresenter(Context context, @NonNull IViewPresenterContract.View mainView, LoaderManager loaderManager,
                         IModelContract modelPresenter) {
        this.mContext  = context;
        this.mMainView = mainView;
        this.mLoaderManager  = loaderManager;
        this.mModelPresenter = modelPresenter;

        mModelPresenter.setMainPresenter(this, mLoaderManager, context);
        mMainView.setPresenter(this);
    }

    @Override
    public void checkConnectivity(){
        if(Utilities.networkUp(mContext))
            mModelPresenter.syncronizeDataImmediately();
        else
            loadLocalData();
    }

    @Override
    public void loadLocalData(){
        mMainView.startRefreshing();
        mModelPresenter.loadLocalData();
    }

    @Override
    public void onUpdateWidget() {
        mMainView.updateWidget();
    }

    @Override
    public void onLocalDataLoaded(Cursor data) {
        mMainView.stopRefreshing();
        mMainView.onCursorLoaderFinished(data);
    }

    @Override
    public void onLocalDataNotAvailable(Cursor data) {
        mMainView.stopRefreshing();
        if(!Utilities.networkUp(mContext))
            mMainView.noNetworkAvailable();
        mMainView.setEmptyView(data);
    }

    @Override
    public void onLocalDataLoaderReset() {
        mMainView.stopRefreshing();
        mMainView.onCursorLoaderReset();
    }

    @Override
    public void addStock(String stockSymbol) {
        mMainView.startRefreshing();
        List<String> list = mMainView.getStocksSymbol();
        mModelPresenter.downloadStock(list, stockSymbol);
    }

    @Override
    public void addDefaultStocks(String[] values) {
        mMainView.startRefreshing();
        mModelPresenter.downloadDefaultStocks(values);
    }

    @Override
    public void onItemSwiped(String symbol) {
        mModelPresenter.removeStockFromLocalStorage(symbol);
        loadLocalData();
    }

    @Override
    public void onStockLareadyPresent(String stockSymbol) {
        mMainView.showToast(stockSymbol);
    }

    @Override
    public void start() {
        scheduleJob();
    }

    @Override
    public void scheduleJob(){
        mModelPresenter.scheduleFirebaseJob(this, mContext);
    }


}
