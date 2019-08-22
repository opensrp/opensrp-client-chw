package org.smartregister.chw.dao;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.dao.AbstractDao;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.List;

import timber.log.Timber;

public class EventDao extends AbstractDao {


    public static List<Event> getEvents(String baseEntityID, String eventType, int limit) {
        String sql = "select json from event where baseEntityId = '" + baseEntityID + "' and eventType = '" + eventType + "' order by updatedAt desc limit " + limit;

        final ECSyncHelper syncHelper = ChwApplication.getInstance().getEcSyncHelper();
        DataMap<Event> dataMap = c -> {
            try {
                return syncHelper.convert(new JSONObject(getCursorValue(c, "json")), Event.class);
            } catch (JSONException e) {
                Timber.e(e);
            }
            return null;
        };

        return AbstractDao.readData(sql, dataMap);
    }
}
