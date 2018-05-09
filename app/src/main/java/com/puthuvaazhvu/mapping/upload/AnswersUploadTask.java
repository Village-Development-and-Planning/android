package com.puthuvaazhvu.mapping.upload;

import com.puthuvaazhvu.mapping.filestorage.AnswerIO;
import com.puthuvaazhvu.mapping.filestorage.StorageUtils;
import com.puthuvaazhvu.mapping.other.Constants;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.root;

/**
 * Created by muthuveerappans on 10/05/18.
 */

public class AnswersUploadTask extends FileUploadTask {

    public AnswersUploadTask(String username, String password, FileUploadResultReceiver receiver) {
        super(username, password, receiver);
    }

    @Override
    protected Observable<List<File>> getFiles() {
        return StorageUtils.getFilesFromDir(getDir());
    }

    @Override
    protected Observable<String> onPostExecute(final UploadResult result) {
        String fileNameWithoutExtension = result.getFile().getName().replaceAll("[.][a-zA-z]+$", "");
        AnswerIO answerIO = new AnswerIO(fileNameWithoutExtension);

        return answerIO.delete()
                .map(new Function<Boolean, String>() {
                    @Override
                    public String apply(Boolean aBoolean) throws Exception {
                        return aBoolean ? "File " + result.getFile().getName() + " deleted successfully. " :
                                "Error deleting the file " + result.getFile().getName();
                    }
                });
    }

    private File getDir() {
        return new File(root().getAbsolutePath() + "/" + Constants.DATA_DIR + "/" + Constants.ANSWER_DIR);
    }
}
