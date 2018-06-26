package org.ptracking.vdp.filestorage.io;

import org.ptracking.vdp.filestorage.StorageUtils;
import org.ptracking.vdp.filestorage.modals.DataInfo;
import org.ptracking.vdp.other.Constants;
import org.ptracking.vdp.utils.ThrowableWithErrorCode;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static org.ptracking.vdp.filestorage.StorageUtils.saveContentsToFile;

/**
 * Created by muthuveerappans on 16/02/18.
 */

public class DataInfoIO extends IOBase {

    public Observable<DataInfo> read() {
        if (!isFileReadable(getAbsolutePath())) {
            return Observable.error(new ThrowableWithErrorCode(
                            "File " + getAbsolutePath() + " cannot be read.",
                            Constants.ErrorCodes.ERROR_READING_FILE
                    )
            );
        }

        return StorageUtils.readFromFile(new File(getAbsolutePath()))
                .observeOn(Schedulers.io())
                .flatMap(new Function<byte[], ObservableSource<DataInfo>>() {
                    @Override
                    public ObservableSource<DataInfo> apply(byte[] bytes) throws Exception {
                        return StorageUtils.deserialize(bytes)
                                .map(new Function<Object, DataInfo>() {
                                    @Override
                                    public DataInfo apply(Object o) throws Exception {
                                        return (DataInfo) o;
                                    }
                                });
                    }
                });
    }

    public Observable<File> save(DataInfo contents) {
        return StorageUtils.serialize(contents)
                .observeOn(Schedulers.io())
                .flatMap(new Function<byte[], ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(byte[] bytes) throws Exception {
                        File file = getFileFromPath(getAbsolutePath());
                        return StorageUtils.saveContentsToFile(file, bytes);
                    }
                });
    }

    public Observable<Boolean> delete() {
        return Observable.just(true)
                .observeOn(Schedulers.io())
                .map(new Function<Boolean, Boolean>() {
                    @Override
                    public Boolean apply(Boolean aBoolean) throws Exception {
                        return StorageUtils.delete(new File(getAbsolutePath()));
                    }
                });
    }

    public boolean isExists() {
        return new File(getAbsolutePath()).exists();
    }

    private String getAbsolutePath() {
        return root().getAbsolutePath() + "/" + Constants.DATA_DIR + "/" + Constants.DATA_INFO_FILE + ".bytes";
    }

}
