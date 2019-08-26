package org.smartregister.chw.core.provider;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import org.jeasy.rules.api.Rules;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.provider.AncRegisterProvider;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.interactor.CoreChildProfileInteractor;
import org.smartregister.chw.core.utils.ChwDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.HomeVisitUtil;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.chw.core.utils.VisitSummary;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.view.contract.SmartRegisterClient;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Set;

public class ChwAncRegisterProvider extends AncRegisterProvider {

    private Context context;
    private View.OnClickListener onClickListener;

    public ChwAncRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener);
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        super.getView(cursor, client, viewHolder);

        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        Utils.startAsyncTask(new UpdateAsyncTask(context, viewHolder, pc), null);
    }

    private void updateDueColumn(Context context, RegisterViewHolder viewHolder, VisitSummary visitSummary) {
        viewHolder.dueButton.setVisibility(View.VISIBLE);
        if (visitSummary.getVisitStatus().equalsIgnoreCase(CoreChildProfileInteractor.VisitType.DUE.name())) {
            setVisitButtonDueStatus(context, viewHolder.dueButton);
        } else if (visitSummary.getVisitStatus().equalsIgnoreCase(CoreChildProfileInteractor.VisitType.OVERDUE.name())) {
            setVisitButtonOverdueStatus(context, viewHolder.dueButton, visitSummary.getNoOfMonthDue());
        } else if (visitSummary.getVisitStatus().equalsIgnoreCase(CoreChildProfileInteractor.VisitType.LESS_TWENTY_FOUR.name())) {
            setVisitLessTwentyFourView(context, viewHolder.dueButton);
        } else if (visitSummary.getVisitStatus().equalsIgnoreCase(CoreChildProfileInteractor.VisitType.VISIT_THIS_MONTH.name())) {
            setVisitAboveTwentyFourView(context, viewHolder.dueButton);
        } else if (visitSummary.getVisitStatus().equalsIgnoreCase(CoreChildProfileInteractor.VisitType.NOT_VISIT_THIS_MONTH.name())) {
            setVisitNotDone(context, viewHolder.dueButton);
        }
    }

    private void setVisitButtonDueStatus(Context context, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(R.color.alert_in_progress_blue));
        dueButton.setText(context.getString(R.string.record_home_visit));
        dueButton.setBackgroundResource(R.drawable.blue_btn_selector);
        dueButton.setOnClickListener(onClickListener);
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

    private void setVisitLessTwentyFourView(Context context, Button dueButton) {
        setVisitAboveTwentyFourView(context, dueButton);
    }

    private void setVisitAboveTwentyFourView(Context context, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(R.color.alert_complete_green));
        dueButton.setText(context.getString(R.string.visit_done));
        dueButton.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        dueButton.setOnClickListener(null);
    }

    private void setVisitNotDone(Context context, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(R.color.progress_orange));
        dueButton.setText(context.getString(R.string.visit_not_done));
        dueButton.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        dueButton.setOnClickListener(null);
    }

    private class UpdateAsyncTask extends AsyncTask<Void, Void, Void> {
        private final RegisterViewHolder viewHolder;
        private final CommonPersonObjectClient pc;
        private final Context context;

        private final Rules rules;
        private VisitSummary visitSummary;

        private UpdateAsyncTask(Context context, RegisterViewHolder viewHolder, CommonPersonObjectClient pc) {
            this.context = context;
            this.viewHolder = viewHolder;
            this.pc = pc;
            this.rules = CoreChwApplication.getInstance().getRulesEngineHelper().rules(CoreConstants.RULE_FILE.ANC_HOME_VISIT);
        }

        @Override
        protected Void doInBackground(Void... params) {
            //map = getChildDetails(pc.getCaseId());

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String lmpDate = org.smartregister.util.Utils.getValue(pc.getColumnmaps(), ChwDBConstants.LMP, false);
            String baseEntityID = org.smartregister.util.Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, false);

            LocalDate dateCreated = (new DateTime(org.smartregister.util.Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DATE_CREATED, false))).toLocalDate();

            Visit lastNotDoneVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(baseEntityID, org.smartregister.chw.anc.util.Constants.EVENT_TYPE.ANC_HOME_VISIT_NOT_DONE);
            if (lastNotDoneVisit != null) {
                Visit lastNotDoneVisitUndo = AncLibrary.getInstance().visitRepository().getLatestVisit(baseEntityID, org.smartregister.chw.anc.util.Constants.EVENT_TYPE.ANC_HOME_VISIT_NOT_DONE_UNDO);
                if (lastNotDoneVisitUndo != null
                        && lastNotDoneVisitUndo.getDate().after(lastNotDoneVisit.getDate())) {
                    lastNotDoneVisit = null;
                }
            }

            Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(baseEntityID, org.smartregister.chw.anc.util.Constants.EVENT_TYPE.ANC_HOME_VISIT);
            String visitDate = lastVisit != null ? sdf.format(lastVisit.getDate()) : null;
            String lastVisitNotDone = lastNotDoneVisit != null ? sdf.format(lastNotDoneVisit.getDate()) : null;

            visitSummary = HomeVisitUtil.getAncVisitStatus(context, rules, lmpDate, visitDate, lastVisitNotDone, dateCreated);
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            // Update status column
            if (visitSummary != null && !visitSummary.getVisitStatus().equalsIgnoreCase(CoreChildProfileInteractor.VisitType.EXPIRY.name())) {
                updateDueColumn(context, viewHolder, visitSummary);
            }
        }
    }

}
