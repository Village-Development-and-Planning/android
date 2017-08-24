package com.puthuvaazhvu.mapping.Modals;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by muthuveerappans on 8/24/17.
 */

public class Question implements Parcelable{
    String id;
    String position;
    Text text;
    String type;
    List<Option> optionList;
    List<String> tags;
    String modifiedAt;
    List<Question> children;

    public Question(String id, String position, Text text, String type, List<Option> optionList, List<String> tags, String modifiedAt, List<Question> children) {
        this.id = id;
        this.position = position;
        this.text = text;
        this.type = type;
        this.optionList = optionList;
        this.tags = tags;
        this.modifiedAt = modifiedAt;
        this.children = children;
    }

    protected Question(Parcel in) {
        id = in.readString();
        position = in.readString();
        text = in.readParcelable(Text.class.getClassLoader());
        type = in.readString();
        optionList = in.createTypedArrayList(Option.CREATOR);
        tags = in.createStringArrayList();
        modifiedAt = in.readString();
        children = in.createTypedArrayList(Question.CREATOR);
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getPosition() {
        return position;
    }

    public Text getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    public List<Option> getOptionList() {
        return optionList;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public List<Question> getChildren() {
        return children;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(position);
        parcel.writeParcelable(text, i);
        parcel.writeString(type);
        parcel.writeTypedList(optionList);
        parcel.writeStringList(tags);
        parcel.writeString(modifiedAt);
        parcel.writeTypedList(children);
    }
}
