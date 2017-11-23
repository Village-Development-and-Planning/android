package com.puthuvaazhvu.mapping.utils.storage;

import com.puthuvaazhvu.mapping.utils.Optional;

import java.io.File;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by muthuveerappans on 11/20/17.
 */

public class DeleteFile {

    public static Single<Optional> deleteFileObservable(final File file) {
        return Single.create(new SingleOnSubscribe<Optional>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Optional> emitter) throws Exception {
                boolean result = file.delete();
                if (!result) {
                    emitter.onError(new Throwable("Error saving the file"));
                }
                emitter.onSuccess(new Optional<>(null));
            }
        });
    }

    public static boolean deleteFile(final File file) {
        return file.delete();
    }
}
