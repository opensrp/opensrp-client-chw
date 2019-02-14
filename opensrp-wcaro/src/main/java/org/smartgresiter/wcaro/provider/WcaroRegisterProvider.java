package org.smartgresiter.wcaro.provider;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.jeasy.rules.api.Rules;
import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.application.WcaroApplication;
import org.smartgresiter.wcaro.interactor.ChildProfileInteractor;
import org.smartgresiter.wcaro.util.ChildDBConstants;
import org.smartgresiter.wcaro.util.ChildUtils;
import org.smartgresiter.wcaro.util.ChildVisit;
import org.smartgresiter.wcaro.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.provider.FamilyRegisterProvider;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WcaroRegisterProvider extends FamilyRegisterProvider {
    private static final String TAG = WcaroRegisterProvider.class.getCanonicalName();

    private final Context context;

    private final View.OnClickListener onClickListener;

    public WcaroRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener);
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        super.getView(cursor, client, viewHolder);

        if (viewHolder.memberIcon == null || !(viewHolder.memberIcon instanceof LinearLayout)) {
            return;
        }

        ((LinearLayout) viewHolder.memberIcon).removeAllViews();

        viewHolder.dueButton.setVisibility(View.GONE);

        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        String familyBaseEntityId = pc.getCaseId();
        Utils.startAsyncTask(new UpdateAsyncTask(context, viewHolder, familyBaseEntityId), null);

    }

    private class UpdateAsyncTask extends AsyncTask<Void, Void, Void> {
        private final Context context;
        private final RegisterViewHolder viewHolder;
        private final String familyBaseEntityId;

        private final Rules rules;

        private List<Map<String, String>> list;
        private ChildVisit childVisit;

        private UpdateAsyncTask(Context context, RegisterViewHolder viewHolder, String familyBaseEntityId) {
            this.context = context;
            this.viewHolder = viewHolder;
            this.familyBaseEntityId = familyBaseEntityId;
            this.rules = WcaroApplication.getInstance().getRulesEngineHelper().rules(Constants.RULE_FILE.HOME_VISIT);
        }

        @Override
        protected Void doInBackground(Void... params) {
            list = getChildren(familyBaseEntityId);

            childVisit = mergeChildVisits(retrieveChildVisitList(rules, list));

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            // Update child Icon
            updateChildIcons(viewHolder, list);

            // Update due column
            updateDueColumn(context, viewHolder, childVisit);
        }
    }

    private void updateChildIcons(RegisterViewHolder viewHolder, List<Map<String, String>> list) {
        if (list != null && !list.isEmpty()) {
            for (Map<String, String> map : list) {
                ImageView imageView = new ImageView(context);
                String gender = map.get(DBConstants.KEY.GENDER);
                if ("Male".equalsIgnoreCase(gender)) {
                    imageView.setImageResource(R.mipmap.ic_boy_child);
                } else {
                    imageView.setImageResource(R.mipmap.ic_girl_child);
                }
                LinearLayout linearLayout = (LinearLayout) viewHolder.memberIcon;
                linearLayout.addView(imageView);
            }
        }
    }

    private void updateDueColumn(Context context, RegisterViewHolder viewHolder, ChildVisit childVisit) {
        if (childVisit != null) {
            viewHolder.dueButton.setVisibility(View.VISIBLE);
            if (childVisit.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.DUE.name())) {
                setVisitButtonDueStatus(context, viewHolder.dueButton);
            } else if (childVisit.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.OVERDUE.name())) {
                setVisitButtonOverdueStatus(context, viewHolder.dueButton, childVisit.getNoOfMonthDue());
            } else if (childVisit.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.LESS_TWENTY_FOUR.name())) {
                setVisitLessTwentyFourView(context, viewHolder.dueButton, childVisit.getLastVisitDays());
            } else if (childVisit.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.VISIT_THIS_MONTH.name())) {
                setVisitAboveTwentyFourView(context, viewHolder.dueButton);
            } else if (childVisit.getVisitStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.NOT_VISIT_THIS_MONTH.name())) {
                setVisitNotDone(context, viewHolder.dueButton);
            }
        } else {
            setVisitButtonDueStatus(context, viewHolder.dueButton);
            viewHolder.dueButton.setVisibility(View.INVISIBLE);
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

        dueButton.setBackgroundResource(R.drawable.red_btn_selector);
        dueButton.setOnClickListener(onClickListener);
    }

    private void setVisitButtonDueStatus(Context context, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(R.color.alert_in_progress_blue));
        dueButton.setText(context.getString(R.string.record_home_visit));
        dueButton.setBackgroundResource(R.drawable.blue_btn_selector);
        dueButton.setOnClickListener(onClickListener);
    }

    private List<Map<String, String>> getChildren(String familyEntityId) {


        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(Constants.TABLE_NAME.CHILD, new String[]{DBConstants.KEY.BASE_ENTITY_ID, DBConstants.KEY.GENDER, ChildDBConstants.KEY.LAST_HOME_VISIT, ChildDBConstants.KEY.VISIT_NOT_DONE, DBConstants.KEY.DOB});
        queryBUilder.mainCondition(String.format(" %s is null AND %s = '%s' ",
                DBConstants.KEY.DATE_REMOVED,
                DBConstants.KEY.RELATIONAL_ID,
                familyEntityId));

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

        return res;
    }

    private List<ChildVisit> retrieveChildVisitList(Rules rules, List<Map<String, String>> list) {
        List<ChildVisit> childVisitList = new ArrayList<>();
        for (Map<String, String> map : list) {
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
            ChildVisit childVisit = ChildUtils.getChildVisitStatus(rules, dobString, lastVisit, visitNot);
            childVisitList.add(childVisit);
        }
        return childVisitList;
    }

    private ChildVisit mergeChildVisits(List<ChildVisit> childVisitList) {
        if (childVisitList == null || childVisitList.isEmpty()) {
            return null;
        }

        ChildVisit overdue = null;
        ChildVisit due = null;
        ChildVisit doneLess24 = null;
        ChildVisit done = null;
        ChildVisit notDone = null;

        for (ChildVisit childVisit : childVisitList) {
            String status = childVisit.getVisitStatus();
            if (status.equalsIgnoreCase(ChildProfileInteractor.VisitType.OVERDUE.name())) {
                if (overdue == null || (overdue.getLastVisitTime() > childVisit.getLastVisitTime())) {
                    overdue = childVisit;
                }
            } else if (status.equalsIgnoreCase(ChildProfileInteractor.VisitType.DUE.name())) {
                if (due == null || (due.getLastVisitTime() > childVisit.getLastVisitTime())) {
                    due = childVisit;
                }
            } else if (status.equalsIgnoreCase(ChildProfileInteractor.VisitType.LESS_TWENTY_FOUR.name())) {
                if (doneLess24 == null || (doneLess24.getLastVisitTime() > childVisit.getLastVisitTime())) {
                    doneLess24 = childVisit;
                }
            } else if (status.equalsIgnoreCase(ChildProfileInteractor.VisitType.VISIT_THIS_MONTH.name())) {
                if (done == null || (done.getLastVisitTime() > childVisit.getLastVisitTime())) {
                    done = childVisit;
                }
            } else if (status.equalsIgnoreCase(ChildProfileInteractor.VisitType.NOT_VISIT_THIS_MONTH.name())) {
                if (notDone == null || (notDone.getLastVisitTime() > childVisit.getLastVisitTime())) {
                    notDone = childVisit;
                }
            }
        }

        if (overdue != null) {
            return overdue;
        } else if (due != null) {
            return due;
        } else if (doneLess24 != null) {
            return doneLess24;
        } else if (done != null) {
            return done;
        } else if (notDone != null) {
            return notDone;
        } else {
            return null;
        }
    }

}
