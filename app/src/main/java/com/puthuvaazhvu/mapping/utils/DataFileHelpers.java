package com.puthuvaazhvu.mapping.utils;

import android.os.Environment;

import com.puthuvaazhvu.mapping.other.Constants;

import java.io.File;
import java.io.IOException;

import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/19/17.
 */

public class DataFileHelpers {

    public static File getSurveyDataFile(String surveyID, boolean forRead) {
        File dir = getSurveyDataDirectory(forRead);

        try {
            if (dir != null) {
                File file = new File(dir, surveyID + ".json");

                file.createNewFile();

                Timber.i("Name of the file to be saved is : " + file.getAbsolutePath());

                return file;
            }
        } catch (IOException e) {
            Timber.e("Error creating file" + e.getMessage());
        }

        return null;
    }

    public static File getFileToDumpSurvey(String surveyID, boolean forRead) {
        File dataDirectory = getDataDirectory(forRead);

        try {
            if (dataDirectory != null) {
                String fileName = "survey_" + surveyID + ".json";
                File file = new File(dataDirectory, fileName);
                file.createNewFile();
                Timber.i("Name of the file to be saved is : " + fileName);
                return file;
            }
        } catch (IOException e) {
            Timber.e("Error creating file" + e.getMessage());
        }
        return null;
    }

    public static File getSurveyInfoFile(boolean forRead) {
        File dataDir = getSurveyDataDirectory(forRead);
        String fileName = "";

        try {
            if (dataDir != null) {
                fileName = Constants.SURVEY_INFO_FILE_NAME;
                File file = new File(dataDir, fileName);
                file.createNewFile();
                return file;
            }
        } catch (IOException e) {
            Timber.e("Error creating " + fileName + " " + e.getMessage());
        }

        return null;
    }

    public static File getSurveyDataDirectory(boolean forRead) {
        File dataDirectory = getDataDirectory(forRead);

        if (dataDirectory != null) {
            String path = dataDirectory.getAbsolutePath() + File.separator + Constants.SURVEY_DATA_DIR;
            File surveyDataDir = new File(path);
            if (!surveyDataDir.exists()) {
                boolean result = surveyDataDir.mkdirs();
                if (result) {
                    Timber.i("Survey data dir created successfully. " + surveyDataDir.getAbsolutePath());
                } else {
                    Timber.i("Failed to create Survey data dir. " + surveyDataDir.getAbsolutePath());
                }
            }
            return surveyDataDir;
        }

        return null;
    }

    public static File getDataDirectory(boolean forRead) {
        boolean positive = false;
        File dataDir = null;

        if ((forRead && isExternalStorageReadable()) ||
                (!forRead && isExternalStorageWritable())) {

            File root = Environment.getExternalStorageDirectory();
            positive = root.exists();
            if (!positive) {
                positive = root.mkdir();
            }

            dataDir = new File(root, Constants.DATA_DIR);
            if (positive && !dataDir.exists()) {
                positive = dataDir.mkdir();

                if (positive) {
                    Timber.i("Created data directory at : " + dataDir.getAbsolutePath());
                }
            }

            if (!positive)
                Timber.e("Error creating data directory at : " + dataDir.getAbsolutePath());

        }

        return positive ? dataDir : null;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
