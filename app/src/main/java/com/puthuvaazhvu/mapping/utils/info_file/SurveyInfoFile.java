package com.puthuvaazhvu.mapping.utils.info_file;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.DataFileHelpers;
import com.puthuvaazhvu.mapping.utils.Optional;
import com.puthuvaazhvu.mapping.utils.info_file.modals.DataModal;
import com.puthuvaazhvu.mapping.utils.info_file.modals.SurveyInfoFileDataModal;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;

import java.io.File;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class SurveyInfoFile extends InfoFileBase {
    private final int version = Constants.Versions.SURVEY_INFO_VERSION;

    public SurveyInfoFile(GetFromFile getFromFile, SaveToFile saveToFile) {
        super(getFromFile, saveToFile);
    }

    @Override
    public File getInfoFile() {
        return DataFileHelpers.getSurveyInfoFile(false);
    }

    public Single<SurveyInfoFileDataModal> getInfoJsonParsed() {
        return getContentsOfFile().map(new Function<JsonObject, SurveyInfoFileDataModal>() {
            @Override
            public SurveyInfoFileDataModal apply(@NonNull JsonObject jsonObject) throws Exception {
                return new SurveyInfoFileDataModal(jsonObject);
            }
        });
    }

    public Single<Optional> updateListOfSurveys(final List<DataModal> data) {
        return getContentsOfFile()
                .flatMap(new Function<JsonObject, SingleSource<Optional>>() {
                    @Override
                    public SingleSource<Optional> apply(@NonNull JsonObject jsonObject) throws Exception {

                        SurveyInfoFileDataModal existing = new SurveyInfoFileDataModal(jsonObject);

                        if (existing.getVersion() != version) {
                            SurveyInfoFileDataModal surveyInfoFileDataModal
                                    = new SurveyInfoFileDataModal(version, data);
                            return saveFile(surveyInfoFileDataModal.getAsJson());
                        } else {
                            existing.updateWithNew(data);
                            return saveFile(existing.getAsJson());
                        }
                    }
                });
    }
}
