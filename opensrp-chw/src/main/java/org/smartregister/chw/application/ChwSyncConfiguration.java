package org.smartregister.chw.application;

import com.google.common.collect.ImmutableList;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.SyncConfiguration;
import org.smartregister.SyncFilter;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.view.activity.BaseLoginActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.smartregister.util.Utils.isEmptyCollection;

/**
 * Created by samuelgithengi on 10/19/18.
 */
public class ChwSyncConfiguration extends SyncConfiguration {
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
        String providerId = allSharedPreferences().fetchRegisteredANM();
        String locationId = allSharedPreferences().fetchDefaultLocalityId(providerId);
        if(StringUtils.isBlank(locationId)) locationId = allSharedPreferences().fetchUserLocalityId(providerId);

        List<String> locationIds = LocationHelper.getInstance().locationsFromHierarchy(true, null);

        if (!isEmptyCollection(locationIds) && locationIds.contains(locationId)) {
            int index = locationIds.indexOf(locationId);
            Set<String> subLocationIds = new HashSet<>(locationIds.subList(index, locationIds.size()));
            return StringUtils.join(subLocationIds, ",");
        }
        return locationId;
    }

    private AllSharedPreferences allSharedPreferences(){
        return org.smartregister.Context.getInstance().allSharedPreferences();
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
        return SyncFilter.TEAM_ID;
    }

    @Override
    public boolean updateClientDetailsTable() {
        return false;
    }

    @Override
    public boolean isSyncUsingPost() {
        return ChwApplication.getApplicationFlavor().syncUsingPost();
    }

    @Override
    public List<String> getSynchronizedLocationTags() {
        return ImmutableList.of("MOH Jhpiego Facility Name", "Health Facility", "Facility");
    }

    @Override
    public SyncFilter getSettingsSyncFilterParam() {
        return SyncFilter.TEAM_ID;
    }

    @Override
    public boolean clearDataOnNewTeamLogin() {
        return false;
    }

    @Override
    public String getTopAllowedLocationLevel() {
        return "District";
    }

    @Override
    public String getOauthClientId() {
        return BuildConfig.OAUTH_CLIENT_ID;
    }

    @Override
    public String getOauthClientSecret() {
        return BuildConfig.OAUTH_CLIENT_SECRET;
    }

    @Override
    public int getConnectTimeout() {
        return BuildConfig.MAX_CONNECTION_TIMEOUT * 300000;
    }

    @Override
    public int getReadTimeout() {
        return BuildConfig.MAX_READ_TIMEOUT *  300000;
    }

    @Override
    public Class<? extends BaseLoginActivity> getAuthenticationActivity() {
        return LoginActivity.class;
    }

    @Override
    public boolean validateUserAssignments() {
        return false;
    }
}
