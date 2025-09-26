package br.com.winter;

import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.Fn;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.events.targets.SnsTopic;
import software.amazon.awscdk.services.sqs.Queue;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.logs.LogGroup;

import java.util.HashMap;
import java.util.Map;

public class Service01Stack extends Stack {
    public Service01Stack(final Construct scope, final String id, Cluster cluster, SnsTopic produtoEventsTopic, Bucket invoiceBucket, Queue invoiceQueue) {
        this(scope, id, null, cluster, produtoEventsTopic, invoiceBucket, invoiceQueue);
    }

    public Service01Stack(final Construct scope, final String id, final StackProps props, Cluster cluster, SnsTopic produtoEventsTopic, Bucket invoiceBucket, Queue invoiceQueue) {
        super(scope, id, props);

        Map<String, String> envVariables = new HashMap<>();
        envVariables.put("SPRING_DATASOURCE_URL", "jdbc:mysql://" + Fn.importValue("rds-endpoint")
                + ":3306/aws_project01?createDatabaseIfNotExist=true");
        envVariables.put("SPRING_DATASOURCE_USERNAME", "admin");
        envVariables.put("SPRING_DATASOURCE_PASSWORD", Fn.importValue("rds-password"));
        envVariables.put("AWS_REGION", "us-east-1");
        envVariables.put("AWS_SNS_TOPIC_PRODUCT_EVENTS_ARN", produtoEventsTopic.getTopic().getTopicArn());

        // Definição da Task
        FargateTaskDefinition taskDef = FargateTaskDefinition.Builder.create(this, "TaskDef")
                .cpu(512)
                .memoryLimitMiB(1024)
                .build();

        taskDef.addContainer("Container01", ContainerDefinitionOptions.builder()
                .containerName("aws_project01")
                .image(ContainerImage.fromRegistry("winteredu/curso_aws_projeto01:4.0.1"))
                .portMappings(java.util.List.of(PortMapping.builder()
                        .containerPort(8080)
                        .build()))
                .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                        .logGroup(LogGroup.Builder.create(this, "Service01LogGroup")
                                .logGroupName("Service01")
                                .removalPolicy(RemovalPolicy.DESTROY)
                                .build())
                        .streamPrefix("Service01")
                        .build()))
                .environment(envVariables)
                .build());

        // Serviço Fargate com Load Balancer (ALB)
        ApplicationLoadBalancedFargateService albService =
                ApplicationLoadBalancedFargateService.Builder.create(this, "Service01ALBService")
                        .cluster(cluster)
                        .taskDefinition(taskDef)
                        .publicLoadBalancer(true) // cria ALB público
                        .desiredCount(1)
                        .listenerPort(80)
                        .build();

        // Permissões
        produtoEventsTopic.getTopic().grantPublish(taskDef.getTaskRole());
        invoiceQueue.grantConsumeMessages(taskDef.getTaskRole());
        invoiceBucket.grantReadWrite(taskDef.getTaskRole());
    }
}
