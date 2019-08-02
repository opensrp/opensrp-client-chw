package com.opensrp.chw.core.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.opensrp.chw.core.R;
import com.opensrp.chw.core.interactor.OnClickServiceTaskAdapter;
import com.opensrp.chw.core.presenter.ChildHomeVisitPresenter;
import com.opensrp.chw.core.utils.CoreChildUtils;
import com.opensrp.chw.core.utils.CoreConstants;
import com.opensrp.chw.core.utils.CoreJsonFormUtils;
import com.opensrp.chw.core.utils.ServiceTask;
import com.opensrp.chw.core.utils.TaskServiceCalculate;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServiceTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ChildHomeVisitPresenter presenter;
    private OnClickServiceTaskAdapter onClickServiceTaskAdapter;
    private Context context;

    public ServiceTaskAdapter(ChildHomeVisitPresenter presenter, Context context, OnClickServiceTaskAdapter onClickAdapter) {
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
        ContentViewHolder contentViewHolder = (ContentViewHolder) viewHolder;
        final ServiceTask serviceTask = presenter.getServiceTasks().get(i);
        contentViewHolder.titleText.setText(serviceTask.getTaskTitle());
        if (!TextUtils.isEmpty(serviceTask.getTaskLabel())) {
            contentViewHolder.labelText.setVisibility(View.VISIBLE);
            contentViewHolder.labelText.setTextColor(context.getResources().getColor((R.color.grey)));
            contentViewHolder.labelText.setText(serviceTask.getTaskLabel());
            contentViewHolder.circleImageView.setImageResource(R.drawable.ic_checked);
            contentViewHolder.circleImageView.setColorFilter(context.getResources().getColor(R.color.white));
            if (!serviceTask.getTaskType().equalsIgnoreCase(TaskServiceCalculate.TASK_TYPE.ECD.name())) {
                if (serviceTask.getTaskType().equalsIgnoreCase(TaskServiceCalculate.TASK_TYPE.Minimum_dietary.name())
                        && serviceTask.getTaskLabel().equalsIgnoreCase(context.getString(R.string.minimum_dietary_choice_3))) {
                    serviceTask.setGreen(true);
                } else if (serviceTask.getTaskType().equalsIgnoreCase(TaskServiceCalculate.TASK_TYPE.MUAC.name())
                        && serviceTask.getTaskLabel().equalsIgnoreCase(context.getString(R.string.muac_choice_1))) {
                    serviceTask.setGreen(true);
                } else if (serviceTask.getTaskType().equalsIgnoreCase(TaskServiceCalculate.TASK_TYPE.LLITN.name())
                        && serviceTask.getTaskLabel().equalsIgnoreCase(context.getString(R.string.yes))) {
                    serviceTask.setGreen(true);
                } else {
                    serviceTask.setGreen(false);
                }
            }


            int color_res = serviceTask.isGreen() ? R.color.alert_complete_green : R.color.pnc_circle_yellow;

            contentViewHolder.circleImageView.setCircleBackgroundColor(context.getResources().getColor(color_res));
            contentViewHolder.circleImageView.setBorderColor(context.getResources().getColor(color_res));
        } else {
            contentViewHolder.labelText.setVisibility(View.GONE);
            contentViewHolder.circleImageView.setCircleBackgroundColor(context.getResources().getColor(R.color.white));
            contentViewHolder.circleImageView.setBorderColor(context.getResources().getColor(R.color.dark_grey));
        }

        contentViewHolder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickServiceTaskAdapter.onClick(viewHolder.getAdapterPosition(), serviceTask);
            }
        });


    }

    public void makeEvent(String homeVisitId, String entityId) {
//        Observable.create(new ObservableOnSubscribe<Object>() {
//            @Override
//            public void subscribe(ObservableEmitter<Object> e) throws Exception {

        for (ServiceTask serviceTask : presenter.getServiceTasks()) {
            if (serviceTask.getTaskType().equalsIgnoreCase(TaskServiceCalculate.TASK_TYPE.Minimum_dietary.name())) {
                CoreChildUtils.updateTaskAsEvent(CoreConstants.EventType.MINIMUM_DIETARY_DIVERSITY, CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.TASK_MINIMUM_DIETARY,
                        CoreJsonFormUtils.toList(CoreJsonFormUtils.getChoiceDietary(context).get(serviceTask.getTaskLabel())), CoreJsonFormUtils.toList(serviceTask.getTaskLabel()),
                        entityId, serviceTask.getTaskLabel(), homeVisitId, CoreConstants.FORM_CONSTANTS.MINIMUM_DIETARY.CODE);
            } else if (serviceTask.getTaskType().equalsIgnoreCase(TaskServiceCalculate.TASK_TYPE.MUAC.name())) {
                CoreChildUtils.updateTaskAsEvent(CoreConstants.EventType.MUAC, CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.TASK_MUAC,
                        CoreJsonFormUtils.toList(CoreJsonFormUtils.getChoiceMuac(context).get(serviceTask.getTaskLabel())), CoreJsonFormUtils.toList(serviceTask.getTaskLabel())
                        , entityId, serviceTask.getTaskLabel(), homeVisitId, CoreConstants.FORM_CONSTANTS.MUAC.CODE);

            } else if (serviceTask.getTaskType().equalsIgnoreCase(TaskServiceCalculate.TASK_TYPE.LLITN.name())) {
                CoreChildUtils.updateTaskAsEvent(CoreConstants.EventType.LLITN, CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.TASK_LLITN,
                        CoreJsonFormUtils.toList(CoreJsonFormUtils.getChoice(context).get(serviceTask.getTaskLabel())), CoreJsonFormUtils.toList(serviceTask.getTaskLabel())
                        , entityId, serviceTask.getTaskLabel(), homeVisitId, CoreConstants.FORM_CONSTANTS.LLITN.CODE);

            } else if (serviceTask.getTaskType().equalsIgnoreCase(TaskServiceCalculate.TASK_TYPE.ECD.name()) && serviceTask.getTaskJson() != null) {
                CoreChildUtils.updateECDTaskAsEvent(homeVisitId, entityId, serviceTask.getTaskJson().toString());
            }
        }

//            }
//        });

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
