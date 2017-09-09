package com.puthuvaazhvu.mapping.utils.FileIO;

/**
 * Created by muthuveerappans on 9/9/17.
 */

public interface SaveOperationCallback {
    void done();

    void error(String message);
}
