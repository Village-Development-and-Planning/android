package com.puthuvaazhvu.mapping.views.fragments.options.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.views.fragments.options.modals.CheckableOptionsAsListUIData;
import com.puthuvaazhvu.mapping.views.fragments.options.modals.CheckableOptionsAsListUIData.SingleDataOption;

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
        RadioButtonVH vh = new RadioButtonVH(
                LayoutInflater
                        .from(parent.getContext()).inflate(R.layout.radio_button_option_row, parent
                        , false));

        vh.setRadioButtonClickListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                // fix https://stackoverflow.com/questions/27070220/android-recyclerview-notifydatasetchanged-illegalstateexception
                if (!onBind) {
                    SingleDataOption singleDataOption = (SingleDataOption) compoundButton.getTag();
                    for (SingleDataOption o : optionsUIData.getSingleDataOptionArrayList()) {
                        o.setSelected(false);
                    }
                    singleDataOption.setSelected(b);
                    RadioButtonAdapter.this.notifyDataSetChanged();
                }
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final RadioButtonVH vh = (RadioButtonVH) holder;
        final SingleDataOption singleDataOption = optionsUIData.getSingleDataOptionArrayList().get(position);
        vh.getRadio_button().setTag(singleDataOption);

        onBind = true;

        vh.populateViews(
                singleDataOption.getText(),
                singleDataOption.isSelected(),
                singleDataOption.getId(),
                singleDataOption.isShouldShowBackgroundColor() ? singleDataOption.getBackgroundColor() : -1
        );

        if (singleDataOption.getImageData() != null) {
            byte[] decodedString = Base64.decode(singleDataOption.getImageData(), Base64.DEFAULT);
            vh.setImageFromBytes(decodedString);
            //Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//            if (decodedByte != null)
//                vh.setImageBitmap(decodedByte);
        } else {
            vh.imageView.setVisibility(View.GONE);
        }
        onBind = false;
    }

    class RadioButtonVH extends RecyclerView.ViewHolder {
        private RadioButton radio_button;
        private View layout;
        private Context context;
        private ImageView imageView;

        public RadioButtonVH(View itemView) {
            super(itemView);
            radio_button = itemView.findViewById(R.id.radio_button);
            layout = itemView.findViewById(R.id.holder);
            context = itemView.getContext();
            imageView = itemView.findViewById(R.id.img_checkmark);
            imageView.setVisibility(View.GONE);
        }

        public void populateViews(String text, boolean isChecked, String id, int color) {
            radio_button.setText(text);
            radio_button.setChecked(isChecked);

            if (color != -1) {
                layout.setBackgroundColor(context.getResources().getColor(color));
            } else {
                layout.setBackgroundColor(context.getResources().getColor(R.color.white));
            }
        }

        public void setImageFromBytes(byte[] imageByteArray) {
            imageView.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(imageByteArray)
                    .apply(new RequestOptions()
                            .fitCenter())
                    .into(imageView);
        }

        public void setImageBitmap(Bitmap image) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(image);
        }

        public void setRadioButtonClickListener(CompoundButton.OnCheckedChangeListener checkBoxClickListener) {
            radio_button.setOnCheckedChangeListener(checkBoxClickListener);
        }

        public RadioButton getRadio_button() {
            return radio_button;
        }
    }
}
