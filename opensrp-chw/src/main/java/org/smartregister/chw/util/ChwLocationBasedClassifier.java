package org.smartregister.chw.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.jsonmapping.Location;
import org.smartregister.domain.jsonmapping.util.LocationTree;
import org.smartregister.domain.jsonmapping.util.TreeNode;
import org.smartregister.p2p.model.DataType;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.P2PClassifier;
import org.smartregister.util.AssetHandler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import timber.log.Timber;

public class ChwLocationBasedClassifier implements P2PClassifier<JSONObject> {

    private String locationId;
    private Map<String, Position> locationPosition;

    @Override
    public boolean isForeign(JSONObject jsonObject, DataType dataType) {
        if (dataType.getName().equals(AllConstants.P2PDataTypes.CLIENT) || dataType.getName().equals(AllConstants.P2PDataTypes.FOREIGN_CLIENT))
            return isForeignClient(jsonObject);

        if (dataType.getName().equals(AllConstants.P2PDataTypes.EVENT) || dataType.getName().equals(AllConstants.P2PDataTypes.FOREIGN_EVENT))
            return isForeignEvent(jsonObject);

        return false;
    }

    private boolean isForeignEvent(JSONObject jsonObject) {
        try {
            String locationID = jsonObject.getString("locationId");
            return !isChildLocation(locationID);
        } catch (JSONException e) {
            Timber.e(e);
        }
        return false;
    }

    private boolean isForeignClient(JSONObject jsonObject) {
        try {
            String locationID = jsonObject.getString("locationId");
            jsonObject.remove("locationId");
            return !isChildLocation(locationID);
        } catch (JSONException e) {
            Timber.e(e);
        }
        return false;
    }

    // TODO check if param location is under the current logged in location
    private boolean isChildLocation(String locationID) {
        if (locationId == null) {
            AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
            locationId = allSharedPreferences.fetchDefaultLocalityId(allSharedPreferences.fetchRegisteredANM());
            if(StringUtils.isBlank(locationId)) locationId = allSharedPreferences.fetchUserLocalityId(allSharedPreferences.fetchRegisteredANM());
        }
        return isLocationEncompassing(locationId, locationID);
    }

    @Nullable
    private LinkedHashMap<String, TreeNode<String, Location>> retrieveLocationHierarchyMap() {
        String locationData = CoreLibrary.getInstance().context().anmLocationController().get();
        LocationTree locationTree = AssetHandler.jsonStringToJava(locationData, LocationTree.class);
        if (locationTree != null) {
            return locationTree.getLocationsHierarchy();
        }

        return null;
    }

    private void readData(@Nullable LinkedHashMap<String, TreeNode<String, Location>> dataInfo, @Nullable Position position) {
        if (dataInfo != null) {
            for (Map.Entry<String, TreeNode<String, Location>> entry : dataInfo.entrySet()) {
                Position childPosition = position != null ? new Position(position, entry.getKey()) : new Position(entry.getKey());
                locationPosition.put(entry.getKey(), childPosition);
                readData(entry.getValue().getChildren(), childPosition);
            }
        }
    }

    private boolean isLocationEncompassing(@NonNull String highLocationId, @NonNull String lowerLocationId) {
        if (locationPosition == null) {
            locationPosition = new HashMap<>();
            LinkedHashMap<String, TreeNode<String, Location>> locationHierarchyMap = retrieveLocationHierarchyMap();
            readData(locationHierarchyMap, null);
        }

        Position highPosition = locationPosition.get(highLocationId);
        Position lowPosition = locationPosition.get(lowerLocationId);
        if (highPosition != null && lowPosition != null) {
            return highPosition.isParentOf(lowPosition);
        }

        return false;
    }
}