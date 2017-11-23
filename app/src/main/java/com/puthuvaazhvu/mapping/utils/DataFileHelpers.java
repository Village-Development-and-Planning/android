package com.puthuvaazhvu.mapping.utils;

import android.os.Environment;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonObject;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.info_file.AnswersInfoFile;
import com.puthuvaazhvu.mapping.utils.info_file.modals.AnswerDataModal;
import com.puthuvaazhvu.mapping.utils.info_file.modals.AnswersInfoFileDataModal;
import com.puthuvaazhvu.mapping.utils.storage.GetFromFile;
import com.puthuvaazhvu.mapping.utils.storage.SaveToFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/19/17.
 */

public class DataFileHelpers {

    public static Single<Optional> dumpSurvey(
            Survey survey,
            final String pathToLastAnsweredQuestion,
            final boolean isSurveyIncomplete,
            final boolean isSurveyDone
    ) {
        JsonObject resultSurveyJson = survey.getAsJson().getAsJsonObject();
        String toSave = resultSurveyJson.toString();

        Timber.i("Survey dump: \n" + resultSurveyJson.toString());

        long random_uuid = System.currentTimeMillis();
        String fileName = survey.getId() + "_" + random_uuid;

        File fileToSave = getFileToDumpAnswers(fileName, false);

        final String surveyName = survey.getName();
        String fileNameComponents[] = fileName.split("_");
        final String surveyID = fileNameComponents[0];
        final String uuid = fileNameComponents[1];

        final SaveToFile saveToFile = SaveToFile.getInstance();
        final GetFromFile getFromFile = GetFromFile.getInstance();
        final AnswersInfoFile answersInfoFile = new AnswersInfoFile(getFromFile, saveToFile);

        return saveToFile.execute(toSave, fileToSave)
                .flatMap(new Function<Optional, SingleSource<? extends Optional>>() {
                    @Override
                    public SingleSource<? extends Optional> apply(@NonNull Optional optional) throws Exception {

                        ArrayList<AnswerDataModal.Snapshot> snapshots = new ArrayList<>();
                        snapshots.add(
                                new AnswerDataModal.Snapshot(
                                        uuid,
                                        surveyName,
                                        pathToLastAnsweredQuestion,
                                        isSurveyIncomplete,
                                        "" + System.currentTimeMillis()
                                )
                        );

                        AnswerDataModal answerDataModal = new AnswerDataModal(surveyID, isSurveyDone, snapshots);

                        return answersInfoFile.updateSurvey(answerDataModal);

                    }
                });
    }

    public static File getSurveyFromSurveyDir(String fileName) {
        if (!fileName.contains("json")) {
            fileName += ".json";
        }
        File dataDir = getSurveyDataDirectory(true);
        return new File(dataDir, fileName);
    }

    public static File getSurveyFromAnswersDir(String fileName) {
        if (!fileName.contains("json")) {
            fileName += ".json";
        }
        File dataDir = getAnswersDataDirectory(true);
        return new File(dataDir, fileName);
    }

    public static String removeExt(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static File getSurveyDataFile(String surveyID, boolean forRead) {
        File dataDir = getSurveyDataDirectory(forRead);
        String fileName = surveyID + ".json";
        return createFileInDir(dataDir, fileName);
    }

    public static File getFileToDumpAnswers(String fileName, boolean forRead) {
        File dataDir = getAnswersDataDirectory(forRead);
        fileName = fileName + ".json";
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
