package com.puthuvaazhvu.mapping.views.fragments.question.modals;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by muthuveerappans on 10/1/17.
 */

public class GridData extends Data implements Parcelable {
    private int count;

    public GridData(Question question, com.puthuvaazhvu.mapping.views.fragments.option.modals.Data optionData, com.puthuvaazhvu.mapping.views.fragments.option.modals.Data responseData, int count) {
        super(question, optionData, responseData);
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.count);
    }

    protected GridData(Parcel in) {
        super(in);
        this.count = in.readInt();
    }

    public static final Creator<GridData> CREATOR = new Creator<GridData>() {
        @Override
        public GridData createFromParcel(Parcel source) {
            return new GridData(source);
        }

        @Override
        public GridData[] newArray(int size) {
            return new GridData[size];
        }
    };
}
