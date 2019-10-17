package org.smartregister.chw.interactor;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.contract.GuideBooksFragmentContract;
import org.smartregister.chw.domain.GuideBooksFragmentVideo;
import org.smartregister.chw.util.FileUtilities;
import org.smartregister.family.util.AppExecutors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GuideBooksFragmentInteractor implements GuideBooksFragmentContract.Interactor {
    private AppExecutors appExecutors;

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
            List<GuideBooksFragmentContract.Video> videos = new ArrayList<>();

            // aggregate the remote videos and local videos then return the
            videos.addAll(getLocalVideos(context));
            videos.addAll(getRemoteVideos(context));

            Runnable runnable1 = () -> appExecutors.mainThread().execute(() -> callBack.onDataFetched(videos));
            appExecutors.mainThread().execute(runnable1);
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void downloadVideo(String url) {
        // TODO
    }

    private List<GuideBooksFragmentContract.Video> getLocalVideos(Context context) {
        List<GuideBooksFragmentContract.Video> res = new ArrayList<>();
        String folder = ChwApplication.getGuideBooksDirectory() + File.separator + context.getResources().getConfiguration().locale.getLanguage();
        File[] files = FileUtilities.getFiles(folder);
        if (files == null || files.length == 0) return res;

        for (File file : files) {
            GuideBooksFragmentVideo video = new GuideBooksFragmentVideo();
            video.setVideoID(file.getName().toLowerCase());
            video.setDownloaded(true);
            video.setLocalPath(file.getPath());
            video.setTitle(toTitle(file.getName()));
            res.add(video);
        }
        return res;
    }

    private List<GuideBooksFragmentContract.Video> getRemoteVideos(Context context) {
        return new ArrayList<>();
    }

    private String toTitle(String s) {
        String[] vals = s.split("\\.");
        String[] title = vals[0].toLowerCase().split("_");
        return StringUtils.capitalize(StringUtils.join(title, " "));
    }
}
