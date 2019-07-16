/*
 * Copyright (C) Globalegrow E-Commerce Co. , Ltd. 2007-2018.
 * All rights reserved.
 * This software is the confidential and proprietary information
 * of Globalegrow E-Commerce Co. , Ltd. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement
 * you entered into with Globalegrow.
 */

package com.fz.network;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.ProxySelector;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.CertificatePinner;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Dispatcher;
import okhttp3.Dns;
import okhttp3.EventListener;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by dingpeihua on 2017/12/5.
 */
public class OKHttpBuilder {

    static class CookieSave {
        boolean isAddCookie;
        Cookie cookie;

        public CookieSave(boolean isAddCookie, Cookie cookie) {
            this.isAddCookie = isAddCookie;
            this.cookie = cookie;
        }
    }

    Context context;
    private static final long defaultTimeout = 20_000;
    private final OkHttpClient.Builder builder;
    private List<CookieSave> cookies = new ArrayList<>();
    final List<Interceptor> networkInterceptors = new ArrayList<>();
    private List<Interceptor> interceptors = new ArrayList<>();
    private List<byte[]> certsData;
    private boolean isEnabledHttpLog;
    private Interceptor securityInterceptor;
    private Interceptor responseCacheInterceptor;

    private OKHttpBuilder(Context context, OkHttpClient.Builder builder) {
        this.context = context;
        this.builder = builder;
    }

    public static OKHttpBuilder newBuilder() {
        return new OKHttpBuilder(null, new OkHttpClient().newBuilder());
    }

    public static OKHttpBuilder newBuilder(Context context) {
        if (context == null) {
            throw new NullPointerException("context == null");
        }
        return new OKHttpBuilder(context.getApplicationContext(), new OkHttpClient().newBuilder());
    }

    private OKHttpBuilder(OKHttpBuilder other) {
        this.context = other.context;
        this.builder = other.builder;
        this.cookies = other.cookies;
        this.interceptors = other.interceptors;
        this.certsData = other.certsData;
        this.isEnabledHttpLog = other.isEnabledHttpLog;
        this.securityInterceptor = other.securityInterceptor;
        this.responseCacheInterceptor = other.responseCacheInterceptor;
    }

    public OKHttpBuilder addCookie(boolean isAddCookie, Cookie cookie) {
        if (cookie != null) {
            this.cookies.add(new CookieSave(isAddCookie, cookie));
        }
        return this;
    }

    public OKHttpBuilder addCookie(Cookie cookie) {
        return addCookie(true, cookie);
    }

    public OKHttpBuilder setCertsData(List<byte[]> certs_data) {
        this.certsData = certs_data;
        return this;
    }

    public OKHttpBuilder setEnabledHttpLog(boolean enabledHttpLog) {
        isEnabledHttpLog = enabledHttpLog;
        return this;
    }

    public OKHttpBuilder addInterceptor(Interceptor... interceptor) {
        if (interceptor != null) {
            this.interceptors.addAll(new ArrayList<>(Arrays.asList(interceptor)));
        }
        return this;
    }

    public OKHttpBuilder addInterceptor(Interceptor interceptor) {
        if (interceptor != null) {
            this.interceptors.add(interceptor);
        }
        return this;
    }

    public OKHttpBuilder addNetworkInterceptor(Interceptor interceptor) {
        if (interceptor != null) {
            networkInterceptors.add(interceptor);
        }
        return this;
    }

    public OKHttpBuilder securityInterceptor(Interceptor interceptor) {
        securityInterceptor = interceptor;
        return this;
    }

    public OKHttpBuilder responseCacheInterceptor(Interceptor interceptor) {
        responseCacheInterceptor = interceptor;
        return this;
    }

    /**
     * @see {@link OkHttpClient.Builder#connectTimeout(long, TimeUnit)}
     */
    public OKHttpBuilder connectTimeout(long timeout, TimeUnit unit) {
        builder.connectTimeout(timeout > 0 ? timeout : defaultTimeout, unit);
        return this;
    }

    /**
     * @see {@link OkHttpClient.Builder#readTimeout(long, TimeUnit)}
     */
    public OKHttpBuilder readTimeout(long timeout, TimeUnit unit) {
        builder.readTimeout(timeout > 0 ? timeout : defaultTimeout, unit);
        return this;
    }

