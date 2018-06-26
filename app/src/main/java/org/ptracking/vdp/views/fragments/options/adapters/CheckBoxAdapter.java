package org.ptracking.vdp.views.fragments.options.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import org.ptracking.vdp.R;
import org.ptracking.vdp.views.fragments.options.modals.CheckableOptionsAsListUIData;
import org.ptracking.vdp.views.fragments.options.modals.CheckableOptionsAsListUIData.SingleDataOption;

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
                    SingleDataOption singleDataOption = (SingleDataOption) compoundButton.getTag();
                    if (hasOptionsLimitReached() && checked) {
                        Timber.i("Options limit reached");
                        singleDataOption.setSelected(false);
                    } else {
                        singleDataOption.setSelected(checked);
                    }
                    CheckBoxAdapter.this.notifyDataSetChanged();
                }
            }
        });
        return cbvh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final CheckBoxVH vh = (CheckBoxVH) holder;
        SingleDataOption singleDataOption = optionsUIData.getSingleDataOptionArrayList().get(position);
        vh.getCheck_box().setTag(singleDataOption);

        onBind = true;
        vh.populateViews(singleDataOption.getText(), singleDataOption.isSelected(),
                singleDataOption.isShouldShowBackgroundColor() ? singleDataOption.getBackgroundColor() : -1);

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

    class CheckBoxVH extends RecyclerView.ViewHolder {
        private CheckBox check_box;
        private ImageView imageView;
        private View layout;
        private Context context;

        public CheckBoxVH(View itemView) {
            super(itemView);
            check_box = itemView.findViewById(R.id.check_box);
            imageView = itemView.findViewById(R.id.img_checkmark);
            imageView.setVisibility(View.GONE);
            layout = itemView.findViewById(R.id.holder);

            this.context = itemView.getContext();
        }

        public void populateViews(String text, boolean isChecked, int color) {
            check_box.setText(text);
            check_box.setChecked(isChecked);

//            if (color != -1) {
//                layout.setBackgroundColor(context.getResources().getColor(color));
//            } else {
//                layout.setBackgroundColor(context.getResources().getColor(R.color.white));
//            }

            layout.setBackgroundColor(context.getResources().getColor(R.color.white));

        }

        public void setImageBitmap(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }

        public void setImageFromBytes(byte[] imageByteArray) {
            imageView.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(imageByteArray)
                    .apply(new RequestOptions()
                            .fitCenter())
                    .into(imageView);
        }

        public void setCheckBoxClickListener(CompoundButton.OnCheckedChangeListener checkBoxClickListener) {
            check_box.setOnCheckedChangeListener(checkBoxClickListener);
        }

        public CheckBox getCheck_box() {
            return check_box;
        }
    }
}
