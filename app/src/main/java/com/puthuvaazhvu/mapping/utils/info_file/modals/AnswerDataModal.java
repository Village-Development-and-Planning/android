package com.puthuvaazhvu.mapping.utils.info_file.modals;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.utils.JsonHelper;

import java.util.ArrayList;

/**
 * Created by muthuveerappans on 11/11/17.
 */

public class AnswerDataModal {
    private String id;
    private ArrayList<Snapshot> snapshots;

    public AnswerDataModal(JsonObject jsonObject) {
        id = JsonHelper.getString(jsonObject, "_id");
        JsonArray array = JsonHelper.getJsonArray(jsonObject, "snap_shots");

        snapshots = new ArrayList<>();

        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                snapshots.add(new Snapshot(array.get(i).getAsJsonObject()));
            }
        }
    }

    public AnswerDataModal(String id, boolean isDone, ArrayList<Snapshot> snapshots) {
        this.id = id;
        this.snapshots = snapshots;
    }

    public void updateOther(AnswerDataModal other) {
        this.id = other.id;
        //this.snapshots.clear();
        this.snapshots.addAll(other.snapshots);
    }

    public String getId() {
        return id;
    }

    public ArrayList<Snapshot> getSnapshots() {
        return snapshots;
    }

    public Snapshot getLatestSnapShot() {
        if (snapshots.size() <= 0) {
            return null;
        }

        Snapshot result = snapshots.get(0);

        for (int i = 1; i < snapshots.size(); i++) {
            Snapshot snapshot = snapshots.get(i);

            if (snapshot.getTimestamp() > result.getTimestamp()) {
                result = snapshot;
            }
        }

        return result;
    }

    public JsonObject getAsJson() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("_id", id);

        JsonArray snapshotsJsonArray = new JsonArray();
        for (Snapshot snapshot : this.snapshots) {
            snapshotsJsonArray.add(snapshot.getAsJson());
        }

        jsonObject.add("snap_shots", snapshotsJsonArray);

        return jsonObject;
    }

    public static class Snapshot {
        private final String snapshot_id;
        private final String survey_name;
        private final String path_to_last_question;
        private final boolean is_incomplete;
        private final String timestamp;

        public Snapshot(JsonObject jsonObject) {
            snapshot_id = JsonHelper.getString(jsonObject, "snapshot_id");
            survey_name = JsonHelper.getString(jsonObject, "survey_name");
            path_to_last_question = JsonHelper.getString(jsonObject, "path_to_last_question");
            is_incomplete = JsonHelper.getBoolean(jsonObject, "is_incomplete");
            timestamp = JsonHelper.getString(jsonObject, "timestamp");
        }

        public Snapshot(
                String snapshot_id,
                String survey_name,
                String path_to_last_question,
                boolean is_incomplete,
                String timestamp
        ) {
            this.snapshot_id = snapshot_id;
            this.survey_name = survey_name;
            this.path_to_last_question = path_to_last_question;
            this.is_incomplete = is_incomplete;
            this.timestamp = timestamp;
        }

        public String getSnapshotId() {
            return snapshot_id;
        }

        public String getSurvey_name() {
            return survey_name;
        }

        public String getPathToLastQuestion() {
            return path_to_last_question;
        }

        public boolean isIncomplete() {
            return is_incomplete;
        }

        public Long getTimestamp() {
            return Long.parseLong(timestamp);
        }

        public JsonObject getAsJson() {
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("snapshot_id", snapshot_id);
            jsonObject.addProperty("survey_name", survey_name);
            jsonObject.addProperty("path_to_last_question", path_to_last_question);
            jsonObject.addProperty("is_incomplete", is_incomplete);
            jsonObject.addProperty("timestamp", timestamp);

            return jsonObject;
        }
    }
}
