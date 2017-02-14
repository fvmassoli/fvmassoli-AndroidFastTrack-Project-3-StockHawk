package com.udacity.stockhawk.utilities;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.res.ResourcesCompat;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.LocalDataSource;
import com.udacity.stockhawk.data.ModelPresenter;
import com.udacity.stockhawk.data.RemoteDataSource;

public class Utilities {

    public static boolean networkUp(Context context){
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static ModelPresenter injectDependency(Context context){
        return ModelPresenter.getInstance(
                RemoteDataSource.getInstance(),
                LocalDataSource.getInstance(context.getContentResolver()));
    }

    public static int getTrendColor(Context context, float absoluteChange){
        if(absoluteChange >= 0)
            return  ResourcesCompat.getColor(context.getResources(), R.color.material_green_700, null);
        else
            return ResourcesCompat.getColor(context.getResources(), R.color.material_red_700, null);
    }

}
