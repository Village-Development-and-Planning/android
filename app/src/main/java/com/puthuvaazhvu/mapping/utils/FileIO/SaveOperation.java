package com.puthuvaazhvu.mapping.utils.FileIO;

import java.io.File;

/**
 * Created by muthuveerappans on 9/9/17.
 */

public interface SaveOperation {
    void save();

    void cancel();

    File getSavedFile();
}
