package com.udacity.stockhawk.detail_view;


import com.udacity.stockhawk.IBaseView;
import com.udacity.stockhawk.IDetailBasePresenter;
import com.udacity.stockhawk.data.Stock;

public interface IDetailViewPresenterContract {

    interface View extends IBaseView<Presenter> {

        void onLocalDataLoaded(Stock stock);

        void showErrorMessage();

        void showLoadingDialog();

        void hideLoadingDialog();
    }

    interface Presenter extends IDetailBasePresenter {

        void setPresenter();

    }
}
