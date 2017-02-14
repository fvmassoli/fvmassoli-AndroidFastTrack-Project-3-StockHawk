package com.udacity.stockhawk.main_view;


import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.dialogs.AddStocksMainDialog;
import com.udacity.stockhawk.dialogs.TemplateDialog;
import com.udacity.stockhawk.utilities.Utilities;
import com.udacity.stockhawk.widget.StockWidgetProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.udacity.stockhawk.main_view.MainActivity.sIsTwoPaneMode;
import static com.udacity.stockhawk.utilities.Constants.ACTION_DATA_ADDED;
import static com.udacity.stockhawk.utilities.Constants.ACTION_DATA_NOT_FOUND;
import static com.udacity.stockhawk.utilities.Constants.ACTION_DATA_UPDATED;
import static com.udacity.stockhawk.utilities.Constants.STOCK_SYMBOL_IS_EMPTY;

public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, IViewPresenterContract.View {

    interface IMainCommunication{
        void onFabClicked(IViewPresenterContract.Presenter presenter, int symbolCount);
        void onCursorLoaderFinishedLoading();
    }
    private IMainCommunication  iMainCommunication;
    private Context             mContext;
    private LinearLayoutManager mLinearLayoutManager;
    private StocksAdapter       mStockAdapter;
    private IViewPresenterContract.Presenter mMainPresenter;
    private static boolean sCursorAlreadyLoaded;
    private boolean mInstaceSaved;
    private TemplateDialog mTemplateDialog;
    private AddStocksMainDialog mAddStocksMainDialog;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_view)
    LinearLayout mLlError;
    @BindView(R.id.fab)
    FloatingActionButton fab;


    public MainFragment() {
    }

    public static MainFragment newInstance(){
        MainFragment mainFragment = new MainFragment();
        return mainFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstaceSaved = false;
        if(mMainPresenter != null)
            mMainPresenter.start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mStockAdapter        = new StocksAdapter(mContext, (StocksAdapter.StockAdapterOnClickHandler) context);

        try {
            iMainCommunication = (IMainCommunication) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main_fragment, container, false);
        ButterKnife.bind(this, view);

        ((MainActivity) getActivity()).setSupportActionBar(mToolbar);

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mStockAdapter);
        mRecyclerView.setHasFixedSize(true);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        if(sIsTwoPaneMode())
            fab.setVisibility(View.GONE);

        sCursorAlreadyLoaded = false;

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                sCursorAlreadyLoaded = false;
                String symbol = mStockAdapter.getSymbolAtPosition(viewHolder.getAdapterPosition());
                mMainPresenter.onItemSwiped(symbol);
            }
        }).attachToRecyclerView(mRecyclerView);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mInstaceSaved = true;
    }

    @Override
    public void setPresenter(IViewPresenterContract.Presenter presenter) {
        mMainPresenter = checkNotNull(presenter);
    }
    @Override
    public void startRefreshing(){
        mSwipeRefreshLayout.setRefreshing(true);
    }
    @Override
    public void stopRefreshing(){
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void noNetworkAvailable() {
        if(!mInstaceSaved) {
            mTemplateDialog = TemplateDialog.newInstance(0);
            mTemplateDialog.setCancelable(true);
            mTemplateDialog.show(getActivity().getFragmentManager(), "templateDialog");
        }
        sCursorAlreadyLoaded = false;
    }

    @Override
    public void setEmptyView(Cursor data){
        if(data != null)
            mStockAdapter.setCursor(data);
        sCursorAlreadyLoaded = false;
        mLlError.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void setEmptyViewGone(){
        mRecyclerView.setVisibility(View.VISIBLE);
        mLlError.setVisibility(View.GONE);
    }

    @Override
    public void onCursorLoaderFinished(Cursor data) {
        setEmptyViewGone();
        if (!sCursorAlreadyLoaded) {
            mStockAdapter.setCursor(data);
            sCursorAlreadyLoaded = true;
            if(!Utilities.networkUp(mContext))
                dataMayBeOld();
        }
        if(sIsTwoPaneMode())
            iMainCommunication.onCursorLoaderFinishedLoading();
    }

    @Override
    public void  onCursorLoaderReset(){
        if(mStockAdapter != null)
            mStockAdapter.setCursor(null);
    }

    @Override
    public void dataMayBeOld(){
        if(!mInstaceSaved) {
            mTemplateDialog = TemplateDialog.newInstance(1);
            mTemplateDialog.setCancelable(true);
            mTemplateDialog.show(getActivity().getFragmentManager(), "templateDialog");
        }
    }

    private void showStockNotFoundDialog(){
        if(!mInstaceSaved) {
            mTemplateDialog = TemplateDialog.newInstance(2);
            mTemplateDialog.setCancelable(true);
            mTemplateDialog.show(getActivity().getFragmentManager(), "templateDialog");
        }
        if(mStockAdapter.getItemCount() == 0)
            setEmptyView(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        startRefreshing();
        mMainPresenter.checkConnectivity();
        registerBroadcasts();
    }

    @Override
    public void onPause() {
        super.onPause();
        closeDialogs();
        stopRefreshing();
        unRegisterBroadcasts();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        iMainCommunication = null;
    }

    @Override
    public void showMainDialogAddStocks(){
        if(!mInstaceSaved) {
            mAddStocksMainDialog = AddStocksMainDialog.newInstance();
            mAddStocksMainDialog.setCancelable(false);
            mAddStocksMainDialog.show(getActivity().getFragmentManager(), "addStocksMainDialog");
            mAddStocksMainDialog.setPresenter(mMainPresenter);
        }
    }

    @Override
    public void onRefresh() {
        mMainPresenter.checkConnectivity();
    }

    @OnClick(R.id.fab)
    public void fabClicked() {
        if(Utilities.networkUp(mContext))
            iMainCommunication.onFabClicked(mMainPresenter, mStockAdapter.getItemCount());
        else
            noNetworkAvailable();
    }

    @Override
    public List<String> getStocksSymbol(){
        if(mStockAdapter != null)
            return mStockAdapter.getStocksList();
        else
            return null;
    }

    @Override
    public void showToast(String stockSymbol){
        stopRefreshing();
        Toast.makeText(mContext, mContext.getString(R.string.already_has_stock, stockSymbol),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateWidget(){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        ComponentName component = new ComponentName(mContext, StockWidgetProvider.class);
        int[] ids = appWidgetManager.getAppWidgetIds(component);
        if(ids.length != 0) {
            Intent toastIntent = new Intent(mContext, StockWidgetProvider.class);
            toastIntent.setAction(StockWidgetProvider.STOCKS_CHANGED);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, ids);
            mContext.sendBroadcast(toastIntent);
        }
    }

    private void closeDialogs(){
        if(mTemplateDialog != null)
            mTemplateDialog.dismiss();
        if(mAddStocksMainDialog != null)
            mAddStocksMainDialog.dismiss();
    }

    BroadcastReceiver mStockPrefEmptyBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setEmptyView(null);
            stopRefreshing();
            showMainDialogAddStocks();
            updateWidget();
        }
    };
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sCursorAlreadyLoaded = false;
            mMainPresenter.loadLocalData();
        }
    };
    BroadcastReceiver mAddedStockBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sCursorAlreadyLoaded = false;
            mMainPresenter.loadLocalData();
        }
    };
    BroadcastReceiver mStockNotFoundBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sCursorAlreadyLoaded = false;
            stopRefreshing();
            showStockNotFoundDialog();
            updateWidget();
        }
    };


    private void registerBroadcasts(){
        mContext.registerReceiver(mStockPrefEmptyBroadcastReceiver, new IntentFilter(STOCK_SYMBOL_IS_EMPTY));
        mContext.registerReceiver(mBroadcastReceiver,               new IntentFilter(ACTION_DATA_UPDATED));
        mContext.registerReceiver(mAddedStockBroadcastReceiver,     new IntentFilter(ACTION_DATA_ADDED));
        mContext.registerReceiver(mStockNotFoundBroadcastReceiver,  new IntentFilter(ACTION_DATA_NOT_FOUND));
    }

    private void unRegisterBroadcasts(){
        mContext.unregisterReceiver(mStockPrefEmptyBroadcastReceiver);
        mContext.unregisterReceiver(mBroadcastReceiver);
        mContext.unregisterReceiver(mAddedStockBroadcastReceiver);
        mContext.unregisterReceiver(mStockNotFoundBroadcastReceiver);
    }

}
