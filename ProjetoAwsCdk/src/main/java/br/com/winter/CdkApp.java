package br.com.winter;

import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;

public class CdkApp {
    public static void main(final String[] args) {
        App app = new App();

        VpcStack vpcStack = new VpcStack(app, "Vpc",
                StackProps.builder()
                        .stackName("Vpc")
                        .build());

        ClusterStack clusterStack = new ClusterStack(app, "Cluster",
                StackProps.builder()
                        .stackName("Cluster")
                        .build(),
                vpcStack.getVpc());
        clusterStack.addDependency(vpcStack);

        RdsStack rdsStack = new RdsStack(app, "Rds", vpcStack.getVpc());
        rdsStack.addDependency(vpcStack);

        SnsStack snsStack = new SnsStack(app, "Sns");

        InvoiceAppStack invoiceAppStack = new InvoiceAppStack(app, "InvoiceStack");

        Service01Stack service01Stack = new Service01Stack(app, "Service01",
                StackProps.builder()
                        .stackName("Service01")
                        .build(),
                clusterStack.getCluster(), snsStack.getProductEventsTopic(), invoiceAppStack.getBucket(), invoiceAppStack.getS3InvoiceQueue());
        service01Stack.addDependency(clusterStack);
        service01Stack.addDependency(rdsStack);
        service01Stack.addDependency(snsStack);
        service01Stack.addDependency(invoiceAppStack);

        DdbStack ddbStack = new DdbStack(app, "Ddb");

        Service02Stack service02Stack = new Service02Stack(app, "Service02",
                StackProps.builder()
                        .stackName("Service02")
                        .build(),
                clusterStack.getCluster(), snsStack.getProductEventsTopic(), ddbStack.getProdutoEventsDbd());
        service02Stack.addDependency(clusterStack);
        service02Stack.addDependency(snsStack);
        service02Stack.addDependency(ddbStack);

        app.synth();
    }
}