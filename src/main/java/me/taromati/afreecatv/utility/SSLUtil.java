package me.taromati.afreecatv.utility;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

public class SSLUtil {

    public static SSLSocketFactory createSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
