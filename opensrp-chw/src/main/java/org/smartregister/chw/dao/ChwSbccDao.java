package org.smartregister.chw.dao;

import org.smartregister.chw.core.dao.SbccDao;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.model.SbccSessionModel;
import org.smartregister.chw.util.Constants;
import org.smartregister.family.util.DBConstants;

import java.util.List;

public class ChwSbccDao extends SbccDao {

    public static List<SbccSessionModel> getSbccSessions() {
        String sql = "SELECT * FROM " + Constants.TableName.SBCC;

        DataMap<SbccSessionModel> dataMap = cursor -> {
            SbccSessionModel sbccSessionModel = new SbccSessionModel();
            sbccSessionModel.setSessionId(cursor.getString(cursor.getColumnIndex(DBConstants.KEY.BASE_ENTITY_ID)));
            sbccSessionModel.setSessionParticipants(cursor.getString(cursor.getColumnIndex(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.SBCC_PARTICIPANTS_NUMBER)));
            sbccSessionModel.setSessionLocation(cursor.getString(cursor.getColumnIndex(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.SBCC_LOCATION_TYPE)));
            sbccSessionModel.setSessionDate(cursor.getString(cursor.getColumnIndex(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.SBCC_DATE)));

            return sbccSessionModel;
        };

        List<SbccSessionModel> res = readData(sql, dataMap);
        if (res == null || res.size() == 0)
            return null;
        return res;
    }
}
