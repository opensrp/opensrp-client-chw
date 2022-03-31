package org.smartregister.chw.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import org.smartregister.provider.PmtctRegisterProvider;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.Set;

public class MotherChampionRegisterProvider extends PmtctRegisterProvider {
    public MotherChampionRegisterProvider(Context context, View.OnClickListener paginationClickListener, View.OnClickListener onClickListener, Set visibleColumns) {
        super(context, paginationClickListener, onClickListener, visibleColumns);
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, RegisterViewHolder registerViewHolder) {
        super.getView(cursor, smartRegisterClient, registerViewHolder);
        registerViewHolder.dueWrapper.setVisibility(View.GONE);
    }
}
