package br.com.winter.projetoAWS01.consumer;

import br.com.winter.projetoAWS01.entity.Invoice;
import br.com.winter.projetoAWS01.entity.SnsMessage;
import br.com.winter.projetoAWS01.repository.InvoiceRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class InvoiceConsumer {

    private static final Logger log = LoggerFactory.getLogger(InvoiceConsumer.class);

    private final ObjectMapper objectMapper;
    private final InvoiceRepository invoiceRepository;
    private final AmazonS3 amazonS3;

    public InvoiceConsumer(ObjectMapper objectMapper, InvoiceRepository invoiceRepository, AmazonS3 amazonS3) {
        this.objectMapper = objectMapper;
        this.invoiceRepository = invoiceRepository;
        this.amazonS3 = amazonS3;
    }

    @SqsListener("${aws.sqs.queue.invoice.events.name}")
    public void receiveS3Event(String message) throws IOException {

        SnsMessage snsMessage = objectMapper.readValue(message, SnsMessage.class);

        S3EventNotification s3EventNotification =
                objectMapper.readValue(snsMessage.getMessage(), S3EventNotification.class);

        processInvoiceNotification(s3EventNotification);
    }

    private void processInvoiceNotification(S3EventNotification s3EventNotification) throws IOException {
        for (S3EventNotification.S3EventNotificationRecord record : s3EventNotification.getRecords()) {
            S3EventNotification.S3Entity s3Entity = record.getS3();

            String bucketName = s3Entity.getBucket().getName();
            String objectKey = s3Entity.getObject().getKey();

            String invoiceFile = downloadObject(bucketName, objectKey);

            Invoice invoice = objectMapper.readValue(invoiceFile, Invoice.class);
            log.info("Invoice received: {}", invoice.getInvoiceNumber());

            invoiceRepository.save(invoice);

            amazonS3.deleteObject(bucketName, objectKey);
        }
    }

    private String downloadObject(String bucketName, String objectKey) throws IOException {
        S3Object s3Object = amazonS3.getObject(bucketName, objectKey);

        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()))) {
            String content;
            while ((content = bufferedReader.readLine()) != null) {
                stringBuilder.append(content);
            }
        }
        return stringBuilder.toString();
    }
}
