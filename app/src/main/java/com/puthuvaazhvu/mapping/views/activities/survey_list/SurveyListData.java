package com.puthuvaazhvu.mapping.views.activities.survey_list;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class SurveyListData implements Parcelable {
    private String id;
    private String name;
    private boolean isChecked;
    private int count;
    private boolean isOngoing;
    private String snapshotPath; // will be null if the survey is new
    private String snapshotID;

    public SurveyListData(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public SurveyListData(String id, String name, int count) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.isOngoing = false;
    }

    public SurveyListData(String id, String name, int count, boolean isOngoing, String snapshotPath, String snapshotID) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.isOngoing = isOngoing;
        this.snapshotPath = snapshotPath;
        this.snapshotID = snapshotID;
    }

    protected SurveyListData(Parcel in) {
        id = in.readString();
        name = in.readString();
        isChecked = in.readByte() != 0;
        count = in.readInt();
        isOngoing = in.readByte() != 0;
        snapshotPath = in.readString();
        snapshotID = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeInt(count);
        dest.writeByte((byte) (isOngoing ? 1 : 0));
        dest.writeString(snapshotPath);
        dest.writeString(snapshotID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SurveyListData> CREATOR = new Creator<SurveyListData>() {
        @Override
        public SurveyListData createFromParcel(Parcel in) {
            return new SurveyListData(in);
        }

        @Override
        public SurveyListData[] newArray(int size) {
            return new SurveyListData[size];
        }
    };

    public String getSnapshotID() {
        return snapshotID;
    }

    public void setSnapshotID(String snapshotID) {
        this.snapshotID = snapshotID;
    }

    public String getSnapshotPath() {
        return snapshotPath;
    }

    public void setSnapshotPath(String snapshotPath) {
        this.snapshotPath = snapshotPath;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isOngoing() {
        return isOngoing;
    }

    public void setOngoing(boolean ongoing) {
        isOngoing = ongoing;
    }
}
