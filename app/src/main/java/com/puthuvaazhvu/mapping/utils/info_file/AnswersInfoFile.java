package com.puthuvaazhvu.mapping.utils.info_file;

import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.utils.DataFileHelpers;
import com.puthuvaazhvu.mapping.utils.info_file.modals.AnswersInfoFileData;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Created by muthuveerappans on 10/31/17.
 */

public class AnswersInfoFile extends InfoFileBase {

    public AnswersInfoFile(GetFromFile getFromFile, SaveToFile saveToFile) {
        super(getFromFile, saveToFile);
    }

    @Override
    public File getInfoFile() {
        return DataFileHelpers.getAnswersInfoFile(false);
    }

    public Callable<AnswersInfoFileData> getInfoJsonParsed() throws ExecutionException, InterruptedException {
        return new Callable<AnswersInfoFileData>() {
            @Override
            public AnswersInfoFileData call() throws Exception {
                JsonObject rootJson = pool.submit(getContentsOfFile()).get();
                return new AnswersInfoFileData(rootJson);
            }
        };
    }

    public Callable<Void> updateListOfSurveys(final AnswersInfoFileData data)
            throws IOException, ExecutionException, InterruptedException {

        JsonObject rootJson = pool.submit(getContentsOfFile()).get();
        AnswersInfoFileData old = new AnswersInfoFileData(rootJson);

        old.updateWithNew(data);

        // save
        return saveFile(old.getAsJson());
    }
}
