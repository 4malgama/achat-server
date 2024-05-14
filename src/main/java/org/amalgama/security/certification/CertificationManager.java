package org.amalgama.security.certification;

import org.amalgama.ResourceLoader;

public class CertificationManager {
    public static String getCertificate() {
        return ResourceLoader.getResourceAsString("certificate.crt");
    }
}
