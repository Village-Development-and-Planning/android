package com.puthuvaazhvu.mapping;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.puthuvaazhvu.mapping.fragments.FragmentInterface;
import com.puthuvaazhvu.mapping.fragments.TestMemoryFragment;
import com.puthuvaazhvu.mapping.views.activities.BaseActivity;
import com.puthuvaazhvu.mapping.views.dialogs.ProgressDialog;

/**
 * Created by muthuveerappans on 21/02/18.
 */

public class MainActivity extends BaseActivity implements FragmentInterface {
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        progressDialog = new ProgressDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.memory:
                addFragment(new TestMemoryFragment());
                return true;
            case R.id.grid_question:
                Intent intent = new Intent(this, GridQuestionFlowTestActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void showLoading(String msg) {
        progressDialog.setTextView(msg);
        progressDialog.show(getSupportFragmentManager(), "msg");
    }

    @Override
    public void hideLoading() {
        progressDialog.dismiss();
    }
}
