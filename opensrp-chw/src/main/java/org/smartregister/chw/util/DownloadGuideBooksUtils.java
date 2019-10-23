package org.smartregister.chw.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.GuideBooksFragmentContract;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

import timber.log.Timber;

public class DownloadGuideBooksUtils extends AsyncTask<String, String, String> {

    private GuideBooksFragmentContract.DownloadListener downloadListener;
    private String fileName;
    private String folder;
    private WeakReference<Context> context;
    private Exception exception;

    public DownloadGuideBooksUtils(GuideBooksFragmentContract.DownloadListener downloadListener, String fileName, Context context) {
        this.fileName = fileName;
        this.context = new WeakReference<>(context);
        folder = Environment.getExternalStorageDirectory() + File.separator +
                ChwApplication.getGuideBooksDirectory() + File.separator +
                context.getResources().getConfiguration().locale + "/";
        this.downloadListener = downloadListener;
    }

    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        downloadListener.onStarted();
    }

    /**
     * Downloading file in background thread
     */
    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            URL url = new URL(BuildConfig.guidebooks_url + context.get().getResources().getConfiguration().locale + "/" + fileName);
            URLConnection connection = url.openConnection();
            connection.connect();
            // getting file length
            int lengthOfFile = connection.getContentLength();


            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            //Create android dest folder if it does not exist
            File directory = new File(folder);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Output stream to write file
            OutputStream output = new FileOutputStream(folder + fileName);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lengthOfFile));
                Timber.d("Progress: %s", (int) ((total * 100) / lengthOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();
            return "Downloaded at: " + folder + fileName;

        } catch (Exception e) {
            Timber.e(e);
        }

        return "Something went wrong";
    }

    @Override
    protected void onPostExecute(String message) {
        // dismiss the dialog after the file was downloaded
        downloadListener.onDownloadComplete(!"Something went wrong".equals(message));
    }
}