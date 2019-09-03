package org.smartregister.chw.core.repository;

import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

public class PncRegisterRepository extends BaseRepository {

    public static final String TABLE_NAME = "ec_pregnancy_outcome";
    public static final String BASE_ENTITY_ID = "base_entity_id";
    public static final String IS_CLOSED = "is_closed";
    public static final String DELIVERY_DATE = "base_entity_id";
    public static final String LAST_VISIT_DATE = "base_entity_id";
    public static final String BASE_ENTITY_ID = "base_entity_id";


    public PncRegisterRepository(Repository repository) {
        super(repository);
    }
}
