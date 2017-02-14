package com.udacity.stockhawk.main_view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.detail_view.DetailActivity;
import com.udacity.stockhawk.detail_view.DetailFragment;
import com.udacity.stockhawk.detail_view.DetailPresenter;
import com.udacity.stockhawk.detail_view.IDetailViewPresenterContract;
import com.udacity.stockhawk.dialogs.AddStockDialog;
import com.udacity.stockhawk.dialogs.AddStocksMainDialog;
import com.udacity.stockhawk.utilities.Utilities;

import static com.udacity.stockhawk.utilities.Constants.DETAIL_FRAGMENT_TAG;
import static com.udacity.stockhawk.utilities.Constants.MAIN_FRAGMENT_TAG;
import static com.udacity.stockhawk.utilities.Constants.STOCK_POSITION;
import static com.udacity.stockhawk.utilities.Constants.FAKE_POSITION;


public class MainActivity extends AppCompatActivity implements StocksAdapter.StockAdapterOnClickHandler,
        MainFragment.IMainCommunication, DetailFragment.DetailFabClicked {

    private static boolean mIsTwoPane;
    private DetailFragment mDetailFragment;
    private MainFragment   mMainFragment;
    private IViewPresenterContract.Presenter       mMainPresenter;
    private IDetailViewPresenterContract.Presenter mDetailPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mMainFragment  = MainFragment.newInstance();
        mMainPresenter = new MainPresenter(
                this,
                mMainFragment,
                getSupportLoaderManager(),
                Utilities.injectDependency(this));
        addMainFragment();

        mIsTwoPane = (findViewById(R.id.detail_container) != null) ? true : false;

        if(mIsTwoPane) {
            mDetailFragment = DetailFragment.newInstance(FAKE_POSITION);
            instanceDetailPresenter();
            addDetailFragment();
        }

    }

    private void addMainFragment(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, mMainFragment, MAIN_FRAGMENT_TAG)
                .commit();
    }

    private void addDetailFragment(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.detail_container, mDetailFragment, DETAIL_FRAGMENT_TAG)
                .commit();
    }
    private void instanceDetailPresenter(){
        mDetailPresenter = new DetailPresenter(mDetailFragment, this);
        mDetailPresenter.setPresenter();
    }

    @Override
    public void onCardClicked(int position) {
        if(mIsTwoPane){
            mDetailFragment = DetailFragment.newInstance(position);
            instanceDetailPresenter();
            addDetailFragment();
        } else
            startActivity(new Intent(MainActivity.this, DetailActivity.class)
                    .putExtra(STOCK_POSITION, position));
    }


    @Override
    public void onFabClicked(IViewPresenterContract.Presenter presenter, int symbolCount) {
        if(symbolCount != 0){
            AddStockDialog addStockDialog = AddStockDialog.newInstance();
            addStockDialog.setCancelable(true);
            addStockDialog.show(getFragmentManager(), "addStockDialog");
            addStockDialog.setPresenter(presenter);
        } else {
            AddStocksMainDialog addStocksMainDialog = AddStocksMainDialog.newInstance();
            addStocksMainDialog.setCancelable(true);
            addStocksMainDialog.show(getFragmentManager(), "addStocksMainDialog");
            addStocksMainDialog.setPresenter(presenter);
        }
    }

    public void openAddStockDialog(IViewPresenterContract.Presenter presenter){
        AddStockDialog addStockDialog = AddStockDialog.newInstance();
        addStockDialog.setCancelable(true);
        addStockDialog.show(getFragmentManager(), "addStockDialog");
        addStockDialog.setPresenter(presenter);
    }

    @Override
    public void onCursorLoaderFinishedLoading() {
        mDetailFragment.startPresenter();
    }

    public static boolean sIsTwoPaneMode(){
        return mIsTwoPane;
    }

    @Override
    public void onDetailFabClicked() {
        MainFragment mainFragment = (MainFragment)
                getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG);
        mainFragment.fabClicked();
    }
}
