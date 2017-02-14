package com.udacity.stockhawk.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.main_view.ISetDialogPresenter;
import com.udacity.stockhawk.main_view.IViewPresenterContract;
import com.udacity.stockhawk.main_view.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import static com.google.common.base.Preconditions.checkNotNull;


public class AddStocksMainDialog extends DialogFragment implements ISetDialogPresenter<IViewPresenterContract.Presenter> {

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.listView)
    ListView mListView;

    private Context mContext;
    private IViewPresenterContract.Presenter mPresenter;
    private String[] mValues;
    private Dialog dialog;

    public AddStocksMainDialog() {
    }

    public static AddStocksMainDialog newInstance(){

        AddStocksMainDialog addStocksMainDialog = new AddStocksMainDialog();
        return addStocksMainDialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") View custom = inflater.inflate(R.layout.dialog_add_stocks_main, null);

        ButterKnife.bind(this, custom);

        setListView();

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
        builder.setTitle(getString(R.string.dialog_title));
        builder.setMessage(getString(R.string.choose_or_insert_stock));

        builder.setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        return dialog;
    }

    private void setListView(){
        mListView.setAdapter(new SimpleAdapter());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position < 4)
                    addStock(mValues[position]);
                else if(position == 4){
                    addDefaultStocks();
                } else if (position == 5){
                    ((MainActivity) getActivity()).openAddStockDialog(mPresenter);
                    dialog.dismiss();
                }
            }
        });
    }


    private void addStock(String stockSymbol) {
        if(mPresenter != null && stockSymbol != null && !stockSymbol.isEmpty())
            mPresenter.addStock(stockSymbol);
        else
            mPresenter.loadLocalData();
        dismissAllowingStateLoss();
    }
    private void addDefaultStocks() {
        int nbDefaultStocks = 4;
        String[] values = new String[nbDefaultStocks];
        for(int i=0; i<nbDefaultStocks; i++)
            values[i] = mValues[i];
        mPresenter.addDefaultStocks(values);
        dismissAllowingStateLoss();
    }

    @Override
    public void setPresenter(IViewPresenterContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    class SimpleAdapter extends BaseAdapter{

        private class ViewHolder{
            TextView list_item;
        }

        public SimpleAdapter() {
            mValues = getActivity().getResources().getStringArray(R.array.default_stocks);
        }

        @Override
        public int getCount() {
            return mValues.length;
        }

        @Override
        public String getItem(int position) {
            return mValues[position];
        }

        @Override
        public long getItemId(int position) {
            return mValues[position].hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

            if(convertView == null){

                viewHolder = new ViewHolder();

                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView    = layoutInflater.inflate(R.layout.dialog_add_stock_row_item, null);

                viewHolder.list_item = (TextView)  convertView.findViewById(R.id.list_item);

                convertView.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) convertView.getTag();

            viewHolder.list_item.setText(getItem(position));

            return convertView;
        }
    }
}
