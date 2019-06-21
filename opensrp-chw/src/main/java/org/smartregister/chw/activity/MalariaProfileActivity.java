package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import org.smartregister.chw.R;
import org.smartregister.chw.malaria.activity.BaseMalariaProfileActivity;
import org.smartregister.chw.malaria.domain.MemberObject;
import org.smartregister.chw.malaria.util.Constants;

public class MalariaProfileActivity extends BaseMalariaProfileActivity {
    private ChildProfileActivityFlv flavor = new ChildProfileActivityFlv();

    public static void startMalariaActivity(Activity activity, MemberObject client) {
        Intent intent = new Intent(activity, MalariaProfileActivity.class);
        intent.putExtra(Constants.MALARIA_MEMBER_OBJECT.MEMBER_OBJECT, client);
        activity.startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.other_member_menu, menu);
        if (flavor.showMalariaConfirmationMenu()) {
            menu.findItem(R.id.action_malaria_registration).setVisible(false);
            menu.findItem(R.id.action_malaria_followup_visit).setVisible(true);
        }
        menu.findItem(R.id.action_anc_registration).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_registration:
                Toast.makeText(getApplicationContext(), "Registration", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_malaria_followup_visit:
                Toast.makeText(getApplicationContext(), "Malaria Follow up", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_remove_member:
                Toast.makeText(getApplicationContext(), "Remove Member", Toast.LENGTH_SHORT).show();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == org.smartregister.malaria.R.id.title_layout) {
            onBackPressed();
        } else if (id == org.smartregister.malaria.R.id.record_visit_malaria) {
            Toast.makeText(this, "Record Malaria", Toast.LENGTH_SHORT).show();
        }
    }

}
