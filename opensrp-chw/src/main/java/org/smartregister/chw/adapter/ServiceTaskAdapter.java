package org.smartregister.chw.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.smartregister.Context;
import org.smartregister.chw.R;
import org.smartregister.chw.listener.OnClickServiceTaskAdapter;
import org.smartregister.chw.presenter.ChildHomeVisitPresenter;
import org.smartregister.chw.util.ServiceTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServiceTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ChildHomeVisitPresenter presenter;
    private OnClickServiceTaskAdapter onClickServiceTaskAdapter;
    private Context context;

    public ServiceTaskAdapter(ChildHomeVisitPresenter presenter, Context context, OnClickServiceTaskAdapter onClickAdapter){
        this.presenter = presenter;
        this.context = context;
        this.onClickServiceTaskAdapter = onClickAdapter;

    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ContentViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_task_home_visit, null));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        ContentViewHolder contentViewHolder  = (ContentViewHolder) viewHolder;
        final ServiceTask serviceTask = presenter.getServiceTasks().get(i);
        contentViewHolder.titleText.setText(serviceTask.getTaskTitle());
        contentViewHolder.labelText.setTextColor(context.getColorResource(R.color.grey));
        contentViewHolder.labelText.setText(serviceTask.getTaskLabel());
        contentViewHolder.circleImageView.setImageResource(R.drawable.ic_checked);
        contentViewHolder.circleImageView.setColorFilter(context.getColorResource(R.color.white));

        int color_res = serviceTask.isGreen() ? R.color.alert_complete_green : R.color.pnc_circle_yellow;

        contentViewHolder.circleImageView.setCircleBackgroundColor(context.getColorResource(color_res));
        contentViewHolder.circleImageView.setBorderColor(context.getColorResource(color_res));
        contentViewHolder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickServiceTaskAdapter.onClick(viewHolder.getAdapterPosition(), serviceTask);
            }
        });


    }

    @Override
    public int getItemCount() {
        return presenter.getServiceTasks().size();
    }
    public class ContentViewHolder extends RecyclerView.ViewHolder {
        public TextView titleText, labelText;
        public CircleImageView circleImageView;
        private View myView;

        private ContentViewHolder(View view) {
            super(view);
            titleText = view.findViewById(R.id.textview_title_service_task);
            labelText = view.findViewById(R.id.textview_task_label);
            circleImageView = view.findViewById(R.id.task_status_circle);
            myView = view;
        }

        public View getView() {
            return myView;
        }
    }
}
