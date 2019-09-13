package org.smartregister.chw.core.task;

import android.os.AsyncTask;

/**
 * Runs and runnable code async
 */
public class RunnableTask extends AsyncTask<Void, Void, Void> {
    private Runnable runnable;

    public RunnableTask(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if(runnable != null){
            runnable.run();
        }
        return null;
    }
}
