package org.smartregister.chw.hf.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import org.jeasy.rules.api.Rules;
import org.smartregister.chw.core.model.ChildVisit;
import org.smartregister.chw.core.provider.CoreRegisterProvider;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class HfRegisterProvider extends CoreRegisterProvider {

    public HfRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener);
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        super.getView(cursor, client, viewHolder);
        hideFamilyServiceButton(viewHolder);
    }

    private void hideFamilyServiceButton(RegisterViewHolder viewHolder) {
        viewHolder.dueButton.setVisibility(View.GONE);
        viewHolder.dueWrapper.setVisibility(View.GONE);
    }

    @Override
    public void updateDueColumn(Context context, RegisterViewHolder viewHolder, ChildVisit childVisit) {
        hideFamilyServiceButton(viewHolder);
    }

    @Override
    public List<ChildVisit> retrieveChildVisitList(Rules rules, List<Map<String, String>> list) {
        return null;
    }

    @Override
    public ChildVisit mergeChildVisits(List<ChildVisit> childVisitList) {
        return null;
    }

}
