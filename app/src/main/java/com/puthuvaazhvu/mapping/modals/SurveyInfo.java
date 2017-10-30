package com.puthuvaazhvu.mapping.modals;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class SurveyInfo {
    private final String _id;
    private final String name;
    private final String description;
    private final String modifiedAt;

    public SurveyInfo(String _id, String name, String description, String modifiedAt) {
        this._id = _id;
        this.name = name;
        this.description = description;
        this.modifiedAt = modifiedAt;
    }

    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }
}
