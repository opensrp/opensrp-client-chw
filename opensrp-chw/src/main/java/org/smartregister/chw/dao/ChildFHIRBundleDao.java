package org.smartregister.chw.dao;

import android.content.Context;

import org.smartregister.chw.R;
import org.smartregister.chw.core.dao.FHIRBundleDao;
import org.smartregister.chw.util.Utils;
import org.smartregister.fp.BuildConfig;
import org.smartregister.thinkmd.model.FHIRBundleModel;

public class ChildFHIRBundleDao extends FHIRBundleDao {

    @Override
    public FHIRBundleModel fetchFHIRDateModel(Context context, String childBaseEntityId) {
        FHIRBundleModel bundle = super.fetchFHIRDateModel(context, childBaseEntityId);
        bundle.setRootPackageName(context.getApplicationContext().getPackageName());
        bundle.setAppVersion(String.valueOf(BuildConfig.VERSION_CODE));
        bundle.setDisplayLanguage(Utils.getDisplayLanguage(context));
        bundle.setAppName(context.getResources().getString(R.string.app_name));
        bundle.setAppLanguage(Utils.getAppLanguage(context));
        return bundle;
    }

}
