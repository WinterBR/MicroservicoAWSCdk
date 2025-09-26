package br.com.winter;

import lombok.Getter;
import software.amazon.awscdk.RemovalPolicy;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.events.targets.SnsTopic;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.EventType;
import software.amazon.awscdk.services.s3.notifications.SnsDestination;
import software.amazon.awscdk.services.sns.Topic;
import software.amazon.awscdk.services.sns.subscriptions.SqsSubscription;
import software.amazon.awscdk.services.sqs.DeadLetterQueue;
import software.amazon.awscdk.services.sqs.Queue;

@Getter
public class InvoiceAppStack extends Stack {

    private final Bucket bucket;
    private final Queue s3InvoiceQueue;

    public InvoiceAppStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public InvoiceAppStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        SnsTopic s3InvoiceTopic = SnsTopic.Builder.create(Topic.Builder.create(this, "S3InvoiceTopic")
                        .build())
                .build();

        bucket = Bucket.Builder.create(this, "S301")
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();

        bucket.addEventNotification(EventType.OBJECT_CREATED_PUT, new SnsDestination(s3InvoiceTopic.getTopic()));

        Queue s3InvoiceDlq = Queue.Builder.create(this, "S3InvoiceDlq")
                .build();

        DeadLetterQueue deadLetterQueue = DeadLetterQueue.builder()
                .queue(s3InvoiceDlq)
                .maxReceiveCount(3)
                .build();

        s3InvoiceQueue = Queue.Builder.create(this, "S3InvoiceQueue")
                .deadLetterQueue(deadLetterQueue)
                .build();

        SqsSubscription sqsSubscription = SqsSubscription.Builder.create(s3InvoiceQueue).build();
        s3InvoiceTopic.getTopic().addSubscription(sqsSubscription);
    }
}
