package org.smartregister.chw.application;

import androidx.annotation.NonNull;

import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.core.service.CoreAuthorizationService;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.domain.jsonmapping.Location;
import org.smartregister.domain.jsonmapping.util.TreeNode;
import org.smartregister.p2p.authorizer.P2PAuthorizationService;
import org.smartregister.repository.AllSharedPreferences;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LmhAuthorizationService implements P2PAuthorizationService {

    private Map<String, Object> authorizationDetails = new HashMap<>();
    private CoreAuthorizationService coreAuthorizationService = new CoreAuthorizationService();

    @Override
    public void authorizeConnection(@NonNull final Map<String, Object> peerDeviceMap, @NonNull final AuthorizationCallback authorizationCallback) {
        getAuthorizationDetails(map -> {
            Object peerDeviceTeamId = peerDeviceMap.get(AllConstants.PeerToPeer.KEY_TEAM_ID);
            if (peerDeviceTeamId instanceof String
                    && peerDeviceTeamId.equals(map.get(AllConstants.PeerToPeer.KEY_TEAM_ID))) {
                Object peerDeviceLocationId = peerDeviceMap.get(CoreConstants.PEER_TO_PEER.LOCATION_ID);
                Object myLocationId = authorizationDetails.get(CoreConstants.PEER_TO_PEER.LOCATION_ID);
                Object myPeerStatus = authorizationDetails.get(org.smartregister.p2p.util.Constants.AuthorizationKeys.PEER_STATUS);

                if (peerDeviceLocationId instanceof String && myLocationId instanceof String && myPeerStatus instanceof String) {

                    if (org.smartregister.p2p.util.Constants.PeerStatus.SENDER.equals(myPeerStatus)) {
                        // If this device is a sender
                        // Make sure that
                        if (isKnownLocation((String) myLocationId)) {
                            authorizationCallback.onConnectionAuthorized();
                        } else {
                            rejectConnection(authorizationCallback);
                        }
                    } else {
                        // If this device is a receiver
                        if (isKnownLocation((String) peerDeviceLocationId)) {
                            authorizationCallback.onConnectionAuthorized();
                        } else {
                            rejectConnection(authorizationCallback);
                        }
                    }
                } else {
                    rejectConnection(authorizationCallback);
                }
            } else {
                rejectConnection(authorizationCallback);
            }
        });
    }

    private boolean isKnownLocation(@NonNull String lowerLocationId) {
        // TODO
        // check to see if the location is in the global hierachy
        /*
        LinkedHashMap<String, TreeNode<String, Location>> locationHierarchyMap = coreAuthorizationService.retrieveLocationHierarchyMap();
        return locationHierarchyMap != null && locationHierarchyMap.containsKey(lowerLocationId);

         */
        return true;
    }

    private void rejectConnection(@NonNull AuthorizationCallback authorizationCallback) {
        authorizationCallback.onConnectionAuthorizationRejected("Incorrect authorization details provided");
    }

    @Override
    public void getAuthorizationDetails(@NonNull OnAuthorizationDetailsProvidedCallback onAuthorizationDetailsProvidedCallback) {
        // Load the preferences here
        AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();

        authorizationDetails.put(AllConstants.PeerToPeer.KEY_TEAM_ID, allSharedPreferences.fetchDefaultTeamId(allSharedPreferences.fetchRegisteredANM()));
        authorizationDetails.put(CoreConstants.PEER_TO_PEER.LOCATION_ID, allSharedPreferences.fetchUserLocalityId(allSharedPreferences.fetchRegisteredANM()));

        onAuthorizationDetailsProvidedCallback.onAuthorizationDetailsProvided(authorizationDetails);
    }
}