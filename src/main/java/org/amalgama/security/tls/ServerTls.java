package org.amalgama.security.tls;


import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.util.HashedWheelTimer;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Enumeration;

public final class ServerTls {
    private static SSLContext context;
    private static HashedWheelTimer timer;

    private ServerTls() {
    }

    public static synchronized void initialize() throws Exception {
        if (context != null) {
            return;
        }

        String file = requireEnvironment("ACHAT_TLS_KEYSTORE");

        char[] password = requireEnvironment("ACHAT_TLS_PASSWORD").toCharArray();

        SSLContext loaded;

        try {
            KeyStore store = KeyStore.getInstance("PKCS12");

            try (InputStream input = Files.newInputStream(Path.of(file))) {
                store.load(input, password);
            }

            int privateKeys = 0;

            for (Enumeration<String> aliases = store.aliases();
                 aliases.hasMoreElements();) {

                String alias = aliases.nextElement();

                if (store.isKeyEntry(alias)
                        && store.getKey(alias, password) instanceof PrivateKey
                        && store.getCertificateChain(alias) != null
                        && store.getCertificateChain(alias).length > 0) {
                    privateKeys++;
                }
            }

            if (privateKeys != 1) {
                throw new IllegalStateException(
                        "The PKCS12 file must contain exactly one "
                                + "private key with its certificate chain"
                );
            }

            KeyManagerFactory managers = KeyManagerFactory.getInstance(
                    KeyManagerFactory.getDefaultAlgorithm()
            );

            managers.init(store, password);

            loaded = SSLContext.getInstance("TLS");

            loaded.init(
                    managers.getKeyManagers(),
                    null,
                    new SecureRandom()
            );
        } finally {
            Arrays.fill(password, '\0');
        }

        HashedWheelTimer loadedTimer = new HashedWheelTimer(task -> {
            Thread thread = new Thread(task, "achat-tls-timeouts");
            thread.setDaemon(true);
            return thread;
        });

        Runtime.getRuntime().addShutdownHook(
                new Thread(
                        () -> loadedTimer.stop(),
                        "achat-tls-cleanup"
                )
        );

        timer = loadedTimer;
        context = loaded;
    }

    public static synchronized SslHandler newHandler() {
        if (context == null) {
            throw new IllegalStateException(
                    "Call ServerTls.initialize() before starting the server"
            );
        }

        SSLEngine engine = context.createSSLEngine();
        engine.setUseClientMode(false);
        engine.setNeedClientAuth(false);
        engine.setEnabledProtocols(new String[] {
                "TLSv1.3",
                "TLSv1.2"
        });

        engine.setEnabledCipherSuites(new String[] {
                "TLS_AES_128_GCM_SHA256",
                "TLS_AES_256_GCM_SHA384",

                "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
                "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",

                "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
                "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384"
        });

        SslHandler handler = new SslHandler(
                engine,
                SslHandler.getDefaultBufferPool(),
                false,
                timer,
                15000
        );

        handler.setIssueHandshake(true);
        handler.setEnableRenegotiation(false);
        handler.setCloseOnSSLException(true);

        return handler;
    }

    private static String requireEnvironment(String name) {
        String value = System.getenv(name);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Missing environment variable: " + name
            );
        }

        return value;
    }
}
