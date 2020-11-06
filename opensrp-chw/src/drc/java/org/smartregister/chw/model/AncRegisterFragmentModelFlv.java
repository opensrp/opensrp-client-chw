package org.smartregister.chw.model;

import java.util.HashSet;
import java.util.Set;

public class AncRegisterFragmentModelFlv extends DefaultAncRegisterFragmentModelFlv {
    @Override
    public Set<String> mainColumns(String tableName) {
        Set<String> columnList = new HashSet<>();
        columnList.add(tableName + "." + org.smartregister.chw.anc.util.DBConstants.KEY.HAS_ANC_CARD);
        columnList.add(tableName + "." + org.smartregister.chw.util.ChwDBConstants.DELIVERY_KIT);
        return columnList;
    }
}
