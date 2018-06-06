package com.puthuvaazhvu.mapping.upload;

import android.content.Context;

import com.puthuvaazhvu.mapping.data.AnswersRepository;
import com.puthuvaazhvu.mapping.filestorage.io.AnswerIO;
import com.puthuvaazhvu.mapping.filestorage.StorageUtils;
import com.puthuvaazhvu.mapping.filestorage.modals.DataInfo;
import com.puthuvaazhvu.mapping.other.Constants;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by muthuveerappans on 10/05/18.
 */

public class AnswersUploadTask extends FileUploadTask {
    private final AnswersRepository answersRepository;
    private final AnswerIO answerIO;

    public AnswersUploadTask(Context context, String username, String password, FileUploadResultReceiver receiver) {
        super(username, password, receiver);

        answerIO = new AnswerIO();
        answersRepository = new AnswersRepository(context, username, password);
    }

    @Override
    protected Observable<List<File>> getFiles() {
        return answersRepository.getAnswerFiles();
    }

    @Override
    protected Observable<String> onPostExecute(final UploadResult result) {
        String answerId = result.getFile().getName().replaceAll("[.][a-zA-z]+$", "");
        return answerIO.delete(answerId, getUsername())
                .map(new Function<DataInfo, String>() {
                    @Override
                    public String apply(DataInfo dataInfo) throws Exception {
                        return "File " + result.getFile().getName() + " deleted successfully. ";
                    }
                });
    }

}
