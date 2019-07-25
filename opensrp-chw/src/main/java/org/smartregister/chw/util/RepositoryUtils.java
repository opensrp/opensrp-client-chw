package org.smartregister.chw.util;

public interface RepositoryUtils {

    String[] UPDATE_REPOSITORY_TYPES = {
            "UPDATE recurring_service_types SET service_group = 'woman' WHERE type = 'IPTp-SP';",
            "UPDATE recurring_service_types SET service_group = 'child' WHERE type != 'IPTp-SP';",
    };

    String[] UPGRADE_V10 = {
            "ALTER TABLE ec_child ADD COLUMN mother_entity_id VARCHAR;",
            "ALTER TABLE ec_child ADD COLUMN entry_point VARCHAR;"
    };
}
