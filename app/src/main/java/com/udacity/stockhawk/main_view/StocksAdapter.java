package com.udacity.stockhawk.main_view;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.Stock;
import com.udacity.stockhawk.utilities.ChartDrawer;
import com.udacity.stockhawk.utilities.IChartDrawer;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.udacity.stockhawk.utilities.Utilities.getTrendColor;

class StocksAdapter extends RecyclerView.Adapter<StocksAdapter.StockViewHolder> implements IChartDrawer.ChartViewParent{

    @Override
    public void setChartDrawer(IChartDrawer.ChartDrawer chartDrawer) {
        this.mChartDrawer = checkNotNull(chartDrawer);
    }
    private IChartDrawer.ChartDrawer mChartDrawer;

    public interface StockAdapterOnClickHandler{
        void onCardClicked(int position);
    }
    private StockAdapterOnClickHandler mStockAdapterOnClickHandler;

    private final Context mContext;
    private List<Stock>   mStocks;
    private List<String> mStocksSymbols;

    private final DecimalFormat dollarFormat;
    private final float mTrendAlpha     = 0.3f;

    StocksAdapter(Context context, StockAdapterOnClickHandler clickHandler) {
        this.mContext                    = context;
        this.mStockAdapterOnClickHandler = clickHandler;
        this.dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        this.mChartDrawer = new ChartDrawer(this);
    }

    void setCursor(Cursor cursor) {
        mStocks = new LinkedList<>();
        mStocksSymbols = new LinkedList<>();
        Stock stock;
        String symbol;
        if(cursor != null) {
            while (cursor.moveToNext()) {
                stock = new Stock();

                symbol = cursor.getString(Contract.Quote.POSITION_SYMBOL);

                stock.setPrice(dollarFormat.format(cursor.getFloat(Contract.Quote.POSITION_PRICE)));
                stock.setSymbol(symbol);
                stock.setAbsoluteChange(cursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE));
                stock.setPercentageChange(cursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE));

                String historyStr = cursor.getString(Contract.Quote.POSITION_HISTORY_VALUE);
                String[] history = historyStr.split("\\s");
                List<String> lHisotry = new LinkedList<>();
                String historyDateStr = cursor.getString(Contract.Quote.POSITION_HISTORY_DATE);
                String[] historyDate = historyDateStr.split("\\s");
                List<String> lHisotryDate = new LinkedList<>();

                for (int i = 0; i < history.length; i++) {
                    lHisotry.add(history[i]);
                    lHisotryDate.add(historyDate[i]);
                }

                stock.setHistoryValue(lHisotry);
                stock.setHistoryDate(lHisotryDate);

                mStocks.add(stock);
                mStocksSymbols.add(symbol);
            }
            cursor.close();
            notifyDataSetChanged();
        }
    }

    public List<String> getStocksList(){
        return mStocksSymbols;
    }

    String getSymbolAtPosition(int position) {
        return mStocks.get(position).getSymbol();
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(mContext).inflate(R.layout.card_view_main_fragment, parent, false);

        return new StockViewHolder(item);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {

        Stock stock;

        if(mStocks.get(position) != null) {

            stock = mStocks.get(position);

            float absoluteChange  = stock.getAbsoluteChange();
            String symbol         = stock.getSymbol();
            String price          = stock.getPrice();

            setTexts(holder, symbol, price);
            setContentDescription(holder, symbol, price);
            initChartDrawer(holder.chart, stock);
            setTrends(holder, absoluteChange);
        }

    }

    private void setTexts(StockViewHolder holder, String symbol, String price){
        holder.symbol.setText(symbol);
        holder.price.setText(price);
    }
    private void setContentDescription(StockViewHolder holder, String symbol, String price){
        holder.symbol.setContentDescription(symbol);
        holder.price.setContentDescription(price);
    }

    private void setTrends(StockViewHolder holder, float absoluteChange){
        holder.price.setTextColor(getTrendColor(mContext, absoluteChange));
        holder.trend.setImageResource(getTrendDrawableId(absoluteChange));
        holder.trend.setAlpha(mTrendAlpha);
    }
    private int getTrendDrawableId(float absoluteChange){
        return absoluteChange >= 0 ? R.drawable.ic_trending_up_green_24dp : R.drawable.ic_trending_down_red_24dp;
    }

    private void initChartDrawer(LineChart lineChart, Stock stock){
        mChartDrawer.initChartDrawer(mContext, stock, lineChart, true);
        mChartDrawer.setAxis();
        mChartDrawer.initializeChart();
        mChartDrawer.drawChart();
    }

    @Override
    public int getItemCount() {
        return mStocks != null ? mStocks.size() : 0;
    }


    class StockViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.symbol)
        TextView symbol;
        @BindView(R.id.price)
        TextView price;
        @BindView(R.id.main_frame)
        FrameLayout mMainFrame;
        @BindView(R.id.iv_trend)
        ImageView trend;
        @BindView(R.id.chart)
        LineChart chart;

        StockViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mMainFrame.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mStockAdapterOnClickHandler.onCardClicked(getAdapterPosition());
        }
    }

}
