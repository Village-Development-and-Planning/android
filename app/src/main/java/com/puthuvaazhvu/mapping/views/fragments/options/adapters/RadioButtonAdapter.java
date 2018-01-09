package com.puthuvaazhvu.mapping.views.fragments.options.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.views.fragments.options.modals.CheckableOptionsAsListUIData;
import com.puthuvaazhvu.mapping.views.fragments.options.modals.CheckableOptionsAsListUIData.SingleData;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public class RadioButtonAdapter extends CheckableOptionsAsListAdapter {
    private boolean onBind;

    public RadioButtonAdapter(CheckableOptionsAsListUIData optionsUIData) {
        super(optionsUIData);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RadioButtonVH vh =
                new RadioButtonVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.radio_button_option_row, parent, false));
        vh.setRadioButtonClickListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                // fix https://stackoverflow.com/questions/27070220/android-recyclerview-notifydatasetchanged-illegalstateexception
                if (!onBind) {
                    SingleData singleData = (SingleData) compoundButton.getTag();
                    for (SingleData o : optionsUIData.getSingleDataArrayList()) {
                        o.setSelected(false);
                    }
                    singleData.setSelected(b);
                    RadioButtonAdapter.this.notifyDataSetChanged();
                }
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RadioButtonVH vh = (RadioButtonVH) holder;
        SingleData singleData = optionsUIData.getSingleDataArrayList().get(position);
        vh.getRadio_button().setTag(singleData);

        onBind = true;
        vh.populateViews(
                singleData.getText(),
                singleData.isSelected(),
                singleData.getId(),
                singleData.getBackgroundColor()
        );
        onBind = false;
    }

    class RadioButtonVH extends RecyclerView.ViewHolder {
        private RadioButton radio_button;
        private View layout;
        private Context context;

        public RadioButtonVH(View itemView) {
            super(itemView);
            radio_button = itemView.findViewById(R.id.radio_button);
            layout = itemView.findViewById(R.id.holder);
            context = itemView.getContext();
        }

        public void populateViews(String text, boolean isChecked, String id, int color) {
            radio_button.setText(text);
            radio_button.setChecked(isChecked);
            radio_button.setContentDescription(id); // to uniquely identify this view. Specifically used for testing

            if (color != -1) {
                layout.setBackgroundColor(context.getResources().getColor(color));
            } else {
                layout.setBackgroundColor(context.getResources().getColor(R.color.white));
            }
        }

        public void setRadioButtonClickListener(CompoundButton.OnCheckedChangeListener checkBoxClickListener) {
            radio_button.setOnCheckedChangeListener(checkBoxClickListener);
        }

        public RadioButton getRadio_button() {
            return radio_button;
        }
    }
}
