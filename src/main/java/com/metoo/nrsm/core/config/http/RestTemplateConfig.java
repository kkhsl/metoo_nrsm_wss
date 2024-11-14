package com.metoo.nrsm.core.config.http;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(150000); // ms
        factory.setConnectTimeout(150000); // ms
        return factory;
    }

    /**
     * Apache HttpClient
     * 绕过ssl 验证
     *
     * @return
     * @see [类、类#方法、类#成员]
     */
    private HttpClient httpClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(30000) // 服务器返回数据(response)的时间，超时抛出read timeout
                .setConnectTimeout(30000) // 连接上服务器(握手成功)的时间，超时抛出connect timeout
                .setConnectionRequestTimeout(1000)// 从连接池中获取连接的超时时间，超时抛出ConnectionPoolTimeoutException
                .build();
        SSLContext sslContext = SSLContextBuilder.create().setProtocol(SSLConnectionSocketFactory.SSL).loadTrustMaterial((x, y) -> true).build();
        return HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setSSLContext(sslContext).setSSLHostnameVerifier((x, y) -> true).build();
    }

}
