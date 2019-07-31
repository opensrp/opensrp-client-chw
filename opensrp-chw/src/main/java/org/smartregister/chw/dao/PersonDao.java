package org.smartregister.chw.dao;

import android.database.Cursor;

import org.smartregister.chw.domain.Person;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class PersonDao extends AbstractDao {

    public static List<Person> getMothersChildren(String baseEntityID) {
        String sql = "select ec_child.base_entity_id , ec_family_member.first_name , ec_family_member.last_name , ec_family_member.middle_name , ec_family_member.dob " +
                "from ec_child " +
                "inner join ec_family_member on ec_child.base_entity_id = ec_family_member.base_entity_id " +
                "where ec_child.mother_entity_id = '" + baseEntityID + "' " +
                "and ec_child.date_removed is null and ec_family_member.date_removed is null " +
                "order by ec_family_member.first_name , ec_family_member.last_name , ec_family_member.middle_name ";

        DataMap<Person> dataMap = new DataMap<Person>() {
            @Override
            public Person readCursor(Cursor c) {
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
            }
        };

        return AbstractDao.readData(sql, dataMap);
    }

}
