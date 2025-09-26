package br.com.winter.projetoAWS02.model;

import br.com.winter.projetoAWS02.enums.EventType;
import lombok.Getter;

@Getter
public class ProdutoEventLogDto {

    private final String code;
    private final EventType eventType;
    private final String messageId;
    private final long produtoId;
    private final String username;
    private final long timestamp;

    public ProdutoEventLogDto(ProdutoEventLog produtoEventLog) {
        this.code = produtoEventLog.getPk();
        this.eventType = produtoEventLog.getEventType();
        this.produtoId = produtoEventLog.getProdutoId();
        this.messageId = produtoEventLog.getMessageId();
        this.username = produtoEventLog.getUsername();
        this.timestamp = produtoEventLog.getTimestamp();
    }
}
