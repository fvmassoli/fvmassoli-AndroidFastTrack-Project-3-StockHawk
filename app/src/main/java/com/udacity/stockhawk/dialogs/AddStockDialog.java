package com.udacity.stockhawk.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.main_view.ISetDialogPresenter;
import com.udacity.stockhawk.main_view.IViewPresenterContract;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.common.base.Preconditions.checkNotNull;


public class AddStockDialog extends DialogFragment implements ISetDialogPresenter<IViewPresenterContract.Presenter> {

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.dialog_stock)
    EditText mStock;
    @BindView(R.id.til)
    TextInputLayout mTil;

    private InputMethodManager imm;
    private IViewPresenterContract.Presenter mPresenter;

    public AddStockDialog() {
    }

    public static AddStockDialog newInstance(){

        AddStockDialog addStockDialog = new AddStockDialog();
        return addStockDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") View custom = inflater.inflate(R.layout.dialog_add_stock, null);

        ButterKnife.bind(this, custom);

        mTil.setHint(getString(R.string.dialog_hint, getString(R.string.dialog_hint)));
        mTil.setContentDescription(getString(R.string.add_stock_content_description));
        mStock.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    addStock();
                }

                return true;
            }
        });

        builder.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    arg0.dismiss();
                }
                return true;
            }
        });

        builder.setView(custom);

        builder.setMessage(getString(R.string.dialog_title));
        builder.setPositiveButton(getString(R.string.dialog_add), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addStock();
                    }
                });
        builder.setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Dialog dialog = builder.create();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        return dialog;
    }

    private void addStock() {
        String stockSymbol = mStock.getText().toString();
        if(mPresenter != null && stockSymbol != null && !stockSymbol.isEmpty())
            mPresenter.addStock(stockSymbol);
        dismissAllowingStateLoss();
    }

    @Override
    public void setPresenter(IViewPresenterContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }
}
