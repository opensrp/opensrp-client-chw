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

    String DELETE_DUPLICATE_SCHEDULES = "delete from schedule_service where id not in ( " +
            "select max(id) from schedule_service " +
            "group by base_entity_id , schedule_group_name , schedule_name " +
            "having count(*) > 1 " +
            ")";

    String ADD_MISSING_REPORTING_COLUMN = "ALTER TABLE 'indicator_queries' ADD COLUMN expected_indicators TEXT NULL;";

}
