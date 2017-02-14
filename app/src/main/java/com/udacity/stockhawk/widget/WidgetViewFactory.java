package com.udacity.stockhawk.widget;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Stock;
import java.util.ArrayList;

import static com.udacity.stockhawk.utilities.Constants.STOCK_POSITION;

public class WidgetViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<Stock> stockList;

    private Context ctxt=null;
    private int appWidgetId;

    public WidgetViewFactory(Context ctxt, Intent intent) {
        this.ctxt=ctxt;
        appWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        return stockList != null ? stockList.size() : 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {

        if(stockList != null && stockList.size() != 0) {

            Stock stock = stockList.get(position);

            RemoteViews row = new RemoteViews(ctxt.getPackageName(), R.layout.widget_layout_row_item);

            float absoluteChange = stock.getAbsoluteChange();

            row.setTextViewText(R.id.symbol, stockList.get(position).getSymbol());
            row.setTextViewText(R.id.price, stock.getPrice());
            row.setTextColor(R.id.price, getColor(absoluteChange));
            row.setImageViewResource(R.id.iv_trending, getTrend(absoluteChange));

            Bundle extras = new Bundle();
            extras.putInt(STOCK_POSITION, position);
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            row.setOnClickFillInIntent(R.id.symbol, fillInIntent);
            row.setOnClickFillInIntent(R.id.price, fillInIntent);
            row.setOnClickFillInIntent(R.id.iv_trending, fillInIntent);
            row.setOnClickFillInIntent(R.id.iv_frame, fillInIntent);

            return row;
        } else
            return null;
    }

    private int getColor(float absoluteChange){
        return absoluteChange >= 0 ? ResourcesCompat.getColor(ctxt.getResources(), R.color.material_green_700, null)
                : ResourcesCompat.getColor(ctxt.getResources(), R.color.material_red_700, null);
    }
    private int getTrend(float absoluteChange){
        return absoluteChange >= 0 ? R.drawable.ic_trending_up_green_24dp : R.drawable.ic_trending_down_red_24dp;
    }

    @Override
    public RemoteViews getLoadingView()
    {
        return(null);
    }

    @Override
    public int getViewTypeCount() {
        return(1);
    }

    @Override
    public long getItemId(int position) {
        return(position);
    }

    @Override
    public boolean hasStableIds() {
        return(true);
    }

    @Override
    public void onDataSetChanged() {
        stockList = new ArrayList<>();
        stockList = (ArrayList<Stock>) FetchDataService.stockList.clone();
    }
}