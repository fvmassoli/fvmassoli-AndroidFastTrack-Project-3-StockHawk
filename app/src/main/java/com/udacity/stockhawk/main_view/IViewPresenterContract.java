package com.udacity.stockhawk.main_view;


import android.database.Cursor;

import com.udacity.stockhawk.IBasePresenter;
import com.udacity.stockhawk.IBaseView;

import java.util.List;

public interface IViewPresenterContract {

    interface View extends IBaseView<Presenter> {

        void setEmptyView(Cursor data);

        void onCursorLoaderFinished(Cursor data);

        void onCursorLoaderReset();

        void noNetworkAvailable();

        void stopRefreshing();

        void dataMayBeOld();

        void showMainDialogAddStocks();

        void startRefreshing();

        void showToast(String stockSymbol);

        List<String> getStocksSymbol();

        void updateWidget();
    }

    interface Presenter extends IBasePresenter {

        void scheduleJob();

        void addStock(String stockSymbol);

        void addDefaultStocks(String[] values);

        void onItemSwiped(String symbol);

        void loadLocalData();

        void checkConnectivity();

    }
}
