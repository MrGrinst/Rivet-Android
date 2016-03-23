package com.jumpintorivet.rivet.injection;

import com.google.gson.Gson;
import com.jumpintorivet.rivet.BuildConfig;
import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.repositories.internal.InternalConversationRepository;
import com.jumpintorivet.rivet.repositories.internal.InternalReportBehaviorRepository;
import com.jumpintorivet.rivet.repositories.internal.InternalUserRepository;
import com.jumpintorivet.rivet.utils.AppState;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import dagger.Module;
import dagger.Provides;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

@Module(
        complete = false,
        library = true
)
public class RepositoryModule {
    public static SSLContext sslContextForTrustedCertificates(MyApplication application) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = new BufferedInputStream(application.getAssets().open(application.getResources().getString(R.string.ssl_cert_name)));
            Certificate ca = cf.generateCertificate(caInput);
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
            return context;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(@ForApplication MyApplication application, Gson gson, final AppState appState) {
        OkHttpClient client;
        String baseEndpoint = application.getResources().getString(R.string.base_endpoint);
        Interceptor authorizationInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (appState.getAuthToken() != null) {
                    request = request.newBuilder()
                            .addHeader("Authorization", "Bearer " + appState.getAuthToken())
                            .build();
                }
                return chain.proceed(request);
            }
        };
        if (BuildConfig.FLAVOR.equals("production")) {
            try {
                client = new OkHttpClient();
                client.interceptors().add(authorizationInterceptor);
                client.setConnectTimeout(20, TimeUnit.SECONDS);
                client.setReadTimeout(20, TimeUnit.SECONDS);
                SSLContext sslContext = sslContextForTrustedCertificates(application);
                client.setSslSocketFactory(sslContext.getSocketFactory());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            client = new OkHttpClient();
            client.interceptors().add(authorizationInterceptor);
            client.setConnectTimeout(20, TimeUnit.SECONDS);
            client.setReadTimeout(20, TimeUnit.SECONDS);
        }
        return new Retrofit.Builder()
                .baseUrl(baseEndpoint)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
    }

    @Provides
    @Singleton
    InternalConversationRepository provideConversationRepository(Retrofit retrofit) {
        return retrofit.create(InternalConversationRepository.class);
    }

    @Provides
    @Singleton
    InternalUserRepository provideUserRepository(Retrofit retrofit) {
        return retrofit.create(InternalUserRepository.class);
    }

    @Provides
    @Singleton
    InternalReportBehaviorRepository provideReportBehaviorRepository(Retrofit retrofit) {
        return retrofit.create(InternalReportBehaviorRepository.class);
    }

}
