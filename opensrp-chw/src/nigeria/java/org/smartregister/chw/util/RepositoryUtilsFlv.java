package org.smartregister.chw.util;

import org.smartregister.chw.anc.repository.VisitDetailsRepository;
import org.smartregister.chw.anc.repository.VisitRepository;

public interface RepositoryUtilsFlv {

    String[] DROP_VISITS_INFO_TABLES = {
            "DROP TABLE " + VisitRepository.VISIT_TABLE + ";",
            "DROP TABLE " + VisitDetailsRepository.VISIT_DETAILS_TABLE + ";",
    };

    String addLbwColumnQuery = "ALTER TABLE ec_child ADD COLUMN low_birth_weight VARCHAR;";
}
