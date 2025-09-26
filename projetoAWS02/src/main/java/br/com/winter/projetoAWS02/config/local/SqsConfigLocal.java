package br.com.winter.projetoAWS02.config.local;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

@Configuration
@Profile("local")
public class SqsConfigLocal {

    @Bean
    public SqsClient sqsClientLocal() {
        return SqsClient.builder()
                .endpointOverride(URI.create("http://localhost:4566")) // LocalStack endpoint
                .region(Region.US_EAST_1)
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("dummy", "dummy") // credenciais fake p/ LocalStack
                        )
                )
                .build();
    }
}
