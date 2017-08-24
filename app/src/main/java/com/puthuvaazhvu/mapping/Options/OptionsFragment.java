package com.puthuvaazhvu.mapping.Options;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.puthuvaazhvu.mapping.Options.Adapter.OptionsAdapter;
import com.puthuvaazhvu.mapping.Options.Modal.OptionData;
import com.puthuvaazhvu.mapping.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class OptionsFragment extends Fragment {
    RecyclerView recyclerView;
    EditText input_edit_text;

    OPTION_TYPES option_type;
    ArrayList<OptionData> optionDataArrayList;
    OptionsAdapter optionsAdapter;

    public static OptionsFragment getInstance(ArrayList<OptionData> optionDataList, OPTION_TYPES option_type) {
        OptionsFragment optionsFragment = new OptionsFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("options_data", optionDataList);
        bundle.putSerializable("option_type", option_type);

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
        option_type = (OPTION_TYPES) getArguments().getSerializable("option_type");
        optionDataArrayList = getArguments().getParcelableArrayList("options_data");

        manipulateViewVisibilityBasedOnOptionType();

        if (option_type == OPTION_TYPES.INPUT) {
            input_edit_text = view.findViewById(R.id.input_edit_text);
        } else {
            recyclerView = view.findViewById(R.id.options_recycler_view);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext()
                    , LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);

            optionsAdapter = new OptionsAdapter(optionDataArrayList, option_type);

            recyclerView.setAdapter(optionsAdapter);
        }

    }

    public ArrayList<OptionData> getSelectedOptions() {
        if (option_type == OPTION_TYPES.INPUT) {
            ArrayList<OptionData> result = new ArrayList<>();
            String option = input_edit_text.getText().toString();
            result.add(new OptionData(-1, true, option, ""));
            return result;
        }

        return optionsAdapter.getSelectedOptions();
    }

    public void manipulateViewVisibilityBasedOnOptionType() {
        if (option_type == OPTION_TYPES.INPUT) {
            input_edit_text.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            input_edit_text.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

}
