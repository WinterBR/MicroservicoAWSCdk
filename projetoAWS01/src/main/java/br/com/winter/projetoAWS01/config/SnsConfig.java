package br.com.winter.projetoAWS01.config;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.Topic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SnsConfig {

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.sns.topic.produto.events.arn}")
    private String productEventsTopic;

    @Bean
    public AmazonSNS snsClient() {
        return AmazonSNSClientBuilder.standard()
                .withRegion(awsRegion)
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
    }

    @Bean
    @Qualifier("produtoEventsTopic")
    public Topic snsProductEventsTopic() {
        return new Topic().withTopicArn(productEventsTopic);
    }
}