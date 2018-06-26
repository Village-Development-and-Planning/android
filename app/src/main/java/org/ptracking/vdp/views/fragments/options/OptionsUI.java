package org.ptracking.vdp.views.fragments.options;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.ptracking.vdp.modals.Answer;
import org.ptracking.vdp.modals.Option;
import org.ptracking.vdp.modals.Question;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public abstract class OptionsUI {
    protected final ViewGroup frame;
    protected final Context context;
    protected final LayoutInflater layoutInflater;
    protected final Question question;

    private AlertDialog errorDialog;

    public OptionsUI(ViewGroup frame, Context context, Question question) {
        this.frame = frame;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.question = question;
    }

    protected ArrayList<Option> getLatestOptions() {
        if (question.getCurrentAnswer() != null
                && question.getCurrentAnswer().isDummy())
            return null;

        Answer answer = question.getCurrentAnswer();
        if (answer != null) {
            ArrayList<Option> latestOptions = answer.getLoggedOptions();
            if (latestOptions != null && latestOptions.size() > 0) {
                return latestOptions;
            }
        }
        return null;
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

    public void onNextPressed() {

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
