package com.ldapauth.utils;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
 
public class SSLUtilities {
 
    public static void trustAllHostnames() {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }
 
    public static void trustAllHttpsCertificates() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
                public void checkClientTrusted(X509Certificate[] certs, String alg) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String alg) {
                }
            }
        };
 
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
 
    // 使用示例
    public static void main(String[] args) throws Exception {
        // 绕过所有主机名的验证
        trustAllHostnames();
 
        // 绕过所有HTTPS证书的验证
        trustAllHttpsCertificates();
 
        // 现在你可以正常地使用HttpsURLConnection或者HttpClient等类进行连接了
        // 例如:
        // URL url = new URL("https://example.com");
        // HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        // // ...
    }
}