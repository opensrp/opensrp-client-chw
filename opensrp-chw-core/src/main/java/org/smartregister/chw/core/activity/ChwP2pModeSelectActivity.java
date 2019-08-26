package org.smartregister.chw.core.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.smartregister.chw.core.R;
import org.smartregister.p2p.activity.P2pModeSelectActivity;
import org.smartregister.util.LangUtils;

public class ChwP2pModeSelectActivity extends P2pModeSelectActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.peer_to_peer_activity_title);
    }

    @Override
    protected void attachBaseContext(Context base) {
        // get language from prefs
        String lang = LangUtils.getLanguage(base.getApplicationContext());
        super.attachBaseContext(LangUtils.setAppLocale(base, lang));
    }
}
