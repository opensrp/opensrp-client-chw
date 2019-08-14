package org.smartregister.chw.model;

import java.util.HashSet;
import java.util.Set;

public abstract class DefaultAncRegisterFragmentModelFlv implements AncRegisterFragmentModel.Flavor {
    @Override
    public Set<String> mainColumns(String tableName) {
        Set<String> columnList = new HashSet<>();
        columnList.add(tableName + "." + org.smartregister.chw.anc.util.DBConstants.KEY.HAS_ANC_CARD);
        return columnList;
    }
}
