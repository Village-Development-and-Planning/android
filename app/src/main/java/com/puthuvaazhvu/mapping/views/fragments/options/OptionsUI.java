package com.puthuvaazhvu.mapping.views.fragments.options;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Option;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public abstract class OptionsUI {
    protected final ViewGroup frame;
    protected final Context context;
    protected final LayoutInflater layoutInflater;

    private AlertDialog errorDialog;

    public OptionsUI(ViewGroup frame, Context context) {
        this.frame = frame;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public abstract View createView();

    public abstract ArrayList<Option> response();

    protected View inflateView(int resID) {
        return getLayoutInflater().inflate(resID, getFrame(), false);
    }

    public ViewGroup getFrame() {
        return frame;
    }

    public LayoutInflater getLayoutInflater() {
        return layoutInflater;
    }

    public void attachToRoot() {
        View view = createView();
        if (view != null)
            frame.addView(view);
    }

    protected void showErrorDialog(String title
            , String message
            , String positiveButtonTitle
            , String negativeButtonTitle
            , DialogInterface.OnClickListener clickListener) {
        if (errorDialog != null) {
            errorDialog.dismiss();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title)
                .setMessage(message);

        if (positiveButtonTitle != null && !positiveButtonTitle.isEmpty()) {
            builder.setPositiveButton(positiveButtonTitle, clickListener);
        }
        if (negativeButtonTitle != null && !negativeButtonTitle.isEmpty()) {
            builder.setNegativeButton(negativeButtonTitle, clickListener);
        }

        errorDialog = builder.create();
        errorDialog.show();
    }
}
