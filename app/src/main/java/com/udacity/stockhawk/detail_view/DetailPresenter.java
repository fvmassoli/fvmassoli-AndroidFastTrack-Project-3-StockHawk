package com.udacity.stockhawk.detail_view;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.Stock;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.udacity.stockhawk.utilities.Constants.FAKE_POSITION;
import static com.udacity.stockhawk.utilities.Constants.STOCK_LOADER;


public class DetailPresenter implements LoaderManager.LoaderCallbacks<Cursor>, IDetailViewPresenterContract.Presenter {

    private Context mContext;
    private IDetailViewPresenterContract.View mDetailView;
    private AppCompatActivity mAppCompatActivity;
    private int mPosition;
    private DecimalFormat dollarFormat;
    private List<String>  mHistoryValue;
    private List<String>  mHistoryDate;

    public DetailPresenter(IDetailViewPresenterContract.View detailView, Context context) {
        this.mContext    = context;
        this.mDetailView = detailView;
        this.mAppCompatActivity = (AppCompatActivity) mContext;
    }

    @Override
    public void setPresenter(){
        mDetailView.setPresenter(this);
    }

    @Override
    public void start(int position) {
        mPosition = position;
        if(position != FAKE_POSITION) {
            mDetailView.showLoadingDialog();
            if (mAppCompatActivity.getSupportLoaderManager().getLoader(STOCK_LOADER) == null)
                mAppCompatActivity.getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);
            else
                mAppCompatActivity.getSupportLoaderManager().restartLoader(STOCK_LOADER, null, this);
        } else {
            mDetailView.showErrorMessage();
        }
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
        setStockDetails(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void setStockDetails(Cursor data){
        Stock stock = new Stock();
        mDetailView.hideLoadingDialog();
        if(data != null && data.getCount() != 0) {
            data.moveToPosition(mPosition);
            dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
            stock.setPrice(dollarFormat.format(data.getFloat(Contract.Quote.POSITION_PRICE)));
            stock.setSymbol(data.getString(Contract.Quote.POSITION_SYMBOL));
            stock.setAbsoluteChange(data.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE));
            stock.setPercentageChange(data.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE));

            String historyValueStr = data.getString(Contract.Quote.POSITION_HISTORY_VALUE);
            String[] historyValue  = historyValueStr.split("\\s");
            String historyDateStr  = data.getString(Contract.Quote.POSITION_HISTORY_DATE);
            String[] historyDate   = historyDateStr.split("\\s");

            mHistoryValue = new LinkedList<>();
            mHistoryDate  = new LinkedList<>();
            for (int i = 0; i < historyValue.length; i++) {
                mHistoryValue.add(historyValue[i]);
                mHistoryDate.add(historyDate[i]);
            }

            stock.setHistoryValue(mHistoryValue);
            stock.setHistoryDate(mHistoryDate);
            mDetailView.onLocalDataLoaded(stock);
        } else {
            mDetailView.onLocalDataLoaded(null);
        }
    }

}
