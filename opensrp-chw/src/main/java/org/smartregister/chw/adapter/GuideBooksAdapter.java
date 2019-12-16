package org.smartregister.chw.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.R;
import org.smartregister.chw.contract.GuideBooksFragmentContract;
import org.smartregister.chw.util.DownloadGuideBooksUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
        AtomicReference<DownloadGuideBooksUtils> downloadTask = new AtomicReference<>(getDownloadTask(video, myViewHolder));

        myViewHolder.progressBar.setOnClickListener(v -> {
            if (myViewHolder.progressBar.getVisibility() == View.VISIBLE) {
                new AlertDialog.Builder(myViewHolder.getContext())
                        .setTitle(myViewHolder.getContext().getString(R.string.cancel_download))
                        .setMessage(myViewHolder.getContext().getString(R.string.cancel_download_confirmation))
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            // Continue with delete operation
                            if (downloadTask.get() != null)
                                downloadTask.get().cancelDownload();

                            downloadTask.set(getDownloadTask(video, myViewHolder));

                            dialog.dismiss();
                            myViewHolder.progressBar.setVisibility(View.GONE);
                            myViewHolder.icon.setVisibility(View.VISIBLE);
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        myViewHolder.icon.setOnClickListener(v -> {
            if (video.isDowloaded()) {
                view.playVideo(video);
            } else {
                if (downloadTask.get() != null) {
                    if (downloadTask.get().getStatus() == AsyncTask.Status.FINISHED)
                        downloadTask.set(getDownloadTask(video, myViewHolder));

                    downloadTask.get().execute();
                }
            }
        });
        myViewHolder.title.setText(video.getTitle());
    }

    @Nullable
    private DownloadGuideBooksUtils getDownloadTask(GuideBooksFragmentContract.Video video, @NonNull GuideBooksAdapter.MyViewHolder myViewHolder) {
        DownloadGuideBooksUtils downloadTask = null;
        if (!video.isDowloaded()) {
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

                        if (view.getViewContext() != null)
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

            downloadTask = new DownloadGuideBooksUtils(listener, video.getName(), myViewHolder.getContext());
        }
        return downloadTask;
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView icon;
        private ProgressBar progressBar;
        private View view;

        private MyViewHolder(View view) {
            super(view);
            this.view = view;
            title = view.findViewById(R.id.tvVideoTitle);
            icon = view.findViewById(R.id.ivIcon);
            progressBar = view.findViewById(R.id.progress_bar);
        }

        private Context getContext() {
            return view.getContext();
        }
    }

}
