package org.smartgresiter.wcaro.provider;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.smartgresiter.wcaro.R;
import org.smartgresiter.wcaro.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
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
    private Context context;

    public WcaroRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener);
        this.context = context;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        super.getView(cursor, client, viewHolder);

        if (viewHolder.memberIcon == null || !(viewHolder.memberIcon instanceof LinearLayout)) {
            return;
        }

        ((LinearLayout) viewHolder.memberIcon).removeAllViews();

        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        String familyBaseEntityId = pc.getCaseId();
        Utils.startAsyncTask(new ChildIconsAsyncTask(context, viewHolder, familyBaseEntityId), null);

    }

    private class ChildIconsAsyncTask extends AsyncTask<Void, Void, Void> {
        private final Context context;
        private final RegisterViewHolder viewHolder;
        private final String familyBaseEntityId;
        private List<Map<String, String>> list;

        private ChildIconsAsyncTask(Context context, RegisterViewHolder viewHolder, String familyBaseEntityId) {
            this.context = context;
            this.viewHolder = viewHolder;
            this.familyBaseEntityId = familyBaseEntityId;
        }

        @Override
        protected Void doInBackground(Void... params) {
            list = getChildren(familyBaseEntityId);
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
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
    }


    private List<Map<String, String>> getChildren(String familyEntityId) {

        String info_columns = DBConstants.KEY.BASE_ENTITY_ID + " , " +
                DBConstants.KEY.GENDER;

        String sql = String.format("select %s from %s where %s = '%s' and %s is null ",
                info_columns,
                Constants.TABLE_NAME.CHILD,
                DBConstants.KEY.RELATIONAL_ID,
                familyEntityId,
                DBConstants.KEY.DATE_REMOVED
        );

        CommonRepository commonRepository = Utils.context().commonrepository(Constants.TABLE_NAME.CHILD);
        List<Map<String, String>> res = new ArrayList<>();

        Cursor cursor = commonRepository.queryTable(sql);
        try {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                int columncount = cursor.getColumnCount();
                Map<String, String> columns = new HashMap<String, String>();
                for (int i = 0; i < columncount; i++) {
                    columns.put(cursor.getColumnName(i), String.valueOf(cursor.getString(i)));
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

}
