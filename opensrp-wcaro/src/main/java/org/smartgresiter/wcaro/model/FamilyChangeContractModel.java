package org.smartgresiter.wcaro.model;

import org.smartgresiter.wcaro.contract.FamilyChangeContract;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FamilyChangeContractModel implements FamilyChangeContract.Model {

    public static final String PRIMARY_ID = "primary_id";
    public static final String HEAD_ID = "head_id";

    @Override
    public List<HashMap<String,String>> getMembersExcluding(List<HashMap<String, String>> clients, String primaryCareID, String headOfHouseID, String... ids) {
        List<HashMap<String,String>> members = new ArrayList<>();
        List<String> listIDs = Arrays.asList(ids);
        for (HashMap<String, String> client : clients) {

            if(
                    !client.containsKey(FamilyChangeContractModel.PRIMARY_ID) &&
                    client.get(DBConstants.KEY.BASE_ENTITY_ID).equals(primaryCareID)
            ){
                client.put(FamilyChangeContractModel.PRIMARY_ID, "PrimaryCare");
            }

            if(
                    !client.containsKey(FamilyChangeContractModel.HEAD_ID) &&
                            client.get(DBConstants.KEY.BASE_ENTITY_ID).equals(primaryCareID)
            ){
                client.put(FamilyChangeContractModel.PRIMARY_ID, "HeadID");
            }

            if (!listIDs.contains(Utils.getValue(client, DBConstants.KEY.BASE_ENTITY_ID, false))) {
                members.add(client);
            }
        }
        return members;
    }

}

