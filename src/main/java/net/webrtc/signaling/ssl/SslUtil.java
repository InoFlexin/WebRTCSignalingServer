package net.webrtc.signaling.ssl;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.net.ssl.SSLException;
import java.io.File;
import java.io.InputStream;

@NoArgsConstructor
@Getter
public class SslUtil {

    private File certificationFile;
    private File pkcs8KeyFile;

    private ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader;
    }

    public void load() {
        ClassLoader classLoader = getClassLoader();
        certificationFile = new File(
                classLoader.getResource("netty.crt").getFile()
        );
        pkcs8KeyFile = new File(
                classLoader.getResource("privatekey.pem").getFile()
        );
    }

    public SslContext getSslContext() {
        if(certificationFile == null || pkcs8KeyFile == null) {
            throw new RuntimeException("인증서 또는 키가 로드되지 않았습니다.");
        }

        try {
            return SslContextBuilder.forServer(certificationFile, pkcs8KeyFile).build();
        } catch (SSLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
