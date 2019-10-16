package org.smartregister.chw.interactor;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

import org.smartregister.chw.contract.GuideBooksFragmentContract;
import org.smartregister.family.util.AppExecutors;

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

            // aggregate the remote vidoes and local videos then return the results

            Runnable runnable1 = () -> appExecutors.mainThread().execute(() -> callBack.onDataFetched(videos));
            appExecutors.mainThread().execute(runnable1);
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void downloadVideo(String url) {

    }

    private List<GuideBooksFragmentContract.Video> getLocalVideos() {
        return new ArrayList<>();
    }

    private List<GuideBooksFragmentContract.Video> getRemoteVideos() {
        return new ArrayList<>();
    }
}
