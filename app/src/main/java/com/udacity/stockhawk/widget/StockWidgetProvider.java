package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.detail_view.DetailActivity;
import com.udacity.stockhawk.main_view.MainActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.udacity.stockhawk.utilities.Constants.STOCK_POSITION;

public class StockWidgetProvider extends AppWidgetProvider {

    public static final String UPDATE                 = "com.example.android.stackwidget.UPDATE";
    public static final String LAUNCH_DETAIL_ACTIIVTY = "com.example.android.stackwidget.LAUNCH_DETAIL_ACTIIVTY";
    public static final String LAUNCH_MAIN_ACTIIVTY   = "com.example.android.stackwidget.LAUNCH_MAIN_ACTIIVTY";
    public static final String STOCKS_CHANGED         = "android.appwidget.action.STOCKS_CHANGED";
    public static final String NOTIFY_WIDGET          = "android.appwidget.action.NOTIFY_WIDGET";

    public StockWidgetProvider(){
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        if (intent.getAction().equals(UPDATE)) {

            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            RemoteViews remoteViews = updateWidgetListView(context, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

        } else if (intent.getAction().equals(LAUNCH_DETAIL_ACTIIVTY)) {

            context.startActivity(new Intent(context, DetailActivity.class)
                            .setFlags(FLAG_ACTIVITY_NEW_TASK)
                            .putExtra(STOCK_POSITION, intent.getIntExtra(STOCK_POSITION, 0)));

        } else if (intent.getAction().equals(LAUNCH_MAIN_ACTIIVTY)) {

            context.startActivity(new Intent(context, MainActivity.class)
                    .setFlags(FLAG_ACTIVITY_NEW_TASK));

        } else if (intent.getAction().equals(STOCKS_CHANGED)) {
            ComponentName component = new ComponentName(context, StockWidgetProvider.class);
            int[] ids = appWidgetManager.getAppWidgetIds(component);

            if(ids.length != 0) {
                Intent serviceIntent = new Intent(context, FetchDataService.class);
                serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, ids[0]);
                serviceIntent.putExtra(NOTIFY_WIDGET, true);
                context.startService(serviceIntent);
            }

        } else if (intent.getAction().equals(NOTIFY_WIDGET)) {
            ComponentName component = new ComponentName(context, StockWidgetProvider.class);
            int[] ids = appWidgetManager.getAppWidgetIds(component);
            for(int i=0; i<ids.length; i++) {
                appWidgetManager.notifyAppWidgetViewDataChanged(ids[i], R.id.listViewWidget);
            }
        }


    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            Intent serviceIntent = new Intent(context, FetchDataService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            context.startService(serviceIntent);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        Intent svcIntent = new Intent(context, WidgetRemoteService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(R.id.listViewWidget, svcIntent);
        remoteViews.setEmptyView(R.id.listViewWidget, R.id.empty_view);
        remoteViews.setImageViewResource(R.id.widget_icon, R.drawable.ic_trending_up_white_24dp);

        startMainActivity(context, remoteViews);
        startDetrailActivity(context, remoteViews);

        return remoteViews;
    }

    private void startMainActivity(Context context, RemoteViews remoteViews){
        Intent startMainActivityIntent = new Intent(context, StockWidgetProvider.class);
        startMainActivityIntent.setAction(StockWidgetProvider.LAUNCH_MAIN_ACTIIVTY);
        PendingIntent mainActivityPendingIntent = PendingIntent.getBroadcast(context, 0, startMainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_icon, mainActivityPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.widget_header_title, mainActivityPendingIntent);
    }

    private void startDetrailActivity(Context context, RemoteViews remoteViews){
        Intent startDetailActivityIntent = new Intent(context, StockWidgetProvider.class);
        startDetailActivityIntent.setAction(StockWidgetProvider.LAUNCH_DETAIL_ACTIIVTY);
        PendingIntent detailActivityPendingIntent = PendingIntent.getBroadcast(context, 0, startDetailActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.listViewWidget, detailActivityPendingIntent);
    }

}
