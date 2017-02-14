package com.udacity.stockhawk.detail_view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.udacity.stockhawk.R;

import static com.udacity.stockhawk.utilities.Constants.DETAIL_FRAGMENT_TAG;
import static com.udacity.stockhawk.utilities.Constants.FAKE_POSITION;
import static com.udacity.stockhawk.utilities.Constants.STOCK_POSITION;

public class DetailActivity extends AppCompatActivity {

    private DetailFragment mDetailFragment;
    private Intent         mIntent;
    private int            mPosition;
    private IDetailViewPresenterContract.Presenter mDetailPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        mIntent = getIntent();
        if(mIntent != null && mIntent.hasExtra(STOCK_POSITION))
            mPosition = mIntent.getIntExtra(STOCK_POSITION, FAKE_POSITION);

        createDetailFragemnt();
        instanceDetailPresenter();
    }

    private void createDetailFragemnt(){
        mDetailFragment = DetailFragment.newInstance(mPosition);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.detail_container, mDetailFragment, DETAIL_FRAGMENT_TAG)
                .commit();
    }

    private void instanceDetailPresenter(){
        mDetailPresenter = new DetailPresenter(mDetailFragment, this);
        mDetailPresenter.setPresenter();
    }

}
