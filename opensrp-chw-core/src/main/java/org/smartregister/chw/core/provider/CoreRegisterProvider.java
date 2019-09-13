package org.smartregister.chw.core.provider;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Rules;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.model.ChildVisit;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
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

import timber.log.Timber;

import static org.smartregister.chw.core.utils.CoreJsonFormUtils.getDayFromDate;

public abstract class CoreRegisterProvider extends FamilyRegisterProvider {

    protected final Context context;
    private final View.OnClickListener onClickListener;
    protected AsyncTask<Void, Void, Void> updateAsyncTask;

    public CoreRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener);
        this.context = context;
        this.onClickListener = onClickListener;
    }


    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        String familyHeadId = pc.getColumnmaps().get("family_head");
        if (StringUtils.isNotBlank(familyHeadId)) {
            super.getView(cursor, client, viewHolder);
        }

        if (!(viewHolder.memberIcon instanceof LinearLayout)) {
            return;
        }

        ((LinearLayout) viewHolder.memberIcon).removeAllViews();

        if (updateAsyncTask != null) {
            Utils.startAsyncTask(updateAsyncTask, null);
        }
    }

    private void addImageView(RegisterViewHolder viewHolder, int res_id) {
        ImageView imageView = new ImageView(context);
        int size = convertDpToPixel(22, context);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setImageResource(res_id);
        imageView.getLayoutParams().height = size;
        imageView.getLayoutParams().width = size;
        LinearLayout linearLayout = (LinearLayout) viewHolder.memberIcon;
        linearLayout.addView(imageView);
    }

    private void updatePncAncIcons(RegisterViewHolder viewHolder, int womanCount, String register) {
        for (int i = 1; i <= womanCount; i++) {
            int res = CoreConstants.TABLE_NAME.ANC_MEMBER.equals(register) ? R.mipmap.ic_anc_pink : org.smartregister.family.R.mipmap.row_pnc;
            addImageView(viewHolder, res);
        }
    }

    public static int convertDpToPixel(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    protected void updateMalariaIcons(RegisterViewHolder viewHolder, int malariaCount) {
        for (int i = 1; i <= malariaCount; i++) {
            addImageView(viewHolder, R.drawable.ic_row_malaria);
        }
    }

    protected void updateChildIcons(RegisterViewHolder viewHolder, List<Map<String, String>> list, int ancWomanCount, int pncWomanCount) {

        updatePncAncIcons(viewHolder, ancWomanCount, CoreConstants.TABLE_NAME.ANC_MEMBER);
        updatePncAncIcons(viewHolder, pncWomanCount, CoreConstants.TABLE_NAME.PNC_MEMBER);

        if (list != null && !list.isEmpty()) {
            for (Map<String, String> map : list) {
                if ("PNC".equals(map.get(CoreConstants.DB_CONSTANTS.ENTRY_POINT))) {
                    String dob = map.get(DBConstants.KEY.DOB);
                    if (dob != null && getDayFromDate(dob) < 29) {
                        return;
                    }
                }

                String gender = map.get(DBConstants.KEY.GENDER);
                int res = ("Male".equalsIgnoreCase(gender)) ? R.mipmap.ic_boy_child : R.mipmap.ic_girl_child;
                addImageView(viewHolder, res);
            }
        }

    }

    public abstract void updateDueColumn(Context context, RegisterViewHolder viewHolder, ChildVisit childVisit);

    protected void setVisitNotDone(Context context, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(R.color.progress_orange));
        dueButton.setText(context.getString(R.string.visit_not_done));
        dueButton.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        dueButton.setOnClickListener(null);
    }

    protected void setVisitLessTwentyFourView(Context context, Button dueButton, String lastVisitMonth) {
        setVisitAboveTwentyFourView(context, dueButton);
    }

    protected void setVisitAboveTwentyFourView(Context context, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(R.color.alert_complete_green));
        dueButton.setText(context.getString(R.string.visit_done));
        dueButton.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        dueButton.setOnClickListener(null);
    }

    protected void setVisitButtonOverdueStatus(Context context, Button dueButton, String lastVisitDays) {
        dueButton.setTextColor(context.getResources().getColor(R.color.white));
        if (TextUtils.isEmpty(lastVisitDays)) {
            dueButton.setText(context.getString(R.string.record_visit));
        } else {
            dueButton.setText(context.getString(R.string.due_visit, lastVisitDays));
        }

        dueButton.setBackgroundResource(R.drawable.overdue_red_btn_selector);
        dueButton.setOnClickListener(onClickListener);
    }

    protected void setVisitButtonDueStatus(Context context, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(R.color.alert_in_progress_blue));
        dueButton.setText(context.getString(R.string.record_home_visit));
        dueButton.setBackgroundResource(R.drawable.blue_btn_selector);
        dueButton.setOnClickListener(onClickListener);
    }

    protected List<Map<String, String>> getChildren(String familyEntityId) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(CoreConstants.TABLE_NAME.CHILD, new String[]{DBConstants.KEY.BASE_ENTITY_ID, DBConstants.KEY.GENDER, ChildDBConstants.KEY.LAST_HOME_VISIT, ChildDBConstants.KEY.VISIT_NOT_DONE, ChildDBConstants.KEY.DATE_CREATED, DBConstants.KEY.DOB, CoreConstants.DB_CONSTANTS.ENTRY_POINT});
        queryBUilder.mainCondition(String.format(" %s is null AND %s = '%s' AND %s ",
                DBConstants.KEY.DATE_REMOVED,
                DBConstants.KEY.RELATIONAL_ID,
                familyEntityId,
                ChildDBConstants.childAgeLimitFilter()));

        String query = queryBUilder.orderbyCondition(DBConstants.KEY.DOB + " ASC ");

        CommonRepository commonRepository = Utils.context().commonrepository(CoreConstants.TABLE_NAME.CHILD);
        List<Map<String, String>> res = new ArrayList<>();

        Cursor cursor = null;
        try {
            cursor = commonRepository.queryTable(query);
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
            Timber.e(e, e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return res;
    }

    public abstract List<ChildVisit> retrieveChildVisitList(Rules rules, List<Map<String, String>> list);

    public abstract ChildVisit mergeChildVisits(List<ChildVisit> childVisitList);

    public void setUpdateAsyncTask(AsyncTask<Void, Void, Void> updateAsyncTask) {
        this.updateAsyncTask = updateAsyncTask;
    }
}
