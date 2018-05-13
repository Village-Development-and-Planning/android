package com.puthuvaazhvu.mapping.views.fragments.question;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.views.fragments.options.CreateOptionsUI;
import com.puthuvaazhvu.mapping.views.fragments.options.OptionsUI;
import com.puthuvaazhvu.mapping.views.fragments.options.factory.OptionsUIFactory;

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
