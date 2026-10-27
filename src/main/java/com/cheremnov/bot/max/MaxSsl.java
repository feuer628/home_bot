package com.cheremnov.bot.max;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SSL-контекст для запросов к API Макса.
 * Сертификат platform-api2.max.ru выпущен подчинённым УЦ Минцифры
 * ("Russian Trusted Sub CA"), поэтому к системным корневым сертификатам
 * добавляется комплект из ресурса /max/russian-trusted-ca.pem.
 */
public final class MaxSsl {

    private static final String CA_RESOURCE = "/max/russian-trusted-ca.pem";

    private MaxSsl() {
    }

    public static SSLSocketFactory createSocketFactory() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            java.security.cert.CertificateFactory cf = java.security.cert.CertificateFactory.getInstance("X.509");
            try (InputStream in = MaxSsl.class.getResourceAsStream(CA_RESOURCE)) {
                if (in == null) {
                    throw new IllegalStateException("Не найден ресурс " + CA_RESOURCE);
                }
                int index = 0;
                for (Certificate certificate : cf.generateCertificates(in)) {
                    keyStore.setCertificateEntry("ru-trusted-ca-" + (index++), certificate);
                }
            }

            TrustManagerFactory custom = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            custom.init(keyStore);
            TrustManagerFactory def = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            def.init((KeyStore) null);

            CompositeTrustManager composite = new CompositeTrustManager(
                    firstX509(def.getTrustManagers()),
                    firstX509(custom.getTrustManagers()));

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{composite}, null);
            return context.getSocketFactory();
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось создать SSL-контекст для API Макса", e);
        }
    }

    private static X509TrustManager firstX509(TrustManager[] managers) {
        for (TrustManager manager : managers) {
            if (manager instanceof X509TrustManager x509) {
                return x509;
            }
        }
        throw new IllegalStateException("X509TrustManager недоступен");
    }

    /**
     * Доверяет цепочкам, принятым либо системным, либо дополнительным набором УЦ.
     */
    private static final class CompositeTrustManager implements X509TrustManager {

        private final X509TrustManager primary;
        private final X509TrustManager secondary;

        private CompositeTrustManager(X509TrustManager primary, X509TrustManager secondary) {
            this.primary = primary;
            this.secondary = secondary;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                primary.checkServerTrusted(chain, authType);
            } catch (CertificateException e) {
                secondary.checkServerTrusted(chain, authType);
            }
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            primary.checkClientTrusted(chain, authType);
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            List<X509Certificate> issuers = new ArrayList<>(Arrays.asList(primary.getAcceptedIssuers()));
            issuers.addAll(Arrays.asList(secondary.getAcceptedIssuers()));
            return issuers.toArray(new X509Certificate[0]);
        }
    }
}