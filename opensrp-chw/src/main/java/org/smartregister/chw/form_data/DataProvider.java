package org.smartregister.chw.form_data;

import org.jetbrains.annotations.Nullable;
import org.smartregister.domain.db.Client;

import java.util.Map;

public interface DataProvider {

    @Nullable
    String getFieldValue(String jsonKey, Client client, Map<String, Map<String, Object>> dbData);
}
