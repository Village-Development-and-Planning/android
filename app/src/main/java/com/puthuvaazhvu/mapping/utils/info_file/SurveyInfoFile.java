package com.puthuvaazhvu.mapping.utils.info_file;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.utils.DataFileHelpers;
import com.puthuvaazhvu.mapping.utils.Optional;
import com.puthuvaazhvu.mapping.utils.info_file.modals.SavedSurveyInfoFileDataModal;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class SurveyInfoFile extends InfoFileBase {
    public SurveyInfoFile(GetFromFile getFromFile, SaveToFile saveToFile) {
        super(getFromFile, saveToFile);
    }

    @Override
    public File getInfoFile() {
        return DataFileHelpers.getSurveyInfoFile(false);
    }

    public Single<SavedSurveyInfoFileDataModal> getInfoJsonParsed() {
        return getContentsOfFile().map(new Function<JsonObject, SavedSurveyInfoFileDataModal>() {
            @Override
            public SavedSurveyInfoFileDataModal apply(@NonNull JsonObject jsonObject) throws Exception {
                return new SavedSurveyInfoFileDataModal(jsonObject);
            }
        });
    }

    public Single<Optional> updateListOfSurveys(final SavedSurveyInfoFileDataModal newData) {
        return getContentsOfFile()
                .flatMap(new Function<JsonObject, SingleSource<Optional>>() {
                    @Override
                    public SingleSource<Optional> apply(@NonNull JsonObject jsonObject) throws Exception {
                        SavedSurveyInfoFileDataModal existing = new SavedSurveyInfoFileDataModal(jsonObject);
                        existing.updateWithNew(newData);
                        return saveFile(existing.getAsJson());
                    }
                });
    }
}
