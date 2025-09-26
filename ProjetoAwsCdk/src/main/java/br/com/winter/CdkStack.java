package br.com.winter;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.constructs.Construct;

public class CdkStack extends Stack {

    public CdkStack(final Construct scope, final String id) { this(scope, id, null);}

    public CdkStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // Modelo base
        // Definir VPC, RDS, ECS, Load Balancer, etc.
    }
}