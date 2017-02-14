package com.udacity.stockhawk.sync;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import timber.log.Timber;
import static com.udacity.stockhawk.utilities.Constants.TAG;

public class QuoteFirebaseService extends JobService {

    private AsyncTask mQuoteAsyncJob;
    private Context mContext;

    @Override
    public boolean onStartJob(final JobParameters job) {
        Timber.d("Job started");
        mContext = this;
        mQuoteAsyncJob = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                QuoteSyncJob.getQuotes(mContext);
                return null;
            }
            @Override
            protected void onPostExecute(Object o) {
                jobFinished(job, false);
            }
        }.execute();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {

        if(mQuoteAsyncJob != null) mQuoteAsyncJob.cancel(true);

        return true;
    }

}
