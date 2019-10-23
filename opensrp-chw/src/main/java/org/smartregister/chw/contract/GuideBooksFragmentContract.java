package org.smartregister.chw.contract;

import android.content.Context;
import android.widget.ImageView;
import android.widget.ProgressBar;

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

        void downloadVideo(ImageView icon, ProgressBar progressBar, Video video);
    }

    interface Presenter {

        void initialize();

        @Nullable View getView();

    }

    interface DownloadListener {

        void onDownloadComplete();

        void onError(String message);

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

        String getLocalPath();

        String getServerUrl();
    }
}
