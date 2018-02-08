package com.puthuvaazhvu.mapping.utils.saving;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.puthuvaazhvu.mapping.modals.Survey;
import com.puthuvaazhvu.mapping.other.Constants;
import com.puthuvaazhvu.mapping.utils.FileUtils;
import com.puthuvaazhvu.mapping.utils.saving.modals.AnswersInfo;
import com.puthuvaazhvu.mapping.utils.saving.modals.SurveyInfo;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Created by muthuveerappans on 31/01/18.
 */

public class SurveyIOUtils extends IOUtilsBase {
    private static SurveyIOUtils surveyIOUtils;

    public static SurveyIOUtils getInstance() {
        if (surveyIOUtils == null) {
            surveyIOUtils = new SurveyIOUtils();
        }
        return surveyIOUtils;
    }

    private SurveyIOUtils() {
    }

    public Observable<SurveyInfo> saveSurvey(final String surveyString) {
        return Observable.just(surveyString)
                .flatMap(new Function<String, ObservableSource<Survey>>() {
                    @Override
                    public ObservableSource<Survey> apply(String survey) throws Exception {
                        JsonParser jsonParser = new JsonParser();
                        JsonElement jsonElement = jsonParser.parse(survey);

                        if (jsonElement == null) {
                            throw new Exception("Error parsing the survey");
                        }

                        final Survey s = new Survey(jsonElement.getAsJsonObject());

                        String path = getRelativePathToSurveysDir() + File.separator + s.getId();
                        return FileUtils.saveToFileFromPath(path, survey)
                                .map(new Function<File, Survey>() {
                                    @Override
                                    public Survey apply(File file) throws Exception {
                                        return s;
                                    }
                                });
                    }
                })

                // updating the survey
                .flatMap(new Function<Survey, ObservableSource<SurveyInfo>>() {
                    @Override
                    public ObservableSource<SurveyInfo> apply(final Survey survey) throws Exception {
                        if (FileUtils.fileExists(getRelativePathToSurveysInfoFile())) {
                            return readSurveysInfoFile()
                                    .map(new Function<SurveyInfo, SurveyInfo>() {
                                        @Override
                                        public SurveyInfo apply(SurveyInfo surveyInfo) throws Exception {
                                            SurveyInfo.Survey s = new SurveyInfo.Survey();
                                            s.setSurveyID(survey.getId());
                                            s.setSurveyName(survey.getName());
                                            s.setTimeStamp(System.currentTimeMillis());

                                            boolean shouldAddSurvey = true;
                                            for (SurveyInfo.Survey currentSurvey : surveyInfo.getSurveys()) {
                                                if (currentSurvey.getSurveyID().equals(s.getSurveyID())) {
                                                    // survey already present
                                                    shouldAddSurvey = false;
                                                }
                                            }

                                            if (shouldAddSurvey)
                                                surveyInfo.getSurveys().add(s);

                                            return surveyInfo;
                                        }
                                    });
                        } else {
                            // create a info.json file
                            return Observable.create(new ObservableOnSubscribe<SurveyInfo>() {
                                @Override
                                public void subscribe(ObservableEmitter<SurveyInfo> e) throws Exception {
                                    JsonObject jsonObject = new JsonObject();
                                    jsonObject.addProperty("version", Constants.Versions.SURVEY_INFO_VERSION);
                                    JsonArray surveysArrayDummy = new JsonArray();
                                    jsonObject.add("surveys", surveysArrayDummy);

                                    SurveyInfo surveyInfo = parseSurveyInfo(jsonObject);

                                    SurveyInfo.Survey s = new SurveyInfo.Survey();
                                    s.setSurveyID(survey.getId());
                                    s.setSurveyName(survey.getName());
                                    s.setTimeStamp(System.currentTimeMillis());

                                    surveyInfo.getSurveys().add(s);

                                    e.onNext(surveyInfo);
                                    e.onComplete();
                                }
                            });
                        }
                    }
                })

                // saving the survey
                .flatMap(new Function<SurveyInfo, ObservableSource<SurveyInfo>>() {
                    @Override
                    public ObservableSource<SurveyInfo> apply(SurveyInfo surveyInfo) throws Exception {
                        return FileUtils.saveToFileFromPath(
                                getRelativePathToSurveysInfoFile(),
                                surveyInfo.getAsJsonString()
                        ).flatMap(new Function<File, ObservableSource<SurveyInfo>>() {
                            @Override
                            public ObservableSource<SurveyInfo> apply(File file) throws Exception {
                                return readSurveysInfoFile();
                            }
                        });
                    }
                });
    }

    public Observable<SurveyInfo> readSurveysInfoFile() {
        return readFileAsJson(getRelativePathToSurveysInfoFile())
                .map(new Function<JsonObject, SurveyInfo>() {
                    @Override
                    public SurveyInfo apply(JsonObject jsonObject) throws Exception {
                        return parseSurveyInfo(jsonObject);
                    }
                });
    }

    public Observable<Survey> readSurvey(String surveyID) {
        return readFileAsJson(getRelativePathToSurveysDir() + File.separator + surveyID)
                .map(new Function<JsonObject, Survey>() {
                    @Override
                    public Survey apply(JsonObject jsonObject) throws Exception {
                        return new Survey(jsonObject);
                    }
                });
    }

    public String getRelativePathToSurveysInfoFile() {
        return getRelativePathToSurveysDir() + File.separator + Constants.INFO_FILE_NAME;
    }

    public String getRelativePathToSurveysDir() {
        return File.separator + Constants.SURVEY_DATA_DIR;
    }

    public SurveyInfo parseSurveyInfo(JsonObject jsonObject) {
        Gson gson = new Gson();
        return gson.fromJson(jsonObject, SurveyInfo.class);
    }
}
