package com.puthuvaazhvu.mapping.views.fragments.option.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.views.fragments.option.modals.Option;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 9/30/17.
 */

public class CheckBoxOptionsListAdapter extends RecyclerView.Adapter<CBVH> {
    private final ArrayList<Option> optionArrayList;

    public CheckBoxOptionsListAdapter(ArrayList<Option> optionArrayList) {
        this.optionArrayList = optionArrayList;
    }

    @Override
    public CBVH onCreateViewHolder(ViewGroup parent, int viewType) {
        CBVH cbvh =
                new CBVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.check_box_option_row, parent, false));
        cbvh.setCheckBoxClickListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Option option = (Option) compoundButton.getTag();
                option.setSelected(b);
            }
        });
        return cbvh;
    }

    @Override
    public void onBindViewHolder(CBVH holder, int position) {
        Option option = optionArrayList.get(position);
        holder.getCheck_box().setTag(option);
        holder.populateViews(option.getText(), option.isSelected());
    }

    @Override
    public int getItemCount() {
        return optionArrayList.size();
    }
}

class CBVH extends RecyclerView.ViewHolder {
    private CheckBox check_box;

    public CBVH(View itemView) {
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
