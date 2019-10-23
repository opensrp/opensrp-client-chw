package org.smartregister.chw.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.application.ChwApplication;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

import static org.smartregister.chw.activity.LoginActivity.TAG;

public class DownloadGuideBooksUtils extends AsyncTask<String, String, String> {

    private WeakReference<ProgressBar> progressBar;
    private WeakReference<ImageView> icon;
    private String fileName;
    private String folder;
    private WeakReference<Context> context;

    public DownloadGuideBooksUtils(ImageView icon, ProgressBar progressBar, String fileName, Context context) {
        this.progressBar = new WeakReference<>(progressBar);
        this.icon = new WeakReference<>(icon);
        this.fileName = fileName;
        this.context = new WeakReference<>(context);
        folder = ChwApplication.getGuideBooksDirectory() + File.separator + context.getResources().getConfiguration().locale + "/";
    }

    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (progressBar.get() != null)
            progressBar.get().setVisibility(View.VISIBLE);

        if (icon.get() != null)
            icon.get().setVisibility(View.GONE);
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
                Log.d(TAG, "Progress: " + (int) ((total * 100) / lengthOfFile));

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
            Log.e("Error: ", e.getMessage());
        }

        return "Something went wrong";
    }

    @Override
    protected void onPostExecute(String message) {
        // dismiss the dialog after the file was downloaded
        if (progressBar.get() != null)
            progressBar.get().setVisibility(View.GONE);

        if (icon.get() != null)
            icon.get().setVisibility(View.VISIBLE);

        // Display File path after downloading
        if (context.get() != null)
            Toast.makeText(context.get(), message, Toast.LENGTH_LONG).show();
    }
}