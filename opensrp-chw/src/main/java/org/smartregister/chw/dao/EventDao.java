package org.smartregister.chw.dao;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.dao.AbstractDao;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class EventDao extends AbstractDao {


    public static List<Event> getEvents(String baseEntityID, String eventType, int limit) {
        String sql = "select json from event where baseEntityId = '" + baseEntityID + "' COLLATE NOCASE and eventType = '" + eventType + "' COLLATE NOCASE order by updatedAt desc limit " + limit;

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

    @Nullable
    public static Event getLatestEvent(String baseEntityID, List<String> eventTypes) {
        StringBuilder types = new StringBuilder();
        for (String eventType : eventTypes) {
            if (types.length() > 0)
                types.append(" , ");

            types.append("'").append(eventType).append("'");
        }

        String sql = "select json from event where baseEntityId = '" + baseEntityID + "' COLLATE NOCASE and eventType in (" + types.toString() + ") COLLATE NOCASE order by updatedAt desc limit 1";

        final ECSyncHelper syncHelper = ChwApplication.getInstance().getEcSyncHelper();
        DataMap<Event> dataMap = c -> {
            try {
                return syncHelper.convert(new JSONObject(getCursorValue(c, "json")), Event.class);
            } catch (JSONException e) {
                Timber.e(e);
            }
            return null;
        };

        List<Event> res = AbstractDao.readData(sql, dataMap);
        if (res != null && res.size() > 0)
            return res.get(0);

        return null;
    }

    public static List<org.smartregister.domain.Event> getUnprocessedEvents(String[] events) {

        StringBuilder builder = new StringBuilder();
        int size = events.length;
        int x = 0;
        while (x < size) {
            builder.append("'").append(events[x]).append("'");

            if (x < (size - 1))
                builder.append(",");
            x++;
        }

        String sql = "select event.json event_json from event " +
                "where event.formSubmissionId not in (select form_submission_id from visits) and event.eventType in (" + builder.toString() + ") " +
                "order by event.rowid asc ";

        List<org.smartregister.domain.Event> results = new ArrayList<>();

        final ECSyncHelper syncHelper = ChwApplication.getInstance().getEcSyncHelper();
        DataMap<Void> dataMap = c -> {
            try {
                org.smartregister.domain.Event event = syncHelper.convert(new JSONObject(getCursorValue(c, "event_json")), org.smartregister.domain.Event.class);
                results.add(event);
                return null;
            } catch (JSONException e) {
                Timber.e(e);
            }
            return null;
        };

        AbstractDao.readData(sql, dataMap);

        return results;
    }
}
