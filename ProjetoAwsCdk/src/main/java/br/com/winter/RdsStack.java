package br.com.winter;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.constructs.Construct;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.rds.*;

import java.util.Collections;

public class RdsStack extends Stack {
    public RdsStack(final Construct scope, final String id, Vpc vpc) {
        this(scope, id, null, vpc);
    }

    public RdsStack(final Construct scope, final String id, final StackProps props, Vpc vpc) {
        super(scope, id, props);

        // parâmetro de senha
        CfnParameter databasePassword = CfnParameter.Builder.create(this, "databasePassword")
                .type("String")
                .description("The RDS instance password")
                .build();

        // segurança
        ISecurityGroup iSecurityGroup = SecurityGroup.fromSecurityGroupId(this, "RdsSG", vpc.getVpcDefaultSecurityGroup());
        iSecurityGroup.addIngressRule(Peer.anyIpv4(), Port.tcp(3306));

        // instância RDS
        DatabaseInstance databaseInstance = DatabaseInstance.Builder
                .create(this, "Rds01")
                .instanceIdentifier("aws-project01-db")
                .engine(DatabaseInstanceEngine.mysql(MySqlInstanceEngineProps.builder()
                        .version(MysqlEngineVersion.of("8.0", "8.0.37"))  // já pode usar MySQL 8
                        .build()))
                .vpc(vpc)
                .credentials(Credentials.fromUsername("admin",
                        CredentialsFromUsernameOptions.builder()
                                .password(SecretValue.unsafePlainText(databasePassword.getValueAsString()))
                                .build()))
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE3, InstanceSize.MICRO)) // Free Tier
                .multiAz(false)
                .allocatedStorage(20)
                .securityGroups(Collections.singletonList(iSecurityGroup))
                .vpcSubnets(SubnetSelection.builder()
                        .subnetType(SubnetType.PRIVATE_WITH_EGRESS)
                        .build())
                .removalPolicy(RemovalPolicy.DESTROY)
                .deletionProtection(false)
                .build();

        // exporta endpoint e senha
        CfnOutput.Builder.create(this, "rds-endpoint")
                .exportName("rds-endpoint")
                .value(databaseInstance.getDbInstanceEndpointAddress())
                .build();

        CfnOutput.Builder.create(this, "rds-password")
                .exportName("rds-password")
                .value(databasePassword.getValueAsString())
                .build();
    }
}
