package org.smartregister.chw.provider;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;

import org.jeasy.rules.api.Rules;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.interactor.ChildProfileInteractor;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.HomeVisitUtil;
import org.smartregister.chw.util.VisitSummary;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import provider.PncRegisterProvider;
import timber.log.Timber;

import static org.smartregister.chw.anc.AncLibrary.getInstance;

public class ChwPncRegisterProvider extends PncRegisterProvider {

    private Context context;
    private View.OnClickListener onClickListener;

    public ChwPncRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener);
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        super.getView(cursor, client, viewHolder);

        viewHolder.dueButton.setVisibility(View.GONE);
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        viewHolder.dueButton.setOnClickListener(null);
        Utils.startAsyncTask(new UpdateAsyncTask(context, viewHolder, pc), null);
    }

    private void setVisitButtonDueStatus(Context context, String visitDue, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(R.color.alert_in_progress_blue));
        dueButton.setText(context.getString(R.string.pnc_visit_day_due, visitDue));
        dueButton.setBackgroundResource(R.drawable.blue_btn_selector);
        dueButton.setOnClickListener(onClickListener);
    }

    private void setVisitButtonOverdueStatus(Context context, String visitDue, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(R.color.white));
        dueButton.setText(context.getString(R.string.pnc_visit_day_overdue, visitDue));
        dueButton.setBackgroundResource(R.drawable.overdue_red_btn_selector);
        dueButton.setOnClickListener(onClickListener);
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

    private void updateDueColumn(Context context, RegisterViewHolder viewHolder, VisitSummary visitSummary) {
        viewHolder.dueButton.setVisibility(View.VISIBLE);
        if (visitSummary.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.DUE.name())) {
            setVisitButtonDueStatus(context, visitSummary.getNoOfDaysDue(), viewHolder.dueButton);
        } else if (visitSummary.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.OVERDUE.name())) {
            setVisitButtonOverdueStatus(context, visitSummary.getNoOfDaysDue(), viewHolder.dueButton);
        } else if (visitSummary.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.VISIT_THIS_MONTH.name())) {
            setVisitAboveTwentyFourView(context, viewHolder.dueButton);
        } else if (visitSummary.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.NOT_VISIT_THIS_MONTH.name())) {
            setVisitNotDone(context, viewHolder.dueButton);
        }
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
            this.rules = ChwApplication.getInstance().getRulesEngineHelper().rules(Constants.RULE_FILE.PNC_HOME_VISIT);
        }

        @Override
        protected Void doInBackground(Void... params) {
            //map = getChildDetails(pc.getCaseId());
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String baseEntityID = org.smartregister.util.Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, false);
            String dayPnc = Utils.getValue(pc.getColumnmaps(), org.smartregister.chw.pnc.util.Constants.KEY.DELIVERY_DATE, true);
            Date deliveryDate = null;
            Date lastVisitDate = null;
            Date lastNotVisitDate = null;
            try {
                deliveryDate = sdf.parse(dayPnc);
            } catch (ParseException e) {
                Timber.e(e);
            }

            Visit lastVisit = getInstance().visitRepository().getLatestVisit(baseEntityID, org.smartregister.chw.anc.util.Constants.EVENT_TYPE.PNC_HOME_VISIT);
            if (lastVisit != null)
                lastVisitDate = lastVisit.getDate();

            Visit lastNotVisit = getInstance().visitRepository().getLatestVisit(baseEntityID, org.smartregister.chw.anc.util.Constants.EVENT_TYPE.PNC_HOME_VISIT_NOT_DONE);
            if (lastNotVisit != null)
                lastNotVisitDate = lastNotVisit.getDate();

            visitSummary = HomeVisitUtil.getPncVisitStatus(rules, lastVisitDate, lastNotVisitDate, deliveryDate);
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            // Update status column
            if (visitSummary != null && !visitSummary.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.EXPIRY.name())) {
                updateDueColumn(context, viewHolder, visitSummary);
            }
        }
    }
}
