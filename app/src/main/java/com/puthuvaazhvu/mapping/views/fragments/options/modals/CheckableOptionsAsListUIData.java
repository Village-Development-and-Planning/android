package com.puthuvaazhvu.mapping.views.fragments.options.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.flow.FlowPattern;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public class CheckableOptionsAsListUIData extends OptionsUIData implements Parcelable {

    private ArrayList<SingleData> singleDataArrayList;

    public CheckableOptionsAsListUIData(
            String questionID,
            String questionRawNumber,
            String questionText,
            FlowPattern flowPattern,
            ArrayList<SingleData> singleDataArrayList) {
        super(questionID, questionRawNumber, questionText, flowPattern);
        this.singleDataArrayList = singleDataArrayList;
    }

    public ArrayList<SingleData> getSingleDataArrayList() {
        return singleDataArrayList;
    }

    public ArrayList<SingleData> getLoggedOptions() {
        ArrayList<SingleData> result = new ArrayList<>();
        for (SingleData o : singleDataArrayList) {
            if (o.isSelected()) {
                result.add(o);
            }
        }
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.singleDataArrayList);
    }

    protected CheckableOptionsAsListUIData(Parcel in) {
        super(in);
        this.singleDataArrayList = in.createTypedArrayList(SingleData.CREATOR);
    }

    public static final Creator<CheckableOptionsAsListUIData> CREATOR = new Creator<CheckableOptionsAsListUIData>() {
        @Override
        public CheckableOptionsAsListUIData createFromParcel(Parcel source) {
            return new CheckableOptionsAsListUIData(source);
        }

        @Override
        public CheckableOptionsAsListUIData[] newArray(int size) {
            return new CheckableOptionsAsListUIData[size];
        }
    };

    public static CheckableOptionsAsListUIData adapter(Question question) {
        ArrayList<SingleData> singleDataArrayList = new ArrayList<>();
        for (Option option : question.getOptionList()) {
            SingleData s = SingleData.adapter(option);
            if (question.getAnswerMatchForOption(option.getPosition()) != null) {
                s.setBackgroundColor(R.color.green_light);
            }
            singleDataArrayList.add(s);
        }

        return new CheckableOptionsAsListUIData(
                "",
                question.getRawNumber(),
                question.getTextForLanguage(),
                question.getFlowPattern(),
                singleDataArrayList
        );
    }

    public static class SingleData implements Parcelable {
        private final String id;
        private final String text;
        private final String position;
        private boolean isSelected;
        private int backgroundColor = -1;

        public SingleData(String id, String text, String position) {
            this.id = id;
            this.text = text;
            this.position = position;
        }

        public String getId() {
            return id;
        }

        public String getText() {
            return text;
        }

        public String getPosition() {
            return position;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public int getBackgroundColor() {
            return backgroundColor;
        }

        public void setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.text);
            dest.writeString(this.position);
            dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
            dest.writeInt(this.backgroundColor);
        }

        protected SingleData(Parcel in) {
            this.id = in.readString();
            this.text = in.readString();
            this.position = in.readString();
            this.isSelected = in.readByte() != 0;
            this.backgroundColor = in.readInt();
        }

        public static final Creator<SingleData> CREATOR = new Creator<SingleData>() {
            @Override
            public SingleData createFromParcel(Parcel source) {
                return new SingleData(source);
            }

            @Override
            public SingleData[] newArray(int size) {
                return new SingleData[size];
            }
        };

        public static SingleData adapter(Option option) {
            return new SingleData(option.getId(), option.getTextString(), option.getPosition());
        }
    }

}
