package com.puthuvaazhvu.mapping.views.fragments.options.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.views.fragments.options.modals.CheckableOptionsAsListUIData;
import com.puthuvaazhvu.mapping.views.fragments.options.modals.CheckableOptionsAsListUIData.SingleData;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public class CheckBoxAdapter extends CheckableOptionsAsListAdapter {
    private boolean onBind;

    public CheckBoxAdapter(CheckableOptionsAsListUIData optionsUIData) {
        super(optionsUIData);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CheckBoxVH cbvh =
                new CheckBoxVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.check_box_option_row, parent, false));
        cbvh.setCheckBoxClickListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (!onBind) {
                    SingleData singleData = (SingleData) compoundButton.getTag();
                    if (hasOptionsLimitReached() && checked) {
                        Timber.i("Options limit reached");
                        singleData.setSelected(false);
                    } else {
                        singleData.setSelected(checked);
                    }
                    CheckBoxAdapter.this.notifyDataSetChanged();
                }
            }
        });
        return cbvh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CheckBoxVH vh = (CheckBoxVH) holder;
        SingleData singleData = optionsUIData.getSingleDataArrayList().get(position);
        vh.getCheck_box().setTag(singleData);

        onBind = true;
        vh.populateViews(singleData.getText(), singleData.isSelected());
        onBind = false;
    }

    class CheckBoxVH extends RecyclerView.ViewHolder {
        private CheckBox check_box;

        public CheckBoxVH(View itemView) {
            super(itemView);
            check_box = itemView.findViewById(R.id.check_box);
        }

        public void populateViews(String text, boolean isChecked) {
            check_box.setText(text);
            check_box.setChecked(isChecked);
        }

        public void setCheckBoxClickListener(CompoundButton.OnCheckedChangeListener checkBoxClickListener) {
            check_box.setOnCheckedChangeListener(checkBoxClickListener);
        }

        public CheckBox getCheck_box() {
            return check_box;
        }
    }
}
