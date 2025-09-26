package br.com.winter.projetoAWS01.config.local;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.Topic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class SnsLocalConfig {

    @Value("${aws.sns.topic.produto.events.arn:arn:aws:sns:us-east-1:000000000000:product-events}")
    private String productEventsTopic;

    @Bean
    public AmazonSNS snsLocalClient() {
        return AmazonSNSClientBuilder.standard()
                .withEndpointConfiguration(
                        new AmazonSNSClientBuilder.EndpointConfiguration("http://localhost:4566", "us-east-1"))
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials("test", "test")))
                .build();
    }

    @Bean
    @Qualifier("produtoEventsTopic")
    public Topic snsProductEventsLocalTopic() {
        return new Topic().withTopicArn(productEventsTopic);
    }
}
