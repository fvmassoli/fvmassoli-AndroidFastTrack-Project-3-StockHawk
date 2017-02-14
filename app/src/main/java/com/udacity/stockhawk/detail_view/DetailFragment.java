package com.udacity.stockhawk.detail_view;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Stock;
import com.udacity.stockhawk.utilities.FragmentBaseClass;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.udacity.stockhawk.main_view.MainActivity.sIsTwoPaneMode;
import static com.udacity.stockhawk.utilities.Constants.STOCK_POSITION;


public class DetailFragment extends FragmentBaseClass implements IDetailViewPresenterContract.View {

    public interface DetailFabClicked {
        void onDetailFabClicked();
    }
    private DetailFabClicked mDetailFabClikced;

    @BindView(R.id.detail_toolbar)
    Toolbar mDetailToolbar;
    @BindView(R.id.toolbar_trend_icon)
    ImageView mToolbarTrendIcon;
    @BindView(R.id.detail_recycler_view)
    RecyclerView mDetailRecyclerView;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private Context  mContext;
    private boolean mIsTwoPane;
    AppCompatActivity mAppCompatActivity;
    private int      mPosition;
    private int mToolbarTrendIconId;
    private IDetailViewPresenterContract.Presenter mDetailPresenter;
    private DetailAdapter mDetailAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    public DetailFragment() {
    }

    public static DetailFragment newInstance(int position){
        DetailFragment detailFragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(STOCK_POSITION, position);
        detailFragment.setArguments(bundle);
        return detailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mPosition = bundle.getInt(STOCK_POSITION);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        this.mAppCompatActivity = (AppCompatActivity) context;
        this.mLinearLayoutManager = new LinearLayoutManager(context);
        this.mDetailAdapter = new DetailAdapter(context);

        mIsTwoPane = sIsTwoPaneMode();

        if(mIsTwoPane){
            try {
                mDetailFabClikced = (DetailFabClicked) getActivity();
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString()
                        + " must implement TextClicked");
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.detail_fragment, container, false);
        ButterKnife.bind(this, view);

        mAppCompatActivity.setSupportActionBar(mDetailToolbar);

        if(mIsTwoPane) {
            mAppCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            fab.setVisibility(View.VISIBLE);
            if(mDetailPresenter != null)
                mDetailPresenter.start(0);
        } else {
            if(mDetailPresenter != null)
                mDetailPresenter.start(mPosition);
            mAppCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            fab.setVisibility(View.GONE);
        }

        mDetailRecyclerView.setLayoutManager(mLinearLayoutManager);
        mDetailRecyclerView.setAdapter(mDetailAdapter);
        mDetailRecyclerView.setHasFixedSize(false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setPresenter(IDetailViewPresenterContract.Presenter presenter) {
        mDetailPresenter = checkNotNull(presenter);
    }

    public void startPresenter(){
        mDetailPresenter.start(0);
    }

    @Override
    public void onLocalDataLoaded(Stock stock){
        setTollbarViews(stock);
        mDetailAdapter.swapAdapter(stock);
    }
    private void setTollbarViews(Stock stock) {
        if(stock != null) {
            mAppCompatActivity.getSupportActionBar().setTitle(stock.getSymbol());
            setTrandIcon(stock);
        } else {
            mAppCompatActivity.getSupportActionBar().setTitle(null);
            mToolbarTrendIcon.setVisibility(View.INVISIBLE);
        }
    }
    private void setTrandIcon(Stock stock){
        mToolbarTrendIcon.setVisibility(View.VISIBLE);
        mToolbarTrendIconId = stock.getAbsoluteChange() >= 0 ? R.drawable.ic_trending_up_white_24dp
                : R.drawable.ic_trending_down_white_24dp;
        mToolbarTrendIcon.setImageResource(mToolbarTrendIconId);
    }

    @OnClick(R.id.fab)
    public void fabClicked() {
        mDetailFabClikced.onDetailFabClicked();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDetailFabClikced = null;
    }

    @Override
    public void showErrorMessage(){

    }

    @Override
    public void showLoadingDialog() {
        showProgressDialog();
    }

    @Override
    public void hideLoadingDialog() {
        hideProgressDialog();
    }


}
