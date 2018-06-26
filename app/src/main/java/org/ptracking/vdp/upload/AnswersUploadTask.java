package org.ptracking.vdp.upload;

import android.content.Context;

import org.ptracking.vdp.filestorage.io.DataInfoIO;
import org.ptracking.vdp.filestorage.modals.SurveyorData;
import org.ptracking.vdp.other.Constants;
import org.ptracking.vdp.repository.AnswersRepository;
import org.ptracking.vdp.filestorage.io.AnswerIO;
import org.ptracking.vdp.filestorage.modals.DataInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/05/18.
 */

public class AnswersUploadTask extends FileUploadTask {
    private final AnswersRepository answersRepository;
    private final AnswerIO answerIO;
    private final DataInfoIO dataInfoIO;
    private final List<String> uploadedAnswerIds = new ArrayList<>();

    public AnswersUploadTask(Context context, String username, String password, FileUploadResultReceiver receiver) {
        super(username, password, receiver);

        answerIO = new AnswerIO();
        dataInfoIO = new DataInfoIO();
        answersRepository = new AnswersRepository(context, username, password);
    }

    @Override
    protected Observable<List<File>> getFiles() {
        return answersRepository.getAnswerFiles();
    }

    @Override
    protected Observable<String> onFileUploaded(final UploadResult result) {
        final String answerId = result.getFile().getName().replaceAll("[.][a-zA-z]+$", "");
        return answerIO.deleteFile(answerId)
                .map(new Function<Boolean, String>() {
                    @Override
                    public String apply(Boolean b) throws Exception {
                        if (!b) {
                            return "Error deleting the file " + result.getFile().getName();
                        }
                        uploadedAnswerIds.add(answerId);
                        return "File " + result.getFile().getName() + " deleted successfully. ";
                    }
                });
    }

    @Override
    protected void onPostExecute() {
        dataInfoIO.read()
                .observeOn(Schedulers.io())
                .flatMap(new Function<DataInfo, ObservableSource<DataInfo>>() {
                    @Override
                    public ObservableSource<DataInfo> apply(DataInfo dataInfo) throws Exception {
                        SurveyorData surveyorData = dataInfo.getSurveyorData(getUsername());
                        if (surveyorData != null) {
                            for (String id : uploadedAnswerIds) {
                                boolean result = surveyorData.getAnswersInfo().removeAnswer(id);
                                Timber.i("Updated " + Constants.DATA_INFO_FILE + " for answer deletion. Status " + result);
                            }
                        }

                        return dataInfoIO.save(dataInfo)
                                .flatMap(new Function<File, ObservableSource<DataInfo>>() {
                                    @Override
                                    public ObservableSource<DataInfo> apply(File file) throws Exception {
                                        return dataInfoIO.read();
                                    }
                                });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DataInfo>() {
                    @Override
                    public void accept(DataInfo dataInfo) throws Exception {
                        Timber.i(Constants.DATA_INFO_FILE + " contents " + dataInfo.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e(throwable);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        Timber.i(Constants.DATA_INFO_FILE + " updated successfully.");
                        resetState();
                    }
                });
    }

    private void resetState() {
        uploadedAnswerIds.clear();
    }
}
