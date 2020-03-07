package mdomasevicius.itunestop5.config;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static okhttp3.logging.HttpLoggingInterceptor.Level;
import static org.slf4j.LoggerFactory.getLogger;

@Configuration
class HTTPConfig {

    @Bean
    OkHttpClient httpClient() {
        Logger httpLog = getLogger("http.client");
        return new OkHttpClient.Builder()
            .connectTimeout(5, SECONDS)
            .retryOnConnectionFailure(true)
            .readTimeout(20, SECONDS)
            .writeTimeout(20, SECONDS)
            .connectionPool(new ConnectionPool(10, 10, MINUTES))
            .addInterceptor(new HttpLoggingInterceptor(httpLog::info).setLevel(Level.BASIC))
            .build();
    }

}
