package org.smartregister.chw.util;

import java.util.ArrayList;

public class ChildUtilsFlv implements ChildUtils.Flavor {
    @Override
    public ArrayList<String> mainColumns(String tableName, String familyTable, String familyMemberTable) {
        return new ArrayList<>();
    }
}
