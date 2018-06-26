package org.ptracking.vdp.upload;

/**
 * Created by muthuveerappans on 09/05/18.
 */

public interface FileUploadResultReceiver {
    void onProgress(String message);

    void onError(String message);

    void onUploadCompleted(int successfulCount, int failedCount);
}
