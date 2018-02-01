package com.puthuvaazhvu.mapping.modals;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class SurveyAPIInfo {
    private final String _id;
    private final String name;
    private final String description;
    private final String modifiedAt;
    private final boolean enabled;

    public SurveyAPIInfo(String _id, String name, String description, String modifiedAt, boolean enabled) {
        this._id = _id;
        this.name = name;
        this.description = description;
        this.modifiedAt = modifiedAt;
        this.enabled = enabled;
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

    public boolean isEnabled() {
        return enabled;
    }
}
