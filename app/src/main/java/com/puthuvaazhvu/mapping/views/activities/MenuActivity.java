package com.puthuvaazhvu.mapping.views.activities;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.views.activities.settings.SettingsActivity;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 11/7/17.
 */

public abstract class MenuActivity extends BaseActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.other_settings:
                Timber.i("Settings menu clicked");
                openSettingsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
