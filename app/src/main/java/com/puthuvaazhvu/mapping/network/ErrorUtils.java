package com.puthuvaazhvu.mapping.network;

import com.puthuvaazhvu.mapping.network.adapters.NetworkAdapter;
import com.puthuvaazhvu.mapping.other.Error;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class ErrorUtils {

    public static Error parseError(Response<?> response) {
        Converter<ResponseBody, Error> converter =
                NetworkAdapter.getInstance().getRetrofit()
                        .responseBodyConverter(Error.class, new Annotation[0]);

        Error error;

        try {
            if (response.errorBody() == null) {
                return new Error();
            }
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new Error();
        }

        return error;
    }

    public static Error parseError(Throwable throwable) {
        Error error = new Error();
        if (throwable.getMessage() != null)
            error.setMessage(throwable.getMessage());
        return error;
    }
}