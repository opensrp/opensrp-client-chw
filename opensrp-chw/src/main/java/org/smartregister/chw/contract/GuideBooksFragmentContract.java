package org.smartregister.chw.contract;

import android.content.Context;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface GuideBooksFragmentContract {

    interface View {

        void initializePresenter();

        Presenter getPresenter();

        void onDataReceived(List<Video> videos);

        Context getViewContext();

        void displayLoadingState(boolean state);

        void playVideo(Video video);

        void downloadVideo(Video video);
    }

    interface Presenter {

        void initialize();

        @Nullable View getView();

        void requestVideoDownload(String url);
    }

    interface Interactor {

        void getVideos(Context context, InteractorCallBack callBack);

        void downloadVideo(String url);
    }

    interface InteractorCallBack {

        void onDataFetched(List<Video> videos);
    }

    interface Video {

        String getID();

        String getTitle();

        Boolean isDowloaded();

        String getLocalPath();

        String getServerUrl();
    }
}
