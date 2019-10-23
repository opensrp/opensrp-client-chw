package org.smartregister.chw.interactor;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.GuideBooksFragmentContract;
import org.smartregister.chw.domain.GuideBooksFragmentVideo;
import org.smartregister.chw.util.DownloadUtil;
import org.smartregister.chw.util.FileUtils;
import org.smartregister.family.util.AppExecutors;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuideBooksFragmentInteractor implements GuideBooksFragmentContract.Interactor {
    private AppExecutors appExecutors;
    private Map<String, GuideBooksFragmentContract.Video> allVideos = new HashMap<>();

    public GuideBooksFragmentInteractor() {
        this(new AppExecutors());
    }

    @VisibleForTesting
    GuideBooksFragmentInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    @Override
    public void getVideos(Context context, GuideBooksFragmentContract.InteractorCallBack callBack) {

        Runnable runnable = () -> {
            getLocalVideos(context, callBack);
            getRemoteVideos(context, callBack);
        };
        appExecutors.diskIO().execute(runnable);
    }

    private synchronized void updateVideos(List<GuideBooksFragmentContract.Video> videos, GuideBooksFragmentContract.InteractorCallBack callBack) {
        List<GuideBooksFragmentContract.Video> res = new ArrayList<>(allVideos.values());
        Collections.sort(res, (video1, video2) -> video1.getTitle().compareTo(video2.getTitle()));
        Runnable runnable1 = () -> appExecutors.mainThread().execute(() -> callBack.onDataFetched(res));
        appExecutors.mainThread().execute(runnable1);
    }

    private void getLocalVideos(Context context, GuideBooksFragmentContract.InteractorCallBack callBack) {
        List<GuideBooksFragmentContract.Video> res = new ArrayList<>();
        String folder = ChwApplication.getGuideBooksDirectory() + File.separator + context.getResources().getConfiguration().locale.getLanguage();
        File[] files = FileUtils.getFiles(folder);
        if (files == null || files.length == 0) return;

        for (File file : files) {
            GuideBooksFragmentVideo video = new GuideBooksFragmentVideo();
            video.setVideoID(file.getName().toLowerCase());
            video.setDownloaded(true);
            video.setLocalPath(file.getPath());
            video.setTitle(toTitle(file.getName()));
            res.add(video);
        }
        updateVideos(res, callBack);
    }

    private void getRemoteVideos(Context context, GuideBooksFragmentContract.InteractorCallBack callBack) {
        if (context == null) return;

        // attempt to refresh the list if the internet is on
        String folder = Environment.getExternalStorageDirectory() + File.separator +
                ChwApplication.getGuideBooksDirectory() + File.separator;
        String name = "files.json";
        new DownloadUtil(BuildConfig.guidebooks_url + name, folder, name).execute();


        // read the files and refresh

        List<GuideBooksFragmentContract.Video> res = new ArrayList<>();

        /*
        GuideBooksFragmentVideo video = new GuideBooksFragmentVideo();
        String name = "ANC_visit.mp4";

        video.setVideoID(name.toLowerCase());
        video.setDownloaded(false);
        video.setLocalPath(null);
        video.setTitle(toTitle(name));
        video.setName(name);

        res.add(video);

         */

        updateVideos(res, callBack);
    }

    private String toTitle(String s) {
        return StringUtils.join(s.split("\\.")[0].split("_"), " ");
    }
}
