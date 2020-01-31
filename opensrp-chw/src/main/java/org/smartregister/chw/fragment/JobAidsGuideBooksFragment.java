package org.smartregister.chw.fragment;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.Nullable;
import org.smartregister.chw.R;
import org.smartregister.chw.adapter.GuideBooksAdapter;
import org.smartregister.chw.contract.GuideBooksFragmentContract;
import org.smartregister.chw.interactor.GuideBooksFragmentInteractor;
import org.smartregister.chw.presenter.GuideBooksFragmentPresenter;
import org.smartregister.chw.util.DownloadGuideBooksUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JobAidsGuideBooksFragment extends Fragment implements GuideBooksFragmentContract.View {

    protected RecyclerView.Adapter mAdapter;
    protected GuideBooksFragmentContract.Presenter presenter;
    private List<GuideBooksFragmentContract.Video> videos = new ArrayList<>();
    private ProgressBar progressBar;
    private Map<String, GuideBooksFragmentContract.Video> allVideos = new HashMap<>();

    public static JobAidsGuideBooksFragment newInstance() {
        JobAidsGuideBooksFragment fragment = new JobAidsGuideBooksFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_job_aids_guide_books, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        progressBar = rootView.findViewById(R.id.progress_bar);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getViewContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new GuideBooksAdapter(videos, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        initializePresenter();
        return rootView;
    }

    @Override
    public void initializePresenter() {
        presenter = new GuideBooksFragmentPresenter(this, new GuideBooksFragmentInteractor());
    }

    @Override
    public GuideBooksFragmentContract.Presenter getPresenter() {
        return presenter;
    }

    @Override
    public void onDataReceived(List<GuideBooksFragmentContract.Video> receivedVideos) {

        for (GuideBooksFragmentContract.Video video : receivedVideos) {
            GuideBooksFragmentContract.Video available = allVideos.get(video.getID());
            if (available == null) {
                allVideos.put(video.getID(), video);
            } else if (video.isDowloaded() && !available.isDowloaded()) {
                allVideos.put(video.getID(), video);
            }
        }

        List<GuideBooksFragmentContract.Video> res = new ArrayList<>(allVideos.values());
        Collections.sort(res, (video1, video2) -> video1.getTitle().compareTo(video2.getTitle()));

        this.videos.clear();
        this.videos.addAll(res);
        this.mAdapter.notifyDataSetChanged();
        this.displayLoadingState(false);
    }

    @Override
    public @Nullable Context getViewContext() {
        return getContext();
    }

    @Override
    public void displayLoadingState(boolean state) {
        progressBar.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    @Override
    public void playVideo(GuideBooksFragmentContract.Video video) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(video.getLocalPath()), "video/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    @Override
    public void downloadVideo(GuideBooksFragmentContract.DownloadListener downloadListener, GuideBooksFragmentContract.Video video) {
        new DownloadGuideBooksUtils(downloadListener, video.getName(), getViewContext()).execute();
    }
}
