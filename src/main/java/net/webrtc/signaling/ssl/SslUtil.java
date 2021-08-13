package net.webrtc.signaling.ssl;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.net.ssl.SSLException;
import java.io.*;

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
                getClass().getResource("/netty.crt").getFile()
        );
        pkcs8KeyFile = new File(
                getClass().getResource("/privatekey.pem").getFile()
        );

        if(!certificationFile.exists() && !pkcs8KeyFile.exists()) {
            loadAtWorkingDirectory();
        }
    }

    private void loadAtWorkingDirectory() {
        final String workDirPath = System.getProperty("user.dir");

        certificationFile = new File(workDirPath + "/pems/netty.crt");
        pkcs8KeyFile = new File(workDirPath + "/pems/privatekey.pem");
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
