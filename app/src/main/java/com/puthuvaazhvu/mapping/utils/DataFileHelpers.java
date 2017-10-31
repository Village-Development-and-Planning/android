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
        File dataDir = getSurveyDataDirectory(forRead);
        String fileName = surveyID + ".json";
        return createFileInDir(dataDir, fileName);
    }

    public static File getFileToDumpAnswers(String surveyID, boolean forRead) {
        File dataDir = getAnswersDataDirectory(forRead);
        long currentTime = System.currentTimeMillis();
        String fileName = surveyID + "_" + currentTime + ".json";
        return createFileInDir(dataDir, fileName);
    }

    public static File getSurveyInfoFile(boolean forRead) {
        File dataDir = getSurveyDataDirectory(forRead);
        String fileName = Constants.INFO_FILE_NAME;
        return createFileInDir(dataDir, fileName);
    }

    public static File getAnswersInfoFile(boolean forRead) {
        File dataDir = getAnswersDataDirectory(forRead);
        String fileName = Constants.INFO_FILE_NAME;
        return createFileInDir(dataDir, fileName);
    }

    public static File getAnswersDataDirectory(boolean forRead) {
        return createDirInRoot(forRead, Constants.ANSWERS_DATA_DIR);
    }

    public static File getSurveyDataDirectory(boolean forRead) {
        return createDirInRoot(forRead, Constants.SURVEY_DATA_DIR);
    }

    private static File createFileInDir(File dir, String fileName) {
        try {
            if (dir != null) {
                File file = new File(dir, fileName);
                file.createNewFile();
                return file;
            } else {
                Timber.e("The dir provided is null.");
            }
        } catch (IOException e) {
            Timber.e("Error creating " + fileName + " " + e.getMessage());
        }
        return null;
    }

    private static File createDirInRoot(boolean forRead, String dirName) {
        File dataDirectory = getDataDirectory(forRead);

        if (dataDirectory != null) {
            String path = dataDirectory.getAbsolutePath() + File.separator + dirName;
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

    private static File getDataDirectory(boolean forRead) {
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
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
