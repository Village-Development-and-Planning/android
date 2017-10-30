package com.puthuvaazhvu.mapping.network;

import com.puthuvaazhvu.mapping.network.adapters.NetworkAdapter;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class ErrorUtils {

    public static APIError parseError(Response<?> response) {
        Converter<ResponseBody, APIError> converter =
                NetworkAdapter.getInstance().getRetrofit()
                        .responseBodyConverter(APIError.class, new Annotation[0]);

        APIError error;

        try {
            if (response.errorBody() == null) {
                return new APIError();
            }
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new APIError();
        }

        return error;
    }

    public static APIError parseError(Throwable throwable) {
        APIError error = new APIError();
        if (throwable.getMessage() != null)
            error.setMessage(throwable.getMessage());
        return error;
    }
}