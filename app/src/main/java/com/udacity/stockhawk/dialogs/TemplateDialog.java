package com.udacity.stockhawk.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.stockhawk.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.udacity.stockhawk.utilities.Constants.DIALOG_TYPE;


public class TemplateDialog extends DialogFragment {

    @BindView(R.id.icon)
    ImageView mIcon;
    @BindView(R.id.message)
    TextView mMessage;

    private int mType;

    public TemplateDialog(){

    }

    public static TemplateDialog newInstance(int type){
        TemplateDialog templateDialog = new TemplateDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(DIALOG_TYPE, type);
        templateDialog.setArguments(bundle);
        return templateDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mType = getArguments().getInt(DIALOG_TYPE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.dialog_template_layout, null);
        ButterKnife.bind(this, rootView);

        mIcon.setImageResource(setIconResouce());
        String message = setMessage();
        mMessage.setText(message);
        mMessage.setContentDescription(message);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog);
        builder
                .setView(rootView)
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        TemplateDialog.this.getDialog().cancel();
                    }
                });

        builder.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    TemplateDialog.this.getDialog().dismiss();
                }
                return true;
            }
        });

        final AlertDialog alert = builder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE);
                btnNegative.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
            }
        });

        return alert;
    }

    private int setIconResouce(){
        if(mType == 0)
            return R.drawable.ic_signal_wifi_off_black_24dp;
        if(mType == 1 || mType == 2)
            return R.drawable.ic_report_problem_black_24dp;
        else
            return -1;
    }
    private String setMessage(){
        if(mType == 0)
            return getString(R.string.error_no_network);
        if(mType == 1)
            return getString(R.string.data_maybe_old);
        if(mType == 2)
            return getString(R.string.stock_not_found);
        else
            return null;
    }
}
