package com.puthuvaazhvu.mapping.utils.info_file;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Option;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.DataFileHelpers;
import com.puthuvaazhvu.mapping.utils.Optional;
import com.puthuvaazhvu.mapping.utils.info_file.modals.AnswerDataModal;
import com.puthuvaazhvu.mapping.utils.info_file.modals.AnswersInfoFileDataModal;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import io.reactivex.MaybeSource;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by muthuveerappans on 10/31/17.
 */

@Deprecated
public class AnswersInfoFile extends InfoFileBase {
    private final int version = Constants.Versions.ANSWERS_INFO_VERSION;

    public AnswersInfoFile(GetFromFile getFromFile, SaveToFile saveToFile) {
        super(getFromFile, saveToFile);
    }

    @Override
    public File getInfoFile() {
        return DataFileHelpers.getAnswersInfoFile(false);
    }

    public Single<AnswersInfoFileDataModal> getInfoJsonParsed() {
        return getContentsOfFile().map(new Function<JsonObject, AnswersInfoFileDataModal>() {
            @Override
            public AnswersInfoFileDataModal apply(@NonNull JsonObject jsonObject) throws Exception {
                return new AnswersInfoFileDataModal(jsonObject);
            }
        });
    }

    public Single<Optional> updateSurvey(final AnswerDataModal answerDataModal) {
        ArrayList<AnswerDataModal> answerDataModals = new ArrayList<>();
        answerDataModals.add(answerDataModal);

        return updateListOfSurveys(answerDataModals);
    }

    public Single<Optional> updateListOfSurveys(final ArrayList<AnswerDataModal> answerDataModals) {
        return getContentsOfFile()
                .flatMap(new Function<JsonObject, SingleSource<? extends Optional>>() {
                             @Override
                             public SingleSource<? extends Optional> apply(@NonNull JsonObject jsonObject) throws Exception {

                                 AnswersInfoFileDataModal existing = new AnswersInfoFileDataModal(jsonObject);

                                 if (existing.getVersion() != version) {
                                     // remove the info.json file
                                     AnswersInfoFileDataModal fileDataModal = new AnswersInfoFileDataModal(answerDataModals,
                                             Constants.Versions.ANSWERS_INFO_VERSION);
                                     return saveFile(fileDataModal.getAsJson());
                                 } else {
                                     existing.updateWithNew(answerDataModals);
                                     return saveFile(existing.getAsJson());
                                 }
                             }
                         }
                );
    }
}
