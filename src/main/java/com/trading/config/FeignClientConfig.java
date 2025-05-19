package com.trading.config;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignClientConfig {

    @Value("${feign.client.timeout:60000}")
    private int timeout;

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CoinGeckoErrorDecoder();
    }

    @Bean
    public Request.Options options() {
        return new Request.Options(timeout, TimeUnit.MILLISECONDS, timeout, TimeUnit.MILLISECONDS, true);
    }

    @Bean
    public Retryer retryer() {
        // No automatic retries, we handle them manually
        return Retryer.NEVER_RETRY;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Ensure we're using HTTPS
            String url = requestTemplate.url();
            if (url.startsWith("http://")) {
                url = url.replace("http://", "https://");
                requestTemplate.target(url);
            }

            // Useful headers for debugging
            requestTemplate.header("User-Agent", "Trading-Backend/1.0");
        };
    }
}

class CoinGeckoErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, feign.Response response) {
        if (response.status() == 429) {
            return new RuntimeException("You are using the free plan. Rate limit exceeded.");
        } else if (response.status() == 301 || response.status() == 302) {
            return new RuntimeException("Redirect detected. Please check if you're using HTTPS.");
        } else {
            // Use the FeignException factory method instead of constructor
            return feign.FeignException.errorStatus(methodKey, response);
        }
    }
}