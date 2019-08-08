package com.opensrp.chw.core.task;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.opensrp.chw.core.R;
import com.opensrp.chw.core.application.CoreChwApplication;
import com.opensrp.chw.core.holders.RegisterViewHolder;
import com.opensrp.chw.core.interactor.CoreChildProfileInteractor;
import com.opensrp.chw.core.model.ChildVisit;
import com.opensrp.chw.core.utils.ChildDBConstants;
import com.opensrp.chw.core.utils.CoreChildUtils;
import com.opensrp.chw.core.utils.CoreConstants;

import org.jeasy.rules.api.Rules;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

public class UpdateLastAsyncTask extends AsyncTask<Void, Void, Void> {
    private final Context context;
    private final CommonRepository commonRepository;
    private final RegisterViewHolder viewHolder;
    private final String baseEntityId;
    private final Rules rules;
    private String TAG = UpdateLastAsyncTask.class.getCanonicalName();
    private CommonPersonObject commonPersonObject;
    private ChildVisit childVisit;
    private View.OnClickListener onClickListener;

    public UpdateLastAsyncTask(Context context, CommonRepository commonRepository, RegisterViewHolder viewHolder, String baseEntityId, View.OnClickListener onClickListener) {
        this.context = context;
        this.commonRepository = commonRepository;
        this.viewHolder = viewHolder;
        this.baseEntityId = baseEntityId;
        this.onClickListener = onClickListener;
        this.rules = CoreChwApplication.getInstance().getRulesEngineHelper().rules(CoreConstants.RULE_FILE.HOME_VISIT);
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (commonRepository != null) {
            commonPersonObject = commonRepository.findByBaseEntityId(baseEntityId);
            if (commonPersonObject != null) {
                String strDateCreated = Utils.getValue(commonPersonObject.getColumnmaps(), ChildDBConstants.KEY.DATE_CREATED, false);
                String lastVisitDate = Utils.getValue(commonPersonObject.getColumnmaps(), ChildDBConstants.KEY.LAST_HOME_VISIT, false);
                String visitNotDone = Utils.getValue(commonPersonObject.getColumnmaps(), ChildDBConstants.KEY.VISIT_NOT_DONE, false);
                long lastVisit = 0, visitNot = 0, dateCreated = 0;
                if (!TextUtils.isEmpty(visitNotDone)) {
                    visitNot = Long.parseLong(visitNotDone);
                }
                if (!TextUtils.isEmpty(lastVisitDate)) {
                    lastVisit = Long.parseLong(lastVisitDate);
                }
                if (!TextUtils.isEmpty(strDateCreated)) {
                    dateCreated = Utils.dobStringToDateTime(strDateCreated).getMillis();
                }

                String dobString = Utils.getDuration(Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.DOB, false));
                childVisit = CoreChildUtils.getChildVisitStatus(context, rules, dobString, lastVisit, visitNot, dateCreated);

            }
            return null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void param) {
        if (commonPersonObject != null) {
            viewHolder.dueButton.setVisibility(View.VISIBLE);
            if (childVisit.getVisitStatus().equalsIgnoreCase(CoreChildProfileInteractor.VisitType.DUE.name())) {
                setVisitButtonDueStatus(context, viewHolder.dueButton);
            } else if (childVisit.getVisitStatus().equalsIgnoreCase(CoreChildProfileInteractor.VisitType.OVERDUE.name())) {
                setVisitButtonOverdueStatus(context, viewHolder.dueButton, childVisit.getNoOfMonthDue());
            } else if (childVisit.getVisitStatus().equalsIgnoreCase(CoreChildProfileInteractor.VisitType.LESS_TWENTY_FOUR.name())) {
                setVisitLessTwentyFourView(context, viewHolder.dueButton, childVisit.getLastVisitDays());
            } else if (childVisit.getVisitStatus().equalsIgnoreCase(CoreChildProfileInteractor.VisitType.VISIT_THIS_MONTH.name())) {
                setVisitAboveTwentyFourView(context, viewHolder.dueButton);
            } else if (childVisit.getVisitStatus().equalsIgnoreCase(CoreChildProfileInteractor.VisitType.NOT_VISIT_THIS_MONTH.name())) {
                setVisitNotDone(context, viewHolder.dueButton);
            }
        } else {
            viewHolder.dueButton.setVisibility(View.GONE);
            //attachSyncOnclickListener(viewHolder.sync, pc);
        }

    }

    private void setVisitNotDone(Context context, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(R.color.progress_orange));
        dueButton.setText(context.getString(R.string.visit_not_done));
        dueButton.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        dueButton.setOnClickListener(null);
    }

    private void setVisitAboveTwentyFourView(Context context, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(R.color.alert_complete_green));
        dueButton.setText(context.getString(R.string.visit_done));
        dueButton.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        dueButton.setOnClickListener(null);
    }

    private void setVisitLessTwentyFourView(Context context, Button dueButton, String lastVisitMonth) {
        setVisitAboveTwentyFourView(context, dueButton);
    }

    private void setVisitButtonOverdueStatus(Context context, Button dueButton, String lastVisitDays) {
        dueButton.setTextColor(context.getResources().getColor(R.color.white));
        if (TextUtils.isEmpty(lastVisitDays)) {
            dueButton.setText(context.getString(R.string.record_visit));
        } else {
            dueButton.setText(context.getString(R.string.due_visit, lastVisitDays));
        }

        dueButton.setBackgroundResource(R.drawable.overdue_red_btn_selector);
        dueButton.setOnClickListener(onClickListener);
    }

    private void setVisitButtonDueStatus(Context context, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(R.color.alert_in_progress_blue));
        dueButton.setText(context.getString(R.string.record_home_visit));
        dueButton.setBackgroundResource(R.drawable.blue_btn_selector);
        dueButton.setOnClickListener(onClickListener);
    }
}
