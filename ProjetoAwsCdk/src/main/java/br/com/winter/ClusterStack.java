package br.com.winter;

import lombok.Getter;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ecs.Cluster;
import software.constructs.Construct;

@Getter
public class ClusterStack extends Stack {

    private final Cluster cluster;

    public ClusterStack(final Construct scope, final String id, Vpc vpc) {
        this(scope, id, null, vpc);
    }

    public ClusterStack(final Construct scope, final String id, final StackProps props, Vpc vpc) {
        super(scope, id, props);

        cluster = Cluster.Builder.create(this, id)
                .clusterName("Cluster-01")
                .vpc(vpc)
                .build();
    }
}