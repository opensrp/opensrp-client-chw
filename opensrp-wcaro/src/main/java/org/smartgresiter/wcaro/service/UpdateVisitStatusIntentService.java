package org.smartgresiter.wcaro.service;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartgresiter.wcaro.interactor.ChildProfileInteractor;
import org.smartgresiter.wcaro.rule.HomeAlertRule;
import org.smartgresiter.wcaro.util.ChildDBConstants;
import org.smartgresiter.wcaro.util.ChildUtils;
import org.smartgresiter.wcaro.util.Constants;
import org.smartgresiter.wcaro.util.Utils;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.DBConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * This service started at first time when all data sync complete.It'll update fts with visit data.
 * based on child lastVisitDate at child table.
 */

public class UpdateVisitStatusIntentService extends IntentService {

    public UpdateVisitStatusIntentService() {
        super("UpdateVisitStatusIntentService");
    }

    public UpdateVisitStatusIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        updateFamilyFts();
    }

    private void updateFamilyFts() {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        String query = queryBUilder.SelectInitiateMainTable(Constants.TABLE_NAME.FAMILY, new String[]{DBConstants.KEY.BASE_ENTITY_ID});
        Cursor cursor = Utils.context().commonrepository(Constants.TABLE_NAME.FAMILY).queryTable(query);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String entityId = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.BASE_ENTITY_ID));
                List<HomeAlertRule> homeAlertRuleList = updateChildStatus(entityId);
                if (!homeAlertRuleList.isEmpty()) {
                    String childStatus = mergeChildStatus(homeAlertRuleList);
                    if (StringUtils.isNotBlank(childStatus)) {
                        Utils.updateFtsSearch(Constants.TABLE_NAME.FAMILY, entityId, ChildDBConstants.KEY.CHILD_VISIT_STATUS, childStatus);
                    }
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
    }


    private List<HomeAlertRule> updateChildStatus(String familyEntityId) {

        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(Constants.TABLE_NAME.CHILD, new String[]{DBConstants.KEY.BASE_ENTITY_ID, ChildDBConstants.KEY.LAST_HOME_VISIT, ChildDBConstants.KEY.VISIT_NOT_DONE, DBConstants.KEY.DOB});
        String query = queryBUilder.mainCondition(String.format(" %s is null AND %s = '%s' ",
                DBConstants.KEY.DATE_REMOVED,
                DBConstants.KEY.RELATIONAL_ID,
                familyEntityId));

        List<HomeAlertRule> res = new ArrayList<>();

        Cursor cursor = Utils.context().commonrepository(Constants.TABLE_NAME.CHILD).queryTable(query);
        if (cursor != null && cursor.moveToFirst()) {
            try {
                do {
                    String entityId = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.BASE_ENTITY_ID));
                    String lastVisitStr = cursor.getString(cursor.getColumnIndex(ChildDBConstants.KEY.LAST_HOME_VISIT));
                    String visitNotDoneStr = cursor.getString(cursor.getColumnIndex(ChildDBConstants.KEY.VISIT_NOT_DONE));
                    String dobString = Utils.getDuration(cursor.getString(cursor.getColumnIndex(DBConstants.KEY.DOB)));

                    long lastVisitDate = TextUtils.isEmpty(lastVisitStr) ? 0 : Long.parseLong(lastVisitStr);
                    long visitNotDate = TextUtils.isEmpty(visitNotDoneStr) ? 0 : Long.parseLong(visitNotDoneStr);

                    HomeAlertRule homeAlertRule = new HomeAlertRule(dobString, lastVisitDate, visitNotDate);
                    res.add(homeAlertRule);

                    String visitStatus = WcaroApplication.getInstance().getRulesEngineHelper().getButtonAlertStatus(homeAlertRule, Constants.RULE_FILE.HOME_VISIT);

                    ChildUtils.updateFtsSearch(entityId, visitStatus);

                } while (cursor.moveToNext());
            } catch (Exception e) {
                Log.e(getClass().getCanonicalName(), e.getMessage(), e);
            } finally {
                cursor.close();
            }
        }

        return res;
    }

    private String mergeChildStatus(List<HomeAlertRule> homeAlertRuleList) {
        int overdue = 0;
        int due = 0;
        int doneLess24 = 0;
        int done = 0;
        int notDone = 0;

        for (HomeAlertRule homeAlertRule : homeAlertRuleList) {
            String status = homeAlertRule.buttonStatus;
            if (status.equalsIgnoreCase(ChildProfileInteractor.VisitType.OVERDUE.name())) {
                overdue++;
            } else if (status.equalsIgnoreCase(ChildProfileInteractor.VisitType.DUE.name())) {
                due++;
            } else if (status.equalsIgnoreCase(ChildProfileInteractor.VisitType.LESS_TWENTY_FOUR.name())) {
                doneLess24++;
            } else if (status.equalsIgnoreCase(ChildProfileInteractor.VisitType.VISIT_THIS_MONTH.name())) {
                done++;
            } else if (status.equalsIgnoreCase(ChildProfileInteractor.VisitType.NOT_VISIT_THIS_MONTH.name())) {
                notDone++;
            }
        }

        if (overdue > 0) {
            return ChildProfileInteractor.VisitType.OVERDUE.name();
        } else if (due > 0) {
            return ChildProfileInteractor.VisitType.DUE.name();
        } else if (doneLess24 > 0) {
            return ChildProfileInteractor.VisitType.LESS_TWENTY_FOUR.name();
        } else if (done > 0) {
            return ChildProfileInteractor.VisitType.VISIT_THIS_MONTH.name();
        } else if (notDone > 0) {
            return ChildProfileInteractor.VisitType.NOT_VISIT_THIS_MONTH.name();
        } else {
            return null;
        }
    }


}
