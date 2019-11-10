package org.smartregister.chw.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.smartregister.chw.R;
import org.smartregister.chw.contract.GuideBooksFragmentContract;

import java.util.List;

public class GuideBooksAdapter extends RecyclerView.Adapter<GuideBooksAdapter.MyViewHolder> {
    private List<GuideBooksFragmentContract.Video> videos;
    private GuideBooksFragmentContract.View view;

    public GuideBooksAdapter(List<GuideBooksFragmentContract.Video> videos, GuideBooksFragmentContract.View view) {
        this.videos = videos;
        this.view = view;
    }

    @NonNull
    @Override
    public GuideBooksAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_job_aids_guide_books_item, viewGroup, false);
        return new GuideBooksAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GuideBooksAdapter.MyViewHolder myViewHolder, int position) {
        GuideBooksFragmentContract.Video video = videos.get(position);

        myViewHolder.icon.setVisibility(View.VISIBLE);
        if (video.isDowloaded()) {
            myViewHolder.icon.setImageResource(R.drawable.ic_play_circle_black);
        } else {
            myViewHolder.icon.setImageResource(R.drawable.ic_save_outline_black);
        }
        myViewHolder.progressBar.setVisibility(View.GONE);

        myViewHolder.icon.setOnClickListener(v -> {
            if (video.isDowloaded()) {
                view.playVideo(video);
            } else {

                GuideBooksFragmentContract.DownloadListener listener = new GuideBooksFragmentContract.DownloadListener() {
                    @Override
                    public void onDownloadComplete(boolean successful, String localPath) {

                        video.setDownloaded(successful);
                        video.setLocalPath(localPath);

                        myViewHolder.progressBar.setVisibility(View.GONE);
                        myViewHolder.icon.setVisibility(View.VISIBLE);

                        if (successful) {
                            myViewHolder.icon.setImageResource(R.drawable.ic_play_circle_black);
                        } else {
                            myViewHolder.icon.setImageResource(R.drawable.ic_save_outline_black);

                            Toast.makeText(view.getViewContext(),
                                    view.getViewContext().getString(R.string.jobs_aid_failed_download, video.getTitle())
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onStarted() {
                        myViewHolder.progressBar.setVisibility(View.VISIBLE);
                        myViewHolder.icon.setVisibility(View.GONE);
                    }
                };

                view.downloadVideo(listener, video);
            }
        });
        myViewHolder.title.setText(video.getTitle());
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView icon;
        private ProgressBar progressBar;

        private MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.tvVideoTitle);
            icon = view.findViewById(R.id.ivIcon);
            progressBar = view.findViewById(R.id.progress_bar);
        }
    }

}
