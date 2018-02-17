package com.puthuvaazhvu.mapping.filestorage;

import com.puthuvaazhvu.mapping.filestorage.modals.DataInfo;
import com.puthuvaazhvu.mapping.other.Constants;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.root;
import static com.puthuvaazhvu.mapping.filestorage.StorageUtils.saveContentsToFile;

/**
 * Created by muthuveerappans on 16/02/18.
 */

public class DataInfoIO extends StorageIO<DataInfo> {

    @Override
    public Observable<DataInfo> read(File file) {
        return StorageUtils.readFromFile(file)
                .map(new Function<byte[], DataInfo>() {
                    @Override
                    public DataInfo apply(byte[] bytes) throws Exception {
                        return (DataInfo) StorageUtils.deserialize(bytes).blockingFirst();
                    }
                });
    }

    @Override
    public Observable<File> save(final File file, DataInfo contents) {
        return StorageUtils.serialize(contents)
                .flatMap(new Function<byte[], ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(byte[] bytes) throws Exception {
                        return StorageUtils.saveContentsToFile(file, bytes);
                    }
                });
    }

    @Override
    public String getAbsolutePath() {
        return root().getAbsolutePath() + "/" + Constants.DATA_DIR + "/" + Constants.DATA_INFO_FILE;
    }

}
