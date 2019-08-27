package org.smartregister.chw.dao;

import android.database.Cursor;

import org.smartregister.chw.core.dao.AbstractDao;
import org.smartregister.chw.core.domain.Person;
import org.smartregister.chw.domain.PncBaby;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.Utils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class PersonDao extends AbstractDao {

    public static List<Person> getMothersChildren(String baseEntityID) {
        String sql = "select ec_child.base_entity_id , ec_family_member.first_name , ec_family_member.last_name , ec_family_member.middle_name , ec_family_member.dob " +
                "from ec_child " +
                "inner join ec_family_member on ec_child.base_entity_id = ec_family_member.base_entity_id " +
                "where ec_child.mother_entity_id = '" + baseEntityID + "' " +
                "and ec_child.date_removed is null and ec_family_member.date_removed is null " +
                "order by ec_family_member.first_name , ec_family_member.last_name , ec_family_member.middle_name ";

        DataMap<Person> dataMap = c -> {
            Date dob = null;
            try {
                dob = getDobDateFormat().parse(c.getString(c.getColumnIndex("dob")));
            } catch (ParseException e) {
                Timber.e(e);
            }
            return new Person(
                    getCursorValue(c, "base_entity_id"),
                    getCursorValue(c, "first_name"),
                    getCursorValue(c, "last_name"),
                    getCursorValue(c, "middle_name"),
                    dob
            );
        };

        return AbstractDao.readData(sql, dataMap);
    }

    /**
     * returns an immutable PncBaby
     *
     * @param baseEntityID
     * @return
     */
    public static List<PncBaby> getMothersPNCBabies(String baseEntityID) {
        String sql = "select ec_child.base_entity_id , ec_family_member.first_name , ec_family_member.last_name , ec_family_member.middle_name , ec_family_member.dob " +
                "from ec_child " +
                "inner join ec_family_member on ec_child.base_entity_id = ec_family_member.base_entity_id " +
                "where ec_child.mother_entity_id = '" + baseEntityID + "' " +
                "and ec_child.date_removed is null and ec_family_member.date_removed is null " +
                "order by ec_family_member.first_name , ec_family_member.last_name , ec_family_member.middle_name ";

        // extract lbw from the pregnancy outcome form
        final String[] lbw = {"No"};
        List<Event> event = EventDao.getEvents(baseEntityID, Constants.EventType.PREGNANCY_OUTCOME, 1);
        if (event != null && event.size() > 0) {
            Map<String, List<Obs>> obsMap = Utils.groupObsByFieldObservations(event.get(0).getObs());
            List<Obs> obs = obsMap.get("lbw");
            if (obs != null && obs.size() == 1) {
                lbw[0] = (String) obs.get(0).getHumanReadableValues().get(0);
            }
        }

        DataMap<PncBaby> dataMap = c -> {
            Date dob = null;
            try {
                dob = getDobDateFormat().parse(c.getString(c.getColumnIndex("dob")));
            } catch (ParseException e) {
                Timber.e(e);
            }
            return new PncBaby(
                    getCursorValue(c, "base_entity_id"),
                    getCursorValue(c, "first_name"),
                    getCursorValue(c, "last_name"),
                    getCursorValue(c, "middle_name"),
                    dob,
                    lbw[0]
            );
        };

        return AbstractDao.readData(sql, dataMap);
    }

    public String getAncCreatedDate(String baseEntityId) {
        String sql = "SELECT date_created FROM ec_anc_log " +
                " INNER JOIN ec_family_member on ec_family_member.base_entity_id = ec_anc_log.base_entity_id " +
                " WHERE ec_family_member.base_entity_id = '" + baseEntityId + "'";

        DataMap<String> dataMap = new DataMap<String>() {
            @Override
            public String readCursor(Cursor c) {
                return getCursorValue(c, "date_created");
            }
        };

        List<String> res = AbstractDao.readData(sql, dataMap);
        if (res == null || res.size() == 0) {
            return null;
        }

        String date = res.get(0);

        return date;
    }

}
