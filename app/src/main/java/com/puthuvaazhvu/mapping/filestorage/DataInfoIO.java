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

public class DataInfoIO extends StorageIO {

    @Override
    public String getAbsolutePath(String filename) {
        return root().getAbsolutePath() + "/" + Constants.DATA_DIR + "/" + Constants.DATA_INFO_FILE;
    }

}
