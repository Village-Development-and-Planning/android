package com.puthuvaazhvu.mapping.views.fragments.options.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.puthuvaazhvu.mapping.R;
import com.puthuvaazhvu.mapping.modals.Answer;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.modals.Question;
import com.puthuvaazhvu.mapping.modals.flow.FlowPattern;
import com.puthuvaazhvu.mapping.modals.flow.QuestionFlow;
import com.puthuvaazhvu.mapping.modals.utils.QuestionUtils;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 1/9/18.
 */

public class CheckableOptionsAsListUIData extends OptionsUIData implements Parcelable {

    private ArrayList<SingleDataOption> singleDataOptionArrayList;

    public CheckableOptionsAsListUIData(
            String questionID,
            String questionRawNumber,
            String questionText,
            FlowPattern flowPattern,
            ArrayList<SingleDataOption> singleDataOptionArrayList) {
        super(questionID, questionRawNumber, questionText, flowPattern);
        this.singleDataOptionArrayList = singleDataOptionArrayList;
    }

    public ArrayList<SingleDataOption> getSingleDataOptionArrayList() {
        return singleDataOptionArrayList;
    }

    public ArrayList<SingleDataOption> getLoggedOptions() {
        ArrayList<SingleDataOption> result = new ArrayList<>();
        for (SingleDataOption o : singleDataOptionArrayList) {
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
        dest.writeTypedList(this.singleDataOptionArrayList);
    }

    protected CheckableOptionsAsListUIData(Parcel in) {
        super(in);
        this.singleDataOptionArrayList = in.createTypedArrayList(SingleDataOption.CREATOR);
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
        boolean showImage = false;

        FlowPattern flowPattern = question.getFlowPattern();
        if (flowPattern != null) {
            QuestionFlow questionFlow = flowPattern.getQuestionFlow();
            if (questionFlow != null)
                showImage = questionFlow.isShowImage();
        }

        ArrayList<SingleDataOption> singleDataOptionArrayList = new ArrayList<>();
        for (Option option : question.getOptionList()) {
            SingleDataOption s = SingleDataOption.adapter(option, showImage, false);
            if (!QuestionUtils.isCurrentAnswerDummy(question) &&
                    question.getCurrentAnswer() != null) {
                for (Option oa : question.getCurrentAnswer().getOptions()) {
                    if (oa.getPosition() != null && oa.getPosition().equals(option.getPosition())) {
                        s.isSelected = true;
                    }
                }
            }
            singleDataOptionArrayList.add(s);
        }

        if (QuestionUtils.isLoopQuestion(question)) {
            // show the background color

            for (SingleDataOption singleDataOption : singleDataOptionArrayList) {
                singleDataOption.setShouldShowBackgroundColor(true);
                for (Answer answer : question.getAnswers()) {
                    if (answer.getOptions().get(0).getPosition().equals(singleDataOption.getPosition())) {
                        singleDataOption.setBackgroundColor(R.color.green_light);
                    }
                }
            }
        }

        return new CheckableOptionsAsListUIData(
                "",
                question.getRawNumber(),
                QuestionUtils.getTextString(question),
                question.getFlowPattern(),
                singleDataOptionArrayList
        );
    }

    public static class SingleDataOption implements Parcelable {
        private final String id;
        private final String text;
        private final String position;
        private boolean isSelected;
        private int backgroundColor = -1;
        private String imageData;
        private boolean shouldShowBackgroundColor = false;

        public SingleDataOption(String id, String text, String position) {
            this.id = id;
            this.text = text;
            this.position = position;
        }

        public SingleDataOption(String id, String text, String position, String imageData) {
            this.id = id;
            this.text = text;
            this.position = position;
            this.imageData = imageData;
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

        public String getImageData() {
            return imageData;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public boolean isShouldShowBackgroundColor() {
            return shouldShowBackgroundColor;
        }

        public void setShouldShowBackgroundColor(boolean shouldShowBackgroundColor) {
            this.shouldShowBackgroundColor = shouldShowBackgroundColor;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.text);
            dest.writeString(this.position);
            dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
            dest.writeInt(this.backgroundColor);
            dest.writeString(this.imageData);
            dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
        }

        protected SingleDataOption(Parcel in) {
            this.id = in.readString();
            this.text = in.readString();
            this.position = in.readString();
            this.isSelected = in.readByte() != 0;
            this.backgroundColor = in.readInt();
            this.imageData = in.readString();
        }

        public static final Creator<SingleDataOption> CREATOR = new Creator<SingleDataOption>() {
            @Override
            public SingleDataOption createFromParcel(Parcel source) {
                return new SingleDataOption(source);
            }

            @Override
            public SingleDataOption[] newArray(int size) {
                return new SingleDataOption[size];
            }
        };

        public static SingleDataOption adapter(Option option, boolean showImage, boolean shouldShowBackgroundColor) {
            SingleDataOption singleDataOption;
            if (showImage)
                singleDataOption = new SingleDataOption(
                        option.getId(),
                        option.getTextString(),
                        option.getPosition(),
                        option.getImageData()
                );
            else
                singleDataOption = new SingleDataOption(
                        option.getId(),
                        option.getTextString(),
                        option.getPosition()
                );

            singleDataOption.setShouldShowBackgroundColor(shouldShowBackgroundColor);
            return singleDataOption;
        }
    }

}
