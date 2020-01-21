package org.smartregister.chw.model;

import java.util.HashSet;
import java.util.Set;

public class AncRegisterFragmentModelFlv implements AncRegisterFragmentModel.Flavor {
    @Override
    public Set<String> mainColumns(String tableName) {
        return new HashSet<>();
    }
}
