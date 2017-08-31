package com.puthuvaazhvu.mapping.Options.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.puthuvaazhvu.mapping.Options.Modal.OptionData;
import com.puthuvaazhvu.mapping.Question.QUESTION_TYPE;
import com.puthuvaazhvu.mapping.R;

import java.util.ArrayList;
import java.util.List;

import static com.puthuvaazhvu.mapping.Constants.DEBUG;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class OptionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<OptionData> optionDataList;
    QUESTION_TYPE question_type;

    public OptionsAdapter(List<OptionData> optionDataList, QUESTION_TYPE question_type) {
        this.optionDataList = optionDataList;
        this.question_type = question_type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = null;
        RecyclerView.ViewHolder viewHolder = null;
        if (question_type == QUESTION_TYPE.SINGLE || question_type == QUESTION_TYPE.LOOP) {
            root = LayoutInflater.from(parent.getContext()).inflate(R.layout.radio_button_option, parent, false);
            viewHolder = new SVH(root);
        } else if (question_type == QUESTION_TYPE.MULTIPLE) {
            root = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_box_option, parent, false);
            viewHolder = new MVH(root);
        } else {
            // TODO: should not throw an exception in production.
            if (DEBUG)
                throw new RuntimeException("The QUESTION_TYPE is NONE");
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VH vh = (VH) holder;

        OptionData optionData = optionDataList.get(position);
        vh.setTagDataForCompoundButton(optionData);

        vh.populateView(optionData.getText(), optionData.isChecked(), optionData.isOptionDone());
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

    private abstract class VH<T extends CompoundButton> extends RecyclerView.ViewHolder {
        T compoundButton;
        ImageView img_check_mark;


        View.OnClickListener optionCLickEvent = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Object tag = view.getTag();

                if (tag == null) {
                    throw new RuntimeException("The object tag for the radio button is null. " +
                            "This should actually contain the options data");
                }

                if (question_type == QUESTION_TYPE.SINGLE || question_type == QUESTION_TYPE.LOOP) {
                    // Reset all the radio buttons to false
                    for (OptionData optionData : optionDataList) {
                        optionData.setChecked(false);
                    }
                }

                OptionData optionData = (OptionData) tag;
                optionData.setChecked(question_type == QUESTION_TYPE.SINGLE || question_type == QUESTION_TYPE.LOOP ?
                        true : !optionData.isChecked());

                OptionsAdapter.this.notifyDataSetChanged();
            }
        };

        public VH(View itemView) {
            super(itemView);

            compoundButton = getCompoundButton(itemView);
            compoundButton.setOnClickListener(optionCLickEvent);

            img_check_mark = itemView.findViewById(R.id.img_checkmark);
        }

        public abstract T getCompoundButton(View itemView);

        public void setTagDataForCompoundButton(OptionData optionData) {
            compoundButton.setTag(optionData);
        }

        public void populateView(String text, boolean isRadioSelected, boolean shouldShowCheckMark) {
            compoundButton.setText(text);
            compoundButton.setChecked(isRadioSelected);
            img_check_mark.setVisibility(shouldShowCheckMark ? View.VISIBLE : View.GONE);
        }
    }

    private class SVH extends VH<RadioButton> {
        public SVH(View itemView) {
            super(itemView);
        }

        @Override
        public RadioButton getCompoundButton(View itemView) {
            return itemView.findViewById(R.id.radio_button);
        }
    }

    private class MVH extends VH<CheckBox> {
        public MVH(View itemView) {
            super(itemView);
        }

        @Override
        public CheckBox getCompoundButton(View itemView) {
            return itemView.findViewById(R.id.check_box);
        }
    }

}
