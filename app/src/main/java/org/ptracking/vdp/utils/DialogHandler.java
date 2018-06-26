package org.ptracking.vdp.utils;

import android.os.Message;
import android.support.v4.app.FragmentManager;

import org.ptracking.vdp.views.dialogs.ProgressDialog;

/**
 * Created by muthuveerappans on 24/02/18.
 */

public class DialogHandler extends PauseHandler {
    private ProgressDialog progressDialog;
    private FragmentManager fragmentManager;

    public DialogHandler(ProgressDialog progressDialog, FragmentManager fragmentManager) {
        this.progressDialog = progressDialog;
        this.fragmentManager = fragmentManager;
    }

    @Override
    protected boolean storeMessage(Message message) {
        return true;
    }

    @Override
    protected void processMessage(Message message) {
        switch (message.what) {
            case 1:
                String tag = (String) message.obj;
                progressDialog.show(fragmentManager, tag);
                break;
            case 0:
                progressDialog.dismiss();
                break;
        }
    }

    public void showDialog(String tag) {
        sendMessage(obtainMessage(1, tag));
    }

    public void hideDialog() {
        sendMessage(obtainMessage(0));
    }
}
