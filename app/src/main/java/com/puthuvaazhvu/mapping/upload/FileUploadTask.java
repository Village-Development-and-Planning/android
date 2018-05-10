package com.puthuvaazhvu.mapping.upload;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.puthuvaazhvu.mapping.modals.Upload;
import com.puthuvaazhvu.mapping.network.implementations.UploadSurveysAPI;
import com.puthuvaazhvu.mapping.utils.Command;
import com.puthuvaazhvu.mapping.utils.RetryWithDelay;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableOperator;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by muthuveerappans on 09/05/18.
 */

public abstract class FileUploadTask implements Command {
    private final int RETRY_COUNT = 3;
    private final int RETRY_INTERVAL = 3000;

    private final FileUploadResultReceiver receiver;

    private final UploadSurveysAPI uploadSurveysAPI;

    private final List<UploadResult> successFiles = new ArrayList<>();
    private final List<UploadResult> failureFiles = new ArrayList<>();

    private final String username, password;

    private Disposable disposable;

    public FileUploadTask(String username, String password, FileUploadResultReceiver receiver) {
        this.receiver = receiver;
        this.uploadSurveysAPI = new UploadSurveysAPI(username, password);

        this.username = username;
        this.password = password;
    }

    protected abstract Observable<List<File>> getFiles();

    @Override
    public void execute() {
        disposable = getFiles().flatMapIterable(new Function<List<File>, Iterable<File>>() {
            @Override
            public Iterable<File> apply(List<File> files) throws Exception {
                return files;
            }
        }).flatMap(new Function<File, ObservableSource<UploadResult>>() {
            @Override
            public ObservableSource<UploadResult> apply(File file) throws Exception {
                return Observable.just(file)
                        .flatMap(new Function<File, ObservableSource<UploadResult>>() {
                            @Override
                            public ObservableSource<UploadResult> apply(final File file) throws Exception {
                                return uploadSurveysAPI.uploadFile(username, file)
                                        .map(new Function<Upload, UploadResult>() {
                                            @Override
                                            public UploadResult apply(Upload upload) throws Exception {
                                                UploadResult uploadResult = UploadResult.adapter(upload, file);
                                                uploadResult.setMessage("File " + file.getName() + " uploaded successfully.");
                                                return uploadResult;
                                            }
                                        })
                                        .flatMap(new Function<UploadResult, ObservableSource<UploadResult>>() {
                                            @Override
                                            public ObservableSource<UploadResult> apply(final UploadResult result) throws Exception {
                                                return onPostExecute(result)
                                                        .onErrorReturnItem("Error deleting the file " + result.getFile().getName())
                                                        .map(new Function<String, UploadResult>() {
                                                            @Override
                                                            public UploadResult apply(String s) throws Exception {
                                                                String message = result.getMessage();
                                                                message += "\n" + s;
                                                                result.setMessage(message);
                                                                return result;
                                                            }
                                                        });
                                            }
                                        });
                            }
                        })
                        .retryWhen(new RetryWithDelay(RETRY_COUNT, RETRY_INTERVAL))
                        .onErrorReturn(new Function<Throwable, UploadResult>() {
                            @Override
                            public UploadResult apply(Throwable throwable) throws Exception {
                                UploadResult uploadResult = new UploadResult();
                                uploadResult.setMessage(throwable.getMessage());
                                uploadResult.setFailure(true);
                                return uploadResult;
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UploadResult>() {
                    @Override
                    public void accept(UploadResult result) throws Exception {
                        if (result.isFailure()) {
                            failureFiles.add(result);
                        } else {
                            successFiles.add(result);
                        }
                        receiver.onProgress(result.getMessage());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        receiver.onError("Error uploading the files. Please try again later.");
                        receiver.onUploadCompleted(successFiles.size(), failureFiles.size());
                        reset();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        receiver.onUploadCompleted(successFiles.size(), failureFiles.size());
                        reset();
                    }
                });
    }

    protected Observable<String> onPostExecute(UploadResult result) {
        return Observable.just("");
    }

    private void reset() {
        successFiles.clear();
        failureFiles.clear();
    }

}
