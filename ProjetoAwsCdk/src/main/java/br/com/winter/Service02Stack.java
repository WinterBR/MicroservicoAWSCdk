package br.com.winter;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.events.targets.SnsTopic;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.sns.subscriptions.SqsSubscription;
import software.amazon.awscdk.services.sqs.DeadLetterQueue;
import software.amazon.awscdk.services.sqs.Queue;
import software.amazon.awscdk.services.sqs.QueueEncryption;
import software.amazon.awscdk.services.ec2.*;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

import java.util.HashMap;
import java.util.Map;

public class Service02Stack extends Stack {
    public Service02Stack(final Construct scope, final String id, Cluster cluster, SnsTopic produtoEventsTopic, Table produtoEventsDdb) {
        this(scope, id, null, cluster, produtoEventsTopic, produtoEventsDdb);
    }

    public Service02Stack(final Construct scope, final String id, final StackProps props, Cluster cluster, SnsTopic produtoEventsTopic, Table produtoEventsDdb) {
        super(scope, id, props);

        // Dead Letter Queue
        Queue produtoEventsDlq = Queue.Builder.create(this, "ProdutoEventsDlq")
                // você pode remover o .queueName() se quiser evitar conflito
                .enforceSsl(false)
                .encryption(QueueEncryption.UNENCRYPTED)
                .build();

        DeadLetterQueue deadLetterQueue = DeadLetterQueue.builder()
                .queue(produtoEventsDlq)
                .maxReceiveCount(3)
                .build();

        // Fila principal
        Queue produtoEventsQueue = Queue.Builder.create(this, "ProdutoEvents")
                .enforceSsl(false)
                .encryption(QueueEncryption.UNENCRYPTED)
                .deadLetterQueue(deadLetterQueue)
                .build();

        // Ligando SNS → SQS
        SqsSubscription sqsSubscription = SqsSubscription.Builder.create(produtoEventsQueue).build();
        produtoEventsTopic.getTopic().addSubscription(sqsSubscription);

        // Variáveis de ambiente
        Map<String, String> envVariables = new HashMap<>();
        envVariables.put("AWS_REGION", "us-east-1");
        envVariables.put("AWS_SQS_QUEUE_PRODUTO_EVENTS_NAME", produtoEventsQueue.getQueueName());

        // Security Group
        SecurityGroup sg = SecurityGroup.Builder.create(this, "Service02SG")
                .vpc(cluster.getVpc())
                .allowAllOutbound(true)
                .build();
        sg.addIngressRule(Peer.anyIpv4(), Port.tcp(80), "Allow HTTP traffic");

        // Serviço Fargate com Application Load Balancer
        ApplicationLoadBalancedFargateService service02 =
                ApplicationLoadBalancedFargateService.Builder.create(this, "Service02ALBService")
                        .cluster(cluster)
                        .cpu(512)
                        .memoryLimitMiB(1024)
                        .desiredCount(2)
                        .assignPublicIp(true)
                        .securityGroups(java.util.List.of(sg))
                        .listenerPort(80)
                        .taskImageOptions(
                                ApplicationLoadBalancedTaskImageOptions.builder()
                                        .containerName("aws_project02")
                                        .image(ContainerImage.fromRegistry("winteredu/curso_aws_projeto02:2.0.1"))
                                        .containerPort(9090) // porta do container
                                        .environment(envVariables)
                                        .logDriver(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                                .logGroup(LogGroup.Builder.create(this, "Service02LogGroup")
                                                        .logGroupName("Service02")
                                                        .removalPolicy(RemovalPolicy.DESTROY)
                                                        .build())
                                                .streamPrefix("Service02")
                                                .build()))
                                        .build()
                        )
                        .build();

        // Auto Scaling
        ScalableTaskCount scalableTaskCount = service02.getService().autoScaleTaskCount(
                EnableScalingProps.builder()
                        .minCapacity(2)
                        .maxCapacity(4)
                        .build()
        );

        scalableTaskCount.scaleOnCpuUtilization("Service02AutoScaling", CpuUtilizationScalingProps.builder()
                .targetUtilizationPercent(50)
                .scaleInCooldown(Duration.seconds(60))
                .scaleOutCooldown(Duration.seconds(60))
                .build());

        // Permissões
        produtoEventsQueue.grantConsumeMessages(service02.getTaskDefinition().getTaskRole());
        produtoEventsDdb.grantReadWriteData(service02.getTaskDefinition().getTaskRole());
    }
}
