package org.smartgresiter.wcaro.service;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartgresiter.wcaro.rule.HomeAlertRule;
import org.smartgresiter.wcaro.util.ChildDBConstants;
import org.smartgresiter.wcaro.util.ChildHomeVisit;
import org.smartgresiter.wcaro.util.ChildUtils;
import org.smartgresiter.wcaro.util.Constants;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

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
    protected void onHandleIntent( Intent intent) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        String query=queryBUilder.SelectInitiateMainTable(Constants.TABLE_NAME.CHILD, new String[]{DBConstants.KEY.BASE_ENTITY_ID,ChildDBConstants.KEY.LAST_HOME_VISIT, ChildDBConstants.KEY.VISIT_NOT_DONE,DBConstants.KEY.DOB});
        Cursor cursor = Utils.context().commonrepository(org.smartgresiter.wcaro.util.Constants.TABLE_NAME.CHILD).queryTable(query);
        if (cursor != null && cursor.moveToFirst()) {
            do{
                String entityId=cursor.getString(1);
                String lastVisitStr = cursor.getString(2);
                String visitNotDoneStr = cursor.getString(3);

                String dobString = Utils.getDuration(cursor.getString(4));
                long lastVisitDate=TextUtils.isEmpty(lastVisitStr)?0:Long.parseLong(lastVisitStr);
                long visitNotDate=TextUtils.isEmpty(visitNotDoneStr)?0:Long.parseLong(visitNotDoneStr);
                HomeAlertRule homeAlertRule = new HomeAlertRule(dobString, lastVisitDate, visitNotDate);
                String visitStatus = WcaroApplication.getInstance().getRulesEngineHelper().getButtonAlertStatus(homeAlertRule, Constants.RULE_FILE.HOME_VISIT);
                ChildUtils.updateFtsSearch(entityId,visitStatus);

            }while (cursor.moveToNext());
            cursor.close();
        }

    }
}
