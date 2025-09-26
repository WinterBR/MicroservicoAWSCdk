package br.com.winter.projetoAWS02.model;

import br.com.winter.projetoAWS02.enums.EventType;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class ProdutoEventLog {

    private String pk;
    private String sk;
    private EventType eventType;
    private String messageId;
    private Long produtoId;
    private String username;
    private Long timestamp;
    private Long ttl;

    @DynamoDbPartitionKey
    public String getPk() {
        return pk;
    }

    @DynamoDbSortKey
    public String getSk() {
        return sk;
    }
}
