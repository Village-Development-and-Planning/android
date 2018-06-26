package org.ptracking.vdp.network.adapters;

import com.burgstaller.okhttp.AuthenticationCacheInterceptor;
import com.burgstaller.okhttp.CachingAuthenticatorDecorator;
import com.burgstaller.okhttp.digest.CachingAuthenticator;
import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.digest.DigestAuthenticator;
import org.ptracking.vdp.network.APIUtils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * Created by muthuveerappans on 10/30/17.
 */

public class NetworkAdapter {
    public static NetworkAdapter networkAdapter;

    protected final OkHttpClient.Builder httpClient;
    protected final Retrofit.Builder builder;

    public static NetworkAdapter getInstance() {
        if (networkAdapter == null) {
            return new NetworkAdapter();
        }
        return networkAdapter;
    }

    protected NetworkAdapter() {
        httpClient = new OkHttpClient.Builder();
        builder = new Retrofit.Builder()
                .baseUrl(APIUtils.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
    }

    public Retrofit getRetrofit() {
        return builder.client(httpClient.build()).build();
    }

    public Retrofit getRetrofit(String authToken) {
        httpClient.addInterceptor(headerInterceptor(authToken));
        return builder.client(httpClient.build()).build();
    }

    public Retrofit getDigestAuthenticationRetrofit(String username, String password) {
        final DigestAuthenticator authenticator = new DigestAuthenticator(
                new Credentials(username, password)
        );

        final Map<String, CachingAuthenticator> authCache = new ConcurrentHashMap<>();

        httpClient
                .authenticator(new CachingAuthenticatorDecorator(authenticator, authCache))
                .addInterceptor(new AuthenticationCacheInterceptor(authCache));

        return builder.client(httpClient.build()).build();
    }

    public Retrofit getUnsafeRetrofit(String authToken) {
        httpClient.addInterceptor(headerInterceptor(authToken));
        try {
            unsafeHttpClient();
        } catch (Exception e) {
            Timber.e("Exception occurred on unsafe retrofit. " + e.getMessage());
        }
        return builder.client(httpClient.build()).build();
    }

    private Interceptor headerInterceptor(final String authToken) {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Authorization", authToken)
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        };
    }

    private void unsafeHttpClient() throws KeyManagementException, NoSuchAlgorithmException {
        // Create a trust manager that does not validate certificate chains
        final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };

        // Install the all-trusting trust manager
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        // Create an ssl socket factory with our all-trusting manager
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        httpClient.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
        httpClient.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }
}
