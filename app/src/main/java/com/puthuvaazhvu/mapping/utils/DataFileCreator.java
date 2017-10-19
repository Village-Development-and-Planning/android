package com.puthuvaazhvu.mapping.utils;

import java.io.File;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/19/17.
 */

public class DataFileCreator {
    public static File getFileToDumpSurvey(String surveyID, boolean forRead) {
        File dataDirectory = Utils.getDataDirectory(forRead);

        if (dataDirectory != null) {
            String fileName = "survey_" + surveyID + ".json";
            Timber.i("Name of the file to be saved is : " + fileName);
            return new File(dataDirectory, fileName);
        }
        return null;
    }
}
