package com.opensrp.chw.hf.sync;

import com.opensrp.chw.core.utils.Utils;
import com.opensrp.hf.BuildConfig;

import org.smartregister.SyncConfiguration;
import org.smartregister.SyncFilter;

/**
 * @author  Elly Nerdstone
 */
public class HfSyncConfiguration extends SyncConfiguration {
    @Override
    public int getSyncMaxRetries() {
        return BuildConfig.MAX_SYNC_RETRIES;
    }

    @Override
    public SyncFilter getSyncFilterParam() {
        return SyncFilter.LOCATION;
    }

    @Override
    public String getSyncFilterValue() {
        return Utils.getSyncFilterValue();
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

    @Override
    public SyncFilter getEncryptionParam() {
        return SyncFilter.LOCATION;
    }

    @Override
    public boolean updateClientDetailsTable() {
        return false;
    }
}
