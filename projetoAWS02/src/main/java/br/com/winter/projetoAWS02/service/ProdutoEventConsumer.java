package br.com.winter.projetoAWS02.service;

import br.com.winter.projetoAWS02.model.Envelope;
import br.com.winter.projetoAWS02.model.ProdutoEventLog;
import br.com.winter.projetoAWS02.model.SnsMessage;
import br.com.winter.projetoAWS02.model.ProdutoEvent;
import br.com.winter.projetoAWS02.repository.ProdutoEventLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.awspring.cloud.sqs.annotation.SqsListener;

import java.time.Duration;
import java.time.Instant;

@Service
public class ProdutoEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(
            ProdutoEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final ProdutoEventLogRepository produtoEventLogRepository;

    @Autowired
    public ProdutoEventConsumer(ObjectMapper objectMapper, ProdutoEventLogRepository produtoEventLogRepository) {
        this.objectMapper = objectMapper;
        this.produtoEventLogRepository = produtoEventLogRepository;
    }

    @SqsListener("${aws.sqs.queue.produto.events.name}")
    public void receiveProdutoEvent(String message) {
        Envelope envelope;
        ProdutoEvent produtoEvent;
        String snsMessageId;

        try {
            SnsMessage snsMessage = objectMapper.readValue(message, SnsMessage.class);
            envelope = objectMapper.readValue(snsMessage.getMessage(), Envelope.class);
            produtoEvent = objectMapper.readValue(envelope.getData(), ProdutoEvent.class);
            snsMessageId = snsMessage.getMessageId();

            log.info("Produto event received - Event: {} - ProdutoId: {} - MessageId: {}",
                    envelope.getEventType(),
                    produtoEvent.getProdutoId(),
                    snsMessageId);

        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
            throw new RuntimeException("Failed to process message", e);
        }

        ProdutoEventLog produtoEventLog = buildProdutoEventLog(envelope, produtoEvent, snsMessageId);
        produtoEventLogRepository.save(produtoEventLog);
    }

    private ProdutoEventLog buildProdutoEventLog(Envelope envelope,
                                                 ProdutoEvent produtoEvent,
                                                 String snsMessageId) {
        long timestamp = Instant.now().toEpochMilli();

        ProdutoEventLog produtoEventLog = new ProdutoEventLog();
        produtoEventLog.setPk(produtoEvent.getCode());
        produtoEventLog.setSk(envelope.getEventType() + "_" + timestamp);
        produtoEventLog.setMessageId(snsMessageId);
        produtoEventLog.setEventType(envelope.getEventType());
        produtoEventLog.setProdutoId(produtoEvent.getProdutoId());
        produtoEventLog.setUsername(produtoEvent.getUsername());
        produtoEventLog.setTimestamp(timestamp);
        produtoEventLog.setTtl(Instant.now().plus(
                Duration.ofMinutes(10)).getEpochSecond());

        return produtoEventLog;
    }
}
