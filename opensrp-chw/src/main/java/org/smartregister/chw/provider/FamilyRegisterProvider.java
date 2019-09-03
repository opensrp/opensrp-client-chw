package org.smartregister.chw.provider;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.View;

import org.jeasy.rules.api.Rules;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.model.ChildVisit;
import org.smartregister.chw.core.provider.CoreRegisterProvider;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.dao.FamilyDao;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class FamilyRegisterProvider extends CoreRegisterProvider {

    public FamilyRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener);
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        super.getView(cursor, client, viewHolder);
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
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

    @Override
    public ChildVisit mergeChildVisits(List<ChildVisit> childVisitList) {
        return null;
    }

    private void updateButtonState(Context context, RegisterViewHolder viewHolder, Map<String, Integer> services) {
        if (services != null) {
            viewHolder.dueButton.setVisibility(View.VISIBLE);
            Integer visits_done = services.get(CoreConstants.VisitType.DONE.name());
            Integer visits_due = services.get(CoreConstants.VisitType.DUE.name());
            Integer visits_not_done = services.get(CoreConstants.VisitType.NOT_VISIT_THIS_MONTH.name());
            Integer visits_over_due = services.get(CoreConstants.VisitType.OVERDUE.name());

            int due = visits_due == null ?0 : visits_due;
            int over_due = visits_over_due == null ? 0 : visits_over_due;
            over_due = over_due + due;

            if(over_due > 0){

            }else if(due > 0){

            }else if (visits_done != null && visits_done > 0){

            }else if (visits_done != null && visits_done > 0){

            }else{

            }

        }
    }

    private class UpdateAsyncTask extends AsyncTask<Void, Void, Void> {
        private final Context context;
        private final RegisterViewHolder viewHolder;
        private final String familyBaseEntityId;

        private List<Map<String, String>> list;
        private int ancWomanCount;
        private int pncWomanCount;
        private Map<String, Integer> services;

        private UpdateAsyncTask(Context context, RegisterViewHolder viewHolder, String familyBaseEntityId) {
            this.context = context;
            this.viewHolder = viewHolder;
            this.familyBaseEntityId = familyBaseEntityId;
        }

        @Override
        protected Void doInBackground(Void... params) {
            list = getChildren(familyBaseEntityId);
            ancWomanCount = ChwApplication.ancRegisterRepository().getAncPncWomenCount(familyBaseEntityId, CoreConstants.TABLE_NAME.ANC_MEMBER);
            pncWomanCount = ChwApplication.ancRegisterRepository().getAncPncWomenCount(familyBaseEntityId, CoreConstants.TABLE_NAME.PNC_MEMBER);
            services = FamilyDao.getFamilyServiceSchedule(familyBaseEntityId);
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            // Update child Icon
            updateChildIcons(viewHolder, list, ancWomanCount, pncWomanCount);
            updateButtonState(context, viewHolder, services);
        }
    }
}
