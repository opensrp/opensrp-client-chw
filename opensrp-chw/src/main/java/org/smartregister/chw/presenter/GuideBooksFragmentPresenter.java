package org.smartregister.chw.presenter;

import org.jetbrains.annotations.Nullable;
import org.smartregister.chw.contract.GuideBooksFragmentContract;

import java.lang.ref.WeakReference;
import java.util.List;

public class GuideBooksFragmentPresenter implements GuideBooksFragmentContract.Presenter, GuideBooksFragmentContract.InteractorCallBack {

    private WeakReference<GuideBooksFragmentContract.View> view;
    private GuideBooksFragmentContract.Interactor interactor;

    public GuideBooksFragmentPresenter(GuideBooksFragmentContract.View view, GuideBooksFragmentContract.Interactor interactor) {
        this.view = new WeakReference<>(view);
        this.interactor = interactor;
    }

    @Override
    public void initialize() {
        if (getView() != null)
            interactor.getVideos(getView().getViewContext(), this);
    }

    @Override
    @Nullable
    public GuideBooksFragmentContract.View getView() {
        if (view.get() != null)
            return view.get();

        return null;
    }

    @Override
    public void onDataFetched(List<GuideBooksFragmentContract.Video> videos) {
        if (getView() == null)
            return;

        getView().onDataReceived(videos);
    }
}
