package org.smartregister.chw.dao;

import android.content.Context;

import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.R;
import org.smartregister.chw.core.dao.FHIRBundleDao;
import org.smartregister.chw.util.Utils;
import org.smartregister.thinkmd.model.FHIRBundleModel;

public class ChildFHIRBundleDao extends FHIRBundleDao {

    @Override
    public FHIRBundleModel fetchFHIRDateModel(Context context, String childBaseEntityId) {
        FHIRBundleModel bundle = super.fetchFHIRDateModel(context, childBaseEntityId);
        bundle.setRootPackageName(context.getApplicationContext().getPackageName());
        bundle.setAppVersion(BuildConfig.VERSION_NAME);
        bundle.setDisplayLanguage(Utils.getDisplayLanguage(context));
        bundle.setAppName(context.getResources().getString(R.string.app_name));
        bundle.setAppLanguage(Utils.getAppLanguage(context));
        //Todo: these values needs to be query and set into model
        bundle.setEndPointPackageName(null);
        return bundle;
    }


}
