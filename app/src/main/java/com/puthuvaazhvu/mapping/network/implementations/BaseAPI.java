package com.puthuvaazhvu.mapping.network.implementations;

import com.puthuvaazhvu.mapping.network.adapters.NetworkAdapter;

import retrofit2.Retrofit;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public abstract class BaseAPI {
    protected final NetworkAdapter adapter;

    public BaseAPI() {
        adapter = NetworkAdapter.getInstance();
    }

    public Retrofit getRetrofit() {
        return adapter.getRetrofit();
    }

    public Retrofit getRetrofit(String authToken) {
        return adapter.getUnsafeRetrofit(authToken);
    }
}
