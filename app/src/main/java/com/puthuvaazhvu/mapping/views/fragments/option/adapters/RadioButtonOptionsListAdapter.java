package com.puthuvaazhvu.mapping.views.fragments.option.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.SingleOptionData;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class RadioButtonOptionsListAdapter extends RecyclerView.Adapter<RBVH> {
    private final ArrayList<SingleOptionData> singleOptionDataArrayList;
    private boolean onBind;

    public RadioButtonOptionsListAdapter(ArrayList<SingleOptionData> singleOptionDataArrayList) {
        this.singleOptionDataArrayList = singleOptionDataArrayList;
    }

    @Override
    public RBVH onCreateViewHolder(ViewGroup parent, int viewType) {
        RBVH rbvh =
                new RBVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.radio_button_option_row, parent, false));
        rbvh.setRadioButtonClickListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SingleOptionData singleOptionData = (SingleOptionData) compoundButton.getTag();
                for (SingleOptionData o : singleOptionDataArrayList) {
                    o.setSelected(false);
                }
                singleOptionData.setSelected(b);

                // fix https://stackoverflow.com/questions/27070220/android-recyclerview-notifydatasetchanged-illegalstateexception
                if (!onBind)
                    RadioButtonOptionsListAdapter.this.notifyDataSetChanged();
            }
        });
        return rbvh;
    }

    @Override
    public void onBindViewHolder(RBVH holder, int position) {
        SingleOptionData singleOptionData = singleOptionDataArrayList.get(position);
        holder.getRadio_button().setTag(singleOptionData);

        onBind = true;
        holder.populateViews(singleOptionData.getText(), singleOptionData.isSelected(), singleOptionData.getId());
        onBind = false;
    }

    @Override
    public int getItemCount() {
        return singleOptionDataArrayList.size();
    }
}

class RBVH extends RecyclerView.ViewHolder {
    private RadioButton radio_button;

    public RBVH(View itemView) {
        super(itemView);
        radio_button = itemView.findViewById(R.id.radio_button);
    }

    public void populateViews(String text, boolean isChecked, String id) {
        radio_button.setText(text);
        radio_button.setChecked(isChecked);
        radio_button.setContentDescription(id); // to uniquely identify this view. Specifically used for testing
    }

    public void setRadioButtonClickListener(CompoundButton.OnCheckedChangeListener checkBoxClickListener) {
        radio_button.setOnCheckedChangeListener(checkBoxClickListener);
    }

    public RadioButton getRadio_button() {
        return radio_button;
    }
}
