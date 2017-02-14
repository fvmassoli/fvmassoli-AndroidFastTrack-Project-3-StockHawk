package com.udacity.stockhawk.utilities;


import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Stock;
import com.udacity.stockhawk.utilities.IChartDrawer;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import static com.udacity.stockhawk.utilities.Utilities.getTrendColor;

public class ChartDrawer implements IChartDrawer.ChartDrawer {

    private IChartDrawer.ChartViewParent mChartView;
    private LineChart mLineChart;
    private YAxis     mYaxis;
    private XAxis     mXaxis;
    private LineData  mLineData;
    private Context   mContext;

    private final float mChartLineWidth = 3.f;
    private final float mCircleRadius   = 3.0f;

    private Stock mStock;
    private List<String> mHistoryValue = new LinkedList<>();
    private List<String> mHistoryDate  = new LinkedList<>();
    private List<String> mHistoryDate_sorted  = new LinkedList<>();
    private float   mXrotationAngle    = -45.f;
    private boolean mIsFromMainView;

    public ChartDrawer(IChartDrawer.ChartViewParent chartView){
        this.mChartView = chartView;
        chartView.setChartDrawer(this);
    }

    @Override
    public void initChartDrawer(Context context,
                       Stock stock, LineChart lineChart, boolean isFromMainView) {
        this.mContext = context;
        this.mStock = stock;
        this.mLineChart = lineChart;
        this.mLineData = new LineData();
        this.mIsFromMainView = isFromMainView;
    }

    @Override
    public void drawChart(){
        if(mStock != null) {
            mHistoryValue = mStock.getHistoryValue();
            mHistoryDate  = mStock.getHistoryDate();
            addEntries();
        }
    }

    @Override
    public void setAxis(){
        mXaxis = mLineChart.getXAxis();
        mXaxis.setDrawGridLines(false);
        mXaxis.setDrawAxisLine(true);
        mXaxis.setAvoidFirstLastClipping(true);
        mXaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        mXaxis.setGranularity(2f);

        mYaxis = mLineChart.getAxisLeft();
        mYaxis.setDrawGridLines(true);
        mYaxis.setDrawAxisLine(false);
        mLineChart.getAxisRight().setEnabled(false);

        if(!mIsFromMainView) {
            mXaxis.setLabelRotationAngle(mXrotationAngle);
            mXaxis.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.text_color, null));
            mXaxis.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.text_color, null));
        } else {
            mXaxis.setDrawLabels(false);
            mYaxis.setDrawLabels(false);
        }
    }

    @Override
    public void initializeChart() {

        Legend legend = mLineChart.getLegend();
        legend.setEnabled(false);

        mLineChart.setNoDataText(mContext.getString(R.string.no_data_available));
        mLineChart.setDescription(null);
        mLineChart.setContentDescription("");
        mLineChart.setContentDescription(null);
        mLineChart.setScaleEnabled(true);
        mLineChart.setDrawGridBackground(false);
        mLineChart.setPinchZoom(false);
        mLineChart.setBackgroundResource(android.R.color.transparent);
        mLineChart.setData(mLineData);
        mLineChart.animateX(mContext.getResources().getInteger(R.integer.chart_animation_duration));

        if(mIsFromMainView){
            mLineChart.setTouchEnabled(false);
            mLineChart.setDoubleTapToZoomEnabled(false);
            mLineChart.setDragEnabled(false);
        } else {
            mLineChart.setTouchEnabled(true);
            mLineChart.setDoubleTapToZoomEnabled(true);
            mLineChart.setDragEnabled(true);
        }

        mLineChart.invalidate();
    }

    @Override
    public void addEntries() {

        LineDataSet lineDataSet;

        mLineData.setValueTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.text_color, null));

        if (mLineData != null) {
            lineDataSet = (LineDataSet) mLineData.getDataSetByIndex(0);

            if (lineDataSet == null)
                mLineData.addDataSet(createSet());

            int historyLenght = mHistoryValue.size();
            if (mIsFromMainView)
                historyLenght = 30;
            for (int i = 1; i <= historyLenght; i++)
                mLineData.addEntry(new Entry(i, Float.parseFloat(mHistoryValue.get(historyLenght - i))), 0);

            mLineChart.notifyDataSetChanged();
            if (!mIsFromMainView) {
                for (int i = 1; i <= historyLenght; i++)
                    mHistoryDate_sorted.add(mHistoryDate.get(historyLenght - i));
                mXaxis.setValueFormatter(new MyXAxisValueFormatter(mHistoryDate_sorted));
            }
            setLineDatasetColor();
            mLineChart.moveViewToX(mLineData.getXMax());
            mLineChart.setVisibleXRangeMaximum(15);
            mLineChart.setMaxVisibleValueCount(10);
        }
    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {

        private List<String> mValues;

        public MyXAxisValueFormatter(List<String> values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if(value < mValues.size())
                return getDate(Long.parseLong(mValues.get((int) value)));
            else
                return "";
        }

    }

    private void setLineDatasetColor(){
        int colorId = getTrendColor(mContext, mStock.getAbsoluteChange());
        ((LineDataSet) mLineChart.getData().getDataSetByIndex(0)).setColor(colorId);
    }

    @Override
    public LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setDrawCircleHole(false);
        set.setLineWidth(mChartLineWidth);
        set.setCircleRadius(mCircleRadius);
        set.setDrawCircles(false);
        set.setDrawFilled(true);
        set.setFillColor(ResourcesCompat.getColor(mContext.getResources(), R.color.colorPrimaryDark, null));
        set.setValueTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.white, null));
        return set;
    }


    private String getDate(long milliSeconds)
    {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
