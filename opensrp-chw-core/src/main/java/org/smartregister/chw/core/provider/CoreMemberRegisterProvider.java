package org.smartregister.chw.core.provider;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;

import org.smartregister.chw.core.R;
import org.smartregister.chw.core.model.ChildVisit;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.commonregistry.CommonFtsObject;
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

import timber.log.Timber;

public class CoreMemberRegisterProvider extends FamilyMemberRegisterProvider {
    private Context context;

    public CoreMemberRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener, String familyHead, String primaryCaregiver) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener, familyHead, primaryCaregiver);
        this.context = context;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, FamilyMemberRegisterProvider.RegisterViewHolder viewHolder) {
        super.getView(cursor, client, viewHolder);

        // Update UI cutoffs
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.profile.getLayoutParams();
        layoutParams.width = context.getResources().getDimensionPixelSize(R.dimen.member_profile_pic_width);
        layoutParams.height = context.getResources().getDimensionPixelSize(R.dimen.member_profile_pic_width);
        viewHolder.profile.setLayoutParams(layoutParams);
        viewHolder.patientNameAge.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.member_profile_list_title_size));

        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;

        viewHolder.statusLayout.setVisibility(View.GONE);
        viewHolder.status.setVisibility(View.GONE);

        String entityType = Utils.getValue(pc.getColumnmaps(), ChildDBConstants.KEY.ENTITY_TYPE, false);
        if (CoreConstants.TABLE_NAME.CHILD.equals(entityType)) {
            Utils.startAsyncTask(new UpdateAsyncTask(viewHolder, pc), null);
        }

    }

    private Map<String, String> getChildDetails(String baseEntityId) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(CommonFtsObject.searchTableName(CoreConstants.TABLE_NAME.CHILD), new String[]{CommonFtsObject.idColumn, ChildDBConstants.KEY.LAST_HOME_VISIT, ChildDBConstants.KEY.VISIT_NOT_DONE, ChildDBConstants.KEY.DATE_CREATED});
        String query = queryBUilder.mainCondition(String.format(" %s is null AND %s = '%s' AND %s ",
                DBConstants.KEY.DATE_REMOVED,
                CommonFtsObject.idColumn,
                baseEntityId,
                ChildDBConstants.childAgeLimitFilter()));

        query = query.replace(CommonFtsObject.searchTableName(CoreConstants.TABLE_NAME.CHILD) + ".id as _id ,", "");

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

        if (res.isEmpty()) {
            return null;
        }
        return res.get(0);
    }

    private ChildVisit retrieveChildVisitList(Map<String, String> map) {
        String lastVisitDate = map.get(ChildDBConstants.KEY.LAST_HOME_VISIT);
        String visitNotDone = map.get(ChildDBConstants.KEY.VISIT_NOT_DONE);
        String strDateCreated = map.get(ChildDBConstants.KEY.DATE_CREATED);
        TextUtils.isEmpty(lastVisitDate);
        TextUtils.isEmpty(visitNotDone);
        TextUtils.isEmpty(strDateCreated);
        return null;// CoreChildUtils.getChildVisitStatus(context, rules, dobString, lastVisit, visitNot, dateCreated);
    }

    private void updateDueColumn(FamilyMemberRegisterProvider.RegisterViewHolder viewHolder, ChildVisit childVisit) {
        viewHolder.statusLayout.setVisibility(View.VISIBLE);
        if (childVisit != null) {
            viewHolder.status.setVisibility(View.VISIBLE);
            if (childVisit.getVisitStatus().equalsIgnoreCase(CoreConstants.VisitType.DUE.name())) {
                viewHolder.status.setImageResource(Utils.getDueProfileImageResourceIDentifier());
            } else if (childVisit.getVisitStatus().equalsIgnoreCase(CoreConstants.VisitType.OVERDUE.name())) {
                viewHolder.status.setImageResource(Utils.getOverDueProfileImageResourceIDentifier());
            } else {
                viewHolder.status.setVisibility(View.INVISIBLE);
            }
        } else {
            viewHolder.status.setVisibility(View.INVISIBLE);
        }
    }

    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    private class UpdateAsyncTask extends AsyncTask<Void, Void, Void> {
        private final FamilyMemberRegisterProvider.RegisterViewHolder viewHolder;
        private final CommonPersonObjectClient pc;

        private Map<String, String> map;
        private ChildVisit childVisit;

        private UpdateAsyncTask(FamilyMemberRegisterProvider.RegisterViewHolder viewHolder, CommonPersonObjectClient pc) {
            this.viewHolder = viewHolder;
            this.pc = pc;
        }

        @Override
        protected Void doInBackground(Void... params) {
            map = getChildDetails(pc.getCaseId());
            if (map != null) {
                childVisit = retrieveChildVisitList(map);
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