    /**
     * @see {@link OkHttpClient.Builder#writeTimeout(long, TimeUnit)}
     */
    public OKHttpBuilder writeTimeout(long timeout, TimeUnit unit) {
        builder.writeTimeout(timeout > 0 ? timeout : defaultTimeout, unit);
        return this;
    }

    /**
     * @see {@link OkHttpClient.Builder#pingInterval(long, TimeUnit)}
     */
    public OKHttpBuilder pingInterval(long interval, TimeUnit unit) {
        builder.pingInterval(interval, unit);
        return this;
    }

    /**
     * @see {@link OkHttpClient.Builder#proxy(Proxy)}
     */
    public OKHttpBuilder proxy(@Nullable Proxy proxy) {
        if (proxy != null) {
            builder.proxy(proxy);
        }
        return this;
    }

    /**
     * @see {@link OkHttpClient.Builder#proxySelector(ProxySelector)}
     */
    public OKHttpBuilder proxySelector(ProxySelector proxySelector) {
        if (proxySelector != null) {
            builder.proxySelector(proxySelector);
        }
        return this;
    }

    /**
     * @see {@link OkHttpClient.Builder#cookieJar(CookieJar)}
     */
    public OKHttpBuilder cookieJar(CookieJar cookieJar) {
        if (cookieJar != null) {
            builder.cookieJar(cookieJar);
        }
        return this;
    }

    /**
     * @see {@link OkHttpClient.Builder#cache(Cache)}
     */
    public OKHttpBuilder cache(@Nullable Cache cache) {
        if (cache != null) {
            builder.cache(cache);
        }
        return this;
    }

    /**
     * @see {@link OkHttpClient.Builder#dns(Dns)}
     */
    public OKHttpBuilder dns(Dns dns) {
        if (dns != null) {
            builder.dns(dns);
        }
        return this;
    }

    /**
     * @see {@link OkHttpClient.Builder#socketFactory(SocketFactory)}
     */
    public OKHttpBuilder socketFactory(SocketFactory socketFactory) {
        if (socketFactory != null) {
            builder.socketFactory(socketFactory);
        }
        return this;
    }

    /**
     * @see {@link OkHttpClient.Builder#sslSocketFactory(SSLSocketFactory)}
     */
    public OKHttpBuilder sslSocketFactory(SSLSocketFactory sslSocketFactory) {
        if (sslSocketFactory != null) {
            builder.sslSocketFactory(sslSocketFactory);
        }
        return this;
    }

    /**
     * @see {@link OkHttpClient.Builder#sslSocketFactory(SSLSocketFactory, X509TrustManager)}
     */
    public OKHttpBuilder sslSocketFactory(SSLSocketFactory sslSocketFactory, X509TrustManager trustManager) {
        if (sslSocketFactory != null && trustManager != null) {
            builder.sslSocketFactory(sslSocketFactory, trustManager);
        }
        return this;
    }

    /**
     * @see {@link OkHttpClient.Builder#hostnameVerifier(HostnameVerifier)}
     */
    public OKHttpBuilder hostnameVerifier(HostnameVerifier hostnameVerifier) {
        if (hostnameVerifier != null) {
            builder.hostnameVerifier(hostnameVerifier);
        }
        return this;
    }

    /**
     * @see {@link OkHttpClient.Builder#certificatePinner(CertificatePinner)}
     */
    public OKHttpBuilder certificatePinner(CertificatePinner certificatePinner) {
        if (certificatePinner != null) {
            builder.certificatePinner(certificatePinner);
        }
        return this;
    }

    /**
     * @see {@link OkHttpClient.Builder#authenticator(Authenticator)}
     */
    public OKHttpBuilder authenticator(Authenticator authenticator) {
        builder.authenticator(authenticator);
        return this;
    }

    /**
     * @see {@link OkHttpClient.Builder#proxyAuthenticator(Authenticator)}
     */
    public OKHttpBuilder proxyAuthenticator(Authenticator proxyAuthenticator) {
        if (proxyAuthenticator != null) {
            builder.proxyAuthenticator(proxyAuthenticator);
        }
        return this;
    }

    /**
     * @see {@link OkHttpClient.Builder#connectionPool(ConnectionPool)}
     */
    public OKHttpBuilder connectionPool(ConnectionPool connectionPool) {
        if (connectionPool != null) {
            builder.connectionPool(connectionPool);
        }
        return this;
    }

    /**
     * @see {@link OkHttpClient.Builder#followRedirects(boolean)}
     */
    public OKHttpBuilder followSslRedirects(boolean followProtocolRedirects) {
        builder.followSslRedirects(followProtocolRedirects);
        return this;
    }

