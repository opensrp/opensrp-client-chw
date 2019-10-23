package org.smartregister.chw.util;

import android.content.Context;
import android.os.Environment;

import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.GuideBooksFragmentContract;

import java.io.File;

public class DownloadGuideBooksUtils extends DownloadUtil {

    private GuideBooksFragmentContract.DownloadListener downloadListener;

    public DownloadGuideBooksUtils(GuideBooksFragmentContract.DownloadListener downloadListener, String fileName, Context context) {
        this.fileName = fileName;
        folder = Environment.getExternalStorageDirectory() + File.separator +
                ChwApplication.getGuideBooksDirectory() + File.separator +
                context.getResources().getConfiguration().locale + "/";
        this.downloadListener = downloadListener;
        this.serverUrl = BuildConfig.guidebooks_url + context.getResources().getConfiguration().locale + "/" + fileName;
    }

    /**
     * Before starting background thread
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        downloadListener.onStarted();
    }

    @Override
    protected void onPostExecute(String message) {
        // dismiss the dialog after the file was downloaded
        downloadListener.onDownloadComplete(!"Something went wrong".equals(message));
    }
}