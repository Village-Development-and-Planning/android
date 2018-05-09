package com.puthuvaazhvu.mapping.upload;

import com.puthuvaazhvu.mapping.modals.Upload;

import java.io.File;

/**
 * Created by muthuveerappans on 09/05/18.
 */

public class UploadResult {
    private String _id;
    private String name;
    private String survey;
    private String checksum;
    private String modifiedAt;
    private File file;
    private boolean existing;
    private boolean failure;
    private String message = "";

    public boolean isFailure() {
        return failure;
    }

    public void setFailure(boolean failure) {
        this.failure = failure;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurvey() {
        return survey;
    }

    public void setSurvey(String survey) {
        this.survey = survey;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public boolean isExisting() {
        return existing;
    }

    public void setExisting(boolean existing) {
        this.existing = existing;
    }

    public static UploadResult adapter(Upload upload, File file) {
        UploadResult uploadResult = new UploadResult();
        uploadResult._id = upload.get_id();
        uploadResult.name = upload.getName();
        uploadResult.survey = upload.getSurvey();
        uploadResult.checksum = upload.getChecksum();
        uploadResult.modifiedAt = upload.getModifiedAt();
        uploadResult.existing = upload.isExisting();
        uploadResult.file = file;
        return uploadResult;
    }
}
