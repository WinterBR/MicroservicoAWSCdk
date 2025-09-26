package br.com.winter.projetoAWS01.service;

import br.com.winter.projetoAWS01.entity.Produto;
import br.com.winter.projetoAWS01.entity.Envelope;
import br.com.winter.projetoAWS01.enums.EventType;
import br.com.winter.projetoAWS01.entity.ProdutoEvent;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.Topic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ProdutoPublisher {
    private static final Logger LOG = LoggerFactory.getLogger(
            ProdutoPublisher.class);

    private final AmazonSNS snsClient;
    private final Topic produtoEventsTopic;
    private final ObjectMapper objectMapper;

    public ProdutoPublisher(AmazonSNS snsClient,
                            @Qualifier("produtoEventsTopic") Topic produtoEventsTopic,
                            ObjectMapper objectMapper) {
        this.snsClient = snsClient;
        this.produtoEventsTopic = produtoEventsTopic;
        this.objectMapper = objectMapper;
    }

    public void publishProdutoEvent(Produto produto, EventType eventType, String username) {
        ProdutoEvent produtoEvent = new ProdutoEvent();
        produtoEvent.setProdutoId(produto.getId());
        produtoEvent.setCode(produto.getCodigo());
        produtoEvent.setUsername(username);

        Envelope envelope = new Envelope();
        envelope.setEventType(eventType);

        try {
            envelope.setData(objectMapper.writeValueAsString(produtoEvent));

            String message = objectMapper.writeValueAsString(envelope);

            // Publica a mensagem no SNS e captura o resultado
            PublishResult publishResult = snsClient.publish(
                    produtoEventsTopic.getTopicArn(),
                    message
            );

            // Loga o MessageId retornado pelo SNS
            LOG.info("Produto event published - Event: {} - ProdutoId: {} - MessageId: {}",
                    eventType,
                    produto.getId(),
                    publishResult.getMessageId());

        } catch (JsonProcessingException e) {
            LOG.error("Failed to create produto event message", e);
        }
    }
}
