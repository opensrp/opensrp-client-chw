package org.smartregister.chw.hf.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import org.smartregister.chw.core.provider.ChwPncRegisterProvider;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.Set;

public class HfPncRegisterProvider extends ChwPncRegisterProvider {
    public HfPncRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener);
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        super.getView(cursor, client, viewHolder);
        viewHolder.dueWrapper.setVisibility(View.GONE);
    }
}
