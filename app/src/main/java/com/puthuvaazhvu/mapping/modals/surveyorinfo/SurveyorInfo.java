package com.puthuvaazhvu.mapping.modals.surveyorinfo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by muthuveerappans on 05/06/18.
 */

public class SurveyorInfo {
    @SerializedName("surveyId")
    @Expose
    private String surveyId;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("payload")
    @Expose
    private Payload payload;
    @SerializedName("roles")
    @Expose
    private List<String> roles = null;
    @SerializedName("modifiedAt")
    @Expose
    private String modifiedAt;

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("surveyId", surveyId);
        jsonObject.addProperty("code", code);
        jsonObject.addProperty("_id", id);
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("username", username);
        jsonObject.add("payload", payload.toJson());
        jsonObject.addProperty("modifiedAt", modifiedAt);

        JsonArray roles = new JsonArray();
        for (String role : this.roles) roles.add(role);
        jsonObject.add("roles", roles);

        return jsonObject;
    }
}

