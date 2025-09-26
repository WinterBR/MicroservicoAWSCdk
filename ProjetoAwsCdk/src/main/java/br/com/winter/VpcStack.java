package br.com.winter;

import lombok.Getter;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.SubnetConfiguration;
import software.amazon.awscdk.services.ec2.SubnetType;
import software.amazon.awscdk.services.ec2.Vpc;
import software.constructs.Construct;

@Getter
public class VpcStack extends Stack {

    private final Vpc vpc;

    public VpcStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public VpcStack(final Construct scope, final String id, final StackProps stack) {
        super(scope, id, stack);

        this.vpc = Vpc.Builder.create(this, "Vpc01")
                .maxAzs(2)
                .subnetConfiguration(java.util.List.of(
                        SubnetConfiguration.builder()
                                .cidrMask(24)
                                .name("public")
                                .subnetType(SubnetType.PUBLIC)
                                .build(),
                        SubnetConfiguration.builder()
                                .cidrMask(24)
                                .name("private-egress")
                                .subnetType(SubnetType.PRIVATE_WITH_EGRESS)
                                .build()
                ))
                .build();
    }
}
