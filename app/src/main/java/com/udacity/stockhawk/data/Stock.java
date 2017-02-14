package com.udacity.stockhawk.data;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Stock implements Parcelable{

    private String   price;
    private String   symbol;
    private Float    absoluteChange;
    private Float    percentageChange;
    private List<String> historyValue = new ArrayList<>();
    private List<String> historyDate  = new ArrayList<>();

    public Stock() {
    }


    protected Stock(Parcel in) {
        price        = in.readString();
        symbol       = in.readString();
        historyValue = in.createStringArrayList();
        historyDate  = in.createStringArrayList();
    }

    public static final Creator<Stock> CREATOR = new Creator<Stock>() {
        @Override
        public Stock createFromParcel(Parcel in) {
            return new Stock(in);
        }

        @Override
        public Stock[] newArray(int size) {
            return new Stock[size];
        }
    };

    public void setPrice(String price) {
        this.price = price;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setAbsoluteChange(Float absoluteChange) {
        this.absoluteChange = absoluteChange;
    }

    public void setPercentageChange(Float percentageChange) {
        this.percentageChange = percentageChange;
    }

    public void setHistoryValue(List<String> historyValue) {
        this.historyValue = historyValue;
    }

    public void setHistoryDate(List<String> historyDate) {
        this.historyDate = historyDate;
    }

    public String getPrice() {
        return price;
    }

    public String getSymbol() {
        return symbol;
    }

    public Float getAbsoluteChange() {
        return absoluteChange;
    }

    public Float getPercentageChange() {
        return percentageChange;
    }

    public List<String> getHistoryValue() {
        return historyValue;
    }

    public List<String> getHistoryDate() {
        return historyDate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(price);
        dest.writeString(symbol);
        dest.writeStringList(historyValue);
        dest.writeStringList(historyDate);
    }
}
