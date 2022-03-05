package org.smartregister.chw.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import org.smartregister.chw.anc.provider.AncRegisterProvider;
import org.smartregister.chw.core.provider.ChwAncRegisterProvider;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.Set;

public class AncFollowupRegisterProvider extends ChwAncRegisterProvider {


    public AncFollowupRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener);
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, AncRegisterProvider.RegisterViewHolder viewHolder) {
        super.getView(cursor, client, viewHolder);
        viewHolder.dueWrapper.setVisibility(View.GONE);
    }
}
