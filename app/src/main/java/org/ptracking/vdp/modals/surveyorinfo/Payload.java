package org.ptracking.vdp.modals.surveyorinfo;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Payload {

    @SerializedName("PANCHAYAT_NAME")
    @Expose
    private String pANCHAYATNAME;
    @SerializedName("PANCHAYAT_CODE")
    @Expose
    private String pANCHAYATCODE;
    @SerializedName("BLOCK_NAME")
    @Expose
    private String bLOCKNAME;
    @SerializedName("BLOCK_CODE")
    @Expose
    private String bLOCKCODE;
    @SerializedName("DISTRICT_NAME")
    @Expose
    private String dISTRICTNAME;
    @SerializedName("DISTRICT_CODE")
    @Expose
    private String dISTRICTCODE;
    @SerializedName("SURVEYOR_CODE")
    @Expose
    private String sURVEYORCODE;
    @SerializedName("SURVEYOR_NAME")
    @Expose
    private String sURVEYORNAME;
    @SerializedName("SURVEY")
    @Expose
    private String sURVEY;
    @SerializedName("HABITATION_NAME")
    @Expose
    private List<String> hABITATIONNAME = null;
    @SerializedName("surveyId")
    @Expose
    private String surveyId;

    public String getPANCHAYATNAME() {
        return pANCHAYATNAME;
    }

    public void setPANCHAYATNAME(String pANCHAYATNAME) {
        this.pANCHAYATNAME = pANCHAYATNAME;
    }

    public String getPANCHAYATCODE() {
        return pANCHAYATCODE;
    }

    public void setPANCHAYATCODE(String pANCHAYATCODE) {
        this.pANCHAYATCODE = pANCHAYATCODE;
    }

    public String getBLOCKNAME() {
        return bLOCKNAME;
    }

    public void setBLOCKNAME(String bLOCKNAME) {
        this.bLOCKNAME = bLOCKNAME;
    }

    public String getBLOCKCODE() {
        return bLOCKCODE;
    }

    public void setBLOCKCODE(String bLOCKCODE) {
        this.bLOCKCODE = bLOCKCODE;
    }

    public String getDISTRICTNAME() {
        return dISTRICTNAME;
    }

    public void setDISTRICTNAME(String dISTRICTNAME) {
        this.dISTRICTNAME = dISTRICTNAME;
    }

    public String getDISTRICTCODE() {
        return dISTRICTCODE;
    }

    public void setDISTRICTCODE(String dISTRICTCODE) {
        this.dISTRICTCODE = dISTRICTCODE;
    }

    public String getSURVEYORCODE() {
        return sURVEYORCODE;
    }

    public void setSURVEYORCODE(String sURVEYORCODE) {
        this.sURVEYORCODE = sURVEYORCODE;
    }

    public String getSURVEYORNAME() {
        return sURVEYORNAME;
    }

    public void setSURVEYORNAME(String sURVEYORNAME) {
        this.sURVEYORNAME = sURVEYORNAME;
    }

    public String getSURVEY() {
        return sURVEY;
    }

    public void setSURVEY(String sURVEY) {
        this.sURVEY = sURVEY;
    }

    public List<String> getHABITATIONNAME() {
        return hABITATIONNAME;
    }

    public void setHABITATIONNAME(List<String> hABITATIONNAME) {
        this.hABITATIONNAME = hABITATIONNAME;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("PANCHAYAT_NAME", pANCHAYATNAME);
        jsonObject.addProperty("PANCHAYAT_CODE", pANCHAYATCODE);
        jsonObject.addProperty("BLOCK_NAME", bLOCKNAME);
        jsonObject.addProperty("BLOCK_CODE", bLOCKCODE);
        jsonObject.addProperty("DISTRICT_NAME", dISTRICTNAME);
        jsonObject.addProperty("DISTRICT_CODE", dISTRICTCODE);
        jsonObject.addProperty("SURVEYOR_CODE", sURVEYORCODE);
        jsonObject.addProperty("SURVEYOR_NAME", sURVEYORNAME);
        jsonObject.addProperty("SURVEY", sURVEY);
        jsonObject.addProperty("surveyId", surveyId);

        JsonArray habitationArray = new JsonArray();
        for (String habitationName : hABITATIONNAME) {
            habitationArray.add(habitationName);
        }

        jsonObject.add("HABITATION_NAME", habitationArray);

        return jsonObject;
    }

}
