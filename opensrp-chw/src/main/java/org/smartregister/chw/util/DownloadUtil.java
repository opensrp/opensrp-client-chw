package org.smartregister.chw.util;

import android.os.AsyncTask;

import org.smartregister.chw.contract.GuideBooksFragmentContract;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class DownloadUtil extends AsyncTask<String, String, String> {

    private static final List<String> downloadQueue = new ArrayList<>();

    protected String serverUrl;
    protected String folder;
    protected String fileName;
    protected GuideBooksFragmentContract.DownloadListener downloadListener;

    protected DownloadUtil() {

    }

    private synchronized void addToQue(String url) {
        downloadQueue.add(url);
    }

    private synchronized void removeFromQue(String url) {
        downloadQueue.remove(url);
    }

    public static boolean isDownloading(String url) {
        return downloadQueue.contains(url);
    }

    public DownloadUtil(GuideBooksFragmentContract.DownloadListener downloadListener, String serverUrl, String folder, String fileName) {
        this.serverUrl = serverUrl;
        this.folder = folder;
        this.fileName = fileName;
        this.downloadListener = downloadListener;
    }


    /**
     * Downloading file in background thread
     */
    @Override
    protected String doInBackground(String... strings) {
        int count;
        try {
            URL url = new URL(serverUrl);
            addToQue(serverUrl);
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

            byte[] data = new byte[1024];

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


    /**
     * Before starting background thread
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (downloadListener != null)
            downloadListener.onStarted();
    }

    @Override
    protected void onPostExecute(String message) {
        // dismiss the dialog after the file was downloaded
        // remove from queue
        removeFromQue(serverUrl);
        if (downloadListener != null)
            downloadListener.onDownloadComplete(!"Something went wrong".equals(message), (folder + fileName));
    }
}
