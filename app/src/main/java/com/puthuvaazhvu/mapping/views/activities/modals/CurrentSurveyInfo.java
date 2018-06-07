package com.puthuvaazhvu.mapping.views.activities.modals;

import android.os.Parcel;
import android.os.Parcelable;

import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.repository.SnapshotRepositoryData;

public class CurrentSurveyInfo implements Parcelable {
    private String id;
    private String name;
    private boolean isChecked;
    private int count;
    private boolean isOngoing;
    private String snapshotPath; // will be null if the survey is new
    private String snapshotID;

    public CurrentSurveyInfo(String id, String name, int count, boolean isOngoing, String snapshotPath, String snapshotID) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.isOngoing = isOngoing;
        this.snapshotPath = snapshotPath;
        this.snapshotID = snapshotID;
    }

    protected CurrentSurveyInfo(Parcel in) {
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

    public static final Creator<CurrentSurveyInfo> CREATOR = new Creator<CurrentSurveyInfo>() {
        @Override
        public CurrentSurveyInfo createFromParcel(Parcel in) {
            return new CurrentSurveyInfo(in);
        }

        @Override
        public CurrentSurveyInfo[] newArray(int size) {
            return new CurrentSurveyInfo[size];
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

    public static CurrentSurveyInfo adapterSurvey(Survey survey) {
        return new CurrentSurveyInfo(survey.getId(), survey.getName(), 0, false, null, null);
    }

    public static CurrentSurveyInfo adapterSnapshotRepositoryData(SnapshotRepositoryData snapshotRepositoryData) {
        return new CurrentSurveyInfo(
                snapshotRepositoryData.getSurvey().getId(),
                snapshotRepositoryData.getSurvey().getName(),
                0,
                true,
                snapshotRepositoryData.getSnapshot().getPathToLastQuestion(),
                snapshotRepositoryData.getSnapshot().getSnapshotID()
        );
    }
}
