package org.smartgresiter.wcaro.application;

import org.apache.commons.lang3.StringUtils;
import org.smartgresiter.wcaro.BuildConfig;
import org.smartgresiter.wcaro.util.Constants;
import org.smartgresiter.wcaro.util.Utils;
import org.smartregister.SyncConfiguration;
import org.smartregister.location.helper.LocationHelper;

import java.util.List;

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
        return Constants.SyncFilters.FILTER_LOCATION_ID;
    }

    @Override
    public String getSyncFilterValue() {
        String providerId = Utils.context().allSharedPreferences().fetchRegisteredANM();
        String userLocationId = Utils.context().allSharedPreferences().fetchUserLocalityId(providerId);

        List<String> locationIds = LocationHelper.getInstance().locationsFromHierarchy(true, null);
        if (!Utils.isEmptyCollection(locationIds)) {
            int index = locationIds.indexOf(userLocationId);
            List<String> subLocationIds = locationIds.subList(index, locationIds.size());
            return StringUtils.join(subLocationIds, ",");
        }
        return "";
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
