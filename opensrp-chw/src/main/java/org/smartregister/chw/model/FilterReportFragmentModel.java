package org.smartregister.chw.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.CoreLibrary;
import org.smartregister.chw.contract.FindReportContract;
import org.smartregister.domain.jsonmapping.Location;
import org.smartregister.domain.jsonmapping.util.LocationTree;
import org.smartregister.domain.jsonmapping.util.TreeNode;
import org.smartregister.util.AssetHandler;

import java.util.LinkedHashMap;
import java.util.Map;

public class FilterReportFragmentModel implements FindReportContract.Model {

    @NonNull
    @Override
    public LinkedHashMap<String, String> getAllLocations() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        // read all the locations and return them as a hierachy
        LinkedHashMap<String, TreeNode<String, Location>> locationMap = readLocationMap();
        if (locationMap == null) return map;

        extractToMap(locationMap, map);

        return map;
    }

    private void extractToMap(@NonNull LinkedHashMap<String, TreeNode<String, Location>> locationMap, @NonNull LinkedHashMap<String, String> destination) {
        for (Map.Entry<String, TreeNode<String, Location>> entry : locationMap.entrySet()) {
            destination.put(entry.getValue().getId(), entry.getValue().getLabel());
            LinkedHashMap<String, TreeNode<String, Location>> children = entry.getValue().getChildren();
            if (children != null && children.size() > 0) {
                extractToMap(children, destination);
            }
        }
    }

    @Nullable
    private LinkedHashMap<String, TreeNode<String, Location>> readLocationMap() {
        String locationData = CoreLibrary.getInstance().context().anmLocationController().get();
        LocationTree locationTree = AssetHandler.jsonStringToJava(locationData, LocationTree.class);
        if (locationTree != null) {
            return locationTree.getLocationsHierarchy();
        }
        return null;
    }

}
