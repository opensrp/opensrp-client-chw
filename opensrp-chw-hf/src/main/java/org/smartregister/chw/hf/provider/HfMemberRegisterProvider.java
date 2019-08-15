package org.smartregister.chw.hf.provider;

import android.content.Context;
import android.view.View;

import org.smartregister.chw.core.provider.CoreMemberRegisterProvider;
import org.smartregister.commonregistry.CommonRepository;

import java.util.Set;

public class HfMemberRegisterProvider extends CoreMemberRegisterProvider {
    public HfMemberRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener, String familyHead, String primaryCaregiver) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener, familyHead, primaryCaregiver);
    }
}
