package org.smartregister.chw.application;

import com.google.common.collect.ImmutableList;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.SyncConfiguration;
import org.smartregister.SyncFilter;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.view.activity.BaseLoginActivity;

import java.util.List;

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
        String locationFilter = Utils.getSyncFilterValue();
        if(StringUtils.isBlank(locationFilter)){
            locationFilter = getUserLocation();
        }
        return locationFilter;
    }

    private String getUserLocation(){
        String providerId = org.smartregister.Context.getInstance().allSharedPreferences().fetchRegisteredANM();
        return org.smartregister.Context.getInstance().allSharedPreferences().fetchUserLocalityId(providerId);
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
        return true;
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
        return true;
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
    public Class<? extends BaseLoginActivity> getAuthenticationActivity() {
        return LoginActivity.class;
    }
}
