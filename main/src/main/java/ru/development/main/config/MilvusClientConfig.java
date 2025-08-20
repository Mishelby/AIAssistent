package ru.development.main.config;

import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MilvusClientConfig {
    private static String TOKEN
            = "5085cb6bd56fe6b6ae0eaa8e953e48770c81332798a29b2f58c5c14a722bc49870fc2d2b6deccbd1c9fdb403c87e38f51a16e08d";

    @Bean
    public MilvusClientV2 createConnection(){
        ConnectConfig connectConfig  = ConnectConfig.builder()
                .uri("https://in03-dff0aa05d426dcf.serverless.gcp-us-west1.cloud.zilliz.com")
                .token(TOKEN)
                .build();

       var milvusClientV2 = new MilvusClientV2(connectConfig);

       return milvusClientV2;
    }

}
