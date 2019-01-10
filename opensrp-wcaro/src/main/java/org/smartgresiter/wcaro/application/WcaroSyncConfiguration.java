package org.smartgresiter.wcaro.application;

import org.smartgresiter.wcaro.BuildConfig;
import org.smartgresiter.wcaro.util.Constants;
import org.smartregister.SyncConfiguration;
import org.smartregister.repository.AllSharedPreferences;

/**
 * Created by samuelgithengi on 10/19/18.
 */
public class WcaroSyncConfiguration extends SyncConfiguration {
    @Override
    public int getSyncMaxRetries() {
        return BuildConfig.MAX_SYNC_RETRIES;
    }

    @Override
    public String getSyncFilterParam() {
        return Constants.SyncFilters.FILTER_TEAM_ID;
    }

    @Override
    public String getSyncFilterValue() {
        AllSharedPreferences sharedPreferences = WcaroApplication.getInstance().getContext().userService().getAllSharedPreferences();
        return sharedPreferences.fetchDefaultTeamId(sharedPreferences.fetchRegisteredANM());
    }

    @Override
    public int getUniqueIdSource() {
        return BuildConfig.OPENMRS_UNIQUE_ID_SOURCE;
    }

    @Override
    public int getUniqueIdBatchSize() {
        return BuildConfig.OPENMRS_UNIQUE_ID_BATCH_SIZE;
    }

    @Override
    public int getUniqueIdInitialBatchSize() {
        return BuildConfig.OPENMRS_UNIQUE_ID_INITIAL_BATCH_SIZE;
    }

    @Override
    public boolean isSyncSettings() {
        return BuildConfig.IS_SYNC_SETTINGS;
    }
}
