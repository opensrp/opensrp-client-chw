package com.opensrp.chw.core.provider;

import android.content.Context;
import android.view.View;

import java.util.Set;

public class BasereferralRegisterProvider extends CoreChildRegisterProvider {
    public BasereferralRegisterProvider(Context context, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, visibleColumns, onClickListener, paginationClickListener);
    }
}
