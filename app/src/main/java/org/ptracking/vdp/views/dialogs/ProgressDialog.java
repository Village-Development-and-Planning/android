package org.ptracking.vdp.views.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.ptracking.vdp.R;

/**
 * Created by muthuveerappans on 10/20/17.
 */

public class ProgressDialog extends DialogFragment {
    private TextView textView;
    private ProgressBar progressBar;
    private String text;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.progress_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        progressBar = view.findViewById(R.id.progressBar);
        textView = view.findViewById(R.id.title_text);
        textView.setText(text);

        view.findViewById(R.id.hide_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setTextView(String text) {
        this.text = text;
    }
}
