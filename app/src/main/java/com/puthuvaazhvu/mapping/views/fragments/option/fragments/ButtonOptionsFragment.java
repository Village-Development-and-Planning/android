package com.puthuvaazhvu.mapping.views.fragments.option.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.puthuvaazhvu.mapping.R;

/**
 * Created by muthuveerappans on 10/12/17.
 */

public abstract class ButtonOptionsFragment extends OptionsFragment implements View.OnClickListener {
    protected Button button;
    private AlertDialog errorDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.options_button, container, false);
        button = view.findViewById(R.id.button);
        button.setOnClickListener(this);
        return view;
    }

    @Override
    public abstract void onViewCreated(View view, @Nullable Bundle savedInstanceState);

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button) {
            onButtonClick(view);
        }
    }

    public Button getButton() {
        return button;
    }

    public abstract void onButtonClick(View view);

    public void showErrorDialog(String title
            , String message
            , String positiveButtonTitle
            , String negativeButtonTitle
            , DialogInterface.OnClickListener clickListener) {
        if (errorDialog != null) {
            errorDialog.dismiss();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

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
