package com.udacity.stockhawk.utilities;
import android.content.Context;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.data.Stock;

public interface IChartDrawer {

    interface ChartViewParent{

        void setChartDrawer(ChartDrawer chartDrawer);

    }

    interface ChartDrawer{

        void initChartDrawer(Context context, Stock stock, LineChart lineChart, boolean isFromMainView);

        void drawChart();

        void setAxis();

        void initializeChart();

        void addEntries();

        LineDataSet createSet();
    }

}
