package org.smartregister.chw.provider;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Rules;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.core.rule.FpAlertRule;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.FpUtil;
import org.smartregister.chw.core.utils.HomeVisitUtil;
import org.smartregister.chw.fp_pathfinder.dao.FpDao;
import org.smartregister.chw.fp_pathfinder.provider.BaseFpRegisterProvider;
import org.smartregister.chw.fp_pathfinder.util.FamilyPlanningConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.Date;
import java.util.Set;

/**
 * Created by cozej4 on 4/28/20.
 *
 * @author cozej4 https://github.com/cozej4
 */
public class PathfinderFpProvider extends BaseFpRegisterProvider {

    private Context context;
    private View.OnClickListener onClickListener;

    public PathfinderFpProvider(Context context, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, paginationClickListener, onClickListener, visibleColumns);
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

    private void updateDueColumn(Context context, RegisterViewHolder viewHolder, FpAlertRule fpAlertRule) {
        viewHolder.dueButton.setVisibility(View.VISIBLE);
        if (fpAlertRule.getButtonStatus().equalsIgnoreCase(CoreConstants.VISIT_STATE.NOT_DUE_YET)) {
            setVisitButtonNextDueStatus(context, FpUtil.sdf.format(fpAlertRule.getDueDate()), viewHolder.dueButton);
        }
        if (fpAlertRule.getButtonStatus().equalsIgnoreCase(CoreConstants.VISIT_STATE.DUE)) {
            setVisitButtonDueStatus(context, String.valueOf(Days.daysBetween(new DateTime(fpAlertRule.getDueDate()), new DateTime()).getDays()), viewHolder.dueButton);
        } else if (fpAlertRule.getButtonStatus().equalsIgnoreCase(CoreConstants.VISIT_STATE.OVERDUE)) {
            setVisitButtonOverdueStatus(context, String.valueOf(Days.daysBetween(new DateTime(fpAlertRule.getOverDueDate()), new DateTime()).getDays()), viewHolder.dueButton);
        } else if (fpAlertRule.getButtonStatus().equalsIgnoreCase(CoreConstants.VISIT_STATE.VISIT_DONE)) {
            setVisitDone(context, viewHolder.dueButton);
        }
    }

    private void setVisitButtonNextDueStatus(Context context, String visitDue, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(org.smartregister.chw.core.R.color.light_grey_text));
        dueButton.setText(context.getString(org.smartregister.chw.core.R.string.fp_visit_day_next_due, visitDue));
        dueButton.setBackgroundResource(org.smartregister.chw.core.R.drawable.colorless_btn_selector);
        dueButton.setOnClickListener(null);
    }


    private void setVisitButtonDueStatus(Context context, String visitDue, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(org.smartregister.chw.core.R.color.alert_in_progress_blue));
        if (visitDue.equalsIgnoreCase("0")) {
            dueButton.setText(context.getString(org.smartregister.chw.core.R.string.fp_visit_day_due_today));
        } else {
            dueButton.setText(context.getString(org.smartregister.chw.core.R.string.fp_visit_day_due, visitDue));
        }
        dueButton.setBackgroundResource(org.smartregister.chw.core.R.drawable.blue_btn_selector);
        dueButton.setOnClickListener(onClickListener);
    }

    private void setVisitButtonOverdueStatus(Context context, String visitDue, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(org.smartregister.chw.core.R.color.white));
        if (visitDue.equalsIgnoreCase("0")) {
            dueButton.setText(context.getString(org.smartregister.chw.core.R.string.fp_visit_day_overdue_today));

        } else {
            dueButton.setText(context.getString(org.smartregister.chw.core.R.string.fp_visit_day_overdue, visitDue));
        }
        dueButton.setBackgroundResource(org.smartregister.chw.core.R.drawable.overdue_red_btn_selector);
        dueButton.setOnClickListener(onClickListener);
    }

    private void setVisitDone(Context context, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(org.smartregister.chw.core.R.color.alert_complete_green));
        dueButton.setText(context.getString(org.smartregister.chw.core.R.string.visit_done));
        dueButton.setBackgroundColor(context.getResources().getColor(org.smartregister.chw.core.R.color.transparent));
        dueButton.setOnClickListener(null);
    }


    private class UpdateAsyncTask extends AsyncTask<Void, Void, Void> {
        private final RegisterViewHolder viewHolder;
        private final CommonPersonObjectClient pc;
        private final Context context;
        private FpAlertRule fpAlertRule;
        private Visit lastVisit;
        private Integer pillCycles;
        private String dayFp;
        private String fpMethod;

        private UpdateAsyncTask(Context context, RegisterViewHolder viewHolder, CommonPersonObjectClient pc) {
            this.context = context;
            this.viewHolder = viewHolder;
            this.pc = pc;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String baseEntityID = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, false);
            dayFp = Utils.getValue(pc.getColumnmaps(), FamilyPlanningConstants.DBConstants.FP_FP_START_DATE, true);
            fpMethod = Utils.getValue(pc.getColumnmaps(), FamilyPlanningConstants.DBConstants.FP_METHOD_ACCEPTED, false);
            pillCycles = FpDao.getLastPillCycle(baseEntityID, fpMethod);
            if (fpMethod.equalsIgnoreCase(FamilyPlanningConstants.DBConstants.FP_INJECTABLE)) {
                lastVisit = FpDao.getLatestInjectionVisit(baseEntityID, fpMethod);
            } else {
                lastVisit = FpDao.getLatestFpVisit(baseEntityID, FamilyPlanningConstants.EventType.FP_FOLLOW_UP_VISIT, fpMethod);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            Date fpDate = FpUtil.parseFpStartDate(dayFp);
            Date lastVisitDate = null;
            if (lastVisit != null) {
                lastVisitDate = lastVisit.getDate();
            }
            Rules rule = FpUtil.getFpRules(fpMethod);
            fpAlertRule = HomeVisitUtil.getFpVisitStatus(rule, lastVisitDate, fpDate, pillCycles, fpMethod);
            if (fpAlertRule != null
                    && StringUtils.isNotBlank(fpAlertRule.getVisitID())
                    && !fpAlertRule.getButtonStatus().equalsIgnoreCase(CoreConstants.VISIT_STATE.EXPIRED)
            ) {
                updateDueColumn(context, viewHolder, fpAlertRule);
            }
        }
    }
}
