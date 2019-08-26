package org.smartregister.chw.provider;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Rules;
import org.smartregister.chw.R;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.model.ChildVisit;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.interactor.ChildProfileInteractor;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;
import org.smartregister.family.provider.FamilyDueRegisterProvider;
import org.smartregister.family.util.DBConstants;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.Set;

public class ChwDueRegisterProvider extends FamilyDueRegisterProvider {
    private static final String TAG = ChwDueRegisterProvider.class.getCanonicalName();

    private final Context context;

    private final View.OnClickListener onClickListener;

    public ChwDueRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener);
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        populatePatientColumn(pc, client, viewHolder);
        // populateIdentifierColumn(pc, viewHolder);

        viewHolder.status.setVisibility(View.GONE);
        Utils.startAsyncTask(new UpdateAsyncTask(viewHolder, pc), null);
    }

    private void populatePatientColumn(CommonPersonObjectClient pc, SmartRegisterClient client, final RegisterViewHolder viewHolder) {

        String firstName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
        String middleName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true);
        String lastName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.LAST_NAME, true);

        String patientName = org.smartregister.family.util.Utils.getName(firstName, middleName, lastName);

        String dob = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DOB, false);
        String dobString = Utils.getDuration(dob);
        dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;

        patientName = patientName + ", " + dobString + " " + context.getString(R.string.home_visit_suffix);
        fillValue(viewHolder.patientNameAge, patientName);

        // Update UI cutoffs
        viewHolder.patientNameAge.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.member_due_list_title_size));

        viewHolder.nextArrow.setVisibility(View.VISIBLE);

        String lastVisit = Utils.getValue(pc.getColumnmaps(), ChildDBConstants.KEY.LAST_HOME_VISIT, false);
        if (StringUtils.isNotBlank(lastVisit)) {
            // String lastVisitString = Utils.actualDuration(context, Utils.getDuration(lastVisit));
            String lastVisitString = org.smartregister.chw.core.utils.Utils.actualDaysBetweenDateAndNow(context, lastVisit);
            viewHolder.lastVisit.setText(String.format(context.getString(R.string.last_visit_prefix), lastVisitString));
            viewHolder.lastVisit.setVisibility(View.VISIBLE);
        } else {
            viewHolder.lastVisit.setVisibility(View.INVISIBLE);
        }

        viewHolder.nextArrowColumn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.nextArrow.performClick();
            }
        });

        viewHolder.statusColumn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.patientColumn.performClick();
            }
        });

        attachPatientOnclickListener(viewHolder.patientColumn, client);

        attachNextArrowOnclickListener(viewHolder.nextArrow, client);
    }

    private void attachPatientOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(R.id.VIEW_ID, BaseFamilyProfileMemberFragment.CLICK_VIEW_NORMAL);
    }

    private void attachNextArrowOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(R.id.VIEW_ID, BaseFamilyProfileMemberFragment.CLICK_VIEW_NEXT_ARROW);
    }

    private void updateDueColumn(RegisterViewHolder viewHolder, ChildVisit childVisit) {
        if (childVisit != null) {
            viewHolder.status.setVisibility(View.VISIBLE);
            if (childVisit.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.DUE.name())) {
                viewHolder.status.setImageResource(Utils.getDueProfileImageResourceIDentifier());
            } else if (childVisit.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.OVERDUE.name())) {
                viewHolder.status.setImageResource(Utils.getOverDueProfileImageResourceIDentifier());
            } else {
                viewHolder.status.setVisibility(View.INVISIBLE);
            }
        } else {
            viewHolder.status.setVisibility(View.INVISIBLE);
        }
    }

    private ChildVisit retrieveChildVisitList(Rules rules, CommonPersonObjectClient pc) {
        String dobString = Utils.getDuration(Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DOB, false));
        String lastVisitDate = Utils.getValue(pc.getColumnmaps(), ChildDBConstants.KEY.LAST_HOME_VISIT, false);
        String visitNotDone = Utils.getValue(pc.getColumnmaps(), ChildDBConstants.KEY.VISIT_NOT_DONE, false);
        String strDateCreated = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), ChildDBConstants.KEY.DATE_CREATED, false);
        long lastVisit = 0, visitNot = 0, dateCreated = 0;
        if (!TextUtils.isEmpty(lastVisitDate)) {
            lastVisit = Long.valueOf(lastVisitDate);
        }
        if (!TextUtils.isEmpty(visitNotDone)) {
            visitNot = Long.valueOf(visitNotDone);
        }
        if (!TextUtils.isEmpty(strDateCreated)) {
            dateCreated = org.smartregister.family.util.Utils.dobStringToDateTime(strDateCreated).getMillis();
        }
        return ChildUtils.getChildVisitStatus(context, rules, dobString, lastVisit, visitNot, dateCreated);
    }

    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    private class UpdateAsyncTask extends AsyncTask<Void, Void, Void> {
        private final RegisterViewHolder viewHolder;
        private final CommonPersonObjectClient pc;

        private final Rules rules;

        private ChildVisit childVisit;

        private UpdateAsyncTask(RegisterViewHolder viewHolder, CommonPersonObjectClient pc) {
            this.viewHolder = viewHolder;
            this.pc = pc;
            this.rules = ChwApplication.getInstance().getRulesEngineHelper().rules(Constants.RULE_FILE.HOME_VISIT);
        }

        @Override
        protected Void doInBackground(Void... params) {
            childVisit = retrieveChildVisitList(rules, pc);
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            // Update status column
            updateDueColumn(viewHolder, childVisit);
        }
    }

}
