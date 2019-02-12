package org.smartgresiter.wcaro.provider;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import org.jeasy.rules.api.Rules;
import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartgresiter.wcaro.interactor.ChildProfileInteractor;
import org.smartgresiter.wcaro.util.ChildDBConstants;
import org.smartgresiter.wcaro.util.ChildUtils;
import org.smartgresiter.wcaro.util.ChildVisit;
import org.smartgresiter.wcaro.util.Constants;
import org.smartgresiter.wcaro.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.provider.FamilyMemberRegisterProvider;
import org.smartregister.family.util.DBConstants;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WcaroMemberRegisterProvider extends FamilyMemberRegisterProvider {
    private static final String TAG = WcaroMemberRegisterProvider.class.getCanonicalName();

    public WcaroMemberRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener, String familyHead, String primaryCaregiver) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener, familyHead, primaryCaregiver);
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        super.getView(cursor, client, viewHolder);

        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;

        viewHolder.statusLayout.setVisibility(View.GONE);
        viewHolder.status.setVisibility(View.GONE);
        Utils.startAsyncTask(new UpdateAsyncTask(viewHolder, pc.getCaseId()), null);
    }

    private Map<String, String> getChildDetails(String baseEntityId) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(Constants.TABLE_NAME.CHILD, new String[]{DBConstants.KEY.BASE_ENTITY_ID, ChildDBConstants.KEY.LAST_HOME_VISIT, ChildDBConstants.KEY.VISIT_NOT_DONE, DBConstants.KEY.DOB});
        queryBUilder.mainCondition(String.format(" %s is null AND %s = '%s' ",
                DBConstants.KEY.DATE_REMOVED,
                DBConstants.KEY.BASE_ENTITY_ID,
                baseEntityId));

        String query = queryBUilder.orderbyCondition(DBConstants.KEY.DOB + " ASC ");

        CommonRepository commonRepository = Utils.context().commonrepository(Constants.TABLE_NAME.CHILD);
        List<Map<String, String>> res = new ArrayList<>();

        Cursor cursor = commonRepository.queryTable(query);
        try {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                int columncount = cursor.getColumnCount();
                Map<String, String> columns = new HashMap<>();
                for (int i = 0; i < columncount; i++) {
                    columns.put(cursor.getColumnName(i), cursor.getType(i) == Cursor.FIELD_TYPE_NULL ? null : String.valueOf(cursor.getString(i)));
                }
                res.add(columns);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        } finally {
            cursor.close();
        }

        if (res.isEmpty()) {
            return null;
        }
        return res.get(0);
    }

    private ChildVisit retrieveChildVisitList(Rules rules, Map<String, String> map) {
        String dobString = Utils.getDuration(map.get(DBConstants.KEY.DOB));
        String lastVisitDate = map.get(ChildDBConstants.KEY.LAST_HOME_VISIT);
        String visitNotDone = map.get(ChildDBConstants.KEY.VISIT_NOT_DONE);
        long lastVisit = 0, visitNot = 0;
        if (!TextUtils.isEmpty(lastVisitDate)) {
            lastVisit = Long.valueOf(lastVisitDate);
        }
        if (!TextUtils.isEmpty(visitNotDone)) {
            visitNot = Long.valueOf(visitNotDone);
        }
        return ChildUtils.getChildVisitStatus(rules, dobString, lastVisit, visitNot);
    }

    private void updateDueColumn(RegisterViewHolder viewHolder, ChildVisit childVisit) {
        viewHolder.statusLayout.setVisibility(View.VISIBLE);
        if (childVisit != null) {
            viewHolder.status.setVisibility(View.VISIBLE);
            if (childVisit.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.DUE.name())) {
                viewHolder.status.setImageResource(Utils.getDueProfileImageResourceIDentifier());
            } else if (childVisit.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.OVERDUE.name())) {
                viewHolder.status.setImageResource(Utils.getOverDueProfileImageResourceIDentifier());
            }
        } else {
            viewHolder.status.setVisibility(View.INVISIBLE);
        }
    }

    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    private class UpdateAsyncTask extends AsyncTask<Void, Void, Void> {
        private final RegisterViewHolder viewHolder;
        private final String baseEntityId;

        private final Rules rules;

        private Map<String, String> map;
        private ChildVisit childVisit;

        private UpdateAsyncTask(RegisterViewHolder viewHolder, String baseEntityId) {
            this.viewHolder = viewHolder;
            this.baseEntityId = baseEntityId;
            this.rules = WcaroApplication.getInstance().getRulesEngineHelper().rules(Constants.RULE_FILE.HOME_VISIT);
        }

        @Override
        protected Void doInBackground(Void... params) {
            map = getChildDetails(baseEntityId);
            if (map != null) {
                childVisit = retrieveChildVisitList(rules, map);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            // Update status column
            if (childVisit != null) {
                updateDueColumn(viewHolder, childVisit);
            }
        }
    }
}
