package org.smartregister.chw.model;

import org.smartregister.chw.contract.AllClientsMemberContract;
import org.smartregister.chw.util.AllClientsUtils;
import org.smartregister.opd.pojo.OpdEventClient;

import java.util.List;

public class AllClientsMemberModel implements AllClientsMemberContract.Model {
    @Override
    public List<OpdEventClient> processUpdateForm(String jsonString) {
        return AllClientsUtils.getOpdEventClients(jsonString, true);
    }
}
