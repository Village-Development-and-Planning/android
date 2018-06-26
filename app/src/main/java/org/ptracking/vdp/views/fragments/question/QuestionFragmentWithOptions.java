package org.ptracking.vdp.views.fragments.question;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import org.ptracking.vdp.R;
import org.ptracking.vdp.modals.Question;
import org.ptracking.vdp.views.fragments.options.CreateOptionsUI;
import org.ptracking.vdp.views.fragments.options.OptionsUI;
import org.ptracking.vdp.views.fragments.options.factory.OptionsUIFactory;

/**
 * Created by muthuveerappans on 13/05/18.
 */

public abstract class QuestionFragmentWithOptions extends QuestionFragment {
    protected ViewGroup optionsContainer;
    protected OptionsUI optionsUI;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        optionsContainer = view.findViewById(R.id.options_container);

        optionsUI = loadOptionsUI(currentQuestion);
    }


    private OptionsUI loadOptionsUI(Question question) {
        OptionsUIFactory optionsUIFactory = new OptionsUIFactory(question, optionsContainer);

        CreateOptionsUI createOptionsUI = new CreateOptionsUI(question);

        OptionsUI optionsUI = createOptionsUI.createOptionsUI(optionsUIFactory);
        optionsUI.attachToRoot();

        return optionsUI;
    }

}
