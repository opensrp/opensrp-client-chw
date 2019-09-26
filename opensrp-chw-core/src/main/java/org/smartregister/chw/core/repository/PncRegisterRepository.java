package org.smartregister.chw.core.repository;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreReferralUtils;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import timber.log.Timber;

public class PncRegisterRepository extends BaseRepository {

    public static final String TABLE_NAME = CoreConstants.TABLE_NAME.PNC_MEMBER;
    public static final String BASE_ENTITY_ID = "base_entity_id";
    public static final String IS_CLOSED = "is_closed";
    public static final String DELIVERY_DATE = "delivery_date";
    public static final String LAST_VISIT_DATE = "last_visit_date";
    public static final String[] PNC_COUNT_TABLE_COLUMNS = {BASE_ENTITY_ID};

    public PncRegisterRepository(Repository repository) {
        super(repository);
    }

    public CommonPersonObject getPncCommonPersonObject(String baseEntityId) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = null;
        CommonPersonObject personObject = null;
        String query = CoreReferralUtils.pncFamilyMemberProfileDetailsSelect(CoreConstants.TABLE_NAME.FAMILY, baseEntityId);
        Timber.d("PNC Member CommonPersonObject Query %s", query);

        try {
            if (database == null) {
                return null;
            }
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                personObject = Utils.context().commonrepository(CoreConstants.TABLE_NAME.FAMILY).readAllcommonforCursorAdapter(cursor);
            }
        } catch (Exception ex) {
            Timber.e(ex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return personObject;
    }
}
