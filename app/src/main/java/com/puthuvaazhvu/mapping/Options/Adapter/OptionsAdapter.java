package com.puthuvaazhvu.mapping.Options.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.puthuvaazhvu.mapping.Options.Modal.OptionData;
import com.puthuvaazhvu.mapping.Options.OPTION_TYPES;
import com.puthuvaazhvu.mapping.Options.OptionsFragment;
import com.puthuvaazhvu.mapping.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class OptionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<OptionData> optionDataList;
    OPTION_TYPES option_type;

    public OptionsAdapter(List<OptionData> optionDataList, OPTION_TYPES option_type) {
        this.optionDataList = optionDataList;
        this.option_type = option_type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.radio_button_option, parent, false);
        return new VH(root);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VH VH = (VH) holder;

        OptionData optionData = optionDataList.get(position);
        VH.setTagDataForRadioButton(optionData);

        VH.populateView(optionData.getText(), optionData.isChecked());
    }

    @Override
    public int getItemCount() {
        return optionDataList.size();
    }

    public ArrayList<OptionData> getSelectedOptions() {
        ArrayList<OptionData> result = new ArrayList<>();
        for (OptionData optionData : optionDataList) {
            if (optionData.isChecked()) {
                result.add(optionData);
            }
        }
        return result;
    }

    private class VH extends RecyclerView.ViewHolder {
        RadioButton radio_button;

        View.OnClickListener radioButtonClickEvent = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Object tag = view.getTag();

                if (tag == null) {
                    throw new RuntimeException("The object tag for the radio button is null. " +
                            "This should actually contain the options data");
                }

                if (option_type == OPTION_TYPES.SINGLE) {
                    // Reset all the radio buttons to false
                    for (OptionData optionData : optionDataList) {
                        optionData.setChecked(false);
                    }
                }

                OptionData optionData = (OptionData) tag;
                optionData.setChecked(option_type == OPTION_TYPES.SINGLE ?
                        true : !optionData.isChecked());
            }
        };

        public VH(View itemView) {
            super(itemView);

            radio_button = itemView.findViewById(R.id.radio_button);
            radio_button.setOnClickListener(radioButtonClickEvent);
        }

        public void setTagDataForRadioButton(OptionData optionData) {
            radio_button.setTag(optionData);
        }

        public void populateView(String text, boolean isRadioSelected) {
            radio_button.setText(text);
            radio_button.setSelected(isRadioSelected);
        }
    }
}
