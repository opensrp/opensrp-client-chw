package org.smartregister.chw.util;

public interface ChwQueryForMaleClients {
    String ALL_MALE_CLIENTS_SELECT_QUERY = "" +
            "/*INDEPENDENT MEMBERS*/\n" +
            "SELECT ec_family_member.first_name,\n" +
            "       ec_family_member.middle_name,\n" +
            "       ec_family_member.last_name,\n" +
            "       ec_family_member.gender,\n" +
            "       ec_family_member.dob,\n" +
            "       ec_family_member.base_entity_id,\n" +
            "       ec_family_member.id                   as _id,\n" +
            "       'Independent'                         AS register_type,\n" +
            "       ec_family_member.relational_id        as relationalid,\n" +
            "       ec_family.village_town                as home_address,\n" +
            "       NULL                                  AS mother_first_name,\n" +
            "       NULL                                  AS mother_last_name,\n" +
            "       NULL                                  AS mother_middle_name,\n" +
            "       ec_family_member.last_interacted_with AS last_interacted_with\n" +
            "FROM ec_family_member\n" +
            "         inner join ec_family on ec_family.base_entity_id = ec_family_member.relational_id\n" +
            "where ec_family_member.date_removed is null\n" +
            "  AND ec_family.entity_type = 'ec_independent_client'\n" +
            "  AND ec_family_member.gender = 'Male' " +
            "  AND ec_family_member.base_entity_id IN (%s)\n" +
            "  AND ec_family_member.base_entity_id NOT IN (\n" +
            "    SELECT ec_anc_register.base_entity_id AS base_entity_id\n" +
            "    FROM ec_anc_register where ec_anc_register.is_closed is 0\n" +
            "    UNION ALL\n" +
            "    SELECT ec_pregnancy_outcome.base_entity_id AS base_entity_id\n" +
            "    FROM ec_pregnancy_outcome where ec_pregnancy_outcome.delivery_date is not null\n" +
            "    UNION ALL\n" +
            "    SELECT ec_child.base_entity_id AS base_entity_id\n" +
            "    FROM ec_child\n" +
            "    UNION ALL\n" +
            "    SELECT ec_malaria_confirmation.base_entity_id AS base_entity_id\n" +
            "    FROM ec_malaria_confirmation\n" +
            "    UNION ALL\n" +
            "    SELECT ec_family_planning.base_entity_id AS base_entity_id\n" +
            "    FROM ec_family_planning\n" +
            "    UNION ALL\n" +
            "    SELECT ec_tb_register.base_entity_id AS base_entity_id\n" +
            "    FROM ec_tb_register\n" +
            "    WHERE ec_tb_register.tb_case_closure_date is null\n" +
            "    UNION ALL\n" +
            "    SELECT ec_hiv_register.base_entity_id AS base_entity_id\n" +
            "    FROM ec_hiv_register\n" +
            ")" +
            "UNION ALL" +
            "/* CBHS REGISTER */\n" +
            "\n" +
            "SELECT ec_family_member.first_name               AS first_name,\n" +
            "       ec_family_member.middle_name              AS middle_name,\n" +
            "       ec_family_member.last_name                AS last_name,\n" +
            "       ec_family_member.gender                   AS gender,\n" +
            "       ec_family_member.dob                      AS dob,\n" +
            "       ec_family_member.base_entity_id           AS base_entity_id,\n" +
            "       ec_family_member.id                       as _id,\n" +
            "       'CBHS'                                     AS register_type,\n" +
            "       ec_family_member.relational_id            as relationalid,\n" +
            "       ec_family.village_town                    as home_address,\n" +
            "       ec_hiv_register.last_interacted_with      AS last_interacted_with,\n" +
            "       NULL                                      AS mother_first_name,\n" +
            "       NULL                                      AS mother_last_name,\n" +
            "       NULL                                      AS mother_middle_name\n" +
            "FROM ec_hiv_register\n" +
            "         inner join ec_family_member on ec_family_member.base_entity_id = ec_hiv_register.base_entity_id\n" +
            "         inner join ec_family on ec_family.base_entity_id = ec_family_member.relational_id\n" +
            "where ec_family_member.date_removed is null\n" +
            "  AND ec_family_member.gender = 'Male' " +
            "  and ec_hiv_register.is_closed is 0\n" +
            "  AND ec_hiv_register.base_entity_id IN (%s)\n" +
            "  AND ec_family_member.base_entity_id NOT IN (\n" +
            "    SELECT ec_anc_register.base_entity_id AS base_entity_id\n" +
            "    FROM ec_anc_register\n" +
            "    UNION ALL\n" +
            "    SELECT ec_pregnancy_outcome.base_entity_id AS base_entity_id\n" +
            "    FROM ec_pregnancy_outcome\n" +
            "    UNION ALL\n" +
            "    SELECT ec_child.base_entity_id AS base_entity_id\n" +
            "    FROM ec_child\n" +
            "    UNION ALL\n" +
            "    SELECT ec_tb_register.base_entity_id AS base_entity_id\n" +
            "    FROM ec_tb_register\n" +
            "    WHERE ec_tb_register.tb_case_closure_date is null\n" +
            ")\n" +
            "\n" +
            "UNION ALL\n" +
            "/*OTHER FAMILY MEMBERS*/\n" +
            "SELECT ec_family_member.first_name,\n" +
            "       ec_family_member.middle_name,\n" +
            "       ec_family_member.last_name,\n" +
            "       ec_family_member.gender,\n" +
            "       ec_family_member.dob,\n" +
            "       ec_family_member.base_entity_id,\n" +
            "       ec_family_member.id                   as _id,\n" +
            "       NULL                                  AS register_type,\n" +
            "       ec_family_member.relational_id        as relationalid,\n" +
            "       ec_family.village_town                as home_address,\n" +
            "       NULL                                  AS mother_first_name,\n" +
            "       NULL                                  AS mother_last_name,\n" +
            "       NULL                                  AS mother_middle_name,\n" +
            "       ec_family_member.last_interacted_with AS last_interacted_with\n" +
            "FROM ec_family_member\n" +
            "         inner join ec_family on ec_family.base_entity_id = ec_family_member.relational_id\n" +
            "where ec_family_member.date_removed is null\n" +
            "  AND ec_family_member.gender = 'Male' " +
            "  AND (ec_family.entity_type = 'ec_family' OR ec_family.entity_type is null)\n" +
            "  AND ec_family_member.base_entity_id IN (%s)\n" +
            "  AND ec_family_member.base_entity_id NOT IN (\n" +
            "    SELECT ec_anc_register.base_entity_id AS base_entity_id\n" +
            "    FROM ec_anc_register where ec_anc_register.is_closed is 0\n" +
            "    UNION ALL\n" +
            "    SELECT ec_pregnancy_outcome.base_entity_id AS base_entity_id\n" +
            "    FROM ec_pregnancy_outcome where ec_pregnancy_outcome.delivery_date is not null\n" +
            "    UNION ALL\n" +
            "    SELECT ec_child.base_entity_id AS base_entity_id\n" +
            "    FROM ec_child\n" +
            "    UNION ALL\n" +
            "    SELECT ec_malaria_confirmation.base_entity_id AS base_entity_id\n" +
            "    FROM ec_malaria_confirmation\n" +
            "    UNION ALL\n" +
            "    SELECT ec_family_planning.base_entity_id AS base_entity_id\n" +
            "    FROM ec_family_planning\n" +
            "    UNION ALL\n" +
            "    SELECT ec_tb_register.base_entity_id AS base_entity_id\n" +
            "    FROM ec_tb_register\n" +
            "    WHERE ec_tb_register.tb_case_closure_date is null\n" +
            "    UNION ALL\n" +
            "    SELECT ec_hiv_register.base_entity_id AS base_entity_id\n" +
            "    FROM ec_hiv_register\n" +
            ")\n" +
            "UNION ALL\n" +
            "/* TB REGISTER */\n" +
            "\n" +
            "SELECT ec_family_member.first_name               AS first_name,\n" +
            "       ec_family_member.middle_name              AS middle_name,\n" +
            "       ec_family_member.last_name                AS last_name,\n" +
            "       ec_family_member.gender                   AS gender,\n" +
            "       ec_family_member.dob                      AS dob,\n" +
            "       ec_family_member.base_entity_id           AS base_entity_id,\n" +
            "       ec_family_member.id                       as _id,\n" +
            "       'TB'                                      AS register_type,\n" +
            "       ec_family_member.relational_id            as relationalid,\n" +
            "       ec_family.village_town                    as home_address,\n" +
            "       ec_tb_register.last_interacted_with       AS last_interacted_with,\n" +
            "       NULL                                      AS mother_first_name,\n" +
            "       NULL                                      AS mother_last_name,\n" +
            "       NULL                                      AS mother_middle_name\n" +
            "FROM ec_tb_register\n" +
            "         inner join ec_family_member on ec_family_member.base_entity_id = ec_tb_register.base_entity_id\n" +
            "         inner join ec_family on ec_family.base_entity_id = ec_family_member.relational_id\n" +
            "where ec_family_member.date_removed is null\n" +
            "  AND ec_family_member.gender = 'Male' " +
            "  and ec_tb_register.is_closed is 0\n" +
            "  and ec_tb_register.tb_case_closure_date is null\n" +
            "  AND ec_tb_register.base_entity_id IN (%s)\n" +
            "  AND ec_family_member.base_entity_id NOT IN (\n" +
            "    SELECT ec_anc_register.base_entity_id AS base_entity_id\n" +
            "    FROM ec_anc_register\n" +
            "    UNION ALL\n" +
            "    SELECT ec_pregnancy_outcome.base_entity_id AS base_entity_id\n" +
            "    FROM ec_pregnancy_outcome\n" +
            "    UNION ALL\n" +
            "    SELECT ec_child.base_entity_id AS base_entity_id\n" +
            "    FROM ec_child\n" +
            ")\n" +
            "\n" +
            "UNION ALL\n" +
            "/*ONLY MALARIA PATIENTS*/\n" +
            "SELECT ec_family_member.first_name,\n" +
            "       ec_family_member.middle_name,\n" +
            "       ec_family_member.last_name,\n" +
            "       ec_family_member.gender,\n" +
            "       ec_family_member.dob,\n" +
            "       ec_family_member.base_entity_id,\n" +
            "       ec_family_member.id                          as _id,\n" +
            "       'Malaria'                                    AS register_type,\n" +
            "       ec_family_member.relational_id               as relationalid,\n" +
            "       ec_family.village_town                       as home_address,\n" +
            "       NULL                                         AS mother_first_name,\n" +
            "       NULL                                         AS mother_last_name,\n" +
            "       NULL                                         AS mother_middle_name,\n" +
            "       ec_malaria_confirmation.last_interacted_with AS last_interacted_with\n" +
            "FROM ec_family_member\n" +
            "         inner join ec_family on ec_family.base_entity_id = ec_family_member.relational_id\n" +
            "         inner join ec_malaria_confirmation\n" +
            "                    on ec_family_member.base_entity_id = ec_malaria_confirmation.base_entity_id\n" +
            "where ec_family_member.date_removed is null\n" +
            "  AND ec_family_member.gender = 'Male' " +
            "  AND ec_family_member.base_entity_id IN (%s)\n" +
            "  AND ec_family_member.base_entity_id NOT IN (\n" +
            "    SELECT ec_anc_register.base_entity_id AS base_entity_id\n" +
            "    FROM ec_anc_register\n" +
            "    UNION ALL\n" +
            "    SELECT ec_pregnancy_outcome.base_entity_id AS base_entity_id\n" +
            "    FROM ec_pregnancy_outcome\n" +
            "    UNION ALL\n" +
            "    SELECT ec_child.base_entity_id AS base_entity_id\n" +
            "    FROM ec_child\n" +
            "    UNION ALL\n" +
            "    SELECT ec_family_planning.base_entity_id AS base_entity_id\n" +
            "    FROM ec_family_planning\n" +
            "    UNION ALL\n" +
            "    SELECT ec_tb_register.base_entity_id AS base_entity_id\n" +
            "    FROM ec_tb_register\n" +
            "    WHERE ec_tb_register.tb_case_closure_date is null\n" +
            "    UNION ALL\n" +
            "    SELECT ec_hiv_register.base_entity_id AS base_entity_id\n" +
            "    FROM ec_hiv_register\n" +
            ")\n" +
            "UNION ALL\n" +
            "\n" +
            "/*ONLY FAMILY PLANNING PATIENTS*/\n" +
            "SELECT ec_family_member.first_name,\n" +
            "       ec_family_member.middle_name,\n" +
            "       ec_family_member.last_name,\n" +
            "       ec_family_member.gender,\n" +
            "       ec_family_member.dob,\n" +
            "       ec_family_member.base_entity_id,\n" +
            "       ec_family_member.id                          as _id,\n" +
            "       'Family Planning'                             AS register_type,\n" +
            "       ec_family_member.relational_id               as relationalid,\n" +
            "       ec_family.village_town                       as home_address,\n" +
            "       NULL                                         AS mother_first_name,\n" +
            "       NULL                                         AS mother_last_name,\n" +
            "       NULL                                         AS mother_middle_name,\n" +
            "       ec_family_planning.last_interacted_with AS last_interacted_with\n" +
            "FROM ec_family_member\n" +
            "         inner join ec_family on ec_family.base_entity_id = ec_family_member.relational_id\n" +
            "         inner join ec_family_planning\n" +
            "                    on ec_family_member.base_entity_id = ec_family_planning.base_entity_id\n" +
            "where ec_family_member.date_removed is null\n" +
            "  AND ec_family_member.gender = 'Male' " +
            "  AND ec_family_planning.is_closed is 0\n" +
            "  AND ec_family_member.base_entity_id IN (%s)\n" +
            "  AND ec_family_member.base_entity_id NOT IN (\n" +
            "    SELECT ec_anc_register.base_entity_id AS base_entity_id\n" +
            "    FROM ec_anc_register\n" +
            "    UNION ALL\n" +
            "    SELECT ec_pregnancy_outcome.base_entity_id AS base_entity_id\n" +
            "    FROM ec_pregnancy_outcome\n" +
            "    UNION ALL\n" +
            "    SELECT ec_child.base_entity_id AS base_entity_id\n" +
            "    FROM ec_child\n" +
            "    UNION ALL\n" +
            "    SELECT ec_malaria_confirmation.base_entity_id AS base_entity_id\n" +
            "    FROM ec_malaria_confirmation\n" +
            "    UNION ALL\n" +
            "    SELECT ec_tb_register.base_entity_id AS base_entity_id\n" +
            "    FROM ec_tb_register\n" +
            "    WHERE ec_tb_register.tb_case_closure_date is null\n" +
            "    UNION ALL\n" +
            "    SELECT ec_hiv_register.base_entity_id AS base_entity_id\n" +
            "    FROM ec_hiv_register\n" +
            ")\n" +
            "ORDER BY last_interacted_with DESC;";
}
