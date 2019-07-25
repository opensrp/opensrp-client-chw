package org.smartregister.chw.util;

public interface RepositoryUtilsFlv {

    String[] UPGRADE_V6 = {
            "ALTER TABLE ec_family ADD COLUMN nearest_facility VARCHAR;",
            "ALTER TABLE ec_family_member ADD COLUMN voter_id VARCHAR;",
            "ALTER TABLE ec_family_member ADD COLUMN driver_license VARCHAR;",
            "ALTER TABLE ec_family_member ADD COLUMN passport VARCHAR;",
            "ALTER TABLE ec_family_member ADD COLUMN insurance_provider VARCHAR;",
            "ALTER TABLE ec_family_member ADD COLUMN insurance_provider_other VARCHAR;",
            "ALTER TABLE ec_family_member ADD COLUMN insurance_provider_number VARCHAR;",
            "ALTER TABLE ec_family_member ADD COLUMN disabilities VARCHAR;",
            "ALTER TABLE ec_family_member ADD COLUMN service_provider VARCHAR;",
            "ALTER TABLE ec_family_member ADD COLUMN leader VARCHAR;",
            "ALTER TABLE ec_family_member ADD COLUMN leader_other VARCHAR;"
    };

    String[] UPGRADE_V8 = {
            "ALTER TABLE ec_family_member ADD COLUMN type_of_disability VARCHAR;"
    };

    String[] UPGRADE_V9 = {
            "ALTER TABLE ec_child ADD COLUMN insurance_provider VARCHAR;",
            "ALTER TABLE ec_child ADD COLUMN insurance_provider_number VARCHAR;",
            "ALTER TABLE ec_child ADD COLUMN insurance_provider_other VARCHAR;",
            "ALTER TABLE ec_child ADD COLUMN type_of_disability VARCHAR;"
    };
}
