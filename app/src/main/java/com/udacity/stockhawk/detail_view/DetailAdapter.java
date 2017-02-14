package com.udacity.stockhawk.detail_view;


import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.github.mikephil.charting.charts.LineChart;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Stock;
import com.udacity.stockhawk.utilities.ChartDrawer;
import com.udacity.stockhawk.utilities.IChartDrawer;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.udacity.stockhawk.utilities.Utilities.getTrendColor;

public class DetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IChartDrawer.ChartViewParent {

    public static final int TYPE_CHART = 0;
    public static final int TYPE_TEXT  = 1;

    private Context mContext;
    private Stock   mStock;
    private IChartDrawer.ChartDrawer mChartDrawer;

    private final DecimalFormat dollarFormatWithPlus;
    private final DecimalFormat percentageFormat;

    private String mDollarFormatPrefix;
    private String mPercentageFormatPrefix;

    public DetailAdapter(Context context) {

        this.mContext = context;
        this.mStock = null;
        this.mChartDrawer = new ChartDrawer(this);
        this.dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        this.percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        this.percentageFormat.setMaximumFractionDigits(2);
        this.percentageFormat.setMinimumFractionDigits(2);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View item;
        RecyclerView.ViewHolder viewHolder;

        if(viewType == TYPE_CHART){
            item = LayoutInflater.from(mContext).inflate(R.layout.detail_fragment_card_chart_item, parent, false);
            viewHolder = new ChartDetailViewHolder(item);
        } else {
            item = LayoutInflater.from(mContext).inflate(R.layout.detail_fragment_card_text_item, parent, false);
            viewHolder = new TextDetailViewHolder(item);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final int itemType = getItemViewType(position);

        if(mStock != null) {
            if (itemType == TYPE_CHART) {
                ChartDetailViewHolder chartDetailViewHolder = (ChartDetailViewHolder) holder;
                initChartDrawer(chartDetailViewHolder.mChart);
            } else {
                TextDetailViewHolder textDetailViewHolder = (TextDetailViewHolder) holder;
                setStockTexts(textDetailViewHolder);
            }
        } else {
            if (itemType == TYPE_CHART) {
                ChartDetailViewHolder chartDetailViewHolder = (ChartDetailViewHolder) holder;
                initChartDrawer(chartDetailViewHolder.mChart);
            } else {
                TextDetailViewHolder textDetailViewHolder = (TextDetailViewHolder) holder;
                textDetailViewHolder.mPrice.setText(null);
                textDetailViewHolder.mChange.setText(null);
                textDetailViewHolder.mPercentage.setText(null);
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void swapAdapter(Stock stock){
        this.mStock = stock;
        notifyDataSetChanged();
    }

    private void initChartDrawer(LineChart lineChart){
        mChartDrawer.initChartDrawer(mContext, mStock, lineChart, false);
        mChartDrawer.setAxis();
        mChartDrawer.initializeChart();
        mChartDrawer.drawChart();
    }

    @Override
    public void setChartDrawer(IChartDrawer.ChartDrawer chartDrawer) {
        this.mChartDrawer = checkNotNull(chartDrawer);
    }

    private void setStockTexts(TextDetailViewHolder textDetailViewHolder){

        float absoluteChange    = mStock.getAbsoluteChange();
        boolean trendIsPositive = absoluteChange >= 0 ? true : false;
        setTextPrefix(trendIsPositive);

        float percentageChange  = mStock.getPercentageChange();
        String change           = dollarFormatWithPlus.format(absoluteChange);
        String percentage       = percentageFormat.format(percentageChange / 100);
        String price            = mStock.getPrice();

        if(price != null) {
            textDetailViewHolder.mPrice.setText(price);
            textDetailViewHolder.mPrice.setContentDescription(price);
        }
        if(change != null) {
            textDetailViewHolder.mChange.setText(change);
            textDetailViewHolder.mChange.setContentDescription(change);
        }
        if(percentage != null) {
            textDetailViewHolder.mPercentage.setText(percentage);
            textDetailViewHolder.mPercentage.setContentDescription(percentage);
        }
        setStockTextsColor(textDetailViewHolder, absoluteChange);
    }
    private void setTextPrefix(boolean trendIsPositive){
        if(trendIsPositive){
            mDollarFormatPrefix     = "+ $";
            mPercentageFormatPrefix = "+ ";
            dollarFormatWithPlus.setPositivePrefix(mDollarFormatPrefix);
            percentageFormat.setPositivePrefix(mPercentageFormatPrefix);
        } else {
            mDollarFormatPrefix     = "- ($ ";
            mPercentageFormatPrefix = "- ";
            dollarFormatWithPlus.setNegativePrefix(mDollarFormatPrefix);
            percentageFormat.setNegativePrefix(mPercentageFormatPrefix);
        }
    }
    private void setStockTextsColor(TextDetailViewHolder textDetailViewHolder, float absoluteChange){
        int textColor = getTrendColor(mContext, absoluteChange);
        textDetailViewHolder.mPrice.setTextColor(textColor);
        textDetailViewHolder.mChange.setTextColor(textColor);
        textDetailViewHolder.mPercentage.setTextColor(textColor);
    }


    class ChartDetailViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.chart)
        LineChart mChart;

        public ChartDetailViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class TextDetailViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.price)
        TextView mPrice;
        @BindView(R.id.change)
        TextView mChange;
        @BindView(R.id.percentage)
        TextView mPercentage;

        public TextDetailViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
