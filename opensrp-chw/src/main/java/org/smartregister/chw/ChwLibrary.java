package org.smartregister.chw;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.P2POptions;
import org.smartregister.SyncConfiguration;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.sync.ClientProcessorForJava;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-06-04
 */

public class ChwLibrary extends CoreLibrary {


    protected ChwLibrary(Context contextArg, SyncConfiguration syncConfiguration, @Nullable P2POptions p2POptions) {
        super(contextArg, syncConfiguration, p2POptions);
    }

    @NonNull
    @Override
    public ClientProcessorForJava getClientProcessor() {
        return ChwApplication.getClientProcessor(ChwApplication.getInstance().getApplicationContext());
    }
}
