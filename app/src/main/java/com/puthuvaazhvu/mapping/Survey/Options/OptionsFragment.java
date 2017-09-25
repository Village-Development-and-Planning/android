package com.puthuvaazhvu.mapping.Survey.Options;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.puthuvaazhvu.mapping.Constants;
import com.puthuvaazhvu.mapping.Survey.Options.Adapter.OptionsAdapter;
import com.puthuvaazhvu.mapping.Survey.Options.Modal.OptionData;
import com.puthuvaazhvu.mapping.R;

import java.util.ArrayList;

import static com.puthuvaazhvu.mapping.Survey.Options.OptionTypes.BUTTON;
import static com.puthuvaazhvu.mapping.Survey.Options.OptionTypes.TEXT_FIELD;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class OptionsFragment extends Fragment implements View.OnClickListener {
    RecyclerView recyclerView;
    EditText input_edit_text;
    Button button;

    OptionTypes optionType;
    ArrayList<OptionData> optionDataArrayList;
    OptionsAdapter optionsAdapter;

    public static OptionsFragment getInstance(ArrayList<OptionData> optionDataList, OptionTypes optionTypes) {
        OptionsFragment optionsFragment = new OptionsFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("options_data", optionDataList);
        bundle.putSerializable("option_type", optionTypes);

        optionsFragment.setArguments(bundle);

        return optionsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.options_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        optionType = (OptionTypes) getArguments().getSerializable("option_type");
        optionDataArrayList = getArguments().getParcelableArrayList("options_data");

        input_edit_text = view.findViewById(R.id.input_edit_text);
        recyclerView = view.findViewById(R.id.options_recycler_view);
        button = view.findViewById(R.id.button);
        button.setOnClickListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext()
                , LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        optionsAdapter = new OptionsAdapter(optionDataArrayList, optionType);
        recyclerView.setAdapter(optionsAdapter);

        if (optionType == OptionTypes.BUTTON) {
            button.setText(Constants.isTamil ? getString(R.string.press_ta) : getString(R.string.press));
        }

        manipulateViewVisibilityBasedOnOptionType();
    }

    public ArrayList<OptionData> getSelectedOptions() {
        if (optionType == TEXT_FIELD) {
            ArrayList<OptionData> result = new ArrayList<>();
            String option = input_edit_text.getText().toString();
            result.add(new OptionData("-1", true, option, "", true));
            return result;
        }

        return optionsAdapter.getSelectedOptions();
    }

    public void manipulateViewVisibilityBasedOnOptionType() {
        if (optionType == TEXT_FIELD) {
            input_edit_text.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
        } else if (optionType == BUTTON) {
            input_edit_text.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            button.setVisibility(View.VISIBLE);
        } else {
            input_edit_text.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            button.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                // TODO: Record GPS.
                break;
        }
    }
}
