package com.example.advertise_service.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.connection.SslSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@Configuration
public class DevMongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Bean
    public MongoClient reactiveMongoClient() throws Exception {
        // 1) ConnectionString 준비
        ConnectionString conn = new ConnectionString(mongoUri);

        // 2) 모든 인증서 신뢰하는 TrustManager 구현
        TrustManager[] trustAll = new TrustManager[]{
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain, String authType) { }
                    public void checkServerTrusted(X509Certificate[] chain, String authType) { }
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                }
        };

        // 3) SSLContext 생성 및 초기화 (모든 인증서·호스트명 우회)
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAll, new SecureRandom());

        // 4) MongoClientSettings 에 SSLContext 주입
        SslSettings sslSettings = SslSettings.builder()
                                             .enabled(true)
                                             .invalidHostNameAllowed(true)
                                             .context(sslContext)
                                             .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                                                          .applyConnectionString(conn)
                                                          .applyToSslSettings(builder -> builder.applySettings(sslSettings))
                                                          .build();

        // 5) Reactive MongoClient 생성
        return MongoClients.create(settings);
    }
}