    /**
     * @see {@link OkHttpClient.Builder#followRedirects(boolean)}
     */
    public OKHttpBuilder followRedirects(boolean followRedirects) {
        builder.followRedirects(followRedirects);
        return this;
    }

    /**
     * @see {@link OkHttpClient.Builder#retryOnConnectionFailure(boolean)}
     */
    public OKHttpBuilder retryOnConnectionFailure(boolean retryOnConnectionFailure) {
        builder.retryOnConnectionFailure(retryOnConnectionFailure);
        return this;
    }

    /**
     * @see {@link OkHttpClient.Builder#dispatcher(Dispatcher)}
     */
    public OKHttpBuilder dispatcher(Dispatcher dispatcher) {
        if (dispatcher != null) {
            builder.dispatcher(dispatcher);
        }
        return this;
    }

    /**
     * @see {@link OkHttpClient.Builder#protocols(List)}
     */
    public OKHttpBuilder protocols(List<Protocol> protocols) {
        if (protocols != null) {
            builder.protocols(protocols);
        }
        return this;
    }

    /**
     * @param connectionSpecs
     * @return
     * @see {@link OkHttpClient.Builder#connectionSpecs(List)}
     */
    public OKHttpBuilder connectionSpecs(List<ConnectionSpec> connectionSpecs) {
        if (connectionSpecs != null) {
            builder.connectionSpecs(connectionSpecs);
        }
        return this;
    }

    /**
     * @param eventListener
     * @return
     * @see {@link OkHttpClient.Builder#eventListener(EventListener)}
     */
    public OKHttpBuilder eventListener(EventListener eventListener) {
        if (eventListener != null) {
            builder.eventListener(eventListener);
        }
        return this;
    }

    /**
     * @param eventListenerFactory
     * @return
     * @see {@link OkHttpClient.Builder#eventListenerFactory(EventListener.Factory)}
     */
    public OKHttpBuilder eventListenerFactory(EventListener.Factory eventListenerFactory) {
        if (eventListenerFactory != null) {
            builder.eventListenerFactory(eventListenerFactory);
        }
        return this;
    }

    public OkHttpClient build() {
        if (cookies != null && context != null) {
            SharedPrefsCookiePersistor sharedPrefsCookiePersistor = new SharedPrefsCookiePersistor(context);
            CookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), sharedPrefsCookiePersistor) {
                @Override
                public synchronized List<Cookie> loadForRequest(HttpUrl url) {
                    final List<Cookie> validCookies = super.loadForRequest(url);
                    for (CookieSave cookieSave : cookies) {
                        if (cookieSave.isAddCookie) {
                            validCookies.add(cookieSave.cookie);
                        }
                    }
                    return validCookies;
                }
            };
            builder.cookieJar(cookieJar);
        }
        if (certsData != null && !certsData.isEmpty()) {
            try {
                List<InputStream> certificates = new ArrayList<>();
                for (byte[] bytes : certsData) {
                    certificates.add(new ByteArrayInputStream(bytes));
                }
                SSLSocketFactory sslSocketFactory = getSocketFactory(certificates);
                if (sslSocketFactory != null) {
                    builder.sslSocketFactory(sslSocketFactory, new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (securityInterceptor != null) {
            builder.addInterceptor(securityInterceptor);
        }
        if (interceptors != null && interceptors.size() > 0) {
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }
        //设置缓存
        if (responseCacheInterceptor != null) {
            builder.addInterceptor(responseCacheInterceptor);
        }
        if (isEnabledHttpLog) {
            builder.addInterceptor(new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY));
        }
        if (networkInterceptors != null && networkInterceptors.size() > 0) {
            for (Interceptor networkInterceptor : networkInterceptors) {
                builder.addNetworkInterceptor(networkInterceptor);
            }
        }
        //错ë误重连
        builder.retryOnConnectionFailure(true);
        return builder.build();
    }

    /**
     * 添加证书
     *
     * @param certificates
     */
    private static SSLSocketFactory getSocketFactory(List<InputStream> certificates) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            try {
                for (int i = 0, size = certificates.size(); i < size; ) {
                    final InputStream certificate = certificates.get(i);
                    final String certificateAlias = Integer.toString(i++);
                    keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                    if (certificate != null) {
                        certificate.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public OKHttpBuilder clone() {
        return new OKHttpBuilder(this);
    }
}
