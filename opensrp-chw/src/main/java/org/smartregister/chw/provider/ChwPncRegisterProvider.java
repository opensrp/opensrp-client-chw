package org.smartregister.chw.provider;

import android.content.Context;
import android.view.View;

import org.smartregister.chw.core.rule.PncVisitAlertRule;
import org.smartregister.commonregistry.CommonRepository;

import java.util.Set;

public class ChwPncRegisterProvider extends org.smartregister.chw.core.provider.ChwPncRegisterProvider {
    public ChwPncRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener);
    }

    @Override
    protected void updateDueColumn(Context context, RegisterViewHolder viewHolder, PncVisitAlertRule pncVisitAlertRule) {
        viewHolder.dueWrapper.setVisibility(View.GONE);
    }
}
