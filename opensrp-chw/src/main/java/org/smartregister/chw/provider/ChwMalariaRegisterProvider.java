package org.smartregister.chw.provider;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.View;

import org.smartregister.chw.R;
import org.smartregister.chw.core.rule.MalariaFollowUpRule;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.interactor.ChildProfileInteractor;
import org.smartregister.chw.malaria.provider.MalariaRegisterProvider;
import org.smartregister.chw.malaria.util.DBConstants;
import org.smartregister.chw.util.MalariaVisitUtil;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

public class ChwMalariaRegisterProvider extends MalariaRegisterProvider {

    private Context context;

    public ChwMalariaRegisterProvider(Context context, View.OnClickListener paginationClickListener, View.OnClickListener onClickListener, Set visibleColumns, CommonRepository commonRepository) {
        super(context, paginationClickListener, onClickListener, visibleColumns, commonRepository);
        this.context = context;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        super.getView(cursor, client, viewHolder);

        viewHolder.dueButton.setVisibility(View.GONE);
        viewHolder.dueButton.setOnClickListener(null);
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        Utils.startAsyncTask(new UpdateAsyncTask(viewHolder, pc), null);
    }

    private void updateDueColumn(RegisterViewHolder viewHolder, MalariaFollowUpRule followUpRule) {
        viewHolder.dueButton.setVisibility(View.VISIBLE);
        viewHolder.dueButton.setOnClickListener(onClickListener);

        if (followUpRule.getButtonStatus().equalsIgnoreCase(CoreConstants.VISIT_STATE.OVERDUE)) {
            viewHolder.dueButton.setBackgroundResource(R.drawable.overdue_red_btn_selector);
            viewHolder.dueButton.setTextColor(context.getResources().getColor(R.color.white));

        } else
            viewHolder.dueButton.setBackgroundResource(R.drawable.blue_btn_selector);

    }

    private class UpdateAsyncTask extends AsyncTask<Void, Void, Void> {
        private final RegisterViewHolder viewHolder;
        private final CommonPersonObjectClient pc;

        private MalariaFollowUpRule malariaFollowUpRule;

        private UpdateAsyncTask(RegisterViewHolder viewHolder, CommonPersonObjectClient pc) {
            this.viewHolder = viewHolder;
            this.pc = pc;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Date date = new SimpleDateFormat(CoreConstants.DATE_FORMATS.NATIVE_FORMS, Locale.getDefault()).parse(Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.MALARIA_TEST_DATE, false));
                malariaFollowUpRule = MalariaVisitUtil.getMalariaStatus(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            if (malariaFollowUpRule != null && (malariaFollowUpRule.getButtonStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.DUE.name()) || malariaFollowUpRule.getButtonStatus().equalsIgnoreCase(ChildProfileInteractor.VisitType.OVERDUE.name()))) {
                updateDueColumn(viewHolder, malariaFollowUpRule);
            }
        }
    }
}
