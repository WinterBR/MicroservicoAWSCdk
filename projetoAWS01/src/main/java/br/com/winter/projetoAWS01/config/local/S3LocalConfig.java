package br.com.winter.projetoAWS01.config.local;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class S3LocalConfig {

    @Bean
    public AmazonS3 amazonS3Local() {
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(
                        new AmazonS3ClientBuilder.EndpointConfiguration("http://localhost:4566", "us-east-1"))
                .withPathStyleAccessEnabled(true) // Necessário para LocalStack
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials("test", "test")))
                .build();
    }
}
