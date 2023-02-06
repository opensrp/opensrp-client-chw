package org.smartregister.chw.provider;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import org.jeasy.rules.api.Rules;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.model.ChildVisit;
import org.smartregister.chw.core.provider.CoreRegisterProvider;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.dao.FamilyDao;
import org.smartregister.chw.fp.dao.FpDao;
import org.smartregister.chw.malaria.dao.MalariaDao;
import org.smartregister.chw.util.UpcomingServicesUtil;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import timber.log.Timber;

public class FamilyRegisterProvider extends CoreRegisterProvider {
    protected final Context context;
    private final View.OnClickListener onClickListener;

    public FamilyRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener);
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        super.getView(cursor, client, viewHolder);
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        viewHolder.dueButton.setVisibility(View.GONE);
        String familyBaseEntityId = pc.getCaseId();
        if (updateAsyncTask == null) { //Ensure this task is only called once
            Utils.startAsyncTask(new UpdateAsyncTask(context, viewHolder, familyBaseEntityId), null);
        }
    }

    @Override
    public void updateDueColumn(Context context, RegisterViewHolder viewHolder, ChildVisit childVisit) {

    }

    @Override
    public List<ChildVisit> retrieveChildVisitList(Rules rules, List<Map<String, String>> list) {
        return null;
    }

    protected void setTasksDoneStatus(Context context, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(R.color.alert_complete_green));
        dueButton.setText(context.getString(R.string.tasks_done));
        dueButton.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        dueButton.setOnClickListener(null);
    }

    private void setTasksOverdueStatus(Context context, Button dueButton, Integer count) {
        dueButton.setTextColor(context.getResources().getColor(R.color.white));
        dueButton.setText(MessageFormat.format(context.getString(R.string.tasks_status), count));
        dueButton.setBackgroundResource(R.drawable.overdue_red_btn_selector);
        dueButton.setOnClickListener(onClickListener);
    }

    private void setTasksDueStatus(Context context, Button dueButton, Integer count) {
        dueButton.setTextColor(context.getResources().getColor(R.color.alert_in_progress_blue));
        dueButton.setText(MessageFormat.format(context.getString(R.string.tasks_status), count));
        dueButton.setBackgroundResource(R.drawable.blue_btn_selector);
        dueButton.setOnClickListener(onClickListener);
    }

    private void setTaskNotDone(Context context, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(R.color.progress_orange));
        dueButton.setText(context.getString(R.string.tasks_not_done));
        dueButton.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        dueButton.setOnClickListener(onClickListener);
    }

    @Override
    public ChildVisit mergeChildVisits(List<ChildVisit> childVisitList) {
        return null;
    }

    @Override
    public String getChildAgeLimitFilter(){
        if(ChwApplication.getApplicationFlavor().showIconsForChildrenUnderTwoAndGirlsAgeNineToEleven()){
          return org.smartregister.chw.util.ChildDBConstants.childDueVaccinesFilterForChildrenBelowTwoAndGirlsAgeNineToEleven();
        }
        else {
            return ChildDBConstants.childAgeLimitFilter();
        }
    }

    private void updateButtonState(Context context, RegisterViewHolder viewHolder, Map<String, Integer> services) {
        if (services != null && !services.isEmpty()) {
            viewHolder.dueButton.setVisibility(View.VISIBLE);
            Integer visits_done = services.get(CoreConstants.VisitType.DONE.name());
            Integer visits_due = services.get(CoreConstants.VisitType.DUE.name());
            Integer visits_not_done = services.get(CoreConstants.VisitType.NOT_VISIT_THIS_MONTH.name());
            Integer visits_over_due = services.get(CoreConstants.VisitType.OVERDUE.name());

            int due = visits_due == null ? 0 : visits_due;
            int over_due = visits_over_due == null ? 0 : visits_over_due;
            if (due > 0 && over_due > 0) {
                over_due = over_due + due;
            }

            //over_due = over_due + due;
            if (over_due > 0) {
                setTasksOverdueStatus(context, viewHolder.dueButton, over_due);
            } else if (due > 0) {
                setTasksDueStatus(context, viewHolder.dueButton, due);
            } else if (visits_done != null && visits_done > 0) {
                setTasksDoneStatus(context, viewHolder.dueButton);
            } else if (visits_not_done != null && visits_not_done > 0) {
                setTaskNotDone(context, viewHolder.dueButton);
            }else if (due == 0 && over_due == 0){
                viewHolder.dueButton.setVisibility(View.GONE);
            }

        } else {
            viewHolder.dueButton.setVisibility(View.GONE);
        }
    }

    private class UpdateAsyncTask extends AsyncTask<Void, Void, Void> {
        private final Context context;
        private final RegisterViewHolder viewHolder;
        private final String familyBaseEntityId;

        private List<Map<String, String>> list;
        private int ancWomanCount;
        private int pncWomanCount;
        private int malariaCount;
        private int fpCount;
        private Map<String, Integer> services;
        private List<MemberObject> memberObjects = new ArrayList<>();

        private UpdateAsyncTask(Context context, RegisterViewHolder viewHolder, String familyBaseEntityId) {
            this.context = context;
            this.viewHolder = viewHolder;
            this.familyBaseEntityId = familyBaseEntityId;
        }

        @Override
        protected Void doInBackground(Void... params) {
            list = getChildren(familyBaseEntityId);

            if (ChwApplication.getApplicationFlavor().hasANC())
                ancWomanCount = getAncWomenCount(familyBaseEntityId);

            if (ChwApplication.getApplicationFlavor().hasPNC())
                pncWomanCount = getPncWomenCount(familyBaseEntityId);

            if (ChwApplication.getApplicationFlavor().hasMalaria())
                malariaCount = MalariaDao.getMalariaFamilyMembersCount(familyBaseEntityId);

            if (ChwApplication.getApplicationFlavor().hasFamilyPlanning())
                fpCount = FpDao.getFpWomenCount(familyBaseEntityId) != null ? FpDao.getFpWomenCount(familyBaseEntityId) : 0;

            services = getFamilyDueState(familyBaseEntityId);
            memberObjects = getFamilyMemberObjects(familyBaseEntityId);
            return null;
        }

        private Map<String, Integer>  getFamilyDueState(String familyBaseEntityId) {
            if (ChwApplication.getApplicationFlavor().showFamilyServicesScheduleWithChildrenAboveTwo()) {
                return FamilyDao.getFamilyServiceSchedule(familyBaseEntityId);
            } else {
                return FamilyDao.getFamilyServiceScheduleWithChildrenOnlyUnderTwo(familyBaseEntityId);
            }
        }

        private List<MemberObject> getFamilyMemberObjects(@NonNull final String familyBaseEntity){
            List<Pair<String, String>> memberBirthDates = ChwApplication.getApplicationFlavor().showFamilyServicesScheduleWithChildrenAboveTwo()
                    ? FamilyDao.getFamilyMemberBirthDates(familyBaseEntity) : FamilyDao.getFamilyMemberBirthDatesWithChildrenUnderTwo(familyBaseEntity);
            List<MemberObject> members = new ArrayList<>(memberBirthDates.size());
            for (int i = 0; i < memberBirthDates.size(); i++) {
                MemberObject memberObject = new MemberObject();
                memberObject.setBaseEntityId(memberBirthDates.get(i).second);
                memberObject.setDob(memberBirthDates.get(i).first);
                members.add(memberObject);
            }
            return members;
        }

        @Override
        protected void onPostExecute(Void param) {
            // Update child Icon
            updateChildIcons(viewHolder, list, ancWomanCount, pncWomanCount);
            updateMalariaIcons(viewHolder, malariaCount);
            if (ChwApplication.getApplicationFlavor().checkDueStatusFromUpcomingServices()){
                UpcomingServicesUtil.fetchFamilyUpcomingDueServicesState(memberObjects, context, new Consumer<Map<String, Integer>>() {
                    @Override
                    public void accept(Map<String, Integer> stringIntegerMap) {
                        // for due stats, use upcoming services calculation over db 'schedule_service'
                        Timber.d(stringIntegerMap.toString());
                        services.put(CoreConstants.VisitType.DUE.name(), stringIntegerMap.getOrDefault(CoreConstants.VISIT_STATE.DUE, 0));
                        services.put(CoreConstants.VisitType.OVERDUE.name(), stringIntegerMap.getOrDefault(CoreConstants.VISIT_STATE.OVERDUE, 0));

                        updateButtonState(context, viewHolder, services);
                    }
                });
            }else {
                updateButtonState(context, viewHolder, services);
            }
            updateFpIcons(viewHolder, fpCount);
        }
    }

}
