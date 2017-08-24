package com.puthuvaazhvu.mapping.Modals;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class Survey implements Parcelable {
    String id;
    String name;
    List<Question> questionList;
    String modifiedAt;

    public Survey(String id, String name, List<Question> questionList, String modifiedAt) {
        this.id = id;
        this.name = name;
        this.questionList = questionList;
        this.modifiedAt = modifiedAt;
    }

    protected Survey(Parcel in) {
        id = in.readString();
        name = in.readString();
        questionList = in.createTypedArrayList(Question.CREATOR);
        modifiedAt = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeTypedList(questionList);
        dest.writeString(modifiedAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Survey> CREATOR = new Creator<Survey>() {
        @Override
        public Survey createFromParcel(Parcel in) {
            return new Survey(in);
        }

        @Override
        public Survey[] newArray(int size) {
            return new Survey[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Question> getQuestionList() {
        return questionList;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }
}
