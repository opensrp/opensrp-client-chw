package org.smartregister.chw.contract;

import android.content.Context;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface GuideBooksFragmentContract {

    interface View {

        void initializePresenter();

        Presenter getPresenter();

        void onDataReceived(List<Video> videos);

        @Nullable
        Context getViewContext();

        void displayLoadingState(boolean state);

        void playVideo(Video video);

        void downloadVideo(DownloadListener downloadListener, Video video);
    }

    interface Presenter {

        void initialize();

        @Nullable View getView();

    }

    interface DownloadListener {

        void onDownloadComplete(boolean successful, String localPath);

        void onStarted();
    }

    interface Interactor {

        void getVideos(Context context, InteractorCallBack callBack);

    }

    interface InteractorCallBack {

        void onDataFetched(List<Video> videos);
    }

    interface Video {

        String getID();

        String getName();

        String getTitle();

        Boolean isDowloaded();

        void setDownloaded(Boolean downloaded);

        String getLocalPath();

        void setLocalPath(String localPath);

        String getServerUrl();
    }
}
