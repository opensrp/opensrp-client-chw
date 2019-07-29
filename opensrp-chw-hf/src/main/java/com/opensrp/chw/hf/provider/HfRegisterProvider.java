package com.opensrp.chw.hf.provider;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;

import com.opensrp.chw.core.model.ChildVisit;
import com.opensrp.chw.core.provider.CoreRegisterProvider;
import com.opensrp.chw.core.utils.ChildDBConstants;
import com.opensrp.chw.core.utils.Constants;

import org.jeasy.rules.api.Rules;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HfRegisterProvider extends CoreRegisterProvider {

    public HfRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener);
    }

    public void updateDueColumn(Context context, RegisterViewHolder viewHolder, ChildVisit childVisit) {
        viewHolder.dueButton.setVisibility(View.GONE);
    }

    public List<ChildVisit> retrieveChildVisitList(Rules rules, List<Map<String, String>> list) {
        return  null;
    }

    public ChildVisit mergeChildVisits(List<ChildVisit> childVisitList) {
        return null;
    }
}
